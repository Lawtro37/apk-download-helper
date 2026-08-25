package dev.rushi.apkdownloadhelper

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

/**
 * Pings every web-based source at app launch and reports whether it is
 * reachable or blocked by a captcha/WAF challenge, so the Settings Health tab
 * reflects reality instead of the last search's outcome.
 *
 * Checks run on a background scope with short timeouts; a slow or challenged
 * source never blocks launch. Results live in [checks] and are re-runnable via
 * [refresh] (the Health tab's "Re-check" button).
 */
internal object SourceHealthChecker {

    enum class Status {
        /** Not part of the web check (e.g. Aurora has no public site). */
        Skipped,
        Checking,
        /** Reachable and served a real page. */
        Good,
        /** Reachable but served a captcha / WAF challenge. */
        CaptchaBlocked,
        /** Could not be reached (DNS/connect/timeout/HTTP error). */
        Unreachable
    }

    data class Check(
        val source: DownloadSource,
        val status: Status,
        val message: String? = null
    )

    private const val TAG = "SourceHealth"
    private const val CONNECT_TIMEOUT_S = 5L
    private const val READ_TIMEOUT_S = 8L

    private val UA = "Mozilla/5.0 (Linux; Android 14; Pixel 8) " +
        "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/125.0 Mobile Safari/537.36"

    private val client = OkHttpClient.Builder()
        .connectTimeout(CONNECT_TIMEOUT_S, TimeUnit.SECONDS)
        .readTimeout(READ_TIMEOUT_S, TimeUnit.SECONDS)
        .followRedirects(true)
        .build()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _checks = MutableStateFlow<List<Check>>(emptyList())
    val checks: StateFlow<List<Check>> = _checks

    private val _checking = MutableStateFlow(false)
    val checking: StateFlow<Boolean> = _checking

    /** Kick off a fresh check of every web source. Safe to call repeatedly. */
    fun refresh() {
        if (_checking.value) return
        _checking.value = true
        val sources = DownloadSource.entries
            .filter { it.searchDomain() != null }
            .map { Check(it, Status.Checking) }
        _checks.value = sources
        scope.launch {
            sources.forEach { check ->
                launch {
                    _checks.update { list ->
                        list.map { if (it.source == check.source) it.copy(status = Status.Checking) else it }
                    }
                    val result = ping(check.source)
                    Log.i(TAG, "${check.source.label}: ${result.status}" +
                        (result.message?.let { " — $it" } ?: ""))
                    _checks.update { list ->
                        list.map { if (it.source == check.source) result else it }
                    }
                }
            }
            _checking.value = false
        }
    }

    private fun ping(source: DownloadSource): Check {
        val domain = source.searchDomain() ?: return Check(source, Status.Skipped)
        val url = "https://$domain/"
        return try {
            val request = Request.Builder()
                .url(url)
                .header("User-Agent", UA)
                .get()
                .build()
            client.newCall(request).execute().use { response ->
                val code = response.code
                val body = response.body?.string().orEmpty()
                when {
                    isCaptchaBlocked(code, body) ->
                        Check(source, Status.CaptchaBlocked, "Blocked by a captcha challenge")
                    code in 200..299 -> Check(source, Status.Good)
                    code == 429 -> Check(source, Status.Unreachable, "Rate limited (HTTP 429)")
                    else -> Check(source, Status.Unreachable, "HTTP $code")
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "${source.label} health check failed: ${e.message}")
            Check(source, Status.Unreachable, "Unreachable")
        }
    }

    /**
     * True when the response is a captcha/WAF challenge rather than a real
     * page: challenge marker text in the body, or a 503 (Cloudflare origin
     * fetch / DDoS-Guard use 503 for challenges). A 403 alone is not treated
     * as a challenge — it can be a real permission denial.
     */
    private fun isCaptchaBlocked(code: Int, body: String): Boolean =
        bodyHasChallenge(body) || code == 503

    private fun bodyHasChallenge(body: String): Boolean {
        val lower = body.lowercase()
        return listOf(
            "just a moment",
            "cf-challenge",
            "challenge-platform",
            "attention required",
            "checking your browser",
            "ddos-guard",
            "enable javascript and cookies to continue"
        ).any { lower.contains(it) }
    }
}
