package dev.rushi.apkdownloadhelper

import android.util.Log
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.security.MessageDigest
import java.util.concurrent.CancellationException
import java.util.concurrent.TimeUnit
import java.util.zip.ZipFile

/**
 * VirusTotal API v3 client for scanning downloaded APK files.
 *
 * Free-tier limits: 4 lookups/min, 500/day, 15.5K/month.
 * Upload size: up to 32 MB via POST /files; larger files (up to 650 MB)
 * go through the single-use /files/upload_url endpoint.
 *
 * Flow: compute the file's SHA-256 locally and ask VirusTotal for the
 * existing report first (GET /files/{sha256}). If the file was already
 * analysed — which is the common case for popular APKs — that report is
 * returned instantly without uploading, so we never hit the "file already
 * submitted" dead end. Only files unknown to VirusTotal are uploaded, then
 * the analysis is polled until it completes.
 */
internal object VirusTotalScanner {

    private const val TAG = "VirusTotalScanner"
    private const val BASE_URL = "https://www.virustotal.com/api/v3"

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(120, TimeUnit.SECONDS)
        .build()

    private val gson = Gson()

    sealed interface ScanResult {
        val sha256: String?
        val fileName: String
        val typeDescription: String?
        val sizeBytes: Long?
        val timesSubmitted: Int?
        val firstSubmissionDate: Long?
        val lastAnalysisDate: Long?
        val reputation: Int?
        /**
         * True when the verdict came from a report VirusTotal already had on
         * file (no upload); false when the file was freshly uploaded and
         * analysed just now. Null verdicts (Error) are never cached.
         */
        val cached: Boolean
        /** Non-null when a XAPK/APKS/APKM bundle was scanned instead of one file. */
        val scannedFiles: Int?
        val bundleName: String?
        /** Per-inner-APK outcomes for bundle scans (empty for single files). */
        val apkResults: List<BundleApkResult>

        data class Clean(
            override val sha256: String?,
            override val fileName: String,
            override val typeDescription: String?,
            override val sizeBytes: Long?,
            override val timesSubmitted: Int?,
            override val firstSubmissionDate: Long?,
            override val lastAnalysisDate: Long?,
            override val reputation: Int?,
            val totalEngines: Int,
            val votesHarmless: Int,
            val votesMalicious: Int,
            override val cached: Boolean = false,
            // Set when a XAPK/APKS/APKM bundle was scanned: how many inner
            // APKs were scanned and the bundle's file name.
            override val scannedFiles: Int? = null,
            override val bundleName: String? = null,
            override val apkResults: List<BundleApkResult> = emptyList()
        ) : ScanResult

        data class Malicious(
            override val sha256: String?,
            override val fileName: String,
            override val typeDescription: String?,
            override val sizeBytes: Long?,
            override val timesSubmitted: Int?,
            override val firstSubmissionDate: Long?,
            override val lastAnalysisDate: Long?,
            override val reputation: Int?,
            val detections: Int,
            val suspicious: Int,
            val totalEngines: Int,
            val engines: List<EngineDetection>,
            val suggestedThreatLabel: String?,
            val sandboxMalwareNames: List<String>,
            override val cached: Boolean = false,
            // Set when a XAPK/APKS/APKM bundle was scanned: how many inner
            // APKs were scanned, how many were flagged, and the bundle name.
            // [fileName] then names the flagged inner APK.
            override val scannedFiles: Int? = null,
            val flaggedFiles: Int? = null,
            override val bundleName: String? = null,
            override val apkResults: List<BundleApkResult> = emptyList()
        ) : ScanResult

        data class Error(val message: String) : ScanResult {
            override val sha256: String? get() = null
            override val fileName: String get() = ""
            override val typeDescription: String? get() = null
            override val sizeBytes: Long? get() = null
            override val timesSubmitted: Int? get() = null
            override val firstSubmissionDate: Long? get() = null
            override val lastAnalysisDate: Long? get() = null
            override val reputation: Int? get() = null
            override val cached: Boolean get() = false
            override val scannedFiles: Int? get() = null
            override val bundleName: String? get() = null
            override val apkResults: List<BundleApkResult> get() = emptyList()
        }
    }

    /** One antivirus engine's verdict: engine name + human-readable result. */
    data class EngineDetection(
        val engine: String,
        val result: String,
        val category: String
    )

    /** One inner APK's outcome inside a scanned bundle. */
    data class BundleApkResult(
        val name: String,
        val totalEngines: Int,
        val detections: Int,
        val sha256: String?,
        val engines: List<EngineDetection> = emptyList(),
        val failed: Boolean = false
    )

    /** Free-tier request quotas reported by /users/{id}/overall_quotas. */
    data class QuotaUsage(
        val hourlyUsed: Int, val hourlyAllowed: Int,
        val dailyUsed: Int, val dailyAllowed: Int,
        val monthlyUsed: Int, val monthlyAllowed: Int
    )

    /**
     * Scan a downloaded file. XAPK/APKS/APKM bundles are split into their
     * inner APKs and each one is scanned individually — engines often skip
     * large container files — while plain APKs are scanned directly.
     */
    suspend fun scanDownloadedFile(
        file: File,
        apiKey: String,
        onProgress: ((String) -> Unit)? = null,
        checkCancelled: () -> Boolean = { false }
    ): ScanResult {
        val lower = file.name.lowercase()
        val isBundle = lower.endsWith(".xapk") || lower.endsWith(".apks") || lower.endsWith(".apkm")
        return if (isBundle) {
            scanBundle(file, apiKey, onProgress, checkCancelled)
        } else {
            scanFile(file, apiKey, onProgress, checkCancelled)
        }
    }

    /**
     * Extract the inner APKs of a bundle container and scan each one,
     * returning a combined verdict. Falls back to scanning the container
     * itself if it contains no APK entries.
     */
    private suspend fun scanBundle(
        file: File,
        apiKey: String,
        onProgress: ((String) -> Unit)? = null,
        checkCancelled: () -> Boolean = { false }
    ): ScanResult {
        val apkNames = runCatching {
            ZipFile(file).use { zip ->
                zip.entries().asSequence()
                    .filter { !it.isDirectory }
                    .map { it.name }
                    .filter { it.lowercase().endsWith(".apk") }
                    .toList()
            }
        }.getOrNull()
        if (apkNames.isNullOrEmpty()) {
            Log.d(TAG, "${file.name} is not a ZIP of APKs — scanning the container itself.")
            return scanFile(file, apiKey, onProgress, checkCancelled)
        }

        onProgress?.invoke("Extracting ${apkNames.size} APKs from ${file.name}…")
        val tempDir = File.createTempFile("vt-bundle", "").apply { delete(); mkdirs() }
        try {
            val total = apkNames.size
            val innerResults = mutableListOf<Pair<ScanResult, String>>()
            ZipFile(file).use { zip ->
                apkNames.forEachIndexed { index, name ->
                    if (checkCancelled()) throw CancellationException("Scan cancelled")
                    val safeName = name.substringAfterLast('/').replace(Regex("[^A-Za-z0-9._-]"), "_")
                    val out = File(tempDir, safeName)
                    zip.getInputStream(zip.getEntry(name)).use { input ->
                        out.outputStream().use { output -> input.copyTo(output) }
                    }
                    // Prefix every per-APK status with which APK is current so the
                    // UI shows live progress instead of a static "Checking…" that
                    // looks stuck during the 12s rate-limit pauses between APKs.
                    onProgress?.invoke("Scanning APK ${index + 1} of $total: $name…")
                    innerResults.add(
                        scanFile(
                            out,
                            apiKey,
                            onProgress = { status ->
                                onProgress?.invoke("APK ${index + 1} of $total ($name): $status")
                            },
                            checkCancelled = checkCancelled
                        ) to name
                    )
                    // The free tier allows ~4 lookups per minute, so space the
                    // per-APK lookups out instead of hammering the rate limit.
                    // Sleep in small slices so a cancel lands within a second.
                    if (index < apkNames.size - 1) {
                        repeat(12) {
                            if (checkCancelled()) throw CancellationException("Scan cancelled")
                            Thread.sleep(1000)
                        }
                    }
                }
            }
            onProgress?.invoke("Aggregating results for ${apkNames.size} APKs…")
            return aggregateBundle(innerResults, file.name)
        } finally {
            tempDir.deleteRecursively()
        }
    }

    private fun aggregateBundle(
        results: List<Pair<ScanResult, String>>,
        bundleName: String
    ): ScanResult {
        val total = results.size
        val apkResults = results.map { (result, name) ->
            when (result) {
                is ScanResult.Clean -> BundleApkResult(
                    name = name,
                    totalEngines = result.totalEngines,
                    detections = 0,
                    sha256 = result.sha256
                )
                is ScanResult.Malicious -> BundleApkResult(
                    name = name,
                    totalEngines = result.totalEngines,
                    detections = result.detections,
                    sha256 = result.sha256,
                    engines = result.engines
                )
                is ScanResult.Error -> BundleApkResult(
                    name = name,
                    totalEngines = 0,
                    detections = 0,
                    sha256 = null,
                    failed = true
                )
            }
        }
        val malicious = results.filter { it.first is ScanResult.Malicious }
        val errors = results.filter { it.first is ScanResult.Error }
        val clean = results.filter { it.first is ScanResult.Clean }

        if (malicious.isNotEmpty()) {
            val worst = malicious.maxByOrNull { (r, _) -> (r as ScanResult.Malicious).detections }!!
            val flagged = worst.first as ScanResult.Malicious
            return flagged.copy(
                fileName = worst.second,
                sizeBytes = null, // would be the inner APK, confusing next to the bundle
                cached = results.all { it.first.cached },
                scannedFiles = total,
                flaggedFiles = malicious.size,
                bundleName = bundleName,
                apkResults = apkResults
            )
        }
        if (errors.isNotEmpty()) {
            val firstError = (errors.first().first as ScanResult.Error).message
            val message = if (clean.isEmpty()) {
                "All $total APKs failed to scan: $firstError"
            } else {
                "${clean.size} of $total APKs scanned clean; ${errors.size} failed ($firstError)"
            }
            return ScanResult.Error(message)
        }
        val totalEngines = clean.sumOf { (it.first as ScanResult.Clean).totalEngines }
        val firstClean = clean.first().first as ScanResult.Clean
        return firstClean.copy(
            fileName = bundleName,
            sha256 = null,
            typeDescription = null,
            sizeBytes = null,
            timesSubmitted = null,
            firstSubmissionDate = null,
            lastAnalysisDate = null,
            reputation = null,
            totalEngines = totalEngines,
            votesHarmless = 0,
            votesMalicious = 0,
            cached = results.all { it.first.cached },
            scannedFiles = total,
            bundleName = bundleName,
            apkResults = apkResults
        )
    }

    /**
     * Check VirusTotal for [file] and return a [ScanResult] with the detection
     * summary plus whatever file metadata the API returned.
     */
    suspend fun scanFile(
        file: File,
        apiKey: String,
        onProgress: ((String) -> Unit)? = null,
        checkCancelled: () -> Boolean = { false }
    ): ScanResult {
        if (checkCancelled()) throw CancellationException("Scan cancelled")
        if (!file.exists()) {
            return ScanResult.Error("File not found: ${file.name}")
        }
        if (file.length() > 650L * 1024 * 1024) {
            return ScanResult.Error("File too large for VirusTotal scan (max 650 MB)")
        }

        return try {
            val sha256 = sha256Of(file)
            Log.d(TAG, "SHA-256: $sha256")

            // Fast path: the file is already in VirusTotal's database.
            onProgress?.invoke("Checking VirusTotal…")
            fetchFileReport(sha256, apiKey)?.let { report ->
                Log.d(TAG, "File already analysed — using existing report.")
                return parseFileReport(report, file.name)
            }

            onProgress?.invoke("Uploading ${file.name} to VirusTotal…")
            val analysisId = try {
                uploadFile(file, apiKey)
            } catch (e: AlreadySubmittedException) {
                // Analysed between our check and the upload: fetch the report.
                Log.d(TAG, "Upload rejected (already submitted) — fetching report.")
                val report = fetchFileReport(sha256, apiKey)
                    ?: throw Exception("File already submitted but no report was returned")
                return parseFileReport(report, file.name)
            }
            Log.d(TAG, "Upload complete, analysis ID: $analysisId")

            onProgress?.invoke("Waiting for analysis…")
            val analysis = pollAnalysis(analysisId, apiKey, onProgress, checkCancelled)
            parseAnalysis(analysis, file.name)
        } catch (e: Exception) {
            Log.e(TAG, "Scan failed", e)
            ScanResult.Error("Scan failed: ${e.message ?: "Unknown error"}")
        }
    }

    /**
     * Fetch the account's request quotas (free tier: 240/hour, 500/day,
     * 15.5K/month). The API key itself works as the user id, and this
     * endpoint does not consume quota. Returns null on failure.
     */
    fun fetchQuotaUsage(apiKey: String): QuotaUsage? {
        if (apiKey.isBlank()) return null
        return try {
            val request = Request.Builder()
                .url("$BASE_URL/users/$apiKey/overall_quotas")
                .addHeader("x-apikey", apiKey)
                .get()
                .build()
            val response = client.newCall(request).execute()
            val body = response.body?.string() ?: return null
            if (!response.isSuccessful) {
                Log.w(TAG, "Quota lookup failed (${response.code})")
                return null
            }
            val quotas = gson.fromJson(body, QuotasResponse::class.java).data ?: return null
            QuotaUsage(
                hourlyUsed = quotas.hourly?.user?.used ?: 0,
                hourlyAllowed = quotas.hourly?.user?.allowed ?: 0,
                dailyUsed = quotas.daily?.user?.used ?: 0,
                dailyAllowed = quotas.daily?.user?.allowed ?: 0,
                monthlyUsed = quotas.monthly?.user?.used ?: 0,
                monthlyAllowed = quotas.monthly?.user?.allowed ?: 0
            )
        } catch (e: Exception) {
            Log.w(TAG, "Quota lookup failed", e)
            null
        }
    }

    private fun sha256Of(file: File): String {
        val digest = MessageDigest.getInstance("SHA-256")
        file.inputStream().use { input ->
            val buffer = ByteArray(64 * 1024)
            while (true) {
                val read = input.read(buffer)
                if (read < 0) break
                digest.update(buffer, 0, read)
            }
        }
        return digest.digest().joinToString("") { (it.toInt() and 0xff).toString(16).padStart(2, '0') }
    }

    /** Returns the stored report for [sha256], or null if VirusTotal has never seen it. */
    private fun fetchFileReport(sha256: String, apiKey: String): FileReportResponse? {
        val request = Request.Builder()
            .url("$BASE_URL/files/$sha256")
            .addHeader("x-apikey", apiKey)
            .get()
            .build()
        val response = client.newCall(request).execute()
        val body = response.body?.string() ?: return null
        if (response.code == 429) {
            // Rate limited: back off once and retry before giving up.
            Log.d(TAG, "Report lookup rate limited (429), retrying after backoff.")
            Thread.sleep(20_000)
            val retry = client.newCall(request).execute()
            val retryBody = retry.body?.string() ?: return null
            if (retry.code == 404) return null
            if (!retry.isSuccessful) {
                throw Exception("Report lookup failed (${retry.code}): $retryBody")
            }
            return gson.fromJson(retryBody, FileReportResponse::class.java)
        }
        if (response.code == 404) return null
        if (!response.isSuccessful) {
            throw Exception("Report lookup failed (${response.code}): $body")
        }
        return gson.fromJson(body, FileReportResponse::class.java)
    }

    private fun uploadFile(file: File, apiKey: String): String {
        if (file.length() > 32L * 1024 * 1024) {
            // POST /files rejects anything above 32 MB; use the presigned URL.
            return uploadViaUploadUrl(file, apiKey)
        }
        return try {
            uploadDirect(file, apiKey)
        } catch (e: PayloadTooLargeException) {
            Log.d(TAG, "Direct upload rejected (too large), falling back to upload URL.")
            uploadViaUploadUrl(file, apiKey)
        }
    }

    /** POST the file straight to /files (only admits files up to 32 MB). */
    private fun uploadDirect(file: File, apiKey: String): String {
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "file",
                file.name,
                file.asRequestBody("application/octet-stream".toMediaType())
            )
            .build()

        val request = Request.Builder()
            .url("$BASE_URL/files")
            .addHeader("x-apikey", apiKey)
            .post(requestBody)
            .build()

        val response = client.newCall(request).execute()
        val body = response.body?.string() ?: throw Exception("Empty response")

        if (!response.isSuccessful) {
            val error = gson.fromJson(body, ErrorResponse::class.java)
            val message = error.error?.message ?: "Upload failed (${response.code})"
            if (response.code == 409) {
                throw AlreadySubmittedException(message)
            }
            if (response.code == 413) {
                throw PayloadTooLargeException(message)
            }
            throw Exception(message)
        }

        val uploadResponse = gson.fromJson(body, UploadResponse::class.java)
        return uploadResponse.data?.id ?: throw Exception("No analysis ID in response")
    }

    /**
     * Request a single-use presigned upload URL, then POST the file to it.
     * Admits files up to 650 MB.
     */
    private fun uploadViaUploadUrl(file: File, apiKey: String): String {
        val urlRequest = Request.Builder()
            .url("$BASE_URL/files/upload_url")
            .addHeader("x-apikey", apiKey)
            .get()
            .build()
        val urlResponse = client.newCall(urlRequest).execute()
        val urlBody = urlResponse.body?.string() ?: throw Exception("Empty upload URL response")
        if (!urlResponse.isSuccessful) {
            throw Exception("Getting upload URL failed (${urlResponse.code}): $urlBody")
        }
        val uploadUrl = gson.fromJson(urlBody, UploadUrlResponse::class.java).data
            ?: throw Exception("No upload URL in response")

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "file",
                file.name,
                file.asRequestBody("application/octet-stream".toMediaType())
            )
            .build()
        val request = Request.Builder()
            .url(uploadUrl)
            .addHeader("x-apikey", apiKey)
            .post(requestBody)
            .build()

        val response = client.newCall(request).execute()
        val body = response.body?.string() ?: throw Exception("Empty response")
        if (!response.isSuccessful) {
            val error = gson.fromJson(body, ErrorResponse::class.java)
            throw Exception(error.error?.message ?: "Upload via URL failed (${response.code})")
        }
        val uploadResponse = gson.fromJson(body, UploadResponse::class.java)
        return uploadResponse.data?.id ?: throw Exception("No analysis ID in response")
    }

    private fun pollAnalysis(
        analysisId: String,
        apiKey: String,
        onProgress: ((String) -> Unit)?,
        checkCancelled: () -> Boolean = { false }
    ): AnalysisResponse {
        val maxAttempts = 90 // Poll every 3s for up to ~4.5 minutes.
        var lastStatus = ""
        repeat(maxAttempts) { attempt ->
            if (checkCancelled()) throw CancellationException("Scan cancelled")
            Thread.sleep(3000)

            val request = Request.Builder()
                .url("$BASE_URL/analyses/$analysisId")
                .addHeader("x-apikey", apiKey)
                .get()
                .build()

            val response = client.newCall(request).execute()
            val body = response.body?.string() ?: throw Exception("Empty response")

            if (response.code == 429) {
                // Rate limited: back off for a while and keep waiting.
                Log.d(TAG, "Analysis poll rate limited (429), backing off.")
                Thread.sleep(15_000)
                return@repeat
            }
            if (!response.isSuccessful) {
                throw Exception("Analysis poll failed (${response.code}): $body")
            }

            val analysis = gson.fromJson(body, AnalysisResponse::class.java)
            val status = analysis.data?.attributes?.status
            Log.d(TAG, "Analysis poll ${attempt + 1}: status=$status")

            if (status == "completed") {
                return analysis
            }
            if (status != lastStatus) {
                lastStatus = status ?: ""
                onProgress?.invoke(
                    when (status) {
                        "queued" -> "Queued at VirusTotal — waiting for engines…"
                        "in-progress" -> "VirusTotal engines scanning…"
                        else -> "Waiting for analysis…"
                    }
                )
            }
        }

        throw Exception("Analysis timed out after ${maxAttempts * 3}s (VirusTotal queue is busy)")
    }

    // --- Result parsing ---

    private fun parseFileReport(report: FileReportResponse, fileName: String): ScanResult {
        val attrs = report.data?.attributes
            ?: return ScanResult.Error("No file data in the VirusTotal report")
        val stats = attrs.lastAnalysisStats
            ?: return ScanResult.Error("No analysis stats in the VirusTotal report")
        val totalEngines = stats.total()
        if (totalEngines == 0) {
            return ScanResult.Error("No engine results available")
        }
        val malicious = stats.malicious
        val suspicious = stats.suspicious
        val engineHits = detectionEngines(attrs.lastAnalysisResults)

        return if (malicious + suspicious > 0) {
            ScanResult.Malicious(
                sha256 = attrs.sha256 ?: report.data?.id,
                fileName = fileName,
                typeDescription = attrs.typeDescription,
                sizeBytes = attrs.size,
                timesSubmitted = attrs.timesSubmitted,
                detections = malicious + suspicious,
                suspicious = suspicious,
                totalEngines = totalEngines,
                engines = engineHits,
                suggestedThreatLabel = suggestedLabel(engineHits, attrs),
                sandboxMalwareNames = attrs.sandboxMalwareNames(),
                firstSubmissionDate = attrs.firstSubmissionDate,
                lastAnalysisDate = attrs.lastAnalysisDate,
                reputation = attrs.reputation,
                // The report came from VirusTotal's database, not a new upload.
                cached = true
            )
        } else {
            ScanResult.Clean(
                sha256 = attrs.sha256 ?: report.data?.id,
                fileName = fileName,
                typeDescription = attrs.typeDescription,
                sizeBytes = attrs.size,
                timesSubmitted = attrs.timesSubmitted,
                totalEngines = totalEngines,
                firstSubmissionDate = attrs.firstSubmissionDate,
                lastAnalysisDate = attrs.lastAnalysisDate,
                reputation = attrs.reputation,
                votesHarmless = attrs.totalVotes?.harmless ?: 0,
                votesMalicious = attrs.totalVotes?.malicious ?: 0,
                // The report came from VirusTotal's database, not a new upload.
                cached = true
            )
        }
    }

    private fun parseAnalysis(analysis: AnalysisResponse, fileName: String): ScanResult {
        val attrs = analysis.data?.attributes
            ?: return ScanResult.Error("No analysis data available")
        val stats = attrs.stats
            ?: return ScanResult.Error("No analysis stats available")
        val totalEngines = stats.total()
        if (totalEngines == 0) {
            return ScanResult.Error("No engine results available")
        }
        val fileInfo = attrs.meta?.fileInfo
        val malicious = stats.malicious
        val suspicious = stats.suspicious
        val engineHits = detectionEngines(attrs.results)

        return if (malicious + suspicious > 0) {
            ScanResult.Malicious(
                sha256 = fileInfo?.sha256,
                fileName = fileName,
                typeDescription = fileInfo?.typeDescription,
                sizeBytes = fileInfo?.size,
                timesSubmitted = fileInfo?.timesSubmitted,
                detections = malicious + suspicious,
                suspicious = suspicious,
                totalEngines = totalEngines,
                engines = engineHits,
                suggestedThreatLabel = attrs.threatNames?.firstOrNull() ?: suggestedLabel(engineHits, null),
                sandboxMalwareNames = emptyList(),
                firstSubmissionDate = null,
                lastAnalysisDate = attrs.date,
                reputation = null,
                // Freshly uploaded and analysed just now.
                cached = false
            )
        } else {
            ScanResult.Clean(
                sha256 = fileInfo?.sha256,
                fileName = fileName,
                typeDescription = fileInfo?.typeDescription,
                sizeBytes = fileInfo?.size,
                timesSubmitted = fileInfo?.timesSubmitted,
                totalEngines = totalEngines,
                firstSubmissionDate = null,
                lastAnalysisDate = attrs.date,
                reputation = null,
                votesHarmless = 0,
                votesMalicious = 0,
                // Freshly uploaded and analysed just now.
                cached = false
            )
        }
    }

    private fun AnalysisStats.total(): Int =
        harmless + malicious + suspicious + undetected + timeout +
            `type-unsupported` + failure + `confirmed-timeout`

    private fun detectionEngines(results: Map<String, EngineResult>?): List<EngineDetection> =
        results.orEmpty()
            .filterValues { it?.category == "malicious" || it?.category == "suspicious" }
            .mapNotNull { (engine, result) ->
                val name = result?.engineName ?: engine
                val detail = result?.result
                if (detail.isNullOrBlank()) null else EngineDetection(name, detail, result.category ?: "")
            }
            .sortedWith(
                compareByDescending<EngineDetection> { it.category == "malicious" }
                    .thenBy { it.engine }
            )

    private fun suggestedLabel(
        engines: List<EngineDetection>,
        attrs: FileReportAttributes?
    ): String? {
        // Prefer VirusTotal's own classification of what the file is.
        attrs?.sandboxVerdicts?.values
            ?.flatMap { it.malwareNames.orEmpty() }
            ?.firstOrNull()
            ?.let { return it }
        // Otherwise use the most common detection string from the engines.
        return engines
            .groupBy { it.result }
            .maxByOrNull { it.value.size }
            ?.key
    }

    private fun FileReportAttributes.sandboxMalwareNames(): List<String> =
        sandboxVerdicts?.values
            ?.flatMap { it.malwareNames.orEmpty() }
            ?.distinct()
            ?.take(4)
            ?: emptyList()

    // --- Response data classes ---

    private class AlreadySubmittedException(message: String) : Exception(message)

    /**
     * Gson adapter that round-trips a [ScanResult] (a sealed interface Gson
     * cannot deserialize on its own) by tagging each object with its kind.
     */
    internal class ScanResultTypeAdapter :
        com.google.gson.JsonSerializer<ScanResult>, com.google.gson.JsonDeserializer<ScanResult> {
        override fun serialize(
            src: ScanResult,
            typeOfSrc: java.lang.reflect.Type,
            context: com.google.gson.JsonSerializationContext
        ): com.google.gson.JsonElement {
            val obj = context.serialize(src, src.javaClass).asJsonObject
            obj.addProperty("kind", when (src) {
                is ScanResult.Clean -> "CLEAN"
                is ScanResult.Malicious -> "MALICIOUS"
                is ScanResult.Error -> "ERROR"
            })
            return obj
        }

        override fun deserialize(
            json: com.google.gson.JsonElement,
            typeOfT: java.lang.reflect.Type,
            context: com.google.gson.JsonDeserializationContext
        ): ScanResult {
            val obj = json.asJsonObject
            return when (obj.get("kind")?.asString) {
                "CLEAN" -> context.deserialize(json, ScanResult.Clean::class.java)
                "MALICIOUS" -> context.deserialize(json, ScanResult.Malicious::class.java)
                else -> context.deserialize(json, ScanResult.Error::class.java)
            }
        }
    }

    private class PayloadTooLargeException(message: String) : Exception(message)

    private data class UploadResponse(
        val data: AnalysisData?
    )

    private data class UploadUrlResponse(
        val data: String?
    )

    private data class QuotasResponse(
        val data: QuotasData?
    )

    private data class QuotasData(
        @SerializedName("api_requests_hourly")
        val hourly: QuotaBucket?,
        @SerializedName("api_requests_daily")
        val daily: QuotaBucket?,
        @SerializedName("api_requests_monthly")
        val monthly: QuotaBucket?
    )

    private data class QuotaBucket(
        val user: QuotaValue?
    )

    private data class QuotaValue(
        val allowed: Int = 0,
        val used: Int = 0
    )

    private data class AnalysisResponse(
        val data: AnalysisData?
    )

    private data class AnalysisData(
        val id: String?,
        val attributes: AnalysisAttributes?
    )

    private data class AnalysisAttributes(
        val status: String?,
        val date: Long?,
        val stats: AnalysisStats?,
        val results: Map<String, EngineResult>?,
        @SerializedName("threat_names")
        val threatNames: List<String>?,
        val meta: AnalysisMeta?
    )

    private data class AnalysisMeta(
        @SerializedName("file_info")
        val fileInfo: FileInfo?
    )

    private data class FileInfo(
        val sha256: String?,
        val size: Long?,
        @SerializedName("type_description")
        val typeDescription: String?,
        @SerializedName("times_submitted")
        val timesSubmitted: Int?
    )

    private data class FileReportResponse(
        val data: FileReportData?
    )

    private data class FileReportData(
        val id: String?,
        val attributes: FileReportAttributes?
    )

    private data class FileReportAttributes(
        val sha256: String?,
        @SerializedName("type_description")
        val typeDescription: String?,
        val size: Long?,
        @SerializedName("first_submission_date")
        val firstSubmissionDate: Long?,
        @SerializedName("last_analysis_date")
        val lastAnalysisDate: Long?,
        @SerializedName("times_submitted")
        val timesSubmitted: Int?,
        val reputation: Int?,
        @SerializedName("total_votes")
        val totalVotes: TotalVotes?,
        @SerializedName("last_analysis_stats")
        val lastAnalysisStats: AnalysisStats?,
        @SerializedName("last_analysis_results")
        val lastAnalysisResults: Map<String, EngineResult>?,
        @SerializedName("sandbox_verdicts")
        val sandboxVerdicts: Map<String, SandboxVerdict>?
    )

    private data class TotalVotes(
        val harmless: Int = 0,
        val malicious: Int = 0
    )

    private data class AnalysisStats(
        val harmless: Int = 0,
        val malicious: Int = 0,
        val suspicious: Int = 0,
        val undetected: Int = 0,
        val timeout: Int = 0,
        @SerializedName("type-unsupported")
        val `type-unsupported`: Int = 0,
        // Note: the API key is "failure" (not "failed") in both the analysis
        // stats and the file report's last_analysis_stats.
        @SerializedName("failure")
        val failure: Int = 0,
        @SerializedName("confirmed-timeout")
        val `confirmed-timeout`: Int = 0,
    )

    private data class EngineResult(
        val category: String?,
        val result: String?,
        @SerializedName("engine_name")
        val engineName: String?
    )

    private data class SandboxVerdict(
        val category: String?,
        @SerializedName("malware_names")
        val malwareNames: List<String>?
    )

    private data class ErrorResponse(
        val error: ErrorDetail?
    )

    private data class ErrorDetail(
        val code: String?,
        val message: String?
    )
}
