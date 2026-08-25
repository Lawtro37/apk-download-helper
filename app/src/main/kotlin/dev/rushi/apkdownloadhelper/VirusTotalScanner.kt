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
import java.util.concurrent.TimeUnit

/**
 * Minimal VirusTotal API v3 client for scanning downloaded APK files.
 *
 * Free-tier limits: 4 lookups/min, 500/day, 15.5K/month.
 * Max upload size: 32 MB (free tier).
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
        data class Clean(
            val totalEngines: Int,
            val fileName: String
        ) : ScanResult

        data class Malicious(
            val detections: Int,
            val totalEngines: Int,
            val detectionNames: List<String>,
            val fileName: String
        ) : ScanResult

        data class Error(val message: String) : ScanResult
    }

    /**
     * Upload a file for scanning and wait for the analysis to complete.
     * Returns a [ScanResult] with the detection summary.
     */
    suspend fun scanFile(
        file: File,
        apiKey: String,
        onProgress: ((String) -> Unit)? = null
    ): ScanResult {
        if (!file.exists()) {
            return ScanResult.Error("File not found: ${file.name}")
        }
        if (file.length() > 32L * 1024 * 1024) {
            return ScanResult.Error("File too large for free-tier scan (max 32 MB)")
        }

        return try {
            onProgress?.invoke("Uploading ${file.name} to VirusTotal…")
            val analysisId = uploadFile(file, apiKey)
            Log.d(TAG, "Upload complete, analysis ID: $analysisId")

            onProgress?.invoke("Waiting for analysis…")
            val analysis = pollAnalysis(analysisId, apiKey)
            parseAnalysis(analysis, file.name)
        } catch (e: Exception) {
            Log.e(TAG, "Scan failed", e)
            ScanResult.Error("Scan failed: ${e.message ?: "Unknown error"}")
        }
    }

    private fun uploadFile(file: File, apiKey: String): String {
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
            throw Exception(error.error?.message ?: "Upload failed (${response.code})")
        }

        val uploadResponse = gson.fromJson(body, UploadResponse::class.java)
        return uploadResponse.data?.id ?: throw Exception("No analysis ID in response")
    }

    private fun pollAnalysis(analysisId: String, apiKey: String): AnalysisResponse {
        val maxAttempts = 30 // Poll for up to ~60 seconds
        repeat(maxAttempts) { attempt ->
            Thread.sleep(2000) // Wait 2 seconds between polls

            val request = Request.Builder()
                .url("$BASE_URL/analyses/$analysisId")
                .addHeader("x-apikey", apiKey)
                .get()
                .build()

            val response = client.newCall(request).execute()
            val body = response.body?.string() ?: throw Exception("Empty response")

            if (!response.isSuccessful) {
                throw Exception("Analysis poll failed (${response.code})")
            }

            val analysis = gson.fromJson(body, AnalysisResponse::class.java)
            val status = analysis.data?.attributes?.status
            Log.d(TAG, "Analysis poll ${attempt + 1}: status=$status")

            if (status == "completed") {
                return analysis
            }
        }

        throw Exception("Analysis timed out after ${maxAttempts * 2}s")
    }

    private fun parseAnalysis(analysis: AnalysisResponse, fileName: String): ScanResult {
        val stats = analysis.data?.attributes?.stats
            ?: return ScanResult.Error("No analysis stats available")

        val detections = stats.malicious + stats.suspicious
        val totalEngines = stats.harmless + stats.malicious + stats.suspicious +
            stats.undetected + stats.timeout + stats.`type-unsupported` +
            stats.failed + stats.`confirmed-timeout`

        if (totalEngines == 0) {
            return ScanResult.Error("No engine results available")
        }

        val detectionNames = analysis.data?.attributes?.results
            ?.filter { it.value?.category == "malicious" || it.value?.category == "suspicious" }
            ?.mapNotNull { it.key }
            ?: emptyList()

        return if (detections > 0) {
            ScanResult.Malicious(
                detections = detections,
                totalEngines = totalEngines,
                detectionNames = detectionNames,
                fileName = fileName
            )
        } else {
            ScanResult.Clean(
                totalEngines = totalEngines,
                fileName = fileName
            )
        }
    }

    // --- Response data classes ---

    private data class UploadResponse(
        val data: AnalysisData?
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
        val stats: AnalysisStats?,
        val results: Map<String, EngineResult>?
    )

    private data class AnalysisStats(
        val harmless: Int = 0,
        val malicious: Int = 0,
        val suspicious: Int = 0,
        val undetected: Int = 0,
        val timeout: Int = 0,
        @SerializedName("type-unsupported")
        val `type-unsupported`: Int = 0,
        val failed: Int = 0,
        @SerializedName("confirmed-timeout")
        val `confirmed-timeout`: Int = 0,
    )

    private data class EngineResult(
        val category: String?
    )

    private data class ErrorResponse(
        val error: ErrorDetail?
    )

    private data class ErrorDetail(
        val message: String?
    )
}
