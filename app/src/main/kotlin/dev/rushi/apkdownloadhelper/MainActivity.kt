package dev.rushi.apkdownloadhelper

import android.Manifest
import android.app.Activity
import android.content.ClipData
import android.content.ContentValues
import android.content.Context
import android.widget.Toast
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.webkit.CookieManager
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.activity.ComponentActivity
import androidx.core.app.NotificationManagerCompat
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Extension
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.SortByAlpha
import androidx.compose.material.icons.outlined.ViewList
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.CleaningServices
import androidx.compose.material.icons.outlined.Dns
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Smartphone
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.ExpandLess
import androidx.compose.material.icons.outlined.BugReport
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material.icons.outlined.FolderOpen
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.OpenInBrowser
import androidx.compose.material.icons.outlined.NetworkCheck
import androidx.compose.material.icons.outlined.OpenInNew
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.SaveAlt
import androidx.compose.material.icons.outlined.SdStorage
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.SignalCellularAlt
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material.icons.outlined.RadioButtonChecked
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.core.graphics.drawable.toBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.aurora.gplayapi.data.models.App
import com.aurora.gplayapi.data.models.AuthData
import com.aurora.gplayapi.data.models.PlayFile
import com.aurora.gplayapi.helpers.AppDetailsHelper
import com.aurora.gplayapi.helpers.PurchaseHelper
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import dev.rushi.apkdownloadhelper.play.NativeDeviceInfoProvider
import dev.rushi.apkdownloadhelper.play.PlayHttpClient
import java.io.ByteArrayInputStream
import java.io.File
import java.io.SequenceInputStream
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.random.Random
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.nodes.Document
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

internal const val AURORA_AUTH_URL = "https://auroraoss.com/api/auth"
internal const val TAG = "ApkDownloadHelper"
internal val DOWNLOAD_FILE_KIND_ORDER = listOf("apk", "apkm", "apks", "xapk")
internal val APK_COMBO_FILE_KIND_ORDER = listOf("apk", "xapk", "apks")
internal val DOWNLOAD_FILE_KIND_SET = DOWNLOAD_FILE_KIND_ORDER.toSet()
internal val SPLIT_ARCHIVE_FILE_KINDS = setOf("apkm", "apks", "xapk")
internal val DOWNLOAD_FILE_KIND_REGEX = Regex("""apkm|apks|xapk|apk""", RegexOption.IGNORE_CASE)
internal val gson = Gson()

// Morphe Manager installs to test against: release first, debug as fallback.
private val MORPHE_MANAGER_PACKAGES = listOf(
    "app.morphe.manager",
    "app.morphe.manager.debug"
)

// Where to point the user when Morphe Manager is not installed at all.
private const val MORPHE_MANAGER_SITE_URL =
    "https://morphe.software/"

private val APK_PICKER_MIME_TYPES = arrayOf(
    "application/vnd.android.package-archive",
    "application/zip",
    "application/octet-stream",
    "*/*"
)

class MainActivity : ComponentActivity() {
    /** APKMirror declares `Crawl-delay: 3`; stay below ~1 request/2.5s. */
    companion object {
        const val APKMIRROR_REQUEST_GAP_MS = 2500L
    }

    private val browserUserAgent =
        "Mozilla/5.0 (Linux; Android ${Build.VERSION.RELEASE}; ${Build.MODEL}) AppleWebKit/537.36 " +
            "(KHTML, like Gecko) Chrome/121.0.0.0 Mobile Safari/537.36"
    private val client = OkHttpClient.Builder()
        .followRedirects(true)
        .followSslRedirects(true)
        .dns(AdGuardDns)
        .addInterceptor { chain ->
            chain.proceed(
                chain.request().newBuilder()
                    .header("User-Agent", browserUserAgent)
                    .build()
            )
        }
        .addInterceptor(httpLoggingInterceptor("Web"))
        .build()
    private val apkPureClient = OkHttpClient.Builder()
        .followRedirects(true)
        .followSslRedirects(true)
        .dns(AdGuardDns)
        .addInterceptor { chain ->
            chain.proceed(
                chain.request().newBuilder()
                    .header("User-Agent", "APKPure/3.19.39 (Aegon)")
                    .build()
            )
        }
        .addInterceptor(httpLoggingInterceptor("APKPure"))
        .build()

    private val apkPureApi = Retrofit.Builder()
        .client(apkPureClient)
        .baseUrl("https://tapi.pureapk.com/")
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
        .create(ApkPureApi::class.java)

    private val aptoideApi = Retrofit.Builder()
        .client(client)
        .baseUrl("https://ws75.aptoide.com/api/7/")
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
        .create(AptoideApi::class.java)

    private val parsers: Map<DownloadSource, ApkSourceParser> by lazy {
        val playHttpClient = PlayHttpClient(Cache(File(cacheDir, "play-cache"), 64L * 1024L * 1024L))
        val parserContext = SourceParserContext(
            fetcher = OkHttpSourceTextFetcher(
                client,
                // APKMirror declares `Crawl-delay: 3` and answers 429 to denser
                // probe runs, so give it a slower pace than the app-wide default.
                hostGapsMillis = mapOf("www.apkmirror.com" to APKMIRROR_REQUEST_GAP_MS)
            ),
            apkPureApi = apkPureApi,
            aptoideApi = aptoideApi,
            playHttpClient = playHttpClient,
            appContext = this
        )
        listOf(
            ApkMirrorParser(parserContext),
            UptodownParser(parserContext),
            ApkPureParser(parserContext),
            ApkComboParser(parserContext),
            AptoideParser(parserContext),
            EvoziParser(parserContext),
            Mi9Parser(),
            ApkDownloaderPagesParser(),
            AuroraParser(parserContext)
        ).associateBy { it.source }
    }

    private var request by mutableStateOf<HelperRequest?>(null)
    private var uiState by mutableStateOf<UiState>(UiState.Idle)
    private var helperSettings by mutableStateOf(HelperSettings())
    private var installedPackageRefreshToken by mutableIntStateOf(0)
    private val historyTimeFormat = SimpleDateFormat("MMM d, HH:mm", Locale.US)
    private var pendingDownload: PendingDownload? = null
    // Non-null while the in-app browser is open for a candidate whose source
    // needs a real browser to reach the APK download.
    private var captchaBrowser by mutableStateOf<DownloadCandidate?>(null)
    // Set when a repeat request matches previous downloads that still exist;
    // the user picks one to reuse or chooses to download a fresh copy.
    private var reuseOffer by mutableStateOf<List<ReuseOption>?>(null)
    private var selectedPagerPage by mutableIntStateOf(0)
    private var fastModeActive = false
    private var fastModeQueue: MutableList<DownloadSource>? = null
    private var fastModeDecision: CompletableDeferred<FastModeChoice?>? = null
    // ALWAYS_ASK: which version the user picked for the current request.
    private var fastModeVersionDecision: CompletableDeferred<FastModePolicy?>? = null
    // Effective policy for the current run (chosen interactively for ALWAYS_ASK).
    private var fastModeRunPolicy = FastModePolicy.REQUESTED

    /**
     * The effective disabled-source set.  If the user disables *every*
     * source, Play Store is forced back on so the app always has at least
     * one fallback source available.
     */
    private val effectiveDisabledSources: Set<DownloadSource>
        get() {
            val disabled = helperSettings.disabledSources
            return if (disabled.size >= DownloadSource.entries.size) {
                // All sources disabled – keep Play Store as the sole fallback.
                disabled - DownloadSource.PLAY
            } else {
                disabled
            }
        }
    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ -> startPendingDownload() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        restoreAndPersistRateLimiter()
        helperSettings = loadHelperSettings()
        request = HelperRequest.from(intent)
        startRequestLog(request)
        lifecycleScope.launch(Dispatchers.IO) {
            val freed = cleanupTemporaryDownloads(helperSettings)
            if (freed > 0L) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@MainActivity,
                        "Cleared ${freed.formatBytes()} of old cache",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        lifecycleScope.launch {
            DownloadJobManager.events.collect(::handleDownloadEvent)
        }
        if (deliverPendingResultIfPresent(request)) return
        if (request != null) offerExistingDownloadIfPresent(request!!)

        setContent {
            HelperTheme(
                themeMode = helperSettings.themeMode,
                dynamicColors = helperSettings.dynamicColors
            ) {
                // Ping every web source at launch so the Settings Health tab
                // reflects current reachability instead of the last resolve.
                LaunchedEffect(Unit) { SourceHealthChecker.refresh() }
                val captcha = captchaBrowser
                if (captcha != null) {
                    CaptchaBrowserScreen(
                        candidate = captcha,
                        onClose = ::closeCaptchaBrowser,
                        onDownloadCaptured = ::onBrowserDownloadCaptured
                    )
                } else {
                    HelperScreen(
                        request = request,
                        state = uiState,
                        settings = helperSettings,
                        virusTotalApiKey = if (helperSettings.virusTotalEnabled) {
                            helperSettings.virusTotalApiKey
                        } else {
                            ""
                        },
                        logs = AppLog.entries,
                        installedPackageRefreshToken = installedPackageRefreshToken,
                        selectedPagerPage = selectedPagerPage,
                        onPagerPageChanged = { selectedPagerPage = it },
                        onSettingsChange = ::updateHelperSettings,
                        onRefresh = ::loadCandidates,
                        onResolve = ::resolveCandidates,
                        onDownload = ::downloadAndReturn,
                        onPickDownloadedFile = ::returnPickedFile,
                        onUseInstalledApp = ::returnInstalledApp,
                        onVersionHistory = ::loadVersionHistory,
                        onDownloadVersion = ::downloadVersion,
                        onOpenHistoryEntry = ::openHistoryEntry,
                        onShareHistoryEntry = ::shareHistoryEntry,
                        onClearHistory = { DownloadHistoryStore.clear(applicationContext) },
                        onClearLogs = { AppLog.clear() },
                        onCancel = {
                            appendLog("Query canceled by user.", LogLevel.Warning)
                            setResult(Activity.RESULT_CANCELED)
                            finish()
                        },
                        onCancelDownload = ::cancelDownload,
                        onSkipScanWait = { VirusTotalScanner.rateLimiter.requestSkip() },
                        onCancelFastMode = ::cancelFastMode,
                        onUseFastModeMismatch = { fastModeChoose(FastModeChoice.USE) },
                        onSkipFastModeMismatch = { fastModeChoose(FastModeChoice.NEXT) },
                        onChooseVersion = ::fastModeChooseVersion,
                        onOpenMorphe = ::openMorpheManager,
                        onSolveCaptcha = ::openCaptchaBrowser,
                        onRequestFileTypeChange = ::changeRequestedFileType,
                        onProceedAfterScan = ::proceedAfterScan,
                        onCancelAfterScan = ::cancelAfterScan,
                        onSkipScan = ::skipScanAndHandoff
                    )
                }
                val offer = reuseOffer
                if (offer != null) {
                    ReuseOfferDialog(
                        options = offer,
                        onUseExisting = ::useReuseOffer,
                        // "Download new" (or dismissing) dismisses the offer and,
                        // when Fast Mode is on, starts it only now  never while
                        // the dialog is up, so it cannot download behind it.
                        onDownloadNew = {
                            reuseOffer = null
                            request?.let(::startFastModeIfEnabled)
                        }
                    )
                }
            }
        }

        val activeRequest = request
        if (activeRequest != null) {
            loadCandidates()
            if (reuseOffer == null) startFastModeIfEnabled(activeRequest)
        }
    }

    override fun onResume() {
        super.onResume()
        installedPackageRefreshToken++
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        request = HelperRequest.from(intent)
        startRequestLog(request)
        val activeRequest = request
        if (activeRequest != null) {
            if (!deliverPendingResultIfPresent(activeRequest)) {
                offerExistingDownloadIfPresent(activeRequest)
                loadCandidates()
                if (reuseOffer == null) startFastModeIfEnabled(activeRequest)
            }
        } else {
            uiState = UiState.Idle
        }
        installedPackageRefreshToken++
    }

    private fun loadCandidates() {
        val activeRequest = request ?: return
        uiState = UiState.Ready(initialCandidateResult(activeRequest))
        appendLog(
            "Ready. Manual links prepared for " +
                "${DownloadSource.entries.count { it !in effectiveDisabledSources }} sources."
        )
    }

    private fun resolveCandidates(source: DownloadSource, option: CandidateOption) {
        val activeRequest = request ?: return
        helperSettings.networkPolicy.blockReason(this)?.let { message ->
            appendLog(message, LogLevel.Warning)
            updateResolveState(source, option, ResolveState.Error(message))
            return
        }
        appendLog("Checking ${option.labelForLogs} from ${source.label}.")
        updateResolveState(source, option, ResolveState.Loading)
        lifecycleScope.launch {
            val resolved = withContext(Dispatchers.IO) {
                resolveSourceSection(activeRequest, source, option)
            }
            logResolveOutcome(source, option, resolved)

            updateResolveState(
                source = source,
                option = option,
                state = resolved.notFoundMessage
                    ?.takeIf { resolved.candidates.isEmpty() }
                    ?.let { ResolveState.Done(emptyList()) }
                    ?: resolved.errorMessage
                        ?.takeIf { resolved.candidates.isEmpty() }
                        ?.let { message ->
                            ResolveState.Error(
                                message = message,
                                fallbackCandidate = resolved.fallbackCandidate
                            )
                        }
                        ?: ResolveState.Done(resolved.candidates)
            )
        }
    }

    private fun updateResolveState(
        source: DownloadSource,
        option: CandidateOption,
        state: ResolveState
    ) {
        val activeRequest = request ?: return
        val current = (uiState as? UiState.Ready)?.result ?: initialCandidateResult(activeRequest)
        uiState = UiState.Ready(current.withResolveState(source, option, state))
    }

    private fun loadVersionHistory(source: DownloadSource) {
        val activeRequest = request ?: return
        helperSettings.networkPolicy.blockReason(this)?.let { message ->
            appendLog(message, LogLevel.Warning)
            updateHistoryState(source, VersionHistoryState.Error(message))
            return
        }
        updateHistoryState(source, VersionHistoryState.Loading)
        appendLog("Loading ${source.label} version history.")
        lifecycleScope.launch {
            val result = withContext(Dispatchers.IO) {
                runCatching { resolveVersionHistory(activeRequest, source) }
            }
            result
                .onSuccess { candidates ->
                    appendLog("${source.label} version history loaded: ${candidates.size} versions.")
                    updateHistoryState(source, VersionHistoryState.Done(candidates))
                }
                .onFailure { error ->
                    val message = sourceFailureMessage(source, error)
                    appendLog(message, LogLevel.Error)
                    updateHistoryState(source, VersionHistoryState.Error(message))
                }
        }
    }

    private fun updateHistoryState(source: DownloadSource, state: VersionHistoryState) {
        val activeRequest = request ?: return
        val current = (uiState as? UiState.Ready)?.result ?: initialCandidateResult(activeRequest)
        uiState = UiState.Ready(current.withHistoryState(source, state))
    }

    private fun downloadVersion(candidate: DownloadCandidate) {
        val activeRequest = request ?: return
        // Capture the current result before Loading replaces it, so a failed
        // resolution can restore the history list with this row flipped to
        // "Open link" instead of wiping back to a fresh screen.
        val currentResult = (uiState as? UiState.Ready)?.result
            ?: initialCandidateResult(activeRequest)
        appendLog("Resolving ${candidate.versionDisplay} from ${candidate.source.label} for download...")
        uiState = UiState.Loading
        lifecycleScope.launch {
            val resolved = withContext(Dispatchers.IO) {
                runCatching { resolveHistoryCandidate(activeRequest, candidate) }
            }
            resolved
                .onSuccess { direct ->
                    if (direct == null) {
                        // No direct download exists for this version. Keep the
                        // history list usable by flipping this row to an
                        // "Open link" action instead of erroring the whole screen.
                        val message =
                            "No direct download was available for ${candidate.versionDisplay} " +
                                "on ${candidate.source.label}. Open the version page manually."
                        appendLog(message, LogLevel.Warning)
                        uiState = UiState.Ready(
                            currentResult.markHistoryCandidateNoDirectDownload(
                                source = candidate.source,
                                candidateKey = candidate.identityKey()
                            )
                        )
                    } else {
                        downloadAndReturn(direct)
                    }
                }
                .onFailure { error ->
                    val message = downloadFailureMessage(candidate, error)
                    appendLog(message, LogLevel.Error)
                    uiState = UiState.Error(message)
                }
        }
    }

    private suspend fun resolveVersionHistory(
        request: HelperRequest,
        source: DownloadSource
    ): List<DownloadCandidate> =
        parsers[source]?.resolveHistory(request).orEmpty()

    private suspend fun resolveHistoryCandidate(
        request: HelperRequest,
        candidate: DownloadCandidate
    ): DownloadCandidate? =
        parsers[candidate.source]?.resolveHistoryCandidate(request, candidate)

    private fun startRequestLog(request: HelperRequest?) {
        AppLog.clear()
        if (request == null) {
            AppLog.setRequestSummary(null)
            appendLog("Opened without a Morphe request.", LogLevel.Warning)
        } else {
            AppLog.setRequestSummary(
                buildString {
                    append("App: ${request.appName}\n")
                    append("Package: ${request.packageName}\n")
                    append("Version: ${request.requestedVersionName ?: "any compatible"}\n")
                    append("Build: ${request.versionCodeSummary ?: "any"}\n")
                    append("Format: ${request.requestedFormatLabel.ifBlank { "any" }}\n")
                    if (request.availableAbis.isNotEmpty()) {
                        append("ABI: ${request.abiSummary}\n")
                    }
                    request.sourceHintUrls.takeIf { it.isNotEmpty() }?.let { urls ->
                        append("Source hints: ${urls.joinToString()}\n")
                    }
                }.trimEnd()
            )
            appendLog(
                "Request for ${request.appName} (${request.packageName}), " +
                    "version ${request.versionName ?: "any compatible"}, " +
                    "build ${request.versionCodeSummary ?: "any"}, format ${request.requestedFormatLabel}."
            )
            logQueryIntentExtras()
        }
    }

    private fun logQueryIntentExtras() {
        val extras = intent.extras ?: return
        val dump = extras.keySet().sorted().joinToString(", ") { key ->
            val value = when (val raw = extras.get(key)) {
                null -> "null"
                is Array<*> -> raw.joinToString("|")
                is LongArray -> raw.joinToString("|")
                is IntArray -> raw.joinToString("|")
                else -> raw.toString()
            }
            "$key=$value"
        }
        if (logcatLoggingEnabled) Log.i(TAG, "Query intent extras: $dump")
    }

    private fun logResolveOutcome(
        source: DownloadSource,
        option: CandidateOption,
        outcome: ResolveOutcome
    ) {
        when {
            outcome.notFoundMessage != null -> {
                appendLog("${source.label} ${option.labelForLogs}: ${outcome.notFoundMessage}.", LogLevel.Warning)
            }
            outcome.errorMessage != null && outcome.candidates.isEmpty() -> {
                appendLog("${source.label} ${option.labelForLogs} failed: ${outcome.errorMessage}", LogLevel.Error)
            }
            outcome.candidates.isEmpty() -> {
                appendLog("${source.label} ${option.labelForLogs} found no candidates.", LogLevel.Warning)
            }
            else -> {
                val summary = outcome.candidates.joinToString(limit = 3, truncated = "…") { candidate ->
                    candidate.versionDisplay
                }
                appendLog("${source.label} ${option.labelForLogs} found ${outcome.candidates.size}: $summary")
            }
        }
    }

    private fun appendLog(message: String, level: LogLevel = LogLevel.Info) {
        // Single shared sink: the Logs tab and the HTTP interceptor both write
        // here, and Logcat mirroring is handled by the sink per the setting.
        AppLog.record(level, message)
    }

    private fun changeRequestedFileType(kind: String) {
        val active = request ?: return
        if (active.requestedFileType?.equals(kind, ignoreCase = true) == true) return
        // Pin the request to a single format so resolution only looks for it.
        request = active.copy(
            requestedFileType = kind,
            allowSplitArchive = false
        )
        appendLog("Request format narrowed to ${kind.uppercase(Locale.US)}.", LogLevel.Info)
        loadCandidates()
    }

    private fun updateHelperSettings(settings: HelperSettings) {
        val sourcesChanged = helperSettings.disabledSources != settings.disabledSources
        helperSettings = settings
        saveHelperSettings(settings)
        // Rebuild the picker so enabled/disabled sources take effect immediately.
        if (sourcesChanged && request != null && uiState is UiState.Ready) {
            uiState = UiState.Ready(initialCandidateResult(request!!))
        }
        lifecycleScope.launch(Dispatchers.IO) {
            cleanupTemporaryDownloads(settings)
        }
    }

    private fun initialCandidateResult(request: HelperRequest): CandidateResult {
        val manual = manualCandidates(request)
        val enabled = DownloadSource.entries
            .filter { it !in effectiveDisabledSources }
        // Put the user's preferred source first so the picker's pager opens on
        // it (page 0) instead of always the first enabled source. Falls back to
        // the default order when no preference is set or it's disabled.
        val ordered = helperSettings.preferredSource
            ?.takeIf { it in enabled }
            ?.let { preferred -> listOf(preferred) + enabled.filterNot { it == preferred } }
            ?: enabled
        return CandidateResult(
            sourceGroups = ordered.map { source ->
                SourceCandidateGroup(
                    source = source,
                    manual = manual.filter { it.source == source },
                    recommended = ResolveState.Idle,
                    latest = ResolveState.Idle
                )
            }
        )
    }

    private suspend fun resolveSourceSection(
        request: HelperRequest,
        source: DownloadSource,
        option: CandidateOption
    ): ResolveOutcome {
        val lookup = runCatching { findSourceCandidates(request, source, option) }
            .onFailure { error ->
                if (error !is SourceAppNotFoundException) {
                    Log.w(TAG, "${source.label} ${option.name.lowercase(Locale.US)} lookup failed", error)
                }
            }
        lookup.exceptionOrNull()
            ?.takeIf { it is SourceAppNotFoundException }
            ?.let {
                return ResolveOutcome(
                    candidates = emptyList(),
                    notFoundMessage = "no listing for ${request.packageName}"
                )
            }
        lookup.exceptionOrNull()?.let { error ->
            return ResolveOutcome(
                candidates = emptyList(),
                errorMessage = sourceFailureMessage(source, error),
                fallbackCandidate = sourceErrorFallbackCandidate(request, source, option)
            )
        }
        val sourceCandidates = lookup
            .getOrDefault(emptyList())
            .distinctBy(DownloadCandidate::identityKey)

        val candidates = when (option) {
            CandidateOption.REQUESTED -> recommendedCandidatesForSource(request, source, sourceCandidates)
            CandidateOption.LATEST -> latestCandidatesForSource(request, source, sourceCandidates)
            CandidateOption.MANUAL -> emptyList()
        }

        return ResolveOutcome(
            candidates = candidates,
            errorMessage = lookup.exceptionOrNull()?.let { sourceFailureMessage(source, it) }
        )
    }

    private suspend fun findSourceCandidates(
        request: HelperRequest,
        source: DownloadSource,
        option: CandidateOption
    ): List<DownloadCandidate> =
        parsers[source]?.findCandidates(request, option).orEmpty()

    private fun recommendedCandidatesForSource(
        request: HelperRequest,
        source: DownloadSource,
        candidates: List<DownloadCandidate>
    ): List<DownloadCandidate> {
        if (!request.hasRequestedVersionRequest || source == DownloadSource.AURORA || source == DownloadSource.PLAY) {
            return emptyList()
        }

        return buildList {
            addAll(candidates.filter(request::isRequestedMatch))
            if (none { it.option == CandidateOption.REQUESTED }) {
                parsers[source]?.requestedFallbackCandidate(request)?.let(::add)
            }
        }
            .distinctBy(DownloadCandidate::identityKey)
            .sortedBy { it.sortIndex }
    }

    private fun latestCandidatesForSource(
        request: HelperRequest,
        source: DownloadSource,
        candidates: List<DownloadCandidate>
    ): List<DownloadCandidate> {
        if (source == DownloadSource.PLAY) {
            return listOf(playStoreCandidate(request))
        }

        return buildList {
            addAll(candidates.filter { it.option == CandidateOption.LATEST })
            if (none { it.option == CandidateOption.LATEST }) {
                parsers[source]?.latestFallbackCandidate(request)?.let(::add)
            }
            if (none { it.option == CandidateOption.LATEST }) {
                latestWebFallback(request, source)?.let(::add)
            }
        }
            .distinctBy(DownloadCandidate::identityKey)
            .sortedBy { it.sortIndex }
    }

    private fun sourceErrorFallbackCandidate(
        request: HelperRequest,
        source: DownloadSource,
        option: CandidateOption
    ): DownloadCandidate? {
        if (option == CandidateOption.MANUAL || source == DownloadSource.AURORA) return null

        val manual = manualCandidates(request).firstOrNull { it.source == source } ?: return null
        val requestedVersionName = request.requestedVersionNames.firstOrNull()
        val requestedVersionCode = request.versionCode ?: request.versionCodes.singleOrNull()
        val url = when (option) {
            CandidateOption.REQUESTED -> request.sourceHintUrlsFor(source).firstOrNull()
                ?: sourceVersionSearchUrl(request, source)
                ?: manual.url
            CandidateOption.LATEST -> manual.url
            CandidateOption.MANUAL -> manual.url
        }

        return manual.copy(
            versionName = requestedVersionName.takeIf { option == CandidateOption.REQUESTED },
            versionCode = requestedVersionCode.takeIf { option == CandidateOption.REQUESTED },
            url = url,
            fileKind = "web",
            option = option,
            directDownload = false,
            versionStatus = if (option == CandidateOption.REQUESTED) VersionStatus.REQUESTED else VersionStatus.LATEST,
            formatMatches = true,
            note = null,
            variantLabel = null,
            files = emptyList()
        )
    }

    private fun sourceVersionSearchUrl(request: HelperRequest, source: DownloadSource): String? {
        val domain = source.searchDomain() ?: return null
        val version = request.requestedVersionLabel.takeIf { it != "any compatible version" } ?: return null
        val query = buildList {
            add(request.packageName)
            add("\"$version\"")
            request.requestedFileKinds
                .orderedFileKinds()
                .firstOrNull()
                ?.let(::add)
            add("site:$domain")
        }.joinToString(" ")
        return "https://www.google.com/search?q=${URLEncoder.encode(query, "UTF-8")}"
    }

    private fun manualCandidates(request: HelperRequest): List<DownloadCandidate> =
        manualSourceUrls(request).map { (source, url) ->
            DownloadCandidate(
                source = source,
                name = request.appName,
                packageName = request.packageName,
                versionName = null,
                versionCode = null,
                url = url,
                fileKind = "web",
                option = CandidateOption.MANUAL,
                directDownload = false,
                versionStatus = VersionStatus.LATEST,
                formatMatches = true
            )
        }.sortedBy { it.sortIndex }

    private fun latestWebFallback(request: HelperRequest, source: DownloadSource): DownloadCandidate? =
        manualSourceUrls(request)
            .firstOrNull { (candidateSource, _) -> candidateSource == source }
            ?.let { (_, url) ->
                DownloadCandidate(
                    source = source,
                    name = request.appName,
                    packageName = request.packageName,
                    versionName = null,
                    versionCode = null,
                    url = url,
                    fileKind = "web",
                    option = CandidateOption.LATEST,
                    directDownload = false,
                    versionStatus = VersionStatus.LATEST,
                    formatMatches = true
                )
            }

    private fun manualSourceUrls(request: HelperRequest): List<Pair<DownloadSource, String>> =
        parsers.values
            .filter { it.source !in effectiveDisabledSources }
            .mapNotNull { parser ->
                parser.searchUrl(request.packageName)?.let { parser.source to it }
            }
            .sortedBy { it.first.ordinal }

    private fun playStoreCandidate(request: HelperRequest) = DownloadCandidate(
        source = DownloadSource.PLAY,
        name = request.appName,
        packageName = request.packageName,
        versionName = null,
        versionCode = null,
        url = playStoreUrl(request.packageName),
        fileKind = "web",
        option = CandidateOption.LATEST,
        directDownload = false,
        versionStatus = VersionStatus.LATEST,
        formatMatches = true
    )

    private fun downloadAndReturn(candidate: DownloadCandidate) {
        val activeRequest = request ?: return
        val settings = helperSettings
        settings.networkPolicy.blockReason(this)?.let { message ->
            appendLog(message, LogLevel.Warning)
            uiState = UiState.Error(message)
            return
        }
        appendLog(
            "Downloading ${candidate.source.label} ${candidate.option.labelForLogs} " +
                "${candidate.versionDisplay} (${candidate.fileKind.uppercase(Locale.US)})."
        )
        uiState = if (fastModeActive) {
            UiState.FastMode(
                FastModeProgress(
                    sourceLabel = candidate.source.label,
                    detail = "Downloading from ${candidate.source.label}…",
                    percent = 0
                )
            )
        } else {
            UiState.Downloading(candidate, 0)
        }
        pendingDownload = PendingDownload(activeRequest, candidate, settings)
        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            startPendingDownload()
        }
    }

    private fun startPendingDownload() {
        val pending = pendingDownload ?: return
        pendingDownload = null
        DownloadJobManager.start(
            DownloadJobManager.DownloadJob(
                request = pending.request,
                candidate = pending.candidate,
                settings = pending.settings,
                requestIntentExtras = intent.getExtras(),
                fastMode = fastModeActive
            )
        )
        startForegroundService(
            Intent(this, DownloadService::class.java).setAction(ACTION_START_DOWNLOAD)
        )
    }

    private fun cancelDownload() {
        appendLog("Cancelling download…", LogLevel.Warning)
        runCatching {
            startService(
                Intent(this, DownloadService::class.java).setAction(ACTION_CANCEL_DOWNLOAD)
            )
        }.onFailure {
            appendLog("Could not cancel the download.", LogLevel.Error)
        }
    }

    // ---- In-app captcha browser: let the user solve a captcha in a real
    // browser and capture the download URL it produces ----

    private fun openCaptchaBrowser(candidate: DownloadCandidate) {
        if (candidate.source == DownloadSource.AURORA || candidate.source == DownloadSource.PLAY) return
        appendLog(
            "Opening in-app browser for ${candidate.source.label} to solve the captcha.",
            LogLevel.Info
        )
        captchaBrowser = candidate
    }

    private fun closeCaptchaBrowser() {
        captchaBrowser = null
    }

    private fun onBrowserDownloadCaptured(capture: BrowserDownloadCapture) {
        val candidate = captchaBrowser ?: return
        captchaBrowser = null
        val fileKind = fileKindFromUrl(capture.downloadUrl)
        val referer = capture.refererUrl
            ?.takeIf(String::isNotBlank)
            ?: candidate.captchaUrl
            ?: candidate.url
        val cookieHeader = capture.cookieHeader
            ?: CookieManager.getInstance().getCookie(referer)
        appendLog(
            "Captured download link from ${candidate.source.label} in the in-app browser " +
                "($fileKind): ${capture.downloadUrl}",
            LogLevel.Info
        )
        downloadAndReturn(
            candidate.copy(
                url = capture.downloadUrl,
                fileKind = fileKind,
                directDownload = true,
                note = null,
                files = listOf(
                    CandidateDownloadFile(
                        url = capture.downloadUrl,
                        fileName = capturedDownloadFileName(candidate, capture.downloadUrl, fileKind),
                        referer = referer,
                        cookieHeader = cookieHeader
                    )
                )
            )
        )
    }

    private fun openMorpheManager() {
        // Prefer the release build of Morphe Manager, then the debug build.
        val launchIntent = MORPHE_MANAGER_PACKAGES
            .asSequence()
            .mapNotNull { packageName -> packageManager.getLaunchIntentForPackage(packageName) }
            .firstOrNull()
        if (launchIntent != null) {
            appendLog("Opening Morphe Manager (${launchIntent.component?.packageName ?: "Morphe Manager"}).")
            runCatching {
                startActivity(launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            }.onFailure {
                appendLog("Could not open Morphe Manager.", LogLevel.Error)
            }
        } else {
            appendLog("Morphe Manager is not installed  opening morphe.software.", LogLevel.Warning)
            val releases = Intent(
                Intent.ACTION_VIEW,
                Uri.parse(MORPHE_MANAGER_SITE_URL)
            ).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
            runCatching { startActivity(releases) }
                .onFailure {
                    appendLog("Could not open Morphe Manager releases page.", LogLevel.Error)
                }
        }
    }

    // ---- Fast Mode: auto-find a version and return it ----

    private fun startFastModeIfEnabled(request: HelperRequest) {
        if (!helperSettings.fastMode) return
        if (fastModeActive) return
        // REQUESTED mode needs a concrete version to match against; LATEST and
        // ALWAYS_ASK can resolve the newest version even with no request fields.
        val policy = helperSettings.fastModePolicy
        if (policy == FastModePolicy.REQUESTED && !request.hasRequestedVersionRequest) return
        fastModeActive = true
        fastModeRunPolicy = if (policy == FastModePolicy.ALWAYS_ASK) FastModePolicy.REQUESTED else policy
        // Walk whatever the user has enabled globally; sources that cannot
        // deliver a direct APK (Play/Aurora links, captcha-gated services)
        // simply resolve to nothing and the walk moves on.
        fastModeQueue = DownloadSource.entries
            .filter { it !in effectiveDisabledSources }
            .toMutableList()
        if (policy == FastModePolicy.ALWAYS_ASK) {
            appendLog("Fast Mode: asking which version to fetch.", LogLevel.Info)
            val gate = CompletableDeferred<FastModePolicy?>()
            fastModeVersionDecision = gate
            uiState = UiState.FastMode(
                FastModeProgress(
                    detail = "Auto-searching sources…",
                    awaitingVersionChoice = true,
                    versionChoiceRequested = request.requestedVersionLabel,
                    versionChoiceDetail =
                    "Retrieve the requested version or fetch the latest available version?"
                )
            )
            // Ask first, resolve later: the run stays parked on the gate and
            // only starts searching sources after the user picks, so the
            // prompt appears instantly instead of waiting on a background scan.
            lifecycleScope.launch {
                val chosen = gate.await()
                fastModeVersionDecision = null
                if (!fastModeActive || chosen == null) return@launch
                fastModeRunPolicy = chosen
                appendLog("Fast Mode: resolving ${chosen.name.lowercase(Locale.US)} version.", LogLevel.Info)
                uiState = UiState.FastMode(FastModeProgress(detail = "Auto-searching sources…"))
                fastModeRun(request, chosen)
            }
        } else {
            appendLog(
                "Fast Mode: auto-searching sources for the ${policy.name.lowercase(Locale.US)} version.",
                LogLevel.Info
            )
            uiState = UiState.FastMode(FastModeProgress(detail = "Auto-searching sources…"))
            lifecycleScope.launch {
                fastModeRun(request, policy)
            }
        }
    }

    private fun fastModeChooseVersion(policy: FastModePolicy) {
        fastModeVersionDecision?.complete(policy)
    }

    private suspend fun fastModeRun(request: HelperRequest, policy: FastModePolicy) {
        if (policy == FastModePolicy.LATEST) {
            fastModeNextLatest(request)
        } else {
            fastModeNext(request)
        }
    }

    private fun cancelFastMode() {
        appendLog("Fast Mode cancelled  taking over manually.", LogLevel.Warning)
        fastModeActive = false
        fastModeQueue = null
        // Unblock a pending "use this version?" question so the loop exits.
        fastModeDecision?.complete(null)
        fastModeDecision = null
        // If a fast-mode download is in flight, stop it too; its Cancelled
        // event also restores the source list.
        if (DownloadJobManager.activeJob != null) {
            cancelDownload()
        }
        val activeRequest = request
        uiState = if (activeRequest != null) {
            UiState.Ready(initialCandidateResult(activeRequest))
        } else {
            UiState.Idle
        }
    }

    private fun fastModeChoose(choice: FastModeChoice) {
        fastModeDecision?.complete(choice)
    }

    private fun proceedAfterScan() {
        startService(
            Intent(this, DownloadService::class.java).setAction(ACTION_SCAN_PROCEED)
        )
        uiState = UiState.Loading
    }

    private fun cancelAfterScan() {
        startService(
            Intent(this, DownloadService::class.java).setAction(ACTION_SCAN_CANCEL)
        )
        uiState = UiState.Loading
    }

    private fun skipScanAndHandoff() {
        appendLog("User skipped the VirusTotal scan — handing off unverified.", LogLevel.Warning)
        startService(
            Intent(this, DownloadService::class.java).setAction(ACTION_SCAN_SKIP)
        )
        uiState = UiState.Loading
    }

    private suspend fun fastModeNext(request: HelperRequest) {
        val queue = fastModeQueue ?: return
        while (queue.isNotEmpty()) {
            // The user may have cancelled while a source was resolving.
            if (!fastModeActive) return
            val source = queue.removeAt(0)
            appendLog("Fast Mode: checking ${source.label} for ${request.requestedVersionLabel}...")
            uiState = UiState.FastMode(
                FastModeProgress(sourceLabel = source.label, detail = "Checking ${source.label}…")
            )
            val result = withContext(Dispatchers.IO) {
                runCatching { fastModeFindCandidate(request, source) }
                    .getOrDefault(FastModeFindResult.None)
            }
            if (!fastModeActive) return
            when (result) {
                is FastModeFindResult.Exact -> {
                    val candidate = result.candidate
                    appendLog(
                        "Fast Mode: found ${candidate.versionDisplay} on ${source.label} " +
                            "(${candidate.fileKind.uppercase(Locale.US)})  downloading.",
                        LogLevel.Info
                    )
                    uiState = UiState.FastMode(
                        FastModeProgress(
                            sourceLabel = source.label,
                            detail = "Found ${candidate.versionDisplay}  downloading…",
                            percent = 0
                        )
                    )
                    downloadAndReturn(candidate)
                    return
                }
                is FastModeFindResult.VersionMismatch -> {
                    val candidate = result.candidate
                    appendLog(
                        "Fast Mode: ${source.label} has ${candidate.versionDisplay}  " +
                            "build differs from requested ${request.versionCodeSummary ?: "version"}. Asking the user.",
                        LogLevel.Warning
                    )
                    val decisionGate = CompletableDeferred<FastModeChoice?>()
                    fastModeDecision = decisionGate
                    uiState = UiState.FastMode(
                        FastModeProgress(
                            sourceLabel = source.label,
                            detail = "Version code mismatch",
                            awaitingDecision = true,
                            mismatchDetail = "Requested build ${request.versionCodeSummary ?: "?"}  " +
                                "${source.label} has ${candidate.versionDisplay}."
                        )
                    )
                    val decision = decisionGate.await()
                    if (!fastModeActive || decision == null) return
                    if (decision == FastModeChoice.USE) {
                        appendLog(
                            "Fast Mode: using ${candidate.versionDisplay} from ${source.label} " +
                                "despite the code mismatch.",
                            LogLevel.Info
                        )
                        uiState = UiState.FastMode(
                            FastModeProgress(
                                sourceLabel = source.label,
                                detail = "Found ${candidate.versionDisplay}  downloading…",
                                percent = 0
                            )
                        )
                        downloadAndReturn(candidate)
                        return
                    }
                    appendLog("Fast Mode: user skipped ${source.label}  trying the next source.", LogLevel.Info)
                    continue
                }
                FastModeFindResult.None -> {
                    appendLog("Fast Mode: no exact match on ${source.label}.", LogLevel.Info)
                }
            }
        }
        fastModeActive = false
        fastModeQueue = null
        appendLog("Fast Mode: no source had the exact version. Use the sources below.", LogLevel.Warning)
        val activeRequest = request
        uiState = if (activeRequest != null) {
            UiState.FastMode(
                FastModeProgress(
                    detail = "No source had the exact version ${request.requestedVersionLabel}.",
                    done = true,
                    succeeded = false,
                    result = initialCandidateResult(activeRequest)
                )
            )
        } else {
            UiState.Idle
        }
    }

    private suspend fun fastModeNextLatest(request: HelperRequest) {
        val queue = fastModeQueue ?: return
        while (queue.isNotEmpty()) {
            if (!fastModeActive) return
            val source = queue.removeAt(0)
            appendLog("Fast Mode: fetching latest from ${source.label}...", LogLevel.Info)
            uiState = UiState.FastMode(
                FastModeProgress(sourceLabel = source.label, detail = "Checking ${source.label}…")
            )
            val candidates = withContext(Dispatchers.IO) {
                runCatching {
                    resolveSourceSection(request, source, CandidateOption.LATEST).candidates
                }.getOrDefault(emptyList())
            }
            if (!fastModeActive) return
            val best = fastModeLatestCandidate(request, candidates)
            if (best != null) {
                appendLog(
                    "Fast Mode: latest ${best.versionDisplay} on ${source.label} " +
                        "(${best.fileKind.uppercase(Locale.US)})  downloading.",
                    LogLevel.Info
                )
                uiState = UiState.FastMode(
                    FastModeProgress(
                        sourceLabel = source.label,
                        detail = "Found latest ${best.versionDisplay}  downloading…",
                        percent = 0
                    )
                )
                downloadAndReturn(best)
                return
            }
            appendLog("Fast Mode: no newer latest on ${source.label}.", LogLevel.Info)
        }
        fastModeActive = false
        fastModeQueue = null
        appendLog("Fast Mode: no source offered a newer version than requested. Use the sources below.", LogLevel.Warning)
        val activeRequest = request
        uiState = if (activeRequest != null) {
            UiState.FastMode(
                FastModeProgress(
                    detail = "No source offered a newer version than ${request.requestedVersionLabel}.",
                    done = true,
                    succeeded = false,
                    result = initialCandidateResult(activeRequest)
                )
            )
        } else {
            UiState.Idle
        }
    }

    /**
     * The newest direct-download candidate a source resolved for [option] LATEST.
     * Keeps the version strictly newer than the request when a requested version
     * name is known (so "latest" is only reported when it actually is newer); with
     * no requested version, returns the newest candidate regardless.
     */
    private fun fastModeLatestCandidate(
        request: HelperRequest,
        candidates: List<DownloadCandidate>
    ): DownloadCandidate? {
        val requested = request.requestedVersionName
        val newer = candidates
            .filter { it.directDownload && it.versionName != null }
            .filter { candidate ->
                requested == null || compareVersionNames(candidate.versionName, requested) > 0
            }
        return newer.maxWithOrNull(
            Comparator<DownloadCandidate> { a, b ->
                val byVersion = compareVersionNames(b.versionName, a.versionName)
                if (byVersion != 0) byVersion else a.sortIndex.compareTo(b.sortIndex)
            }
        )
    }

    private suspend fun fastModeFindCandidate(
        request: HelperRequest,
        source: DownloadSource
    ): FastModeFindResult {
        val outcome = resolveSourceSection(request, source, CandidateOption.REQUESTED)
        val exact = outcome.candidates.firstOrNull { candidate ->
            // Format differences are fine, but the exact version name AND
            // version code are both required. A candidate that reports a code
            // outside the request is surfaced as a mismatch the user can
            // accept or skip  never silently downloaded.
            candidate.directDownload &&
                request.matchesRequestedVersionStrict(candidate.versionName, candidate.versionCode)
        }
        if (exact != null) return FastModeFindResult.Exact(exact)

        // The version name exists on this source but with a different build 
        // hand it to the loop so it can ask the user before proceeding.
        val mismatch = outcome.candidates.firstOrNull { candidate ->
            candidate.directDownload &&
                request.requestedVersionName != null &&
                candidate.versionName != null &&
                candidate.versionName.versionNameEquals(request.requestedVersionName) &&
                candidate.versionCode != null &&
                !request.matchesRequestedVersionStrict(candidate.versionName, candidate.versionCode)
        }
        return if (mismatch != null) {
            FastModeFindResult.VersionMismatch(mismatch)
        } else {
            FastModeFindResult.None
        }
    }

    private fun handleDownloadEvent(event: DownloadJobManager.Event?) {
        when (event) {
            null -> Unit
            is DownloadJobManager.Event.Progress -> {
                uiState = if (fastModeActive) {
                    UiState.FastMode(
                        FastModeProgress(
                            sourceLabel = event.candidate.source.label,
                            detail = "Downloading from ${event.candidate.source.label}…",
                            percent = event.percent,
                            speedBytesPerSec = event.speedBytesPerSec,
                            etaMs = event.etaMs
                        )
                    )
                } else {
                    UiState.Downloading(
                        event.candidate,
                        event.percent,
                        event.speedBytesPerSec,
                        event.etaMs
                    )
                }
            }
            is DownloadJobManager.Event.PostDownloadStatus -> {
                uiState = if (fastModeActive) {
                    UiState.FastMode(
                        FastModeProgress(
                            sourceLabel = event.candidate.source.label,
                            detail = event.status,
                            percent = 100
                        )
                    )
                } else {
                    UiState.Downloading(
                        event.candidate,
                        percent = 100,
                        statusMessage = event.status
                    )
                }
            }
            is DownloadJobManager.Event.Scanning -> {
                val pct = event.percent ?: 100
                uiState = if (fastModeActive) {
                    UiState.FastMode(
                        FastModeProgress(
                            sourceLabel = event.candidate.source.label,
                            detail = event.status,
                            percent = pct,
                            etaMs = event.etaMs,
                            shaVerified = event.shaVerified
                        )
                    )
                } else {
                    UiState.Downloading(
                        event.candidate,
                        percent = pct,
                        etaMs = event.etaMs,
                        statusMessage = event.status
                    )
                }
            }
            is DownloadJobManager.Event.ScanAsk -> {
                uiState = UiState.ScanAsk(event.candidate)
            }
            is DownloadJobManager.Event.ScanComplete -> {
                val scanResult = event.result
                val isMalicious = scanResult is VirusTotalScanner.ScanResult.Malicious
                val detail = scanResultDetail(scanResult)
                uiState = UiState.ScanResult(
                    candidate = event.candidate,
                    scanResult = scanResult,
                    detail = detail,
                    isMalicious = isMalicious,
                    shaVerified = event.shaVerified
                )
            }
            is DownloadJobManager.Event.Completed -> {
                // StateFlow replays its last value to every new collector, so a
                // freshly created activity can receive the Completed event of a
                // previous request session. Only act when the event comes from
                // the session currently in flight (same package, same epoch);
                // otherwise discard it so the old file is never handed to a
                // different request. The version is intentionally NOT checked
                // here  the user may have deliberately downloaded a different
                // version (e.g. the Latest tab) in this session, and that file
                // must still be returned instead of leaving the UI stuck at
                // 100%.
                if (event.result.belongsToCurrentSession(request, event.epoch)) {
                    appendLog("Download validated: ${event.result.fileName}.")
                    if (!deliverResult(event.result)) {
                        appendLog(
                            "Download is ready; ${event.result.callerPackage} can request it again to receive the file.",
                            LogLevel.Warning
                        )
                        // Opened standalone with no caller to return to  keep the
                        // Fast Mode card showing the result and the source list
                        // reachable beneath it.
                        val wasFastMode = fastModeActive
                        fastModeActive = false
                        fastModeQueue = null
                        val activeRequest = request
                        uiState = if (wasFastMode) {
                            UiState.FastMode(
                                FastModeProgress(
                                    sourceLabel = event.result.sourceName,
                                    detail = "Download ready: ${event.result.fileName}. " +
                                        "Request it again from Morphe to receive it.",
                                    done = true,
                                    succeeded = true,
                                    result = activeRequest?.let { initialCandidateResult(it) }
                                )
                            )
                        } else if (activeRequest != null) {
                            UiState.Ready(initialCandidateResult(activeRequest))
                        } else {
                            UiState.Idle
                        }
                    }
                } else {
                    appendLog(
                        "Ignoring download completion for a different request session " +
                            "(epoch ${event.epoch}, current ${DownloadJobManager.currentEpoch}).",
                        LogLevel.Warning
                    )
                    // The stuck-at-100% symptom is gone once a terminal event
                    // always lands somewhere  but if the UI is still showing a
                    // stale in-flight download, reset it so the user is never
                    // left frozen on a progress bar.
                    if (uiState is UiState.Downloading) {
                        val activeRequest = request
                        uiState = if (activeRequest != null) {
                            UiState.Ready(initialCandidateResult(activeRequest))
                        } else {
                            UiState.Idle
                        }
                    }
                }
                // The event has been observed (or discarded)  clear it so a
                // future activity recreation cannot replay it into a new
                // request session.
                DownloadJobManager.clearEvent()
            }
            is DownloadJobManager.Event.Failed -> {
                appendLog(event.message, LogLevel.Error)
                if (fastModeActive) {
                    val activeRequest = request
                    if (activeRequest != null && !fastModeQueue.isNullOrEmpty()) {
                        appendLog(
                            "Fast Mode: ${event.candidate.source.label} did not work  trying the next source.",
                            LogLevel.Warning
                        )
                        lifecycleScope.launch { fastModeNext(activeRequest) }
                    } else {
                        fastModeActive = false
                        fastModeQueue = null
                        uiState = if (activeRequest != null) {
                            UiState.FastMode(
                                FastModeProgress(
                                    sourceLabel = event.candidate.source.label,
                                    detail = "Fast Mode failed: ${event.message.lineSequence().firstOrNull().orEmpty().take(160)}",
                                    done = true,
                                    succeeded = false,
                                    result = initialCandidateResult(activeRequest)
                                )
                            )
                        } else {
                            UiState.Error(event.message)
                        }
                    }
                } else {
                    uiState = UiState.Error(event.message)
                }
            }
            is DownloadJobManager.Event.ValidationMismatch -> {
                // The download itself succeeded; only the version code differs
                // from the request (the parser couldn't know it up front). The
                // file is kept. Only Fast Mode asks the user; the check itself
                // is gated to Fast Mode, so this else branch is defensive only.
                val activeRequest = request
                // Only the activity that owns this request may act on the
                // event. A stale instance (no request, or a different request)
                // must ignore it  and must NOT clear it first, or the owner's
                // collector would never see it (StateFlow conflates: a clear
                // before the owner processes the event swallows it).
                if (activeRequest == null || event.candidate.packageName != activeRequest.packageName) return
                if (fastModeActive) {
                    appendLog(
                        "Fast Mode: ${event.candidate.source.label} downloaded " +
                            "${event.candidate.versionDisplay}  build differs from requested " +
                            "${activeRequest.versionCodeSummary ?: "version"}. Asking the user.",
                        LogLevel.Warning
                    )
                    val decisionGate = CompletableDeferred<FastModeChoice?>()
                    fastModeDecision = decisionGate
                    uiState = UiState.FastMode(
                        FastModeProgress(
                            sourceLabel = event.candidate.source.label,
                            detail = "Version code mismatch",
                            awaitingDecision = true,
                            mismatchDetail = "Requested build ${activeRequest.versionCodeSummary ?: "?"}  " +
                                "${event.candidate.source.label} downloaded " +
                                "${event.candidate.versionDisplay}, found build " +
                                "${event.foundVersionCode ?: "unknown"}."
                        )
                    )
                    lifecycleScope.launch {
                        val decision = decisionGate.await()
                        DownloadJobManager.clearEvent()
                        if (!fastModeActive || decision == null) {
                            event.file.delete()
                            return@launch
                        }
                        if (decision == FastModeChoice.USE) {
                            appendLog(
                                "Fast Mode: using downloaded ${event.candidate.versionDisplay} from " +
                                    "${event.candidate.source.label} despite the code mismatch.",
                                LogLevel.Info
                            )
                            runCatching {
                                returnPreparedFile(activeRequest, event.candidate, event.file, helperSettings)
                            }.onFailure { error ->
                                appendLog(error.message ?: "Could not return the file.", LogLevel.Error)
                                event.file.delete()
                            }
                            fastModeActive = false
                            fastModeQueue = null
                            uiState = UiState.Ready(initialCandidateResult(activeRequest))
                        } else {
                            event.file.delete()
                            appendLog(
                                "Fast Mode: user skipped ${event.candidate.source.label}  " +
                                    "trying the next source.",
                                LogLevel.Info
                            )
                            fastModeNext(activeRequest)
                        }
                    }
                } else {
                    DownloadJobManager.clearEvent()
                    event.file.delete()
                    val message = "Downloaded version code does not match the request " +
                        "(found ${event.foundVersionCode ?: "unknown"})."
                    appendLog(message, LogLevel.Error)
                    uiState = UiState.Error(message)
                }
            }
            is DownloadJobManager.Event.Cancelled -> {
                appendLog("Download cancelled.", LogLevel.Warning)
                fastModeActive = false
                fastModeQueue = null
                val activeRequest = request
                uiState = if (activeRequest != null) {
                    UiState.Ready(initialCandidateResult(activeRequest))
                } else {
                    UiState.Idle
                }
            }
        }
    }

    private fun deliverPendingResultIfPresent(request: HelperRequest?): Boolean {
        if (request == null) return false
        val pending = DownloadJobManager.readPendingResult(applicationContext) ?: return false
        // The stored result belongs to a previous request session. A new request
        // for a different app (or for a different version of the same app)
        // invalidates it  drop the pending file and its stale "return to
        // Morphe" notification so the old APK can never be handed back for the
        // new request (e.g. when the old completion notification is tapped
        // while the new request is on screen).
        if (!pending.belongsTo(request)) {
            DownloadJobManager.clearPendingResult(applicationContext)
            cancelCompletionNotification()
            return false
        }
        return deliverResult(pending)
    }

    private fun cancelCompletionNotification() {
        runCatching {
            NotificationManagerCompat.from(this).cancel(NOTIFICATION_ID_DONE)
        }
    }

    /**
     * When Morphe re-requests a package and version that were already
     * downloaded (e.g. a patch failed and the user retries, or the same
     * version needs patching again) and the file still exists because
     * auto-clear is off or a visible copy was saved to Downloads, offer the
     * user the choice: reuse the existing APK or download a fresh copy.
     * Only matches an exact package + version, so a different app or version
     * never gets an offer.
     */
    private fun offerExistingDownloadIfPresent(request: HelperRequest) {
        if (request.callerPackage.isEmpty() || request.requestedVersionName == null) return
        val existing = DownloadHistoryStore.entries(applicationContext)
            .filter { history ->
                history.packageName == request.packageName &&
                    request.matchesRequestedVersion(history.versionName, null)
            }
            // The same file may be recorded twice (cache copy + Downloads
            // copy); show each distinct file once, newest record first.
            .distinctBy { it.fileName }
            .filter { entry -> historyFileExists(entry) }
            .map { entry -> ReuseOption(entry, historyFileSize(entry)) }
        if (existing.isNotEmpty()) reuseOffer = existing
    }

    private fun historyFileExists(entry: DownloadHistoryEntry): Boolean {
        val uri = Uri.parse(entry.uri)
        return runCatching {
            if (entry.uri.startsWith("content://")) {
                contentResolver.openFileDescriptor(uri, "r")?.use { true } ?: false
            } else {
                File(uri.path ?: return@runCatching false).exists()
            }
        }.getOrDefault(false)
    }

    private fun historyFileSize(entry: DownloadHistoryEntry): Long {
        val uri = Uri.parse(entry.uri)
        return runCatching {
            if (entry.uri.startsWith("content://")) {
                contentResolver.openAssetFileDescriptor(uri, "r")?.use { it.length } ?: 0L
            } else {
                File(uri.path ?: return@runCatching 0L).length()
            }
        }.getOrDefault(0L)
    }

    /** Hand the selected offered file back to Morphe instead of downloading again. */
    private fun useReuseOffer(option: ReuseOption) {
        reuseOffer = null
        val request = request ?: return
        val entry = option.entry
        appendLog(
            "Reusing previously downloaded ${entry.fileName} for ${request.packageName} " +
                "instead of re-downloading.",
            LogLevel.Info
        )
        if (logcatLoggingEnabled) {
            Log.i(TAG, "Reusing cached download for ${request.packageName}: ${entry.fileName}")
        }
        val pending = PendingDownloadResult(
            uri = entry.uri,
            fileName = entry.fileName,
            packageName = entry.packageName,
            versionName = entry.versionName,
            sourceName = entry.sourceName,
            requestPackage = request.packageName,
            callerPackage = request.callerPackage
        )
        DownloadJobManager.persistPendingResult(pending, applicationContext)
        deliverResult(pending)
    }

    private fun deliverResult(pending: PendingDownloadResult): Boolean {
        if (getCallingActivity() == null) return false
        val uri = Uri.parse(pending.uri)
        val result = Intent().apply {
            data = uri
            clipData = ClipData.newUri(contentResolver, pending.fileName, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            putExtra(DownloadHelperContract.EXTRA_RESULT_PACKAGE_NAME, pending.packageName)
            putExtra(DownloadHelperContract.EXTRA_RESULT_VERSION_NAME, pending.versionName)
            putExtra(DownloadHelperContract.EXTRA_RESULT_SOURCE_NAME, pending.sourceName)
            putExtra(DownloadHelperContract.EXTRA_RESULT_FILE_NAME, pending.fileName)
        }
        setResult(Activity.RESULT_OK, result)
        appendLog("Returned ${pending.fileName} to ${pending.callerPackage}.")
        if (logcatLoggingEnabled) {
            Log.i(
                TAG,
                "Query result: OK package=${pending.packageName}, " +
                    "version=${pending.versionName ?: "any"}, source=${pending.sourceName}, " +
                    "file=${pending.fileName}, uri=${pending.uri}"
            )
        }
        DownloadJobManager.clearPendingResult(applicationContext)
        cancelCompletionNotification()
        finish()
        return true
    }

    private fun shareHistoryEntry(entry: DownloadHistoryEntry) {
        val uri = Uri.parse(entry.uri)
        val share = Intent(Intent.ACTION_SEND).apply {
            type = fileNameMimeType(entry.fileName)
            putExtra(Intent.EXTRA_STREAM, uri)
            clipData = ClipData.newUri(contentResolver, entry.fileName, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        runCatching {
            startActivity(Intent.createChooser(share, "Share ${entry.fileName}"))
        }.onFailure {
            appendLog("Could not share ${entry.fileName}.", LogLevel.Warning)
        }
    }

    private fun openHistoryEntry(entry: DownloadHistoryEntry) {
        val uri = Uri.parse(entry.uri)
        val open = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, fileNameMimeType(entry.fileName))
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        runCatching {
            startActivity(open)
        }.onFailure {
            appendLog("Could not open ${entry.fileName}.", LogLevel.Warning)
        }
    }

    private fun returnPreparedFile(
        request: HelperRequest,
        candidate: DownloadCandidate,
        file: File,
        settings: HelperSettings
    ) {
        val uri = when (settings.downloadLocation) {
            DownloadLocation.TEMPORARY -> FileProvider.getUriForFile(this, "${BuildConfig.APPLICATION_ID}.files", file)
            DownloadLocation.DOWNLOADS -> copyToDownloads(file)
        }
        val pending = PendingDownloadResult(
            uri = uri.toString(),
            fileName = file.name,
            packageName = candidate.packageName,
            versionName = candidate.versionName,
            sourceName = candidate.source.label,
            requestPackage = request.packageName,
            callerPackage = request.callerPackage
        )
        DownloadJobManager.persistPendingResult(pending, applicationContext)
        recordHandOff(request, candidate, file, uri)
        if (
            settings.downloadLocation == DownloadLocation.DOWNLOADS ||
            settings.deleteTemporaryAfterHandoff
        ) {
            scheduleTemporaryDelete(file)
        }
        if (!deliverResult(pending)) {
            appendLog(
                "File is ready; ${request.callerPackage} can request it again to receive it.",
                LogLevel.Warning
            )
        }
    }

    private fun returnPickedFile(candidate: DownloadCandidate, uri: Uri?) {
        val activeRequest = request ?: return
        val settings = helperSettings

        if (uri == null) {
            appendLog("File selection canceled.", LogLevel.Warning)
            return
        }

        appendLog("Checking manually selected file for ${candidate.source.label}.")
        uiState = UiState.CheckingPickedFile(candidate)

        lifecycleScope.launch {
            val result = withContext(Dispatchers.IO) {
                runCatching {
                    val file = copyPickedFileToTemporary(candidate, uri)
                    validateDownloadedArtifact(this@MainActivity, activeRequest, candidate, file)
                    file
                }
            }

            result
                .onSuccess { file ->
                    appendLog("Selected file validated: ${file.name} (${file.length()} bytes).")
                    runCatching {
                        returnPreparedFile(activeRequest, candidate, file, settings)
                    }.onFailure { error ->
                        val message = (error.message ?: "Could not return selected APK to Morphe.")
                            .withManualModeHint()
                        appendLog(message, LogLevel.Error)
                        uiState = UiState.Error(message)
                    }
                }
                .onFailure { error ->
                    // A code-mismatch keeps the file (so Fast Mode can ask); the
                    // picked-file flow has no ask, so clean it up here.
                    if (error is VersionCodeMismatchException) {
                        error.file.delete()
                    }
                    val message = (error.message ?: "Selected file could not be used.")
                        .withManualModeHint()
                    appendLog(message, LogLevel.Error)
                    uiState = UiState.Error(message)
                }
        }
    }

    private fun returnInstalledApp(candidate: DownloadCandidate) {
        val activeRequest = request ?: return
        if (!isPackageInstalled(candidate.packageName)) {
            appendLog("${candidate.packageName} is not installed yet.", LogLevel.Warning)
            installedPackageRefreshToken++
            return
        }

        val result = Intent().apply {
            putExtra(DownloadHelperContract.EXTRA_RESULT_USE_INSTALLED_APP, true)
            putExtra(DownloadHelperContract.EXTRA_RESULT_PACKAGE_NAME, candidate.packageName)
            putExtra(DownloadHelperContract.EXTRA_RESULT_VERSION_NAME, candidate.versionName)
            putExtra(DownloadHelperContract.EXTRA_RESULT_SOURCE_NAME, candidate.source.label)
        }

        setResult(Activity.RESULT_OK, result)
        appendLog("Returned installed ${candidate.packageName} to ${activeRequest.callerPackage}.")
        if (logcatLoggingEnabled) {
            Log.i(
                TAG,
                "Query result: OK package=${candidate.packageName}, " +
                    "version=${candidate.versionName ?: "any"}, source=${candidate.source.label}, " +
                    "useInstalledApp=true"
            )
        }
        finish()
    }

    private fun copyPickedFileToTemporary(candidate: DownloadCandidate, uri: Uri): File {
        val displayName = displayNameForUri(uri)
        val extension = displayName
            ?.substringAfterLast('.', "")
            ?.takeIf { it.isNotBlank() && it != displayName }
            ?: candidate.fileKind.takeUnless { it.equals("web", ignoreCase = true) }
            ?: request?.requestedFileKinds?.orderedFileKinds()?.firstOrNull()
            ?: "apk"
        val outputName = displayName
            ?.takeIf(String::isNotBlank)
            ?: "${candidate.packageName}-${candidate.versionName ?: "manual"}.$extension"
        val outputFile = temporaryDownloadsDir().apply { mkdirs() }.uniqueChild(outputName)

        try {
            val bytesCopied = contentResolver.openInputStream(uri)?.use { input ->
                outputFile.outputStream().use { output -> input.copyTo(output) }
            } ?: error("Selected file could not be opened.")
            check(bytesCopied > 0L) { "Selected file was empty." }
            return outputFile
        } catch (error: Throwable) {
            outputFile.delete()
            throw error
        }
    }

    private fun displayNameForUri(uri: Uri): String? =
        contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
            ?.use { cursor ->
                val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (index >= 0 && cursor.moveToFirst()) cursor.getString(index) else null
            }
            ?.takeIf(String::isNotBlank)


}

private val historyTimeFormat = SimpleDateFormat("MMM d, HH:mm", Locale.US)

private fun formatHistoryTimestamp(timestamp: Long): String =
    synchronized(historyTimeFormat) { historyTimeFormat.format(Date(timestamp)) }

private fun fileNameMimeType(fileName: String): String =
    when (fileName.substringAfterLast('.', "").lowercase(Locale.US)) {
        "apk" -> "application/vnd.android.package-archive"
        "apks",
        "apkm",
        "xapk" -> "application/zip"
        else -> "application/octet-stream"
    }

private fun Context.isHistoryUriUsable(uriString: String): Boolean {
    val uri = runCatching { Uri.parse(uriString) }.getOrNull() ?: return false
    return runCatching {
        contentResolver.openInputStream(uri)?.use { } != null
    }.getOrDefault(false)
}

internal fun File.mimeType(): String = when (extension.lowercase(Locale.US)) {
    "apk" -> "application/vnd.android.package-archive"
    "apks",
    "apkm",
    "xapk" -> "application/zip"
    else -> "application/octet-stream"
}

internal fun File.uniqueChild(fileName: String): File {
    val safeName = fileName.sanitizeFileName()
    val base = safeName.substringBeforeLast('.', safeName)
    val extension = safeName.substringAfterLast('.', "")
        .takeIf { it != safeName && it.isNotBlank() }
        ?.let { ".$it" }
        .orEmpty()
    var candidate = File(this, safeName)
    var index = 1
    while (candidate.exists()) {
        candidate = File(this, "$base ($index)$extension")
        index++
    }
    return candidate
}

private object HelperDefaults {
    val CardCornerRadius = 16.dp
    val CompactCornerRadius = 12.dp
    val SectionCornerRadius = 18.dp
    val ButtonCornerRadius = 16.dp
    val ContentPadding = 16.dp
    val ContentPaddingSmall = 8.dp
    val ItemSpacing = 12.dp
    val IconSizeSmall = 20.dp
    val ButtonHeight = 52.dp
    val ActionClearWidth = 96.dp
}

@Composable
private fun HelperTheme(
    themeMode: ThemeMode,
    dynamicColors: Boolean,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val dark = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
    }
    helperDarkTheme = dark
    helperDynamicColors = dynamicColors && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    val colorScheme = when {
        dynamicColors && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ->
            if (dark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        dark -> darkHelperColorScheme()
        else -> lightHelperColorScheme()
    }
    // Keep the system bars in sync with the active theme. On Android 15+ (which
    // enforces edge-to-edge) the appearance flags decide whether the transparent
    // nav bar renders with light or dark icons; without them some devices default
    // to a light nav bar even in dark mode. Below Android 15 the bar colors still
    // apply, so paint them explicitly to match the theme and avoid OEM defaults.
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !dark
                isAppearanceLightNavigationBars = !dark
            }
            @Suppress("DEPRECATION")
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.VANILLA_ICE_CREAM) {
                val barColor = (if (dark) Color(0xFF0B0C10) else Color(0xFFF7F8FA)).toArgb()
                window.statusBarColor = barColor
                window.navigationBarColor = barColor
            }
        }
    }
    MaterialTheme(colorScheme = colorScheme, content = content)
}

@Composable
private fun darkHelperColorScheme() = darkColorScheme(
    primary = Color(0xFFA4C9FF),
    onPrimary = Color(0xFF00315D),
    primaryContainer = Color(0xFF004884),
    onPrimaryContainer = Color(0xFFD4E3FF),
    secondary = Color(0xFFBCC7DB),
    onSecondary = Color(0xFF263141),
    secondaryContainer = Color(0xFF3D4758),
    onSecondaryContainer = Color(0xFFD8E3F8),
    tertiary = Color(0xFFD9BDE3),
    onTertiary = Color(0xFF3D2946),
    tertiaryContainer = Color(0xFF543F5E),
    onTertiaryContainer = Color(0xFFF6D9FF),
    error = Color(0xFFFFB4AB),
    errorContainer = Color(0xFF93000A),
    onError = Color(0xFF690005),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF0B0C10),
    onBackground = Color(0xFFE3E2E6),
    surface = Color(0xFF1A1C1E),
    onSurface = Color(0xFFE3E2E6),
    surfaceVariant = Color(0xFF43474E),
    onSurfaceVariant = Color(0xFFC3C6CF),
    outline = Color(0xFF8D9199),
    outlineVariant = Color(0xFF43474E),
)

@Composable
private fun lightHelperColorScheme() = lightColorScheme(
    primary = Color(0xFF005FAD),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFD4E3FF),
    onPrimaryContainer = Color(0xFF001C3A),
    secondary = Color(0xFF3D4758),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFD8E3F8),
    onSecondaryContainer = Color(0xFF263141),
    tertiary = Color(0xFF543F5E),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFF6D9FF),
    onTertiaryContainer = Color(0xFF3D2946),
    error = Color(0xFFBA1A1A),
    errorContainer = Color(0xFFFFDAD6),
    onError = Color(0xFFFFFFFF),
    onErrorContainer = Color(0xFF93000A),
    background = Color(0xFFF7F8FA),
    onBackground = Color(0xFF1A1C1E),
    surface = Color(0xFFEFF0F3),
    onSurface = Color(0xFF1A1C1E),
    surfaceVariant = Color(0xFFDEE1E8),
    onSurfaceVariant = Color(0xFF43474E),
    outline = Color(0xFF74777F),
    outlineVariant = Color(0xFFC4C6CF),
)

@Composable
private fun HelperCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = HelperDefaults.CardCornerRadius,
    color: Color = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(cornerRadius),
        color = color,
        contentColor = MaterialTheme.colorScheme.onSurface,
        tonalElevation = 2.dp,
        shadowElevation = 0.dp
    ) {
        content()
    }
}

@Composable
private fun HelperButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null
) {
    val primary = MaterialTheme.colorScheme.primary
    Button(
        onClick = onClick,
        modifier = modifier.height(HelperDefaults.ButtonHeight),
        shape = RoundedCornerShape(HelperDefaults.ButtonCornerRadius),
        colors = ButtonDefaults.buttonColors(
            containerColor = primary.copy(alpha = 0.28f),
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        border = BorderStroke(1.dp, primary.copy(alpha = 0.48f)),
        contentPadding = PaddingValues(horizontal = HelperDefaults.ContentPadding)
    ) {
        icon?.let {
            Icon(
                imageVector = it,
                contentDescription = null,
                modifier = Modifier.size(HelperDefaults.IconSizeSmall)
            )
            Spacer(Modifier.width(HelperDefaults.ContentPaddingSmall))
        }
        Text(text, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
private fun HelperOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null
) {
    val primary = MaterialTheme.colorScheme.primary
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(HelperDefaults.ButtonHeight),
        shape = RoundedCornerShape(HelperDefaults.ButtonCornerRadius),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
        ),
        border = BorderStroke(1.dp, primary.copy(alpha = 0.32f)),
        contentPadding = PaddingValues(horizontal = HelperDefaults.ContentPadding)
    ) {
        icon?.let {
            Icon(
                imageVector = it,
                contentDescription = null,
                modifier = Modifier.size(HelperDefaults.IconSizeSmall)
            )
            Spacer(Modifier.width(HelperDefaults.ContentPaddingSmall))
        }
        Text(text, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

// ---- In-app captcha browser: a real WebView that passes Cloudflare-style
// challenges, captures the download URL the page produces, and hands it to the
// normal download pipeline. Any source that gates its file behind a captcha can
// opt in by setting DownloadCandidate.captchaUrl.

@Composable
private fun CaptchaBrowserScreen(
    candidate: DownloadCandidate,
    onClose: () -> Unit,
    onDownloadCaptured: (BrowserDownloadCapture) -> Unit
) {
    val context = LocalContext.current
    var progress by remember { mutableIntStateOf(0) }
    val bridge = remember {
        CaptchaCaptureBridge { url ->
            Handler(Looper.getMainLooper()).post {
                onDownloadCaptured(
                    BrowserDownloadCapture(
                        downloadUrl = url,
                        refererUrl = candidate.captchaUrl ?: candidate.url,
                        cookieHeader = CookieManager.getInstance().getCookie(
                            candidate.captchaUrl ?: candidate.url
                        )
                    )
                )
            }
        }
    }
    val webView = remember {
        WebView(context).apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            addJavascriptInterface(bridge, CAPTCHA_BRIDGE_NAME)
            webViewClient = object : WebViewClient() {
                override fun shouldInterceptRequest(
                    view: WebView?,
                    request: WebResourceRequest?
                ): WebResourceResponse? {
                    // Apply AdGuard DNS filtering to the in-app browser: when the
                    // setting is on, drop subresources whose host AdGuard filters
                    // (ads/trackers answer 0.0.0.0). Any resolver failure means
                    // "not blocked", so the page still loads.
                    val host = request?.url?.host ?: return null
                    if (AdGuardDns.isBlocked(host)) {
                        Log.i(TAG, "AdGuard DNS: blocked ${request.url} in the in-app browser")
                        return WebResourceResponse(
                            "text/plain",
                            "utf-8",
                            ByteArrayInputStream(ByteArray(0))
                        )
                    }
                    return null
                }

                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    val url = request?.url?.toString() ?: return false
                    if (url.looksLikeApkDownload()) {
                        val referer = view?.url ?: view?.originalUrl ?: candidate.captchaUrl ?: candidate.url
                        onDownloadCaptured(
                            BrowserDownloadCapture(
                                downloadUrl = url,
                                refererUrl = referer,
                                cookieHeader = CookieManager.getInstance().getCookie(referer)
                            )
                        )
                        return true
                    }
                    return false
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    view?.evaluateJavascript(CAPTCHA_CAPTURE_JS, null)
                }
            }
            setDownloadListener { url, _, _, _, _ ->
                // Authoritative signal: the page started a real download.
                val referer = candidate.captchaUrl ?: candidate.url
                onDownloadCaptured(
                    BrowserDownloadCapture(
                        downloadUrl = url,
                        refererUrl = referer,
                        cookieHeader = CookieManager.getInstance().getCookie(referer)
                    )
                )
            }
        }
    }
    DisposableEffect(webView) {
        onDispose {
            webView.stopLoading()
            webView.destroy()
        }
    }
    LaunchedEffect(candidate.captchaUrl ?: candidate.url) {
        webView.loadUrl(candidate.captchaUrl ?: candidate.url)
    }
    BackHandler {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            onClose()
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = HelperDefaults.ContentPadding, vertical = HelperDefaults.ContentPadding),
            verticalArrangement = Arrangement.spacedBy(HelperDefaults.ItemSpacing)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(HelperDefaults.ItemSpacing),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    val browserTitle = if (candidate.captchaUrl != null && !candidate.directDownload) {
                        "Solve captcha"
                    } else {
                        "Open in app"
                    }
                    Text(
                        text = "$browserTitle  ${candidate.source.label}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = candidate.versionDisplay,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                HelperOutlinedButton(
                    text = "Close",
                    onClick = onClose,
                    icon = Icons.Outlined.Close,
                    modifier = Modifier.widthIn(min = 96.dp)
                )
            }
            InfoCard(
                if (candidate.captchaUrl != null && !candidate.directDownload) {
                    "Solve the captcha in the browser. When the page starts the download, " +
                        "the file is captured and returned to Morphe automatically." 
                } else {
                    "Find the download link in the web page. The app will handle the download." +
                        "\npackage: \"${candidate.packageName}\"\nversion: \"${candidate.versionDisplay}\"."
                }
            )
            if (progress > 0 && progress < 100) {
                LinearProgressIndicator(
                    progress = { progress / 100f },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            AndroidView(
                factory = { webView },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(HelperDefaults.SectionCornerRadius))
            )
        }
    }
}

/** Posts captured URLs from the WebView's JS bridge back to Kotlin. */
private class CaptchaCaptureBridge(
    private val onUrl: (String) -> Unit
) {
    @JavascriptInterface
    fun capture(url: String) {
        onUrl(url)
    }
}

private const val CAPTCHA_BRIDGE_NAME = "Android"

/**
 * Injected into every page the captcha browser finishes loading. Captures
 * clicks on download-looking links and window.open calls that the download
 * listener would not see (e.g. links opened in a new tab).
 */
private const val CAPTCHA_CAPTURE_JS = """
(function () {
  var re = /\.(apk|apks|apkm|xapk)(\?|#|$)/i;
  function looksLike(u) {
    if (!u) return false;
    try { u = decodeURIComponent(u); } catch (e) {}
    return re.test(u) || /filename[^.]*\.(apk|apks|apkm|xapk)/i.test(u);
  }
  
  document.addEventListener('click', function (e) {
    var el = e.target;
    while (el && el !== document && !(el.tagName === 'A' && el.href)) el = el.parentNode;
    if (el && el.tagName === 'A' && looksLike(el.href)) {
      window.Android && window.Android.capture(el.href);
    }
  }, true);
  var origOpen = window.open;
  window.open = function (u) {
    if (u && looksLike(u)) { window.Android && window.Android.capture(u); return null; }
    return origOpen ? origOpen.apply(window, arguments) : null;
  };
})();
"""

/** True for URLs that point at an APK-family file (extension or filename hint). */
private fun String.looksLikeApkDownload(): Boolean {
    val decoded = Uri.decode(this).lowercase(Locale.US)
    return Regex("""\.(apk|apks|apkm|xapk)(\?|#|$)""").containsMatchIn(decoded) ||
        Regex("""filename[^.]*\.(apk|apks|apkm|xapk)""").containsMatchIn(decoded)
}

@Composable
private fun HelperScreen(
    request: HelperRequest?,
    state: UiState,
    settings: HelperSettings,
    logs: List<RequestLogEntry>,
    installedPackageRefreshToken: Int,
    selectedPagerPage: Int,
    onPagerPageChanged: (Int) -> Unit,
    onSettingsChange: (HelperSettings) -> Unit,
    onRefresh: () -> Unit,
    // VirusTotal API key, threaded into the scan-flow cards (ask, progress,
    // result) so quota is shown where scanning actually happens instead of
    // cluttering the home screen. Empty when VirusTotal is disabled.
    virusTotalApiKey: String,
    onResolve: (DownloadSource, CandidateOption) -> Unit,
    onDownload: (DownloadCandidate) -> Unit,
    onPickDownloadedFile: (DownloadCandidate, Uri?) -> Unit,
    onUseInstalledApp: (DownloadCandidate) -> Unit,
    onVersionHistory: (DownloadSource) -> Unit,
    onDownloadVersion: (DownloadCandidate) -> Unit,
    onOpenHistoryEntry: (DownloadHistoryEntry) -> Unit,
    onShareHistoryEntry: (DownloadHistoryEntry) -> Unit,
    onClearHistory: () -> Unit,
    onClearLogs: () -> Unit,
    onCancel: () -> Unit,
    onCancelDownload: () -> Unit,
    onSkipScanWait: () -> Unit,
    onCancelFastMode: () -> Unit,
    onUseFastModeMismatch: () -> Unit,
    onSkipFastModeMismatch: () -> Unit,
    onChooseVersion: (FastModePolicy) -> Unit,
    onOpenMorphe: () -> Unit,
    onSolveCaptcha: (DownloadCandidate) -> Unit,
    onRequestFileTypeChange: (String) -> Unit,
    onProceedAfterScan: () -> Unit,
    onCancelAfterScan: () -> Unit,
    onSkipScan: () -> Unit
) {
    var showSettings by remember { mutableStateOf(false) }
    var showAppBrowser by remember { mutableStateOf(false) }
    var pendingFilePick by remember { mutableStateOf<DownloadCandidate?>(null) }
    var primaryAction by remember { mutableStateOf<PrimaryAction?>(null) }
    val context = LocalContext.current
    var historyEntries by remember { mutableStateOf<List<DownloadHistoryEntry>>(emptyList()) }
    val refreshHistory = {
        historyEntries = DownloadHistoryStore.entries(context)
    }
    val filePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        val candidate = pendingFilePick
        pendingFilePick = null
        candidate?.let { onPickDownloadedFile(it, uri) }
    }
    val openDownloadedFilePicker: (DownloadCandidate) -> Unit = { candidate ->
        pendingFilePick = candidate
        filePickerLauncher.launch(APK_PICKER_MIME_TYPES)
    }
    BackHandler(enabled = showSettings || showAppBrowser) {
        showSettings = false
        showAppBrowser = false
    }

    val flowVisible = request != null &&
        (state is UiState.Ready || (state is UiState.FastMode && state.progress.result != null))
    LaunchedEffect(flowVisible) {
        if (!flowVisible) primaryAction = null
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        if (showAppBrowser) {
            AppBrowserScreen(onBack = { showAppBrowser = false })
            return@Surface
        }

        if (showSettings) {
            HelperSettingsScreen(
                settings = settings,
                onSettingsChange = onSettingsChange,
                logs = logs,
                onClearLogs = onClearLogs,
                historyEntries = historyEntries,
                onOpenHistoryEntry = onOpenHistoryEntry,
                onShareHistoryEntry = onShareHistoryEntry,
                onClearHistory = {
                    onClearHistory()
                    refreshHistory()
                },
                onBack = { showSettings = false }
            )
            return@Surface
        }

        Column(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = HelperDefaults.ContentPadding, vertical = HelperDefaults.ContentPadding),
            verticalArrangement = Arrangement.spacedBy(HelperDefaults.ItemSpacing)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(HelperDefaults.ItemSpacing),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Helper",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            // Yield space to the version label first: the version is
                            // the last child, so without this it gets whatever the
                            // title leaves and clips ("v1.") when the row is tight.
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        Text(
                            text = "v${BuildConfig.VERSION_NAME}",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.titleSmall,
                            maxLines = 1,
                            overflow = TextOverflow.Clip
                        )
                    }
                    HelperBoltButton(
                        fastMode = settings.fastMode,
                        onClick = {
                            val enabled = !settings.fastMode
                            onSettingsChange(settings.copy(fastMode = enabled))
                            Toast.makeText(
                                context,
                                if (enabled) "Fast Mode on" else "Fast Mode off",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    )
                    HelperThemeButton(
                        dark = when (settings.themeMode) {
                            ThemeMode.DARK -> true
                            ThemeMode.LIGHT -> false
                            ThemeMode.SYSTEM -> isSystemInDarkTheme()
                        },
                        onToggle = {
                            val target = if (settings.themeMode == ThemeMode.LIGHT) {
                                ThemeMode.DARK
                            } else {
                                ThemeMode.LIGHT
                            }
                            onSettingsChange(settings.copy(themeMode = target))
                            Toast.makeText(
                                context,
                                if (target == ThemeMode.LIGHT) "Light theme" else "Dark theme",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    )
                    HelperHeaderIconButton(
                        icon = Icons.Outlined.Settings,
                        contentDescription = "Settings",
                        onClick = {
                            refreshHistory()
                            showSettings = true
                        }
                    )
                }
            }

            if (request == null) {
                item {
                    EmptyLaunchState(
                        onOpenMorphe = onOpenMorphe,
                        onFindApps = { showAppBrowser = true }
                    )
                }
                return@LazyColumn
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    SectionTitle("App info")
                    AppInfoCard(
                        request = request,
                        onFormatSelected = onRequestFileTypeChange
                    )
                }
            }
            when (state) {
                UiState.Idle,
                UiState.Loading -> item { LoadingState() }

                is UiState.Ready -> {
                    item {
                        SourcePickerFlow(
                            request = request,
                            result = state.result,
                            selectedPagerPage = selectedPagerPage,
                            onPagerPageChanged = onPagerPageChanged,
                            onResolve = onResolve,
                            onDownload = onDownload,
                            onPickDownloadedFile = openDownloadedFilePicker,
                            onUseInstalledApp = onUseInstalledApp,
                            onSolveCaptcha = onSolveCaptcha,
                            onVersionHistory = onVersionHistory,
                            onDownloadVersion = onDownloadVersion,
                            onRefresh = onRefresh,
                            onCancel = onCancel,
                            installedPackageRefreshToken = installedPackageRefreshToken,
                            onPrimaryActionChanged = { primaryAction = it }
                        )
                    }
                }

                is UiState.CheckingPickedFile -> item { CheckingPickedFileState(state) }
                is UiState.Downloading -> {
                    item {
                        DownloadingState(
                            state = state,
                            onCancel = onCancelDownload,
                            onSkipWait = onSkipScanWait,
                            onSkipScan = onSkipScan
                        )
                    }
                    if (isScanStatus(state.statusMessage)) {
                        item { VirusTotalQuotaCard(virusTotalApiKey) }
                    }
                }
                is UiState.ScanAsk -> {
                    item {
                        ScanAskCard(
                            candidate = state.candidate,
                            onScan = onProceedAfterScan,
                            onSkip = onCancelAfterScan
                        )
                    }
                    item { VirusTotalQuotaCard(virusTotalApiKey) }
                }
                is UiState.ScanResult -> {
                    item {
                        ScanResultCard(
                            scanResult = state.scanResult,
                            detail = state.detail,
                            isMalicious = state.isMalicious,
                            onProceed = onProceedAfterScan,
                            onCancel = onCancelAfterScan,
                            shaVerified = state.shaVerified
                        )
                    }
                    item { VirusTotalQuotaCard(virusTotalApiKey) }
                }
                is UiState.Error -> item {
                    ErrorState(message = state.message, onRefresh = onRefresh, onCancel = onCancel)
                }

                is UiState.FastMode -> {
                    item {
                        FastModeCard(
                            progress = state.progress,
                            onCancel = onCancelFastMode,
                            onSkipWait = onSkipScanWait,
                            onUseMismatch = onUseFastModeMismatch,
                            onSkipMismatch = onSkipFastModeMismatch,
                            onSkipScan = onSkipScan,
                            onChooseVersion = onChooseVersion
                        )
                    }
                    if (isScanStatus(state.progress.detail)) {
                        item { VirusTotalQuotaCard(virusTotalApiKey) }
                    }
                    state.progress.result?.let { result ->
                        item {
                            SourcePickerFlow(
                                request = request,
                                result = result,
                                selectedPagerPage = selectedPagerPage,
                                onPagerPageChanged = onPagerPageChanged,
                                onResolve = onResolve,
                                onDownload = onDownload,
                                onPickDownloadedFile = openDownloadedFilePicker,
                                onUseInstalledApp = onUseInstalledApp,
                                onSolveCaptcha = onSolveCaptcha,
                                onVersionHistory = onVersionHistory,
                                onDownloadVersion = onDownloadVersion,
                                onRefresh = onRefresh,
                                onCancel = onCancel,
                                installedPackageRefreshToken = installedPackageRefreshToken,
                                onPrimaryActionChanged = { primaryAction = it }
                            )
                        }
                    }
                }
            }
        }
        val action = primaryAction
        if (action != null) {
            SourceBottomBar(action = action, onRefresh = onRefresh, onCancel = onCancel)
        }
        }
    }
}

@Composable
private fun SourceHealthCard() {
    val checks by SourceHealthChecker.checks.collectAsState()
    val checking by SourceHealthChecker.checking.collectAsState()
    val webChecks = checks.filter { it.status != SourceHealthChecker.Status.Skipped }

    val goodCount = webChecks.count { it.status == SourceHealthChecker.Status.Good }
    val blockedCount = webChecks.count { it.status == SourceHealthChecker.Status.CaptchaBlocked }
    val unreachableCount = webChecks.count { it.status == SourceHealthChecker.Status.Unreachable }
    val hasProblems = blockedCount > 0 || unreachableCount > 0

    val summaryParts = mutableListOf<String>()
    if (goodCount > 0) summaryParts.add("$goodCount available")
    if (blockedCount > 0) summaryParts.add("$blockedCount captcha-blocked")
    if (unreachableCount > 0) summaryParts.add("$unreachableCount unreachable")
    val summary = when {
        checking -> "Checking sources…"
        webChecks.isEmpty() -> "Sources not checked yet"
        else -> summaryParts.ifEmpty { listOf("No sources available") }.joinToString(" · ")
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(HelperDefaults.ItemSpacing)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(HelperDefaults.ItemSpacing),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Source health",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            HelperOutlinedButton(
                text = if (checking) "Checking…" else "Re-check",
                onClick = { SourceHealthChecker.refresh() },
                icon = if (checking) null else Icons.Outlined.Refresh,
                modifier = Modifier.width(HelperDefaults.ActionClearWidth + 28.dp)
            )
        }
        Text(
            text = summary,
            color = if (hasProblems) {
                MaterialTheme.colorScheme.error
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
            style = MaterialTheme.typography.bodySmall
        )

        if (webChecks.isEmpty()) {
            InfoCard("No sources checked yet — the check runs automatically when the app opens.")
        } else {
            webChecks.forEach { check ->
                SourceHealthRow(check)
            }
        }
    }
}

@Composable
private fun SourceHealthRow(check: SourceHealthChecker.Check) {
    val (dotColor, statusText) = when (check.status) {
        SourceHealthChecker.Status.Good -> MaterialTheme.colorScheme.primary to "Available"
        SourceHealthChecker.Status.Checking -> warningAccent() to "Checking…"
        SourceHealthChecker.Status.CaptchaBlocked ->
            MaterialTheme.colorScheme.error to (check.message ?: "Blocked by a captcha challenge")
        SourceHealthChecker.Status.Unreachable ->
            MaterialTheme.colorScheme.error to (check.message ?: "Unreachable")
        SourceHealthChecker.Status.Skipped -> MaterialTheme.colorScheme.outline to "Not checked"
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(HelperDefaults.ContentPaddingSmall),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .padding(top = 5.dp)
                .size(8.dp)
                .clip(RoundedCornerShape(50))
                .background(dotColor)
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = check.source.label,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = statusText,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun HistoryEntryCard(
    entry: DownloadHistoryEntry,
    usable: Boolean,
    onOpen: () -> Unit,
    onShare: () -> Unit
) {
    val context = LocalContext.current
    var showScanResult by remember { mutableStateOf(false) }
    HelperCard(cornerRadius = HelperDefaults.CompactCornerRadius) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(HelperDefaults.ContentPadding),
            verticalArrangement = Arrangement.spacedBy(HelperDefaults.ItemSpacing)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(HelperDefaults.ItemSpacing),
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = entry.appName,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = entry.packageName,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Text(
                    text = formatHistoryTimestamp(entry.timestamp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Text(
                text = buildString {
                    entry.versionName?.let { append(it).append(" · ") }
                    append(entry.sourceName)
                    append(" · ")
                    append(entry.fileName)
                    if (!entry.fileKind.equals("web", ignoreCase = true)) {
                        append(" · ")
                        append(entry.fileKind.uppercase(Locale.US))
                    }
                },
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            entry.scanVerdict?.let { verdict ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .then(if (verdict.result != null) {
                            Modifier.clickable { showScanResult = true }
                        } else {
                            Modifier
                        })
                        .padding(vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = when {
                            verdict.malicious -> Icons.Outlined.Warning
                            verdict.failed -> Icons.Outlined.HelpOutline
                            else -> Icons.Outlined.CheckCircle
                        },
                        contentDescription = null,
                        tint = when {
                            verdict.malicious -> MaterialTheme.colorScheme.error
                            verdict.failed -> MaterialTheme.colorScheme.onSurfaceVariant
                            else -> MaterialTheme.colorScheme.primary
                        },
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = verdict.label,
                        style = MaterialTheme.typography.bodySmall,
                        color = when {
                            verdict.malicious -> MaterialTheme.colorScheme.error
                            verdict.failed -> MaterialTheme.colorScheme.onSurfaceVariant
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    verdict.result?.let { result ->
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = if (result.cached) {
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant
                            }
                        ) {
                            Text(
                                text = if (result.cached) "Cached" else "Fresh",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (result.cached) {
                                    MaterialTheme.colorScheme.onPrimaryContainer
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                },
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                            )
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                            contentDescription = "View scan details",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
            if (usable) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(HelperDefaults.ItemSpacing)
                ) {
                    HelperButton(
                        text = "Share",
                        onClick = onShare,
                        icon = Icons.Outlined.Share,
                        modifier = Modifier.weight(1f)
                    )
                    HelperOutlinedButton(
                        text = "Open",
                        onClick = onOpen,
                        icon = Icons.Outlined.FolderOpen,
                        modifier = Modifier.weight(1f)
                    )
                }                } else {
                Text(
                    text = "File no longer available (temporary hand-off files are cleaned up after Morphe copies them).",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }

    val savedResult = entry.scanVerdict?.result
    if (showScanResult && savedResult != null) {
        AlertDialog(
            onDismissRequest = { showScanResult = false },
            confirmButton = {},
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    ScanResultCard(
                        scanResult = savedResult,
                        detail = scanResultDetail(savedResult),
                        isMalicious = savedResult is VirusTotalScanner.ScanResult.Malicious,
                        onProceed = {},
                        onCancel = {},
                        readOnly = true
                    )
                    Spacer(Modifier.height(8.dp))
                    HelperButton(
                        text = "Close",
                        onClick = { showScanResult = false },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        )
    }
}

@Composable
private fun HelperSettingsScreen(
    settings: HelperSettings,
    onSettingsChange: (HelperSettings) -> Unit,
    logs: List<RequestLogEntry>,
    onClearLogs: () -> Unit,
    historyEntries: List<DownloadHistoryEntry>,
    onOpenHistoryEntry: (DownloadHistoryEntry) -> Unit,
    onShareHistoryEntry: (DownloadHistoryEntry) -> Unit,
    onClearHistory: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val swipeThresholdPx = with(LocalDensity.current) { 72.dp.toPx() }
    var tab by rememberSaveable { mutableStateOf(SettingsTab.Settings) }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = HelperDefaults.ContentPadding, vertical = HelperDefaults.ContentPadding)
            .pointerInput(swipeThresholdPx) {
                var totalDrag = 0f
                detectHorizontalDragGestures(
                    onDragStart = { totalDrag = 0f },
                    onHorizontalDrag = { _, dragAmount ->
                        totalDrag += dragAmount
                    },
                    onDragEnd = {
                        if (totalDrag >= swipeThresholdPx) onBack()
                    },
                    onDragCancel = { totalDrag = 0f }
                )
            },
        verticalArrangement = Arrangement.spacedBy(HelperDefaults.ItemSpacing)
    ) {
        item {
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Version ${BuildConfig.VERSION_NAME}",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                HelperHeaderIconButton(
                    icon = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Back",
                    onClick = onBack,
                    modifier = Modifier.align(Alignment.CenterStart)
                )
            }
        }

        item {
            SettingsTabRow(
                selected = tab,
                onSelect = { tab = it }
            )
        }

        if (tab == SettingsTab.Settings) {
            item {
                HelperSettingsCard(
                    settings = settings,
                    onSettingsChange = onSettingsChange
                )
            }
        }

        if (tab == SettingsTab.Health) {
            item {
                SourceHealthCard()
            }
        }

        if (tab == SettingsTab.History) {
            item {
                DownloadHistorySection(
                    entries = historyEntries,
                    onClear = onClearHistory,
                    onOpen = onOpenHistoryEntry,
                    onShare = onShareHistoryEntry
                )
            }
        }

        if (tab == SettingsTab.Logs) {
            item {
                RequestLogsCard(
                    logs = logs,
                    onClearLogs = onClearLogs
                )
            }
        }
    }
}

@Composable
private fun HelperSettingsCard(
    settings: HelperSettings,
    onSettingsChange: (HelperSettings) -> Unit
) {
    val context = LocalContext.current
    var cacheBytes by remember(context) { mutableStateOf(context.temporaryDownloadsSize()) }
    var defaultSourceExpanded by remember { mutableStateOf(false) }
    var downloadsBytes by remember(context) { mutableStateOf(context.downloadsCopySize()) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(HelperDefaults.ItemSpacing)
    ) {
        SettingsGroupCard("Save downloads") {
            DownloadLocation.entries.forEach { location ->
                SettingsOptionCard(
                    icon = location.icon(),
                    title = location.title,
                    description = location.description,
                    selected = settings.downloadLocation == location,
                    onClick = {
                        onSettingsChange(settings.copy(downloadLocation = location))
                    }
                )
            }
            SettingsStorageCard(
                cacheBytes = cacheBytes,
                downloadsBytes = downloadsBytes,
                onClear = {
                    context.clearTemporaryDownloads()
                    context.clearDownloadsCopies()
                    cacheBytes = 0L
                    downloadsBytes = 0L
                }
            )
            SettingSwitchRow(
                icon = Icons.Outlined.CleaningServices,
                title = "Auto-clear after hand-off",
                description = "Remove temporary APKs after handing off to Morphe, and clear old cache files on launch.",
                checked = settings.deleteTemporaryAfterHandoff,
                onCheckedChange = {
                    onSettingsChange(settings.copy(deleteTemporaryAfterHandoff = it))
                }
            )
        }

        SettingsGroupCard("Connection") {
            NetworkPolicy.entries.forEach { policy ->
                SettingsOptionCard(
                    icon = policy.icon(),
                    title = policy.title,
                    description = policy.description,
                    selected = settings.networkPolicy == policy,
                    onClick = {
                        onSettingsChange(settings.copy(networkPolicy = policy))
                    }
                )
            }
            SettingSwitchRow(
                icon = Icons.Outlined.Dns,
                title = "AdGuard DNS",
                description = "Resolve app traffic through AdGuard DNS (blocks ads and trackers on " +
                    "download pages and in the in-app captcha browser). Falls back to the " +
                    "system resolver whenever it fails.",
                checked = settings.adGuardDns,
                onCheckedChange = {
                    onSettingsChange(settings.copy(adGuardDns = it))
                }
            )
        }

        SettingsGroupCard("Security") {
            SettingSwitchRow(
                icon = Icons.Outlined.Shield,
                title = "VirusTotal scanning",
                description = "Scan downloaded files with VirusTotal before returning them to Morphe.",
                checked = settings.virusTotalEnabled,
                onCheckedChange = {
                    onSettingsChange(settings.copy(virusTotalEnabled = it))
                }
            )
            if (settings.virusTotalEnabled) {
                VirusTotalScanMode.entries.forEach { mode ->
                    SettingsOptionCard(
                        icon = when (mode) {
                            VirusTotalScanMode.NEVER -> Icons.Outlined.Block
                            VirusTotalScanMode.ASK -> Icons.Outlined.HelpOutline
                            VirusTotalScanMode.ALWAYS -> Icons.Outlined.CheckCircle
                        },
                        title = mode.title,
                        description = mode.description,
                        selected = settings.virusTotalScanMode == mode,
                        onClick = {
                            onSettingsChange(settings.copy(virusTotalScanMode = mode))
                        }
                    )
                }
                SettingTextFieldRow(
                    icon = Icons.Outlined.Key,
                    title = "API key",
                    description = "Get your free key at virustotal.com",
                    value = settings.virusTotalApiKey,
                    onValueChange = {
                        onSettingsChange(settings.copy(virusTotalApiKey = it))
                    }
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(HelperDefaults.CompactCornerRadius))
                        .clickable {
                            val open = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://docs.virustotal.com/docs/please-give-me-an-api-key")
                            ).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
                            runCatching { context.startActivity(open) }
                                .onFailure {
                                    Toast.makeText(
                                        context,
                                        "No browser available",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        }
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "How to get a free API key",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.Outlined.OpenInNew,
                        contentDescription = "Open VirusTotal API key guide",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                }
                if (settings.virusTotalApiKey.isNotBlank()) {
                    VirusTotalQuotaCard(apiKey = settings.virusTotalApiKey)
                }
            }
        }

        SettingsGroupCard("Appearance") {
            ThemeMode.entries.forEach { mode ->
                SettingsOptionCard(
                    icon = mode.icon(),
                    title = mode.title,
                    description = mode.description,
                    selected = settings.themeMode == mode,
                    onClick = {
                        onSettingsChange(settings.copy(themeMode = mode))
                    }
                )
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                SettingSwitchRow(
                    icon = Icons.Outlined.Palette,
                    title = "Material You colors",
                    description = "Tint the app from your wallpaper instead of the default blue accent.",
                    checked = settings.dynamicColors,
                    onCheckedChange = {
                        onSettingsChange(settings.copy(dynamicColors = it))
                    }
                )
            }
        }

        SettingsGroupCard("Sources") {
            // Preferred source: one compact row + dropdown instead of a long
            // radio list, so choosing the default doesn't dominate the screen.
            Box {
                SettingsDropdownCard(
                    icon = Icons.Outlined.Star,
                    title = "Default source",
                    value = settings.preferredSource?.label
                        ?: "Automatic (first enabled source)",
                    onClick = { defaultSourceExpanded = true }
                )
                DropdownMenu(
                    expanded = defaultSourceExpanded,
                    onDismissRequest = { defaultSourceExpanded = false },
                    modifier = Modifier.fillMaxWidth(0.85f)
                ) {
                    DropdownMenuItem(
                        text = { Text("Automatic (first enabled source)") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.AutoAwesome,
                                contentDescription = null
                            )
                        },
                        trailingIcon = {
                            if (settings.preferredSource == null) {
                                Icon(
                                    imageVector = Icons.Outlined.Check,
                                    contentDescription = null
                                )
                            }
                        },
                        onClick = {
                            onSettingsChange(settings.copy(preferredSource = null))
                            defaultSourceExpanded = false
                        }
                    )
                    sourceCategories.forEach { (title, catSources) ->
                        val visibleSources = catSources.filter { src ->
                            val disabled = settings.disabledSources
                            src !in disabled ||
                                (disabled.size >= DownloadSource.entries.size && src == DownloadSource.PLAY)
                        }
                        if (visibleSources.isNotEmpty()) {
                            SourceMenuHeader(title)
                            visibleSources.forEach { src ->
                                SourceMenuItem(
                                    source = src,
                                    selected = settings.preferredSource == src,
                                    onClick = {
                                        onSettingsChange(settings.copy(preferredSource = src))
                                        defaultSourceExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
            DownloadSource.entries.forEach { source ->
                SourceToggleRow(
                    source = source,
                    enabled = source !in settings.disabledSources,
                    onToggle = { on ->
                        onSettingsChange(
                            settings.copy(
                                disabledSources = if (on) {
                                    settings.disabledSources - source
                                } else {
                                    settings.disabledSources + source
                                }
                            )
                        )
                    }
                )
            }
        }

        SettingsGroupCard("Fast Mode") {
            SettingSwitchRow(
                icon = Icons.Outlined.Bolt,
                title = "Fast Mode",
                description = "Auto-fetch a version across sources and return it to Morphe automatically. " +
                    "Format differences are allowed.",
                checked = settings.fastMode,
                onCheckedChange = {
                    onSettingsChange(settings.copy(fastMode = it))
                }
            )
            if (settings.fastMode) {
                Text(
                    text = "Which version to fetch",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 4.dp)
                )
                FastModePolicy.entries.forEach { policy ->
                    SettingsOptionCard(
                        icon = when (policy) {
                            FastModePolicy.REQUESTED -> Icons.Outlined.Tune
                            FastModePolicy.LATEST -> Icons.Outlined.TrendingUp
                            FastModePolicy.ALWAYS_ASK -> Icons.Outlined.HelpOutline
                        },
                        title = policy.title,
                        description = policy.description,
                        selected = settings.fastModePolicy == policy,
                        onClick = {
                            onSettingsChange(settings.copy(fastModePolicy = policy))
                        }
                    )
                }
            }
        }

        SettingsGroupCard("Logging") {
            SettingSwitchRow(
                icon = Icons.Outlined.BugReport,
                title = "Log to Logcat",
                description = "Write request, result, and source HTTP details to the system log (adb logcat) for debugging.",
                checked = settings.logcatLogging,
                onCheckedChange = {
                    onSettingsChange(settings.copy(logcatLogging = it))
                }
            )
        }
    }
}

@Composable
private fun SettingsGroupCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    // No card background: the group is just a section title followed by its
    // individual option rows, which carry their own card styling.
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(HelperDefaults.ItemSpacing)
    ) {
        SectionTitle(title)
        content()
    }
}

private fun DownloadLocation.icon(): ImageVector = when (this) {
    DownloadLocation.TEMPORARY -> Icons.Outlined.SdStorage
    DownloadLocation.DOWNLOADS -> Icons.Outlined.SaveAlt
}

private fun NetworkPolicy.icon(): ImageVector = when (this) {
    NetworkPolicy.WIFI_ONLY -> Icons.Outlined.Wifi
    NetworkPolicy.MOBILE_DATA_ONLY -> Icons.Outlined.SignalCellularAlt
    NetworkPolicy.WIFI_AND_MOBILE -> Icons.Outlined.NetworkCheck
}

private fun ThemeMode.icon(): ImageVector = when (this) {
    ThemeMode.SYSTEM -> Icons.Outlined.Smartphone
    ThemeMode.DARK -> Icons.Outlined.DarkMode
    ThemeMode.LIGHT -> Icons.Outlined.LightMode
}

@Composable
private fun SettingsOptionCard(
    icon: ImageVector,
    title: String,
    description: String,
    selected: Boolean,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(HelperDefaults.CardCornerRadius)
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .clickable(enabled = enabled, onClick = onClick),
        shape = shape,
        // Matches the home screen's source cards: dark fill + hairline border,
        // with the selected option getting a primary tint + border + radio dot.
        color = if (selected) {
            colors.primary.copy(alpha = 0.16f)
        } else {
            sourceCardFill()
        },
        contentColor = colors.onSurface,
        border = BorderStroke(
            width = if (selected) 1.5.dp else 1.dp,
            color = if (selected) {
                colors.primary
            } else {
                sourceCardBorder()
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (selected) colors.primary else colors.onSurfaceVariant,
                modifier = Modifier.size(22.dp)
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    title,
                    fontWeight = FontWeight.Bold,
                    color = if (selected) colors.primary else colors.onSurface
                )
                Text(
                    description,
                    color = if (selected) {
                        colors.primary.copy(alpha = 0.78f)
                    } else {
                        colors.onSurfaceVariant
                    },
                    style = MaterialTheme.typography.bodySmall
                )
            }
            RadioDot(selected = selected)
        }
    }
}

/**
 * Compact dropdown trigger in the same card language as [SettingsOptionCard],
 * but showing the current value on the right and a chevron instead of a radio
 * dot. Tap opens the anchored [DropdownMenu].
 */
@Composable
private fun SettingsDropdownCard(
    icon: ImageVector,
    title: String,
    value: String,
    onClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(HelperDefaults.CardCornerRadius)
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .clickable(onClick = onClick),
        shape = shape,
        color = sourceCardFill(),
        contentColor = colors.onSurface,
        border = BorderStroke(1.dp, sourceCardBorder())
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = colors.onSurfaceVariant,
                modifier = Modifier.size(22.dp)
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    title,
                    fontWeight = FontWeight.Bold,
                    color = colors.onSurface
                )
                Text(
                    value,
                    color = colors.primary,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Icon(
                imageVector = Icons.Outlined.ArrowDropDown,
                contentDescription = "Change default source",
                tint = colors.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SettingsStorageCard(
    cacheBytes: Long,
    downloadsBytes: Long,
    onClear: () -> Unit
) {
    val shape = RoundedCornerShape(HelperDefaults.CardCornerRadius)
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = shape,
        color = sourceCardFill(),
        border = BorderStroke(1.dp, sourceCardBorder())
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.DeleteOutline,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(22.dp)
                )
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text("Clear storage, cache & downloads", fontWeight = FontWeight.Bold)
                    Text(
                        text = "Removes both the cache copies and the visible Downloads copies.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                HelperOutlinedButton(
                    text = "Clear",
                    onClick = onClear,
                    modifier = Modifier.widthIn(min = 96.dp)
                )
            }
            androidx.compose.material3.HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)
            )
            StorageInfoLine(
                icon = Icons.Outlined.SdStorage,
                label = "Helper cache",
                sizeBytes = cacheBytes
            )
            StorageInfoLine(
                icon = Icons.Outlined.FolderOpen,
                label = "Downloads copy",
                sizeBytes = downloadsBytes
            )
        }
    }
}

@Composable
private fun StorageInfoLine(
    icon: ImageVector,
    label: String,
    sizeBytes: Long
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = sizeBytes.formatBytes(),
            fontWeight = FontWeight.Bold,
            color = if (sizeBytes > 0L) {
                MaterialTheme.colorScheme.onSurface
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            },
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

private fun Long.formatBytes(): String {
    if (this <= 0L) return "0 B"
    val units = listOf("B", "KB", "MB", "GB")
    var value = toDouble()
    var unit = 0
    while (value >= 1024.0 && unit < units.lastIndex) {
        value /= 1024.0
        unit++
    }
    return if (unit == 0) "${toLong()} B" else String.format(Locale.US, "%.1f %s", value, units[unit])
}

@Composable
private fun SettingSwitchRow(
    icon: ImageVector,
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(HelperDefaults.CardCornerRadius)
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = shape,
        // Like the option cards: the switch card tints when the toggle is on.
        color = if (checked) colors.primary.copy(alpha = 0.16f) else sourceCardFill(),
        border = BorderStroke(
            width = if (checked) 1.5.dp else 1.dp,
            color = if (checked) colors.primary else sourceCardBorder()
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (checked) colors.primary else colors.onSurfaceVariant,
                modifier = Modifier.size(22.dp)
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    title,
                    fontWeight = FontWeight.Bold,
                    color = if (checked) colors.primary else colors.onSurface
                )
                Text(
                    description,
                    color = colors.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        }
    }
}

@Composable
private fun SettingTextFieldRow(
    icon: ImageVector,
    title: String,
    description: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(HelperDefaults.CardCornerRadius)
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = shape,
        color = sourceCardFill(),
        border = BorderStroke(1.dp, sourceCardBorder())
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = colors.onSurfaceVariant,
                    modifier = Modifier.size(22.dp)
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        title,
                        fontWeight = FontWeight.Bold,
                        color = colors.onSurface
                    )
                    Text(
                        description,
                        color = colors.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    color = colors.onSurface
                ),
                placeholder = {
                    Text("Enter API key", color = colors.onSurfaceVariant.copy(alpha = 0.5f))
                }
            )
        }
    }
}

/**
 * Detailed VirusTotal quota card shared by the Settings screen and the
 * scan-flow screens. Shows the live per-minute bucket (tracked client-side
 * against the 4/min free tier) plus the hourly/daily/monthly buckets reported
 * by the API. Returns nothing when no API key is set.
 */
@Composable
private fun VirusTotalQuotaCard(apiKey: String) {
    if (apiKey.isBlank()) return
    val colors = MaterialTheme.colorScheme
    var quota by remember { mutableStateOf<VirusTotalScanner.QuotaUsage?>(null) }
    var loading by remember { mutableStateOf(false) }
    var failed by remember { mutableStateOf(false) }
    var minuteUsed by remember { mutableIntStateOf(0) }
    var rollingUsed by remember { mutableIntStateOf(0) }
    // Seconds until the wall-clock minute boundary ticks over and the per-minute
    // count resets to 0. Ceiled so it reads 60 right after a rollover.
    var secsToRollover by remember { mutableIntStateOf(60) }
    val scope = rememberCoroutineScope()
    val load: () -> Unit = {
        scope.launch {
            loading = true
            failed = false
            quota = withContext(Dispatchers.IO) { VirusTotalScanner.fetchQuotaUsage(apiKey) }
            if (quota == null) failed = true
            loading = false
        }
    }
    LaunchedEffect(apiKey) {
        load()
        // The per-minute count is tracked locally in the rate limiter, so tick
        // it once a second to keep the bar honest while a scan is running.
        while (true) {
            minuteUsed = VirusTotalScanner.rateLimiter.callsInCurrentMinute()
            rollingUsed = VirusTotalScanner.rateLimiter.callsInLastMinute()
            secsToRollover =
                ((60_000L - System.currentTimeMillis() % 60_000L + 999L) / 1000L).toInt()
            delay(1000)
        }
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(HelperDefaults.CardCornerRadius),
        color = sourceCardFill(),
        border = BorderStroke(1.dp, sourceCardBorder())
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Shield,
                    contentDescription = null,
                    tint = colors.onSurfaceVariant,
                    modifier = Modifier.size(22.dp)
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "VirusTotal quota",
                        fontWeight = FontWeight.Bold,
                        color = colors.onSurface
                    )
                }
                if (loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Outlined.Refresh,
                        contentDescription = "Refresh quota",
                        tint = colors.primary,
                        modifier = Modifier
                            .size(20.dp)
                            .clip(RoundedCornerShape(50))
                            .clickable { load() }
                            .padding(2.dp)
                    )
                }
            }
            // Per minute is always available locally, even while the API quota
            // is still loading. The wall-clock minute count resets at each
            // minute boundary; the rolling 60s count is what the API actually
            // enforces, so show both and flash when either nears the cap.
            QuotaBar(
                "Per minute",
                minuteUsed,
                VirusTotalScanner.MINUTE_LOOKUP_LIMIT,
                flash = maxOf(minuteUsed, rollingUsed) >= VirusTotalScanner.MINUTE_LOOKUP_LIMIT - 1,
                caption = "rolling 60s: $rollingUsed / ${VirusTotalScanner.MINUTE_LOOKUP_LIMIT} · resets in ${secsToRollover}s"
            )
            val current = quota
            when {
                current != null -> {
                    QuotaBar("Per hour", current.hourlyUsed, current.hourlyAllowed)
                    QuotaBar("Per day", current.dailyUsed, current.dailyAllowed)
                    QuotaBar("Per month", current.monthlyUsed, current.monthlyAllowed)
                }
                failed -> Text(
                    text = "Couldn't load quota — check your API key.",
                    color = colors.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun QuotaBar(
    label: String,
    used: Int,
    allowed: Int,
    // Pulsing amber flash used for the per-minute bar when it nears the cap.
    flash: Boolean = false,
    // Optional second line under the bar, e.g. the rolling 60s count next to
    // the wall-clock minute count.
    caption: String? = null
) {
    val colors = MaterialTheme.colorScheme
    val ratio = if (allowed > 0) used.toFloat() / allowed else 0f
    val baseColor = when {
        ratio >= 0.9f -> colors.error
        ratio >= 0.7f -> Color(0xFFE0A030)
        else -> colors.primary
    }
    // When near the cap, pulse the bar between amber and a dim amber so the
    // mid-scan warning is visible even without looking at the number.
    val barColor = if (flash) {
        val transition = rememberInfiniteTransition(label = "quota-flash-$label")
        val amber = Color(0xFFE0A030)
        amber.copy(
            alpha = transition.animateFloat(
                initialValue = 1f,
                targetValue = 0.3f,
                animationSpec = infiniteRepeatable(
                    animation = tween(600),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "quota-alpha-$label"
            ).value
        )
    } else {
        baseColor
    }
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = colors.onSurfaceVariant,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "$used / $allowed",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = if (ratio >= 0.7f) barColor else colors.onSurface
            )
        }
        LinearProgressIndicator(
            progress = { ratio.coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp)),
            color = barColor,
            trackColor = colors.surfaceVariant
        )
        if (caption != null) {
            Text(
                text = caption,
                style = MaterialTheme.typography.labelSmall,
                color = colors.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SourceToggleRow(
    source: DownloadSource,
    enabled: Boolean,
    onToggle: (Boolean) -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(HelperDefaults.CardCornerRadius)
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = shape,
        color = sourceCardFill(),
        border = BorderStroke(1.dp, sourceCardBorder())
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SourceAvatar(source = source, size = 32.dp)
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(1.dp)
            ) {
                Text(
                    text = source.label,
                    fontWeight = FontWeight.Bold,
                    color = if (enabled) colors.onSurface else colors.onSurfaceVariant.copy(alpha = 0.7f)
                )
                Text(
                    text = if (enabled) "Enabled" else "Disabled",
                    color = colors.onSurfaceVariant.copy(alpha = 0.8f),
                    style = MaterialTheme.typography.labelSmall
                )
            }
            Switch(
                checked = enabled,
                onCheckedChange = onToggle
            )
        }
    }
}

@Composable
private fun EmptyLaunchState(
    onOpenMorphe: () -> Unit,
    onFindApps: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(HelperDefaults.ItemSpacing)) {
        InfoCard("Open this helper from Morphe Manager when it asks for an original APK.")
        HelperButton(
            text = "Open Morphe Manager",
            onClick = onOpenMorphe,
            icon = Icons.Outlined.OpenInNew,
            modifier = Modifier.fillMaxWidth()
        )
        HelperOutlinedButton(
            text = "Find New Apps",
            onClick = onFindApps,
            icon = Icons.Outlined.Explore,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/** Which slice of the archive list to show. */
private enum class AppListTab(val label: String, val icon: ImageVector) {
    All("All", Icons.Outlined.ViewList),
    // Icon-only (heart) so the neighbouring labels keep more room.
    Favourites("", Icons.Outlined.FavoriteBorder),
    Installed("Installed", Icons.Outlined.CheckCircle),
    NotInstalled("Not installed", Icons.Outlined.Block)
}

/** How the archive list is ordered. */
private enum class AppSort(val label: String, val icon: ImageVector, val rotation: Float = 0f) {
    AZ("A–Z", Icons.Outlined.SortByAlpha),
    // Same glyph flipped 180° so it reads descending (mirror of A–Z).
    ZA("Z–A", Icons.Outlined.SortByAlpha, 180f),
    Sources("Sources", Icons.Outlined.Extension)
}

/** Compact icon+label pill used for the archive tabs and sort options. */
@Composable
private fun CompactPill(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    iconRotation: Float = 0f
) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(50)
    Surface(
        modifier = Modifier.clip(shape).clickable(onClick = onClick),
        shape = shape,
        color = if (selected) colors.primary.copy(alpha = 0.16f) else sourceCardFill(),
        contentColor = colors.onSurface,
        border = BorderStroke(
            width = if (selected) 1.5.dp else 1.dp,
            color = if (selected) colors.primary else sourceCardBorder()
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label.ifBlank { null },
                tint = if (selected) colors.primary else colors.onSurfaceVariant,
                modifier = Modifier
                    .size(14.dp)
                    .rotate(iconRotation)
            )
            if (label.isNotBlank()) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                    maxLines = 1
                )
            }
        }
    }
}

/**
 * Persisted favourites for the "Find New Apps" browser. Survives app
 * launches so a pinned app stays pinned even as the live index grows.
 */
private object MorpheFavourites {
    private const val PREFS = "morphe_favourites"
    private const val KEY = "packages"

    fun load(context: Context): Set<String> =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getStringSet(KEY, emptySet())
            .orEmpty()

    /** Toggles [packageName] and returns the new set. */
    fun toggle(context: Context, packageName: String): Set<String> {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val current = prefs.getStringSet(KEY, emptySet()).orEmpty().toMutableSet()
        if (!current.add(packageName)) current.remove(packageName)
        prefs.edit().putStringSet(KEY, current).apply()
        return current
    }
}

/** Sticky disclaimer pinned above the archive list. */
@Composable
private fun AppDisclaimerBanner() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(HelperDefaults.CompactCornerRadius),
        color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.22f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.35f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Outlined.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = "Patch index maintained by the community. Use at your own risk. " +
                    "Community bundles are maintained by their respective authors and are not " +
                    "individually verified. Morphe and the developer of this app are not " +
                    "responsible for third-party patches.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * "Find New Apps" browser: fetches the Morphe archive index live on every
 * open (never cached), offers a searchable list of all patched apps, and a
 * per-app detail view with its versions, patches, and source repos.
 */
@Composable
private fun AppBrowserScreen(
    onBack: () -> Unit
) {
    var apps by remember { mutableStateOf<List<ArchiveApp>?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    var query by remember { mutableStateOf("") }
    var selected by remember { mutableStateOf<ArchiveApp?>(null) }
    var loadKey by remember { mutableIntStateOf(0) }
    var tab by remember { mutableStateOf(AppListTab.All) }
    var sort by remember { mutableStateOf(AppSort.AZ) }
    val context = LocalContext.current
    var favourites by remember { mutableStateOf<Set<String>>(emptySet()) }
    var installedPackages by remember { mutableStateOf<Set<String>>(emptySet()) }
    LaunchedEffect(Unit) {
        favourites = MorpheFavourites.load(context)
        installedPackages = withContext(Dispatchers.IO) {
            context.packageManager.getInstalledApplications(0)
                .mapNotNull { it.packageName.takeIf(String::isNotBlank) }
                .toSet()
        }
    }

    LaunchedEffect(loadKey) {
        apps = null
        error = null
        try {
            apps = MorpheArchive.fetchApps().sortedBy { it.name.lowercase(Locale.US) }
        } catch (e: Exception) {
            error = e.message ?: "Failed to load the app index"
        }
    }

    // System back (including the TalkBack back gesture) should first leave the
    // app detail back to the list; only from the list does it close the browser.
    BackHandler(enabled = selected != null) {
        selected = null
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(
                horizontal = HelperDefaults.ContentPadding,
                vertical = HelperDefaults.ContentPadding
            ),
        verticalArrangement = Arrangement.spacedBy(HelperDefaults.ItemSpacing)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = "Find New Apps",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                val current = selected
                Text(
                    text = when {
                        current != null -> current.name
                        apps != null -> "${apps!!.size} apps with patches"
                        else -> "Morphe patch archive"
                    },
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            HelperHeaderIconButton(
                icon = Icons.AutoMirrored.Outlined.ArrowBack,
                contentDescription = "Back",
                onClick = {
                    if (selected != null) selected = null else onBack()
                },
                modifier = Modifier.align(Alignment.CenterStart)
            )
            if (selected == null) {
                HelperHeaderIconButton(
                    icon = Icons.Outlined.Refresh,
                    contentDescription = "Refresh",
                    onClick = { loadKey++ },
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }
        }

        val current = selected
        if (current != null) {
            AppDetailView(
                app = current,
                onBack = { selected = null },
                modifier = Modifier.weight(1f)
            )
        } else {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface
                ),
                placeholder = {
                    Text(
                        "Search apps or packages",
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = null
                    )
                }
            )

            val loaded = apps
            when {
                error != null -> {
                    InfoCard(error!!)
                    HelperButton(
                        text = "Retry",
                        onClick = { loadKey++ },
                        icon = Icons.Outlined.Refresh,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                loaded == null -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(Modifier.width(HelperDefaults.ContentPaddingSmall))
                        Text(
                            "Loading app index…",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                else -> {
                    AppDisclaimerBanner()
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AppListTab.entries.forEach { t ->
                            CompactPill(
                                label = t.label,
                                icon = t.icon,
                                selected = tab == t,
                                onClick = { tab = t }
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
                    ) {
                        AppSort.entries.forEach { s ->
                            CompactPill(
                                label = s.label,
                                icon = s.icon,
                                selected = sort == s,
                                onClick = { sort = s },
                                iconRotation = s.rotation
                            )
                        }
                    }
                    val filtered = loaded
                        .filter { app ->
                            query.isBlank() ||
                                app.name.contains(query, ignoreCase = true) ||
                                app.packageName.contains(query, ignoreCase = true)
                        }
                        .filter { app ->
                            when (tab) {
                                AppListTab.All -> true
                                AppListTab.Favourites -> app.packageName in favourites
                                AppListTab.Installed -> app.packageName in installedPackages
                                AppListTab.NotInstalled -> app.packageName !in installedPackages
                            }
                        }
                        .let { apps ->
                            when (sort) {
                                AppSort.AZ -> apps.sortedBy { it.name.lowercase(Locale.US) }
                                AppSort.ZA -> apps.sortedByDescending { it.name.lowercase(Locale.US) }
                                AppSort.Sources -> apps.sortedByDescending { it.sourceCount }
                            }
                        }
                    if (filtered.isEmpty()) {
                        InfoCard(
                            when (tab) {
                                AppListTab.Favourites ->
                                    "No favourites yet. Tap the heart on any app to pin it here."
                                AppListTab.Installed -> "No patched apps in the index are installed."
                                AppListTab.NotInstalled ->
                                    "Every app in the index is installed on this device."
                                AppListTab.All -> "No apps match \"$query\"."
                            }
                        )
                    } else {
                        val listState = rememberLazyListState()
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                        ) {
                            LazyColumn(
                                state = listState,
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight(),
                                verticalArrangement = Arrangement.spacedBy(HelperDefaults.ItemSpacing)
                            ) {
                                items(filtered, key = { it.packageName }) { app ->
                                    AppBrowserRow(
                                        app = app,
                                        favourite = app.packageName in favourites,
                                        onToggleFavourite = {
                                            favourites = MorpheFavourites.toggle(context, app.packageName)
                                        },
                                        onClick = { selected = app }
                                    )
                                }
                            }
                            LazyListScrollbar(
                                listState = listState,
                                modifier = Modifier.fillMaxHeight()
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AppBrowserRow(
    app: ArchiveApp,
    favourite: Boolean,
    onToggleFavourite: () -> Unit,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(HelperDefaults.CompactCornerRadius))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(HelperDefaults.CompactCornerRadius),
        color = sourceCardFill(),
        border = BorderStroke(1.dp, sourceCardBorder())
    ) {
        Row(
            modifier = Modifier.padding(start = 12.dp, end = 6.dp, top = 10.dp, bottom = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppAvatar(
                packageName = app.packageName,
                initial = app.name.firstOrNull()?.uppercaseChar() ?: '?'
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = app.name,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = app.packageName,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = app.sourceCount.toString(),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = if (app.sourceCount == 1) "source" else "sources",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Icon(
                imageVector = if (favourite) {
                    Icons.Filled.Favorite
                } else {
                    Icons.Outlined.FavoriteBorder
                },
                contentDescription = if (favourite) "Remove from favourites" else "Add to favourites",
                tint = if (favourite) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(50))
                    .clickable(onClick = onToggleFavourite)
                    .padding(6.dp)
            )
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Small async image loader for source avatars: fetches the URL off the main
 * thread and falls back to a letter tile while loading / on failure.
 */
@Composable
private fun AsyncAvatar(
    url: String?,
    fallbackText: String,
    size: Dp = 20.dp,
    cornerRadius: Dp = 6.dp
) {
    var bitmap by remember(url) { mutableStateOf<ImageBitmap?>(null) }
    LaunchedEffect(url) {
        bitmap = if (url == null) {
            null
        } else {
            withContext(Dispatchers.IO) {
                runCatching {
                    val request = Request.Builder()
                        .url(url)
                        .header(
                            "User-Agent",
                            "Mozilla/5.0 (Linux; Android 14) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/125.0 Mobile Safari/537.36"
                        )
                        .build()
                    MorpheArchive.http.newCall(request).execute().use { response ->
                        if (!response.isSuccessful) return@use null
                        response.body?.bytes()?.let { bytes ->
                            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                        }?.asImageBitmap()
                    }
                }.getOrNull()
            }
        }
    }
    if (bitmap != null) {
        Image(
            bitmap = bitmap!!,
            contentDescription = null,
            modifier = Modifier
                .size(size)
                .clip(RoundedCornerShape(cornerRadius))
        )
    } else {
        Box(
            modifier = Modifier
                .size(size)
                .clip(RoundedCornerShape(cornerRadius))
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = fallbackText,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * Opens a Morphe "add-source" deep link. The manager's intent filter for
 * https://morphe.software/add-source has no autoVerify, and on Android 12+
 * unverified https apps are hidden from the implicit resolver (so the browser
 * would win). Pinning the package restores the direct handoff to Morphe.
 */
private fun openAddSource(context: Context, addUrl: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(addUrl))
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    val manager = MORPHE_MANAGER_PACKAGES.firstOrNull { pkg ->
        runCatching {
            context.packageManager.queryIntentActivities(intent.setPackage(pkg), 0).isNotEmpty()
        }.getOrDefault(false)
    }
    if (manager != null) intent.setPackage(manager)
    runCatching { context.startActivity(intent) }
}

@Composable
private fun AppDetailView(
    app: ArchiveApp,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val openUrl: (String) -> Unit = { url ->
        runCatching {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        }
    }
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(HelperDefaults.ItemSpacing)
    ) {
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(HelperDefaults.CardCornerRadius),
                color = sourceCardFill(),
                border = BorderStroke(1.dp, sourceCardBorder())
            ) {
                Row(
                    modifier = Modifier.padding(HelperDefaults.ContentPadding),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AppAvatar(
                        packageName = app.packageName,
                        initial = app.name.firstOrNull()?.uppercaseChar() ?: '?'
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = app.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = app.packageName,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = buildString {
                                append("${app.sourceCount} ")
                                append(if (app.sourceCount == 1) "source" else "sources")
                                if (app.versions.isNotEmpty()) {
                                    append(" · ${app.versions.size} ")
                                    append(if (app.versions.size == 1) "version" else "versions")
                                }
                            },
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    // Open the Play Store listing, same as the Play source's
                    // action, so users can see the official page for the app.
                    Icon(
                        imageVector = Icons.Outlined.Storefront,
                        contentDescription = "Open in Play Store",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                context.openPlayStoreListing(app.packageName, playStoreUrl(app.packageName))
                            }
                            .padding(10.dp)
                    )
                }
            }
        }
        if (app.versions.isNotEmpty()) {
            item {
                SectionTitle("Versions")
                Text(
                    text = app.versions.joinToString(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        if (app.sources.isEmpty()) {
            item { InfoCard("No patch sources listed for this app.") }
        } else {
            item {
                SectionTitle("Sources")
            }
            items(app.sources, key = { it.repo }) { source ->
                AppSourceCard(
                    source = source,
                    onOpenUrl = openUrl,
                    onAddToMorphe = { openAddSource(context, it) }
                )
            }
        }
    }
}

/**
 * One patch source with its own collapsible patch list (collapsed by default)
 * so patches are clearly attributed to the repo that ships them. The
 * Add-to-Morphe / Open-repo actions stay visible whether or not it's expanded.
 */
@Composable
private fun AppSourceCard(
    source: ArchiveSource,
    onOpenUrl: (String) -> Unit,
    onAddToMorphe: (String) -> Unit
) {
    var expanded by remember(source.repo) { mutableStateOf(false) }
    val colors = MaterialTheme.colorScheme
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(HelperDefaults.CompactCornerRadius),
        color = sourceCardFill(),
        border = BorderStroke(1.dp, sourceCardBorder())
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncAvatar(
                    url = MorpheArchive.avatarUrlFor(source),
                    fallbackText = source.repo.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                    size = 28.dp,
                    cornerRadius = 8.dp
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = source.repo,
                        fontWeight = FontWeight.Bold,
                        color = colors.onSurface,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${source.patches.size} " +
                            (if (source.patches.size == 1) "patch" else "patches"),
                        color = colors.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Icon(
                    imageVector = if (expanded) Icons.Outlined.ExpandLess else Icons.Outlined.ExpandMore,
                    contentDescription = if (expanded) "Collapse patches" else "Expand patches",
                    tint = colors.onSurfaceVariant
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(HelperDefaults.ContentPaddingSmall)
            ) {
                source.addUrl?.takeIf { it.isNotBlank() }?.let { addUrl ->
                    SourceActionButton(
                        text = "Add to Morphe",
                        icon = Icons.Outlined.Add,
                        outlined = false,
                        onClick = { onAddToMorphe(addUrl) },
                        modifier = Modifier.weight(1f)
                    )
                }
                source.webUrl?.takeIf { it.isNotBlank() }?.let { webUrl ->
                    SourceActionButton(
                        text = "Open repo",
                        icon = Icons.Outlined.OpenInNew,
                        outlined = true,
                        onClick = { onOpenUrl(webUrl) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            AnimatedExpand(visible = expanded) {
                Column(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (source.patches.isEmpty()) {
                        Text(
                            "No patches listed for this source.",
                            color = colors.onSurfaceVariant,
                            style = MaterialTheme.typography.bodySmall
                        )
                    } else {
                        val longList = source.patches.size > 5
                        val patchesScroll = rememberScrollState()
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .heightIn(max = if (longList) 240.dp else Dp.Unspecified)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .then(
                                            if (longList) Modifier.verticalScroll(patchesScroll) else Modifier
                                        ),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    source.patches.forEach { patch ->
                                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Outlined.Extension,
                                                    contentDescription = null,
                                                    tint = colors.primary,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Text(
                                                    text = patch.name,
                                                    fontWeight = FontWeight.Bold,
                                                    color = colors.onSurface,
                                                    style = MaterialTheme.typography.bodySmall
                                                )
                                            }
                                            patch.description?.takeIf { it.isNotBlank() }?.let { desc ->
                                                Text(
                                                    text = desc,
                                                    color = colors.onSurfaceVariant,
                                                    style = MaterialTheme.typography.bodySmall
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                            if (longList) {
                                ScrollStateScrollbar(
                                    scrollState = patchesScroll,
                                    modifier = Modifier.fillMaxHeight()
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/** Compact action button used in the app-detail source cards. */
@Composable
private fun SourceActionButton(
    text: String,
    icon: ImageVector,
    outlined: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(HelperDefaults.CompactCornerRadius)
    Surface(
        onClick = onClick,
        modifier = modifier.height(34.dp).clip(shape),
        shape = shape,
        color = if (outlined) Color.Transparent else colors.primary.copy(alpha = 0.22f),
        contentColor = colors.onSurface,
        border = BorderStroke(1.dp, colors.primary.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (outlined) colors.onSurfaceVariant else colors.primary,
                modifier = Modifier.size(14.dp)
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = colors.onSurface,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun HelperBoltButton(
    fastMode: Boolean,
    onClick: () -> Unit
) {
    val primary = MaterialTheme.colorScheme.primary
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.size(HelperDefaults.ButtonHeight),
        shape = RoundedCornerShape(HelperDefaults.ButtonCornerRadius),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (fastMode) primary.copy(alpha = 0.28f) else Color.Transparent,
            contentColor = if (fastMode) {
                primary
            } else {
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
            }
        ),
        border = BorderStroke(
            1.dp,
            if (fastMode) primary.copy(alpha = 0.6f) else primary.copy(alpha = 0.32f)
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        Icon(
            imageVector = Icons.Outlined.Bolt,
            contentDescription = if (fastMode) "Fast Mode on" else "Fast Mode off",
            modifier = Modifier.size(HelperDefaults.IconSizeSmall)
        )
    }
}

/** A previously downloaded file offered for reuse, with its size on disk. */
private data class ReuseOption(
    val entry: DownloadHistoryEntry,
    val sizeBytes: Long
)

@Composable
private fun ReuseOfferDialog(
    options: List<ReuseOption>,
    onUseExisting: (ReuseOption) -> Unit,
    onDownloadNew: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDownloadNew,
        title = {
            Text(
                text = "Use an existing APK?",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(HelperDefaults.ContentPaddingSmall)
            ) {
                Text(
                    text = "A previous download for this exact version is still available. Pick one to return to Morphe without downloading again.",
                    style = MaterialTheme.typography.bodyMedium
                )
                options.forEach { option ->
                    val entry = option.entry
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(HelperDefaults.CompactCornerRadius))
                            .clickable { onUseExisting(option) },
                        shape = RoundedCornerShape(HelperDefaults.CompactCornerRadius),
                        color = sourceCardFill(),
                        border = BorderStroke(1.dp, sourceCardBorder())
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = entry.sourceName,
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                entry.versionName?.let {
                                    Text(
                                        text = "· $it",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                if (option.sizeBytes > 0L) {
                                    Text(
                                        text = "· ${option.sizeBytes.formatBytes()}",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Text(
                                text = entry.fileName,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            entry.scanVerdict?.let { verdict ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                                    modifier = Modifier.padding(top = 3.dp)
                                ) {
                                    Icon(
                                        imageVector = when {
                                            verdict.malicious -> Icons.Outlined.Warning
                                            verdict.failed -> Icons.Outlined.HelpOutline
                                            else -> Icons.Outlined.CheckCircle
                                        },
                                        contentDescription = null,
                                        tint = when {
                                            verdict.malicious -> MaterialTheme.colorScheme.error
                                            verdict.failed -> MaterialTheme.colorScheme.onSurfaceVariant
                                            else -> MaterialTheme.colorScheme.primary
                                        },
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Text(
                                        text = verdict.label,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Medium,
                                        color = when {
                                            verdict.malicious -> MaterialTheme.colorScheme.error
                                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                                        },
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(Modifier.height(4.dp))
                HelperButton(
                    text = "Download new",
                    onClick = onDownloadNew,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {}
    )
}

@Composable
private fun HelperHeaderIconButton(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val primary = MaterialTheme.colorScheme.primary
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.size(HelperDefaults.ButtonHeight),
        shape = RoundedCornerShape(HelperDefaults.ButtonCornerRadius),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
        ),
        border = BorderStroke(1.dp, primary.copy(alpha = 0.32f)),
        contentPadding = PaddingValues(0.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(HelperDefaults.IconSizeSmall)
        )
    }
}

@Composable
private fun HelperThemeButton(
    dark: Boolean,
    onToggle: () -> Unit
) {
    val primary = MaterialTheme.colorScheme.primary
    OutlinedButton(
        onClick = onToggle,
        modifier = Modifier.size(HelperDefaults.ButtonHeight),
        shape = RoundedCornerShape(HelperDefaults.ButtonCornerRadius),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
        ),
        border = BorderStroke(1.dp, primary.copy(alpha = 0.32f)),
        contentPadding = PaddingValues(0.dp)
    ) {
        Icon(
            imageVector = if (dark) Icons.Outlined.LightMode else Icons.Outlined.DarkMode,
            contentDescription = if (dark) "Switch to light theme" else "Switch to dark theme",
            modifier = Modifier.size(HelperDefaults.IconSizeSmall)
        )
    }
}

@Composable
private fun AppInfoCard(
    request: HelperRequest,
    onFormatSelected: (String) -> Unit
) {
    HelperCard(cornerRadius = HelperDefaults.SectionCornerRadius) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(HelperDefaults.ContentPadding),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AppInfoHeader(request, onFormatSelected)
        }
    }
}

@Composable
private fun AppInfoHeader(
    request: HelperRequest,
    onFormatSelected: (String) -> Unit
) {
    val context = LocalContext.current
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? android.content.ClipboardManager
    var copied by remember(request.packageName) { mutableStateOf(false) }
    var installed by remember(request.packageName) { mutableStateOf(false) }
    LaunchedEffect(request.packageName) {
        installed = withContext(Dispatchers.IO) {
            context.isPackageInstalled(request.packageName)
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppAvatar(
                packageName = request.packageName,
                initial = request.appName.firstOrNull()?.uppercaseChar() ?: '?'
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = request.appName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (installed) {
                        Icon(
                            imageVector = Icons.Outlined.CheckCircle,
                            contentDescription = "Installed",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = request.packageName,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Icon(
                        imageVector = Icons.Outlined.ContentCopy,
                        contentDescription = "Copy package",
                        tint = if (copied) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        modifier = Modifier
                            .size(16.dp)
                            .clip(RoundedCornerShape(50))
                            .clickable {
                                clipboard?.setPrimaryClip(
                                    ClipData.newPlainText("package", request.packageName)
                                )
                                copied = true
                            }
                            .padding(2.dp)
                    )
                }
            }
        }

        // Version + Format cards side by side, same height (the build subtext
        // would otherwise make the Version card taller).
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            AppInfoStatCard(
                label = "Version",
                value = request.requestedVersionName ?: "Any",
                subtext = request.versionCodeSummary?.let { "build $it" },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )
            AppInfoFormatCard(
                kinds = request.requestedFileKinds.orderedFileKinds(),
                onSelect = onFormatSelected,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )
        }

        AppInfoArchCard(
            abis = request.availableAbis
        )
    }
}

@Composable
private fun AppInfoStatCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    subtext: String? = null
) {
    val shape = RoundedCornerShape(HelperDefaults.CompactCornerRadius)
    Surface(
        modifier = modifier.clip(shape),
        shape = shape,
        color = sourceCardFill(),
        border = BorderStroke(1.dp, sourceCardBorder())
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = label,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelSmall
            )
            Text(
                text = value,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            subtext?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun AppInfoFormatCard(
    kinds: List<String>,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(HelperDefaults.CompactCornerRadius)
    val current = kinds.firstOrNull() ?: "apk"
    var expanded by rememberSaveable(kinds) { mutableStateOf(false) }

    Surface(
        modifier = modifier.clip(shape),
        shape = shape,
        color = sourceCardFill(),
        border = BorderStroke(1.dp, sourceCardBorder())
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = "Format",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelSmall
                    )
                    Text(
                        text = current.uppercase(Locale.US),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                if (kinds.size > 1) {
                    // Same +N pill + chevron affordance as the Architecture card.
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .clickable { expanded = !expanded }
                            .padding(horizontal = 2.dp, vertical = 2.dp)
                    ) {
                        if (!expanded) {
                            Text(
                                text = "+${kinds.size - 1}",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(
                                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                        Icon(
                            imageVector = if (expanded) {
                                Icons.Outlined.ExpandLess
                            } else {
                                Icons.Outlined.ExpandMore
                            },
                            contentDescription = if (expanded) "Collapse formats" else "Choose format",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
            // Plain conditional (no AnimatedVisibility): this card sits in an
            // equal-height Row(IntrinsicSize.Min), where the expand animation's
            // intrinsic height is measured as collapsed and clips the list.
            if (expanded) {
                androidx.compose.material3.HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)
                )
                kinds.forEach { kind ->
                    val selected = kind == current
                    Text(
                        text = kind.uppercase(Locale.US),
                        color = if (selected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        },
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                onSelect(kind)
                                expanded = false
                            }
                            .padding(vertical = 6.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun AppInfoArchCard(abis: List<String>) {
    val shape = RoundedCornerShape(HelperDefaults.CompactCornerRadius)
    var expanded by rememberSaveable { mutableStateOf(false) }
    val displayAbis = abis.takeIf { it.isNotEmpty() } ?: listOf("Default")

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .clickable(enabled = displayAbis.size > 1) { expanded = !expanded },
        shape = shape,
        color = sourceCardFill(),
        border = BorderStroke(1.dp, sourceCardBorder())
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Architecture",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.weight(1f)
                )
                if (displayAbis.size > 1 && !expanded) {
                    Text(
                        text = "+${displayAbis.size - 1}",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            )
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
                Icon(
                    imageVector = if (expanded) {
                        Icons.Outlined.ExpandLess
                    } else {
                        Icons.Outlined.ExpandMore
                    },
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            }
            AnimatedExpand(visible = expanded) {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    displayAbis.forEach { abi ->
                        HelperChip(text = abi)
                    }
                }
            }
            if (!expanded) {
                Text(
                    text = displayAbis.first(),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun AppAvatar(packageName: String, initial: Char) {
    val context = LocalContext.current
    // Fetch the installed app's real icon; when the app isn't installed use the
    // Android default app icon; the letter tile is the last-resort fallback.
    var iconBitmap by remember(packageName) { mutableStateOf<ImageBitmap?>(null) }
    LaunchedEffect(packageName) {
        iconBitmap = withContext(Dispatchers.IO) {
            val installed = runCatching {
                context.packageManager.getApplicationIcon(packageName)
            }.getOrNull()
            val drawable = installed
                ?: context.packageManager.getDefaultActivityIcon()
            runCatching {
                drawable.toBitmap(width = 176, height = 176).asImageBitmap()
            }.getOrNull()
        }
    }

    if (iconBitmap != null) {
        Image(
            bitmap = iconBitmap!!,
            contentDescription = null,
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
        )
    } else {
        val colors = listOf(
            Color(0xFF1A73E8),
            Color(0xFF4C8DFF),
            Color(0xFFFBBC04),
            Color(0xFFEA4335),
            Color(0xFF4285F4),
            Color(0xFFF25C1B)
        )
        val color = colors[initial.code % colors.size]
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(color.copy(alpha = 0.85f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = initial.toString(),
                color = Color.White,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * Grabbable scrollbar for a [LazyColumn], rendered in a narrow gutter beside
 * the list (never overlapping it). The thumb keeps a minimum grab size and
 * dragging it maps finger travel proportionally across the whole list, so a
 * long list stays navigable. Compose 1.10 dropped the built-in scrollbar API
 * from the foundation artifact, so this is a dependency-free re-implementation.
 */
@Composable
private fun LazyListScrollbar(
    listState: LazyListState,
    modifier: Modifier = Modifier
) {
    val info = listState.layoutInfo
    val total = info.totalItemsCount
    val visible = info.visibleItemsInfo.size
    if (total <= 0 || visible !in 1 until total) return
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    var dragStartY by remember { mutableFloatStateOf(0f) }
    var dragStartIndex by remember { mutableIntStateOf(0) }
    BoxWithConstraints(modifier = modifier.width(20.dp)) {
        val containerPx = with(density) { maxHeight.toPx() }
        val fraction = visible.toFloat() / total.toFloat()
        val thumbPx = (containerPx * fraction).coerceIn(44f, containerPx)
        val travelPx = (containerPx - thumbPx).coerceAtLeast(0f)
        val scrollable = (total - visible).coerceAtLeast(1)
        val pos = listState.firstVisibleItemIndex.toFloat() / scrollable.toFloat()
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(total, visible, travelPx, scrollable) {
                    detectVerticalDragGestures(
                        onDragStart = { offset ->
                            dragStartY = offset.y
                            dragStartIndex = listState.firstVisibleItemIndex
                        },
                        onVerticalDrag = { change, _ ->
                            val delta = change.position.y - dragStartY
                            val target = dragStartIndex +
                                (delta / travelPx * scrollable).toInt()
                            scope.launch {
                                listState.scrollToItem(target.coerceIn(0, total - 1))
                            }
                        }
                    )
                }
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .width(3.dp)
                    .height(with(density) { thumbPx.toDp() })
                    .offset { IntOffset(0, (travelPx * pos).toInt()) }
                    .clip(RoundedCornerShape(2.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.45f))
            )
        }
    }
}

/** Same grabbable indicator for a plain vertical [ScrollState] (patch lists). */
@Composable
private fun ScrollStateScrollbar(
    scrollState: ScrollState,
    modifier: Modifier = Modifier
) {
    val maxPx = scrollState.maxValue
    if (maxPx <= 0) return
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    var dragStartY by remember { mutableFloatStateOf(0f) }
    var dragStartVal by remember { mutableIntStateOf(0) }
    BoxWithConstraints(modifier = modifier.width(20.dp)) {
        val containerPx = with(density) { maxHeight.toPx() }
        val thumbFraction =
            (containerPx / (containerPx + maxPx.toFloat())).coerceIn(0.1f, 1f)
        val thumbPx = (containerPx * thumbFraction).coerceIn(44f, containerPx)
        val travelPx = (containerPx - thumbPx).coerceAtLeast(0f)
        val pos = scrollState.value.toFloat() / maxPx.toFloat()
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(maxPx, travelPx) {
                    detectVerticalDragGestures(
                        onDragStart = { offset ->
                            dragStartY = offset.y
                            dragStartVal = scrollState.value
                        },
                        onVerticalDrag = { change, _ ->
                            val delta = change.position.y - dragStartY
                            val target = dragStartVal + (delta / travelPx * maxPx).toInt()
                            scope.launch {
                                scrollState.scrollTo(target.coerceIn(0, maxPx))
                            }
                        }
                    )
                }
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .width(3.dp)
                    .height(with(density) { thumbPx.toDp() })
                    .offset { IntOffset(0, (travelPx * pos).toInt()) }
                    .clip(RoundedCornerShape(2.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.45f))
            )
        }
    }
}

/** Shared expand/collapse animation for every collapsible section. */
@Composable
private fun AnimatedExpand(
    visible: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = expandVertically(
            animationSpec = tween(220),
            expandFrom = Alignment.Top
        ) + fadeIn(animationSpec = tween(220)),
        exit = shrinkVertically(
            animationSpec = tween(180),
            shrinkTowards = Alignment.Top
        ) + fadeOut(animationSpec = tween(180))
    ) {
        content()
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.SemiBold
    )
}

@Composable
private fun LoadingState() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        CircularProgressIndicator()
    }
}

private data class PrimaryAction(
    val label: String,
    val icon: ImageVector,
    val enabled: Boolean = true,
    val loading: Boolean = false,
    val run: () -> Unit
)

@Composable
private fun SourcePickerFlow(
    request: HelperRequest,
    result: CandidateResult,
    selectedPagerPage: Int,
    onPagerPageChanged: (Int) -> Unit,
    onResolve: (DownloadSource, CandidateOption) -> Unit,
    onDownload: (DownloadCandidate) -> Unit,
    onPickDownloadedFile: (DownloadCandidate) -> Unit,
    onUseInstalledApp: (DownloadCandidate) -> Unit,
    onSolveCaptcha: (DownloadCandidate) -> Unit,
    onVersionHistory: (DownloadSource) -> Unit,
    onDownloadVersion: (DownloadCandidate) -> Unit,
    onRefresh: () -> Unit,
    onCancel: () -> Unit,
    installedPackageRefreshToken: Int,
    onPrimaryActionChanged: (PrimaryAction?) -> Unit
) {
    val groups = result.sourceGroups

    // All sources disabled: show a hint instead of crashing on an empty pager.
    if (groups.isEmpty()) {
        SideEffect { onPrimaryActionChanged(null) }
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(HelperDefaults.ItemSpacing)
        ) {
            InfoCard(
                "All sources are disabled. Enable at least one source in " +
                    "Settings → Sources to resolve or download."
            )
        }
        return
    }

    val initialPage = selectedPagerPage.coerceIn(0, (groups.size - 1).coerceAtLeast(0))
    val pagerState = rememberPagerState(initialPage = initialPage) { groups.size }
    LaunchedEffect(pagerState.currentPage) { onPagerPageChanged(pagerState.currentPage) }
    val scope = rememberCoroutineScope()
    var showHowItWorks by remember { mutableStateOf(false) }
    // The source cards collapse by default so the page stays focused on the
    // selected source's content; the SelectedSourceBar below always shows
    // what's selected.
    var sourcesExpanded by rememberSaveable { mutableStateOf(false) }
    // Version type per source, so switching sources keeps the chosen mode.
    var subTabBySource by remember { mutableStateOf<Map<DownloadSource, SourceSubTab>>(emptyMap()) }

    // Clamp in case a source was disabled while this screen was showing.
    val currentGroup = groups[pagerState.currentPage.coerceIn(0, groups.lastIndex)]
    val currentSubTab = subTabBySource[currentGroup.source]
        ?: defaultSubTab(currentGroup, request)

    val context = LocalContext.current
    val openCandidateLink: (DownloadCandidate) -> Unit = { candidate ->
        if (candidate.source == DownloadSource.PLAY) {
            context.openPlayStoreListing(candidate.packageName, candidate.url)
        } else {
            onSolveCaptcha(candidate)
        }
    }

    val action = remember(currentGroup, currentSubTab) {
        buildPrimaryAction(
            group = currentGroup,
            tab = currentSubTab,
            onResolve = onResolve,
            onDownload = onDownload,
            onVersionHistory = onVersionHistory,
            openLink = openCandidateLink
        )
    }
    SideEffect { onPrimaryActionChanged(action) }

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        SelectedSourceBar(source = currentGroup.source)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(HelperDefaults.ItemSpacing),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Tapping the title toggles the source cards.
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .clickable { sourcesExpanded = !sourcesExpanded }
                        .padding(horizontal = 4.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SectionTitle("Download source")
                    if (!sourcesExpanded) {
                        Text(
                            text = "· ${groups.size}",
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Icon(
                        imageVector = if (sourcesExpanded) {
                            Icons.Outlined.ExpandLess
                        } else {
                            Icons.Outlined.ExpandMore
                        },
                        contentDescription = if (sourcesExpanded) "Collapse sources" else "Expand sources",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(Modifier.weight(1f))
                Text(
                    text = "How it works",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .clickable { showHowItWorks = !showHowItWorks }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
            // Two separate expand blocks: the "How it works" card and the source
            // grid used to share one AnimatedExpand, and when the card toggled while
            // the grid was already expanded, its content stayed at the container's
            // top and the paragraph rendered over the grid (overlapping text).
            // Keeping each in its own block means no block's content height changes
            // mid-animation, so the layout can never go stale.
            AnimatedExpand(visible = showHowItWorks) {
                InfoCard(
                    "Pick a source, then a version type. The helper finds the version, " +
                        "downloads it, validates it against Morphe's request, and returns it. " +
                        "If a source gates the file behind a captcha, tap \"Solve captcha in app\" " +
                        " a real browser opens and any download it produces is captured back."
                )
            }
            Box {
                DropdownMenu(
                    expanded = sourcesExpanded,
                    onDismissRequest = { sourcesExpanded = false },
                    modifier = Modifier.fillMaxWidth(0.85f)
                ) {
                    sourceCategories.forEach { (title, catSources) ->
                        val visibleSources = catSources.filter { src ->
                            groups.any { it.source == src }
                        }
                        if (visibleSources.isNotEmpty()) {
                            SourceMenuHeader(title)
                            visibleSources.forEach { src ->
                                val index = groups.indexOfFirst { it.source == src }
                                if (index >= 0) {
                                    SourceMenuItem(
                                        source = src,
                                        selected = index == pagerState.currentPage,
                                        onClick = {
                                            scope.launch {
                                                if (abs(index - pagerState.currentPage) <= 1) {
                                                    pagerState.animateScrollToPage(index)
                                                } else {
                                                    pagerState.scrollToPage(index)
                                                }
                                            }
                                            sourcesExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        HorizontalPager(
            state = pagerState,
            key = { index -> groups[index].source },
            // Only compose the current page so the pager's height matches the page on
            // screen instead of the tallest neighbor (which left dead space on short
            // pages). Pages are top-aligned so content never floats away from the cards.
            beyondViewportPageCount = 0,
            verticalAlignment = Alignment.Top,
            modifier = Modifier.fillMaxWidth()
        ) { page ->
            val group = groups[page]
            SourcePageContent(
                request = request,
                group = group,
                selectedTab = subTabBySource[group.source] ?: defaultSubTab(group, request),
                onSelectTab = { tab ->
                    subTabBySource = subTabBySource + (group.source to tab)
                },
                onResolve = onResolve,
                onDownload = onDownload,
                onPickDownloadedFile = onPickDownloadedFile,
                onUseInstalledApp = onUseInstalledApp,
                onSolveCaptcha = onSolveCaptcha,
                onVersionHistory = onVersionHistory,
                onDownloadVersion = onDownloadVersion,
                installedPackageRefreshToken = installedPackageRefreshToken
            )
        }
    }
}

private fun subTabsFor(group: SourceCandidateGroup, request: HelperRequest): List<SourceSubTab> =
    buildList {
        if (group.manual.isNotEmpty()) add(SourceSubTab.Manual)
        if (request.hasKnownVersionRequest && group.source.supportsRecommended) {
            add(SourceSubTab.Recommended)
        }
        add(SourceSubTab.Latest)
        if (group.source.supportsHistory) {
            add(SourceSubTab.History)
        }
    }

private fun defaultSubTab(group: SourceCandidateGroup, request: HelperRequest): SourceSubTab =
    subTabsFor(group, request).firstOrNull() ?: SourceSubTab.Latest

private fun buildPrimaryAction(
    group: SourceCandidateGroup,
    tab: SourceSubTab,
    onResolve: (DownloadSource, CandidateOption) -> Unit,
    onDownload: (DownloadCandidate) -> Unit,
    onVersionHistory: (DownloadSource) -> Unit,
    openLink: (DownloadCandidate) -> Unit
): PrimaryAction = when (tab) {
    SourceSubTab.Manual -> {
        val first = group.manual.firstOrNull()
        PrimaryAction(
            label = if (first == null) "No manual link" else "Open source site",
            icon = Icons.Outlined.OpenInBrowser,
            enabled = first != null,
            run = { first?.let(openLink) }
        )
    }

    SourceSubTab.Recommended,
    SourceSubTab.Latest -> {
        val isLatest = tab == SourceSubTab.Latest
        val state = if (isLatest) group.latest else group.recommended
        val option = if (isLatest) CandidateOption.LATEST else CandidateOption.REQUESTED
        val resolveLabel = if (isLatest) "Find latest version" else "Find requested version"
        when (state) {
            ResolveState.Loading -> PrimaryAction(
                label = if (isLatest) "Checking latest..." else "Checking requested version...",
                icon = Icons.Outlined.Search,
                enabled = false,
                loading = true,
                run = {}
            )
            is ResolveState.Done -> {
                val direct = state.candidates.firstOrNull { it.directDownload }
                if (direct != null) {
                    PrimaryAction(
                        label = "Download ${direct.versionDisplay}",
                        icon = Icons.Outlined.Download,
                        run = { onDownload(direct) }
                    )
                } else {
                    PrimaryAction(
                        label = resolveLabel,
                        icon = Icons.Outlined.Search,
                        run = { onResolve(group.source, option) }
                    )
                }
            }
            else -> PrimaryAction(
                label = resolveLabel,
                icon = Icons.Outlined.Search,
                run = { onResolve(group.source, option) }
            )
        }
    }

    SourceSubTab.History -> when (group.history) {
        VersionHistoryState.Idle -> PrimaryAction(
            label = "Load versions",
            icon = Icons.Outlined.History,
            run = { onVersionHistory(group.source) }
        )
        VersionHistoryState.Loading -> PrimaryAction(
            label = "Loading versions...",
            icon = Icons.Outlined.History,
            enabled = false,
            loading = true,
            run = {}
        )
        else -> PrimaryAction(
            label = "Reload versions",
            icon = Icons.Outlined.Refresh,
            run = { onVersionHistory(group.source) }
        )
    }
}

private val sourceCategories: List<Pair<String, List<DownloadSource>>> = listOf(
    "Official" to listOf(
        DownloadSource.PLAY,
        DownloadSource.AURORA
    ),
    "Trusted mirrors" to listOf(
        DownloadSource.APK_MIRROR,
        DownloadSource.UPTODOWN,
        DownloadSource.APK_PURE,
        DownloadSource.APK_COMBO
    ),
    "Other sources" to listOf(
        DownloadSource.APTOIDE,
        DownloadSource.EVOZI,
        DownloadSource.MI9,
        DownloadSource.APK_DOWNLOADER
    )
)

@Composable
private fun SourceGrid(
    groups: List<SourceCandidateGroup>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        sourceCategories.forEach { (title, sources) ->
            val catGroups = sources.mapNotNull { source ->
                groups.firstOrNull { it.source == source }
            }
            if (catGroups.isEmpty()) return@forEach

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SectionTitle(title)
                catGroups.chunked(2).forEach { rowGroups ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        rowGroups.forEach { group ->
                            val index = groups.indexOfFirst { it.source == group.source }
                            SourceCard(
                                group = group,
                                selected = index == selectedIndex,
                                onClick = { onSelect(index) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (rowGroups.size == 1) {
                            Spacer(Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

/** Reusable dropdown item: source brand icon + label + optional check. */
@Composable
private fun SourceMenuItem(
    source: DownloadSource,
    selected: Boolean,
    onClick: () -> Unit
) {
    DropdownMenuItem(
        text = { Text(source.label) },
        leadingIcon = { SourceAvatar(source = source, size = 24.dp) },
        trailingIcon = {
            if (selected) {
                Icon(
                    imageVector = Icons.Outlined.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
        onClick = onClick
    )
}

/** Section header inside a dropdown menu (Official / Trusted mirrors / Other). */
@Composable
private fun SourceMenuHeader(title: String) {
    Text(
        text = title,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

// Tracks the active dark/light and Material You states (themeMode-aware) so
// non-scheme helpers like the source-card fill can adapt with the rest of the
// UI  wallpaper-tinted surfaces when dynamic colors are on.
private var helperDarkTheme by mutableStateOf(true)
private var helperDynamicColors by mutableStateOf(false)

@Composable
private fun sourceCardFill(): Color = when {
    helperDynamicColors -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
    helperDarkTheme -> Color(0xFF1C1E22)
    else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
}

@Composable
private fun sourceCardBorder(): Color = when {
    helperDynamicColors -> MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
    helperDarkTheme -> Color(0xFF2A2D33)
    else -> MaterialTheme.colorScheme.outlineVariant
}

@Composable
private fun SourceCard(
    group: SourceCandidateGroup,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val confirmed = group.source != DownloadSource.PLAY &&
        ((group.latest as? ResolveState.Done)?.candidates?.isNotEmpty() == true ||
            (group.recommended as? ResolveState.Done)?.candidates?.isNotEmpty() == true)

    val shape = RoundedCornerShape(16.dp)
    Surface(
        modifier = modifier
            .clip(shape)
            .clickable(onClick = onClick),
        shape = shape,
        color = sourceCardFill(),
        contentColor = MaterialTheme.colorScheme.onSurface,
        border = BorderStroke(
            width = if (selected) 1.5.dp else 1.dp,
            color = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.8f) else sourceCardBorder()
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SourceAvatar(source = group.source)
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = group.source.label,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (confirmed) {
                    Icon(
                        imageVector = Icons.Outlined.CheckCircle,
                        contentDescription = "Available",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
            RadioDot(selected = selected)
        }
    }
}

private data class SourceBrand(
    val resId: Int
)

private fun DownloadSource.brand(): SourceBrand = when (this) {
    DownloadSource.PLAY -> SourceBrand(resId = R.drawable.ic_src_play)
    DownloadSource.APK_MIRROR -> SourceBrand(resId = R.drawable.ic_src_apkmirror)
    DownloadSource.APK_PURE -> SourceBrand(resId = R.drawable.ic_src_apkpure)
    DownloadSource.APK_COMBO -> SourceBrand(resId = R.drawable.ic_src_apkcombo)
    DownloadSource.UPTODOWN -> SourceBrand(resId = R.drawable.ic_src_uptodown)
    DownloadSource.AURORA -> SourceBrand(resId = R.drawable.ic_src_aurora)
    DownloadSource.APTOIDE -> SourceBrand(resId = R.drawable.ic_src_aptoide)
    DownloadSource.EVOZI -> SourceBrand(resId = R.drawable.ic_src_evozi)
    DownloadSource.MI9 -> SourceBrand(resId = R.drawable.ic_src_mi9)
    DownloadSource.APK_DOWNLOADER -> SourceBrand(resId = R.drawable.ic_src_apkdownloader)
}

@Composable
private fun SourceAvatar(source: DownloadSource, size: Dp = 40.dp) {
    // Official brand logo: square artwork, shown as-is with rounded corners.
    Image(
        painter = painterResource(source.brand().resId),
        contentDescription = null,
        modifier = Modifier
            .size(size)
            .clip(RoundedCornerShape(12.dp))
    )
}

@Composable
private fun RadioDot(selected: Boolean) {
    Icon(
        imageVector = if (selected) {
            Icons.Outlined.RadioButtonChecked
        } else {
            Icons.Outlined.RadioButtonUnchecked
        },
        contentDescription = if (selected) "Selected" else null,
        tint = if (selected) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.55f)
        },
        modifier = Modifier.size(18.dp)
    )
}

@Composable
private fun SelectedSourceBar(source: DownloadSource) {
    val shape = RoundedCornerShape(16.dp)
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape),
        shape = shape,
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.10f),
        contentColor = MaterialTheme.colorScheme.onSurface,
        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.65f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SourceAvatar(source = source, size = 32.dp)
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(1.dp)
            ) {
                Text(
                    text = "Selected source",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelSmall
                )
                Text(
                    text = source.label,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Icon(
                imageVector = Icons.Outlined.CheckCircle,
                contentDescription = "Selected",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
private fun SourceBottomBar(
    action: PrimaryAction,
    onRefresh: () -> Unit,
    onCancel: () -> Unit
) {
    Column {
        androidx.compose.material3.HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = HelperDefaults.ContentPadding, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SourceSquareButton(
                icon = Icons.Outlined.Refresh,
                contentDescription = "Refresh",
                onClick = onRefresh
            )
            SourceSquareButton(
                icon = Icons.Outlined.Close,
                contentDescription = "Cancel",
                onClick = onCancel
            )
            Button(
                onClick = action.run,
                enabled = action.enabled,
                modifier = Modifier
                    .weight(1f)
                    .height(HelperDefaults.ButtonHeight),
                shape = RoundedCornerShape(HelperDefaults.ButtonCornerRadius),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.28f),
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.48f)),
                contentPadding = PaddingValues(horizontal = HelperDefaults.ContentPadding)
            ) {
                if (action.loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = action.icon,
                        contentDescription = null,
                        modifier = Modifier.size(HelperDefaults.IconSizeSmall)
                    )
                }
                Spacer(Modifier.width(HelperDefaults.ContentPaddingSmall))
                Text(action.label, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

@Composable
private fun SourceSquareButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit
) {
    val primary = MaterialTheme.colorScheme.primary
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.size(HelperDefaults.ButtonHeight),
        shape = RoundedCornerShape(HelperDefaults.ButtonCornerRadius),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
        ),
        border = BorderStroke(1.dp, primary.copy(alpha = 0.32f)),
        contentPadding = PaddingValues(0.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(HelperDefaults.IconSizeSmall)
        )
    }
}

private enum class SourceSubTab(
    val label: String,
    val icon: ImageVector
) {
    Manual("Manual", Icons.Outlined.Tune),
    Recommended("Recommended", Icons.Outlined.CheckCircle),
    Latest("Latest", Icons.Outlined.Star),
    History("History", Icons.Outlined.History)
}

/** Sources that can resolve the exact requested version from their own data. */
private val DownloadSource.supportsRecommended: Boolean
    get() = when (this) {
        // Mi9 exposes a real version-history page that the in-app captcha
        // browser opens; its recommended candidate routes there. APK
        // Downloader stays manual-only (Cloudflare-gated with no version list).
        DownloadSource.APK_DOWNLOADER,
        DownloadSource.AURORA,
        DownloadSource.PLAY -> false
        else -> true
    }

/** Sources that expose a version history list. */
private val DownloadSource.supportsHistory: Boolean
    get() = when (this) {
        // Evozi has an old-versions page but no history list; APK Downloader
        // is Cloudflare-gated with no version list. Mi9's history tab offers a
        // "browse the version history in the in-app browser" row. Aurora/Play
        // never offer originals.
        DownloadSource.EVOZI,
        DownloadSource.APK_DOWNLOADER,
        DownloadSource.AURORA,
        DownloadSource.PLAY -> false
        else -> true
    }

@Composable
private fun SourcePageContent(
    request: HelperRequest,
    group: SourceCandidateGroup,
    selectedTab: SourceSubTab,
    onSelectTab: (SourceSubTab) -> Unit,
    onResolve: (DownloadSource, CandidateOption) -> Unit,
    onDownload: (DownloadCandidate) -> Unit,
    onPickDownloadedFile: (DownloadCandidate) -> Unit,
    onUseInstalledApp: (DownloadCandidate) -> Unit,
    onSolveCaptcha: (DownloadCandidate) -> Unit,
    onVersionHistory: (DownloadSource) -> Unit,
    onDownloadVersion: (DownloadCandidate) -> Unit,
    installedPackageRefreshToken: Int
) {
    val subTabs = remember(group, request) { subTabsFor(group, request) }
    val safeTab = subTabs.firstOrNull { it == selectedTab } ?: subTabs.firstOrNull()
        ?: SourceSubTab.Latest

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            SectionTitle("Version type")
            VersionTypeRow(
                tabs = subTabs,
                selected = safeTab,
                onSelect = onSelectTab
            )
        }

        AboutModeCard(tab = safeTab)

        when (safeTab) {
            SourceSubTab.Manual -> {
                group.manual.forEach { candidate ->
                    CandidateCard(
                        request = request,
                        candidate = candidate,
                        onDownload = { onDownload(candidate) },
                        onPickDownloadedFile = { onPickDownloadedFile(candidate) },
                        onUseInstalledApp = { onUseInstalledApp(candidate) },
                        onSolveCaptcha = { onSolveCaptcha(candidate) },
                        installedPackageRefreshToken = installedPackageRefreshToken
                    )
                }
            }

            SourceSubTab.Recommended -> {
                CandidateResolveSection(
                    request = request,
                    state = group.recommended,
                    emptyText = "Requested version was not found on this source. Use Manual mode for this source instead.",
                    onDownload = onDownload,
                    onPickDownloadedFile = onPickDownloadedFile,
                    onUseInstalledApp = onUseInstalledApp,
                    onSolveCaptcha = onSolveCaptcha,
                    installedPackageRefreshToken = installedPackageRefreshToken
                )
            }

            SourceSubTab.Latest -> {
                when (group.source) {
                    DownloadSource.AURORA -> {
                        InfoCard("Aurora only provides the latest Play Store version. Use Manual mode if you need a specific version.")
                    }
                    DownloadSource.PLAY -> {
                        InfoCard("Play opens the official Play Store listing for this app. Use Manual mode if you need a specific version.")
                    }
                    else -> Unit
                }
                CandidateResolveSection(
                    request = request,
                    state = group.latest,
                    emptyText = "Latest version was not found on this source. Use Manual mode for this source instead.",
                    onDownload = onDownload,
                    onPickDownloadedFile = onPickDownloadedFile,
                    onUseInstalledApp = onUseInstalledApp,
                    onSolveCaptcha = onSolveCaptcha,
                    installedPackageRefreshToken = installedPackageRefreshToken
                )
            }

            SourceSubTab.History -> {
                VersionHistorySection(
                    state = group.history,
                    onDownloadVersion = onDownloadVersion,
                    onSolveCaptcha = onSolveCaptcha
                )
            }
        }
    }
}

@Composable
private fun VersionTypeRow(
    tabs: List<SourceSubTab>,
    selected: SourceSubTab,
    onSelect: (SourceSubTab) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        tabs.forEach { tab ->
            VersionTypeCard(
                tab = tab,
                selected = tab == selected,
                onClick = { onSelect(tab) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun VersionTypeCard(
    tab: SourceSubTab,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconTabCard(
        label = tab.label,
        icon = tab.icon,
        selected = selected,
        onClick = onClick,
        modifier = modifier
    )
}

/** Icon + label card used for tab rows (home version types and settings tabs). */
@Composable
private fun IconTabCard(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(16.dp)
    Surface(
        modifier = modifier
            .clip(shape)
            .clickable(onClick = onClick),
        shape = shape,
        color = if (selected) {
            MaterialTheme.colorScheme.primary.copy(alpha = 0.16f)
        } else {
            sourceCardFill()
        },
        contentColor = MaterialTheme.colorScheme.onSurface,
        border = BorderStroke(
            width = if (selected) 1.5.dp else 1.dp,
            color = if (selected) {
                MaterialTheme.colorScheme.primary
            } else {
                sourceCardBorder()
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (selected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = label,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                color = if (selected) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }
    }
}

private enum class SettingsTab(
    val label: String,
    val icon: ImageVector
) {
    Settings("Settings", Icons.Outlined.Tune),
    Health("Health", Icons.Outlined.VerifiedUser),
    History("History", Icons.Outlined.History),
    Logs("Logs", Icons.Outlined.BugReport)
}

@Composable
private fun SettingsTabRow(
    selected: SettingsTab,
    onSelect: (SettingsTab) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        SettingsTab.entries.forEach { tab ->
            IconTabCard(
                label = tab.label,
                icon = tab.icon,
                selected = tab == selected,
                onClick = { onSelect(tab) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun AboutModeCard(tab: SourceSubTab) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val title = when (tab) {
        SourceSubTab.Manual -> "About manual mode"
        SourceSubTab.Recommended -> "About recommended mode"
        SourceSubTab.Latest -> "About latest mode"
        SourceSubTab.History -> "About version history"
    }
    val description = when (tab) {
        SourceSubTab.Manual ->
            "Opens the source's website so you can download the file yourself, " +
                "then select it to return it to Morphe."
        SourceSubTab.Recommended ->
            "Finds the exact version Morphe requested from the selected source " +
                "and downloads it."
        SourceSubTab.Latest ->
            "Finds the newest compatible version from the selected source and downloads it."
        SourceSubTab.History ->
            "Lists every version this source offers  pick any of them and download it."
    }
    HelperCard(cornerRadius = HelperDefaults.CompactCornerRadius) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(HelperDefaults.CompactCornerRadius))
                    .clickable { expanded = !expanded }
                    .padding(horizontal = HelperDefaults.ContentPadding, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (expanded) {
                        Icons.Outlined.ExpandLess
                    } else {
                        Icons.Outlined.ExpandMore
                    },
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            AnimatedExpand(visible = expanded) {
                androidx.compose.material3.HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)
                )
                Text(
                    text = description,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(
                        horizontal = HelperDefaults.ContentPadding,
                        vertical = 14.dp
                    )
                )
            }
        }
    }
}

@Composable
private fun VersionHistorySection(
    state: VersionHistoryState,
    onDownloadVersion: (DownloadCandidate) -> Unit,
    onSolveCaptcha: (DownloadCandidate) -> Unit
) {
    when (state) {
        // Idle and Loading are driven entirely by the bottom-bar primary
        // action button; the content area shows results only.
        VersionHistoryState.Idle,
        VersionHistoryState.Loading -> Unit

        is VersionHistoryState.Error -> {
            InfoCard(state.message)
        }

        is VersionHistoryState.Done -> {
            if (state.candidates.isEmpty()) {
                InfoCard("No version list was available for this source.")
            } else {
                state.candidates.forEach { candidate ->
                    VersionHistoryRow(
                        candidate = candidate,
                        showOpenLink = candidate.identityKey() in state.noDirectDownloadKeys,
                        onDownloadVersion = { onDownloadVersion(candidate) },
                        onSolveCaptcha = { onSolveCaptcha(candidate) }
                    )
                }
            }
        }
    }
}

@Composable
private fun VersionHistoryRow(
    candidate: DownloadCandidate,
    showOpenLink: Boolean,
    onDownloadVersion: () -> Unit,
    onSolveCaptcha: (DownloadCandidate) -> Unit
) {
    val context = LocalContext.current
    HelperCard(cornerRadius = HelperDefaults.CompactCornerRadius) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(HelperDefaults.ContentPadding),
            horizontalArrangement = Arrangement.spacedBy(HelperDefaults.ItemSpacing),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = candidate.versionDisplay,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = when {
                        // Captcha-gated rows (e.g. Mi9's version history) are
                        // browsed in the in-app captcha browser, not downloaded.
                        candidate.captchaUrl != null && !candidate.directDownload ->
                            "Browsable in the in-app browser (version history)"
                        showOpenLink -> "No direct download  open the version page"
                        // History rows know only the version page until the
                        // user taps Download, which resolves the real format.
                        // "web" is a placeholder, not an actual file type, so
                        // don't render it as if the row just links out.
                        candidate.fileKind.equals("web", ignoreCase = true) ->
                            "Direct download  format resolved on download"
                        else -> candidate.fileKind.uppercase(Locale.US)
                    },
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            when {
                (showOpenLink || (candidate.captchaUrl != null && !candidate.directDownload)) &&
                    candidate.source != DownloadSource.AURORA &&
                    candidate.source != DownloadSource.PLAY -> {
                    HelperButton(
                        text = "Solve captcha",
                        onClick = { onSolveCaptcha(candidate) },
                        icon = Icons.Outlined.VerifiedUser,
                        modifier = Modifier.widthIn(min = 140.dp)
                    )
                }
                showOpenLink -> {
                    HelperOutlinedButton(
                        text = "Open in app",
                        onClick = {
                            if (candidate.source == DownloadSource.PLAY) {
                                context.openPlayStoreListing(candidate.packageName, candidate.url)
                            } else {
                                onSolveCaptcha(candidate)
                            }
                        },
                        icon = Icons.Outlined.OpenInBrowser,
                        modifier = Modifier.widthIn(min = 120.dp)
                    )
                }
                else -> {
                    HelperButton(
                        text = "Download",
                        onClick = onDownloadVersion,
                        icon = Icons.Outlined.Download,
                        modifier = Modifier.widthIn(min = 120.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun CandidateResolveSection(
    request: HelperRequest,
    state: ResolveState,
    emptyText: String,
    onDownload: (DownloadCandidate) -> Unit,
    onPickDownloadedFile: (DownloadCandidate) -> Unit,
    onUseInstalledApp: (DownloadCandidate) -> Unit,
    onSolveCaptcha: (DownloadCandidate) -> Unit,
    installedPackageRefreshToken: Int
) {
    when (state) {
        // Idle and Loading are driven entirely by the bottom-bar primary
        // action button; the content area shows results only.
        ResolveState.Idle,
        ResolveState.Loading -> Unit

        is ResolveState.Done -> {
            if (state.candidates.isEmpty()) {
                InfoCard(emptyText)
            } else {
                state.candidates.forEach { candidate ->
                    CandidateCard(
                        request = request,
                        candidate = candidate,
                        onDownload = { onDownload(candidate) },
                        onPickDownloadedFile = { onPickDownloadedFile(candidate) },
                        onUseInstalledApp = { onUseInstalledApp(candidate) },
                        onSolveCaptcha = { onSolveCaptcha(candidate) },
                        installedPackageRefreshToken = installedPackageRefreshToken
                    )
                }
            }
        }

        is ResolveState.Error -> {
            InfoCard(state.message)
            state.fallbackCandidate?.let { candidate ->
                CandidateCard(
                    request = request,
                    candidate = candidate,
                    onDownload = { onDownload(candidate) },
                    onPickDownloadedFile = { onPickDownloadedFile(candidate) },
                    onUseInstalledApp = { onUseInstalledApp(candidate) },
                    onSolveCaptcha = { onSolveCaptcha(candidate) },
                    installedPackageRefreshToken = installedPackageRefreshToken
                )
            }
        }
    }
}



@Composable
private fun InfoCard(text: String) {
    HelperCard(
        cornerRadius = HelperDefaults.CompactCornerRadius,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.44f)
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(HelperDefaults.ContentPadding)
        )
    }
}

private enum class HistoryFilter(val label: String) {
    All("All"),
    Scanned("Scanned"),
    Flagged("Flagged")
}

@Composable
private fun DownloadHistorySection(
    entries: List<DownloadHistoryEntry>,
    onClear: () -> Unit,
    onOpen: (DownloadHistoryEntry) -> Unit,
    onShare: (DownloadHistoryEntry) -> Unit
) {
    val context = LocalContext.current
    var filter by rememberSaveable { mutableStateOf(HistoryFilter.All) }
    val filtered = entries.filter { entry ->
        when (filter) {
            HistoryFilter.All -> true
            HistoryFilter.Scanned -> entry.scanVerdict != null
            HistoryFilter.Flagged -> entry.scanVerdict?.malicious == true
        }
    }
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(HelperDefaults.ItemSpacing)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(HelperDefaults.ItemSpacing),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Download history",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            HelperOutlinedButton(
                text = "Clear",
                onClick = onClear,
                modifier = Modifier.width(HelperDefaults.ActionClearWidth)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            HistoryFilter.entries.forEach { f ->
                val count = when (f) {
                    HistoryFilter.All -> entries.size
                    HistoryFilter.Scanned -> entries.count { it.scanVerdict != null }
                    HistoryFilter.Flagged -> entries.count { it.scanVerdict?.malicious == true }
                }
                val selected = f == filter
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(50))
                        .clickable { filter = f },
                    // Passing the shape to the Surface (not just clipping) makes
                    // the border follow the rounded outline; without it the
                    // border is drawn as a rectangle and cut at the rounded
                    // ends, which reads as a clipped border.
                    shape = RoundedCornerShape(50),
                    color = if (selected) {
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.16f)
                    } else {
                        sourceCardFill()
                    },
                    border = BorderStroke(
                        width = if (selected) 1.5.dp else 1.dp,
                        color = if (selected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            sourceCardBorder()
                        }
                    )
                ) {
                    Text(
                        text = "${f.label} · $count",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                        color = if (selected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 9.dp)
                    )
                }
            }
        }

        if (entries.isEmpty()) {
            InfoCard("No hand-offs recorded yet. Downloads and picked files you return to Morphe show up here.")
        } else if (filtered.isEmpty()) {
            InfoCard(
                when (filter) {
                    HistoryFilter.All -> "No hand-offs recorded yet."
                    HistoryFilter.Scanned -> "None of these downloads were scanned by VirusTotal."
                    HistoryFilter.Flagged -> "No flagged downloads — everything came back clean."
                }
            )
        } else {
            filtered.forEach { entry ->
                val usable = remember(entry.uri) { context.isHistoryUriUsable(entry.uri) }
                HistoryEntryCard(
                    entry = entry,
                    usable = usable,
                    onOpen = { onOpen(entry) },
                    onShare = { onShare(entry) }
                )
            }
        }
    }
}

@Composable
private fun RequestLogsCard(
    logs: List<RequestLogEntry>,
    onClearLogs: () -> Unit
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(HelperDefaults.ItemSpacing)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(HelperDefaults.ItemSpacing),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Request logs",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            HelperOutlinedButton(
                text = "Share",
                icon = Icons.Outlined.Share,
                onClick = {
                    val share = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_SUBJECT, "APK Download Helper logs")
                        putExtra(Intent.EXTRA_TEXT, AppLog.exportText())
                    }
                    context.startActivity(Intent.createChooser(share, "Share logs"))
                },
                // Icon + text needs more room than the plain "Clear" button.
                modifier = Modifier.widthIn(min = 120.dp)
            )
            HelperOutlinedButton(
                text = "Clear",
                onClick = onClearLogs,
                modifier = Modifier.width(HelperDefaults.ActionClearWidth)
            )
        }

        if (logs.isEmpty()) {
            InfoCard("No logs yet.")
        } else {
            HelperCard(cornerRadius = HelperDefaults.CompactCornerRadius) {
                SelectionContainer {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(HelperDefaults.ContentPadding),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        logs.takeLast(80).forEach { entry ->
                            Text(
                                text = "${entry.time} ${entry.level.badge} ${entry.message}",
                                color = entry.level.color(),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CandidateCard(
    request: HelperRequest,
    candidate: DownloadCandidate,
    onDownload: () -> Unit,
    onPickDownloadedFile: () -> Unit,
    onUseInstalledApp: () -> Unit,
    onSolveCaptcha: (DownloadCandidate) -> Unit,
    installedPackageRefreshToken: Int
) {
    val context = LocalContext.current
    val match = candidate.matchSummary(request)
    val hasResolvedCandidateInfo = candidate.versionName != null ||
        candidate.versionCode != null ||
        !candidate.fileKind.equals("web", ignoreCase = true)
    var hasOpenedLink by remember(candidate.identityKey()) { mutableStateOf(false) }
    // Manual-mode links are opened by the bottom-bar primary action, so the
    // "installed app" offer applies as soon as the row renders.
    val linkConsideredOpened = hasOpenedLink || candidate.option == CandidateOption.MANUAL
    val showUseInstalledApp = candidate.source == DownloadSource.PLAY &&
        linkConsideredOpened &&
        remember(candidate.packageName, linkConsideredOpened, installedPackageRefreshToken) {
            context.isPackageInstalled(candidate.packageName)
        }

    // A plain web link with no resolved metadata (manual-mode rows, and
    // info-less Play/Aurora listings) renders as a bare action instead of a
    // filled card around a single button.
    val bareLink = candidate.note == null &&
        !hasResolvedCandidateInfo &&
        !candidate.directDownload &&
        (candidate.option == CandidateOption.MANUAL ||
            candidate.source == DownloadSource.AURORA ||
            candidate.source == DownloadSource.PLAY)

    val body: @Composable ColumnScope.() -> Unit = {
        if (candidate.option != CandidateOption.MANUAL && hasResolvedCandidateInfo) {
            CandidateInfoChips(request, candidate)
        }
        if (candidate.option != CandidateOption.MANUAL && hasResolvedCandidateInfo && !match.matches) {
            CandidateMatchBox(match)
        }
        candidate.note?.let { note ->
            InfoCard(note)
        }

        if (candidate.directDownload) {
            HelperButton(
                text = "Download and return",
                onClick = onDownload,
                icon = Icons.Outlined.Download,
                modifier = Modifier.fillMaxWidth()
            )
        } else if (candidate.option == CandidateOption.MANUAL) {
            HelperButton(
                text = "Open in app",
                onClick = { onSolveCaptcha(candidate) },
                icon = Icons.Outlined.OpenInBrowser,
                modifier = Modifier.fillMaxWidth()
            )
            if (showUseInstalledApp) {
                HelperButton(
                    text = "Use installed app",
                    onClick = onUseInstalledApp,
                    icon = Icons.Outlined.CheckCircle,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        } else {
            // Resolved candidates (Recommended/Latest/History) from every source
            // except Aurora and Play can fall back to the in-app captcha
            // browser: it opens the candidate's page in a real WebView (passing
            // any Cloudflare challenge) and captures the download URL the page
            // produces. The browser action stays here because the bottom bar
            // for these tabs shows "Find latest/requested" instead.
            if (candidate.source != DownloadSource.AURORA &&
                candidate.source != DownloadSource.PLAY
            ) {
                HelperButton(
                    text = "Solve captcha in app",
                    onClick = { onSolveCaptcha(candidate) },
                    icon = Icons.Outlined.VerifiedUser,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            HelperOutlinedButton(
                text = "Open in app",
                onClick = {
                    if (candidate.source == DownloadSource.PLAY) {
                        context.openPlayStoreListing(candidate.packageName, candidate.url)
                    } else {
                        onSolveCaptcha(candidate)
                    }
                    hasOpenedLink = true
                },
                icon = Icons.Outlined.OpenInBrowser,
                modifier = Modifier.fillMaxWidth()
            )
            if (showUseInstalledApp) {
                HelperButton(
                    text = "Use installed app",
                    onClick = onUseInstalledApp,
                    icon = Icons.Outlined.CheckCircle,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    if (bareLink) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(HelperDefaults.ItemSpacing)
        ) {
            body()
        }
    } else {
        HelperCard(cornerRadius = HelperDefaults.SectionCornerRadius) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(HelperDefaults.ContentPadding),
                verticalArrangement = Arrangement.spacedBy(HelperDefaults.ItemSpacing)
            ) {
                body()
            }
        }
    }
}

@Composable
private fun CandidateInfoChips(request: HelperRequest, candidate: DownloadCandidate) {
    val requestedVersionNames = request.requestedVersionNames
    val requestedVersionCodes = request.requestedVersionCodes
    val versionTone = when {
        requestedVersionNames.isEmpty() -> ChipTone.Success
        candidate.versionName != null && requestedVersionNames.any { candidate.versionName.versionNameEquals(it) } -> {
            ChipTone.Success
        }
        else -> ChipTone.Error
    }
    val versionCodeTone = when {
        requestedVersionCodes.isEmpty() -> ChipTone.Success
        candidate.versionCode in requestedVersionCodes -> ChipTone.Success
        else -> ChipTone.Error
    }
    val formatTone = when {
        candidate.fileKind.equals("web", ignoreCase = true) -> ChipTone.Neutral
        request.acceptsFormat(candidate.fileKind) -> ChipTone.Success
        else -> ChipTone.Error
    }

    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(HelperDefaults.ContentPaddingSmall),
        verticalArrangement = Arrangement.spacedBy(HelperDefaults.ContentPaddingSmall)
    ) {
        candidate.versionName?.let {
            HelperChip(text = "Version $it", tone = versionTone)
        }
        if (candidate.versionCode != null) {
            HelperChip(text = "Code ${candidate.versionCode}", tone = versionCodeTone)
        }
        if (candidate.versionName == null && candidate.versionCode == null) {
            HelperChip(text = candidate.versionDisplay, tone = versionTone)
        }
        if (!candidate.fileKind.equals("web", ignoreCase = true)) {
            HelperChip(text = candidate.fileKind.uppercase(), tone = formatTone)
        }
        candidate.variantLabel?.let { label ->
            HelperChip(text = label, tone = ChipTone.Neutral)
        }
    }
}

@Composable
private fun CandidateMatchBox(match: CandidateMatchSummary) {
    val tone = if (match.matches) ChipTone.Success else ChipTone.Error
    val containerColor = toneContainer(tone)
    val contentColor = toneContent(tone)

    HelperCard(
        cornerRadius = HelperDefaults.CompactCornerRadius,
        color = containerColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(HelperDefaults.ItemSpacing),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(match.title, color = contentColor, fontWeight = FontWeight.Bold)
            match.details.forEach { detail ->
                Text(
                    text = detail,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun HelperChip(text: String, tone: ChipTone = ChipTone.Neutral) {
    val containerColor = toneContainer(tone)
    val contentColor = toneContent(tone)
    val borderColor = toneBorder(tone)

    Surface(
        shape = RoundedCornerShape(50),
        color = containerColor,
        contentColor = contentColor,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = HelperDefaults.ItemSpacing, vertical = 7.dp)
        )
    }
}

private enum class ChipTone {
    Neutral,
    Success,
    Error
}

@Composable
private fun toneContainer(tone: ChipTone): Color = when (tone) {
    ChipTone.Neutral -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.42f)
    ChipTone.Success -> if (helperDarkTheme) Color(0xFF12382D) else Color(0xFFC8E6C9)
    ChipTone.Error -> if (helperDarkTheme) Color(0xFF432023) else Color(0xFFFFCDD2)
}

@Composable
private fun toneContent(tone: ChipTone): Color = when (tone) {
    ChipTone.Neutral -> MaterialTheme.colorScheme.onSurfaceVariant
    ChipTone.Success -> if (helperDarkTheme) Color(0xFF79DEAF) else Color(0xFF1B5E20)
    ChipTone.Error -> if (helperDarkTheme) Color(0xFFFFB3AC) else Color(0xFFB71C1C)
}

@Composable
private fun toneBorder(tone: ChipTone): Color = when (tone) {
    ChipTone.Neutral -> MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
    ChipTone.Success -> toneContent(ChipTone.Success).copy(alpha = if (helperDarkTheme) 0.34f else 0.35f)
    ChipTone.Error -> toneContent(ChipTone.Error).copy(alpha = if (helperDarkTheme) 0.34f else 0.35f)
}

@Composable
private fun warningAccent(): Color =
    if (helperDarkTheme) Color(0xFFFFD166) else Color(0xFF9A6700)

@Composable
private fun CheckingPickedFileState(state: UiState.CheckingPickedFile) {
    HelperCard {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(HelperDefaults.ContentPadding),
            horizontalArrangement = Arrangement.spacedBy(HelperDefaults.ItemSpacing),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularProgressIndicator(modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Checking selected file")
                Text(
                    text = state.candidate.source.label,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun FastModeCard(
    progress: FastModeProgress,
    onCancel: () -> Unit,
    onSkipWait: () -> Unit,
    onUseMismatch: () -> Unit,
    onSkipMismatch: () -> Unit,
    onSkipScan: () -> Unit,
    onChooseVersion: (FastModePolicy) -> Unit
) {
    HelperCard(cornerRadius = HelperDefaults.SectionCornerRadius) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(HelperDefaults.ContentPadding),
            verticalArrangement = Arrangement.spacedBy(HelperDefaults.ItemSpacing)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(HelperDefaults.ItemSpacing),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text("Fast Mode", fontWeight = FontWeight.Bold)
                    progress.sourceLabel?.let { source ->
                        Text(
                            text = source,
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
                if (!progress.done && progress.percent == null && !progress.awaitingDecision) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.5.dp
                    )
                }
            }
            Text(
                text = progress.detail,
                color = when {
                    progress.done && !progress.succeeded -> MaterialTheme.colorScheme.error
                    progress.done -> MaterialTheme.colorScheme.onSurface
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                },
                style = MaterialTheme.typography.bodyMedium
            )
            // Persist the SHA-256 verification on the card once the file's
            // bytes matched the source-published hash (not just the transient
            // post-download status line).
            if (progress.shaVerified) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                ) {
                    Box(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Verified,
                            contentDescription = "SHA-256 verified",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
            }
            if (progress.awaitingVersionChoice) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    shape = MaterialTheme.shapes.small
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(HelperDefaults.ContentPadding),
                        verticalArrangement = Arrangement.spacedBy(HelperDefaults.ItemSpacing)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(HelperDefaults.ItemSpacing),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Tune,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = progress.versionChoiceDetail
                                    ?: "Which version should Fast Mode fetch?",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        progress.versionChoiceRequested?.let { requested ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Requested",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                                )
                                Text(
                                    text = requested,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(HelperDefaults.ItemSpacing)
                ) {
                    HelperButton(
                        text = "Requested version",
                        onClick = { onChooseVersion(FastModePolicy.REQUESTED) },
                        modifier = Modifier.weight(1f)
                    )
                    HelperButton(
                        text = "Latest version",
                        onClick = { onChooseVersion(FastModePolicy.LATEST) },
                        modifier = Modifier.weight(1f)
                    )
                }
                HelperOutlinedButton(
                    text = "Cancel",
                    onClick = onCancel,
                    icon = Icons.Outlined.Close,
                    modifier = Modifier.fillMaxWidth()
                )
            } else if (progress.awaitingDecision) {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer,
                    shape = MaterialTheme.shapes.small
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(HelperDefaults.ContentPadding),
                        horizontalArrangement = Arrangement.spacedBy(HelperDefaults.ItemSpacing),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Warning,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = progress.mismatchDetail ?: "Version code mismatch.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(HelperDefaults.ItemSpacing)
                ) {
                    HelperButton(
                        text = "Use this version",
                        onClick = onUseMismatch,
                        modifier = Modifier.weight(1f)
                    )
                    HelperOutlinedButton(
                        text = "Skip to next source",
                        onClick = onSkipMismatch,
                        modifier = Modifier.weight(1f)
                    )
                }
                HelperOutlinedButton(
                    text = "Cancel",
                    onClick = onCancel,
                    icon = Icons.Outlined.Close,
                    modifier = Modifier.fillMaxWidth()
                )
            } else if (progress.percent != null && !progress.done) {
                LinearProgressIndicator(
                    progress = { progress.percent / 100f },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = buildString {
                            append(progress.percent).append('%')
                            val speed = formatTransferSpeed(progress.speedBytesPerSec)
                            if (speed.isNotEmpty()) append("  ·  ").append(speed)
                            if (progress.etaMs != null && progress.etaMs > 0L) {
                                append("  ·  ").append(formatTransferEta(progress.etaMs)).append(" left")
                            }
                        },
                        fontWeight = FontWeight.Medium
                    )
                    HelperOutlinedButton(
                        text = "Cancel",
                        onClick = onCancel,
                        icon = Icons.Outlined.Close
                    )
                }
                SkipWaitButton(
                    active = isRateLimitWait(progress.detail),
                    onSkipWait = onSkipWait
                )
                if (isScanStatus(progress.detail)) {
                    HelperButton(
                        text = "Skip scan & hand off",
                        onClick = onSkipScan,
                        icon = Icons.Outlined.Shield,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            } else if (!progress.done) {
                HelperOutlinedButton(
                    text = "Cancel",
                    onClick = onCancel,
                    icon = Icons.Outlined.Close,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

// True when the scan is paused on the app's own pacing gap ("Waiting Ns for
// rate limit…") — the only wait the Skip button can meaningfully cut short.
// Match the pause anywhere in the status: the bundle scanner prefixes inner
// APK statuses with "APK 2 of 4 (split_1.apk): …", so a plain prefix check
// would never see the "Waiting Ns for rate limit…" inside.
//
// A genuine 429 ("Rate limit hit — retrying in Ns…") is deliberately NOT
// matched: skipping it is futile (the server keeps rejecting until the 60s
// window clears) and its countdown comes from the window, not the gap, so the
// Skip button would show a wrong number. That wait auto-retries instead.
private fun isRateLimitWait(status: String): Boolean =
    status.contains("waiting", ignoreCase = true) &&
        status.contains("rate limit", ignoreCase = true)

/** True when a status message belongs to the VirusTotal scan phase (as opposed
 *  to the download phase that reuses the same card). */
private fun isScanStatus(status: String?): Boolean =
    status?.let {
        it.startsWith("APK ") || it.startsWith("Extract") ||
            it.startsWith("Opening") || it.startsWith("Upload") ||
            it.startsWith("Waiting") || it.startsWith("Aggregat") ||
            it.startsWith("Scanning") || it.startsWith("Checking VirusTotal") ||
            it.startsWith("Queued") || it.startsWith("VirusTotal engines")
    } == true

/**
 * "Skip wait" with a live countdown of the seconds left in the current
 * rate-limit pause. While the scan status shows a wait, this re-reads the
 * shared limiter's remaining gap each second and shows "Skip wait · 12s";
 * once the pause ends the button disappears. Tapping skips the wait early.
 */
@Composable
private fun SkipWaitButton(active: Boolean, onSkipWait: () -> Unit) {
    var secondsLeft by remember { mutableIntStateOf(0) }
    // Flips when the user taps: hides the button immediately so the tap gives
    // visible feedback even though the parent's "Waiting…" status (and hence
    // `active`) stays true until the scan's next network result arrives.
    var skipped by remember { mutableStateOf(false) }
    LaunchedEffect(active) {
        if (!active) {
            skipped = false
            return@LaunchedEffect
        }
        while (!skipped) {
            // Re-read from the limiter each tick so the countdown stays honest
            // even if the status message lags a second behind the real pause.
            secondsLeft = ((VirusTotalScanner.rateLimiter.millisUntilNextSlot() + 999) / 1000).toInt()
            if (secondsLeft <= 0) break
            delay(1000)
        }
    }
    if (active && !skipped) {
        HelperOutlinedButton(
            text = if (secondsLeft > 0) "Skip wait · ${secondsLeft}s" else "Skip wait",
            onClick = {
                skipped = true
                onSkipWait()
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

private fun scanPhaseSplit(status: String): Pair<String, String> {
    val apk = Regex("""APK (\d+) of (\d+) \(([^)]+)\):? ?(.*)""").find(status)
    if (apk != null) {
        val (_, _, name, rest) = apk.destructured
        val phase = "Scanning APK ${apk.groupValues[1]} of ${apk.groupValues[2]}"
        return phase to rest.ifBlank { name }
    }
    val extract = Regex("""Extract(ing|ed) ([^:]+)""").find(status)
    if (extract != null) {
        val action = extract.groupValues[1]  // ing | ed
        val body = extract.groupValues[2]
        return "Extract$action ${body.take(30)}" to (status.removePrefix(extract.value).trim())
    }
    // Everything else: first clause is the phase (before "·"/":"), rest is detail.
    val sep = status.indexOfFirst { it == '·' || it == ':' }
    return if (sep > 0 && sep < status.length - 1) {
        (status.substring(0, sep).trim().replaceFirstChar { it.uppercase() }) to
            status.substring(sep + 1).trim()
    } else {
        status to ""
    }
}

@Composable
private fun DownloadingState(
    state: UiState.Downloading,
    onCancel: () -> Unit,
    onSkipWait: () -> Unit,
    onSkipScan: () -> Unit
) {
    HelperCard {
        Column(
            modifier = Modifier.padding(HelperDefaults.ContentPadding),
            verticalArrangement = Arrangement.spacedBy(HelperDefaults.ItemSpacing)
        ) {
            val statusText = state.statusMessage ?: "Downloading from ${state.candidate.source.label}"
            val isScan = isScanStatus(statusText)
            val (phase, detail) = if (isScan) scanPhaseSplit(statusText) else (statusText to "")
            Text(
                text = phase,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyLarge
            )
            if (detail.isNotBlank()) {
                Text(
                    text = detail,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            LinearProgressIndicator(
                progress = { state.percent / 100f },
                modifier = Modifier.fillMaxWidth()
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = buildString {
                        append(state.percent).append('%')
                        val speed = formatTransferSpeed(state.speedBytesPerSec)
                        if (speed.isNotEmpty()) append("  ·  ").append(speed)
                        if (state.etaMs != null && state.etaMs > 0L) {
                            append("  ·  ").append(formatTransferEta(state.etaMs)).append(" left")
                        }
                    },
                    fontWeight = FontWeight.Medium
                )
                HelperOutlinedButton(
                    text = "Cancel",
                    onClick = onCancel,
                    icon = Icons.Outlined.Close
                )
            }
            SkipWaitButton(
                active = isRateLimitWait(statusText),
                onSkipWait = onSkipWait
            )
            if (isScan) {
                HelperButton(
                    text = "Skip scan & hand off",
                    onClick = onSkipScan,
                    icon = Icons.Outlined.Shield,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "Hand the downloaded file to Morphe now without waiting for VirusTotal.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun ScanAskCard(
    candidate: DownloadCandidate,
    onScan: () -> Unit,
    onSkip: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(HelperDefaults.CardCornerRadius),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(HelperDefaults.ContentPadding),
            verticalArrangement = Arrangement.spacedBy(HelperDefaults.ItemSpacing)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Shield,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Scan with VirusTotal?",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = "Check ${candidate.name} with VirusTotal before returning it to Morphe.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                HelperButton(
                    text = "Scan",
                    onClick = onScan,
                    modifier = Modifier.weight(1f)
                )
                HelperOutlinedButton(
                    text = "Skip",
                    onClick = onSkip,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

private fun scanResultDetail(scanResult: VirusTotalScanner.ScanResult): String = when (scanResult) {
    is VirusTotalScanner.ScanResult.Clean ->
        if (scanResult.scannedFiles != null) {
            "All ${scanResult.scannedFiles} APKs in the bundle are clean — " +
                "0 of ${scanResult.totalEngines} engines flagged them"
        } else {
            "0 of ${scanResult.totalEngines} antivirus engines flagged this file"
        }
    is VirusTotalScanner.ScanResult.Malicious ->
        if (scanResult.scannedFiles != null) {
            "${scanResult.flaggedFiles ?: 1} of ${scanResult.scannedFiles} APKs in the " +
                "bundle flagged — ${scanResult.detections} of ${scanResult.totalEngines} engines"
        } else {
            "${scanResult.detections} of ${scanResult.totalEngines} antivirus engines flagged this file"
        }
    is VirusTotalScanner.ScanResult.Error ->
        "Scan error: ${scanResult.message}"
}

private fun openVirusTotalPage(context: Context, sha256: String) {
    val open = Intent(
        Intent.ACTION_VIEW,
        Uri.parse("https://www.virustotal.com/gui/file/$sha256")
    ).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
    runCatching { context.startActivity(open) }
        .onFailure {
            Toast.makeText(context, "No browser available", Toast.LENGTH_SHORT).show()
        }
}

@Composable
private fun ScanResultCard(
    scanResult: VirusTotalScanner.ScanResult,
    detail: String,
    isMalicious: Boolean,
    onProceed: () -> Unit,
    onCancel: () -> Unit,
    readOnly: Boolean = false,
    // True if the downloaded file's bytes matched the source-published SHA-256.
    shaVerified: Boolean = false
) {
    val colors = MaterialTheme.colorScheme
    val context = LocalContext.current
    val isError = scanResult is VirusTotalScanner.ScanResult.Error
    val containerColor = if (isMalicious) {
        MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.15f)
    } else if (scanResult is VirusTotalScanner.ScanResult.Clean) {
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f)
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }
    val borderColor = if (isMalicious) {
        MaterialTheme.colorScheme.error.copy(alpha = 0.5f)
    } else if (scanResult is VirusTotalScanner.ScanResult.Clean) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
    } else {
        MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(HelperDefaults.CardCornerRadius),
        color = containerColor,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Column(
            modifier = Modifier.padding(HelperDefaults.ContentPadding),
            verticalArrangement = Arrangement.spacedBy(HelperDefaults.ItemSpacing)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = when {
                        isMalicious || isError -> Icons.Outlined.Warning
                        else -> Icons.Outlined.CheckCircle
                    },
                    contentDescription = null,
                    tint = when {
                        isMalicious || isError -> MaterialTheme.colorScheme.error
                        else -> MaterialTheme.colorScheme.primary
                    },
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = when {
                        isMalicious -> "VirusTotal — Threats detected"
                        isError -> "VirusTotal — Scan error"
                        else -> "VirusTotal — Clean"
                    },
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                if (!isError) {
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = if (scanResult.cached) {
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        }
                    ) {
                        Text(
                            text = if (scanResult.cached) "Cached report" else "Fresh scan",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (scanResult.cached) {
                                MaterialTheme.colorScheme.onPrimaryContainer
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                        )
                    }
                }
                if (shaVerified) {
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Verified,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(Modifier.width(3.dp))
                            Text(
                                text = "SHA-256 verified",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }
            Text(
                text = detail,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (scanResult is VirusTotalScanner.ScanResult.Malicious) {
                scanResult.suggestedThreatLabel?.let { label ->
                    Text(
                        text = "Detected as: $label",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                if (scanResult.engines.isNotEmpty()) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        scanResult.engines.take(8).forEach { hit ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = hit.engine,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.weight(0.38f)
                                )
                                Text(
                                    text = hit.result,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.weight(0.62f)
                                )
                            }
                        }
                        if (scanResult.engines.size > 8) {
                            Text(
                                text = "+${scanResult.engines.size - 8} more engines",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
            if (isMalicious) {
                Text(
                    text = "This file was flagged by antivirus engines. " +
                        "Only proceed if you trust the source.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            } else if (isError) {
                Text(
                    text = "The scan did not complete (network or VirusTotal queue). " +
                        "You can still proceed, or cancel to keep the file out of Morphe.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
            if (scanResult.apkResults.isNotEmpty()) {
                androidx.compose.material3.HorizontalDivider(
                    modifier = Modifier.padding(vertical = 4.dp),
                    color = colors.outline.copy(alpha = 0.3f)
                )
                Text(
                    text = "APK details",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                scanResult.apkResults.forEach { apk ->
                    val apkVerdictColor = when {
                        apk.failed -> colors.onSurfaceVariant
                        apk.detections > 0 -> colors.error
                        else -> colors.primary
                    }
                    val apkIcon = when {
                        apk.failed -> Icons.Outlined.HelpOutline
                        apk.detections > 0 -> Icons.Outlined.Warning
                        else -> Icons.Outlined.CheckCircle
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = apkIcon,
                            contentDescription = null,
                            tint = apkVerdictColor,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = apk.name,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        // Distinguish a cached report (VirusTotal already had it) from a
                        // fresh upload so users can tell why an APK's scan was instant.
                        if (!apk.failed) {
                            androidx.compose.material3.Surface(
                                shape = RoundedCornerShape(50),
                                color = if (apk.cached) {
                                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                                } else {
                                    MaterialTheme.colorScheme.surfaceVariant
                                }
                            ) {
                                Text(
                                    text = if (apk.cached) "cached" else "fresh",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (apk.cached) {
                                        MaterialTheme.colorScheme.onPrimaryContainer
                                    } else {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    },
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                                )
                            }
                        }
                        Text(
                            text = if (apk.failed) "failed" else "${apk.detections}/${apk.totalEngines}",
                            style = MaterialTheme.typography.bodySmall,
                            color = apkVerdictColor
                        )
                        if (apk.sha256 != null) {
                            Icon(
                                imageVector = Icons.Outlined.OpenInNew,
                                contentDescription = "Open ${apk.name} in VirusTotal",
                                tint = colors.primary,
                                modifier = Modifier
                                    .size(14.dp)
                                    .clip(RoundedCornerShape(50))
                                    .clickable { openVirusTotalPage(context, apk.sha256) }
                                    .padding(2.dp)
                            )
                        }
                    }
                    if (apk.detections > 0 && apk.engines.isNotEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 22.dp),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            apk.engines.take(3).forEach { hit ->
                                Text(
                                    text = "${hit.engine} → ${hit.result}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = colors.error,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            if (apk.engines.size > 3) {
                                Text(
                                    text = "+${apk.engines.size - 3} more engines",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = colors.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
            ScanMetaRows(scanResult)
            if (!readOnly) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    HelperButton(
                        text = "Proceed",
                        onClick = onProceed,
                        modifier = Modifier.weight(1f)
                    )
                    HelperOutlinedButton(
                        text = "Cancel",
                        onClick = onCancel,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun ScanMetaRows(scanResult: VirusTotalScanner.ScanResult) {
    if (scanResult is VirusTotalScanner.ScanResult.Error) return
    val context = LocalContext.current
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? android.content.ClipboardManager
    var hashCopied by remember { mutableStateOf(false) }
    val sha256 = scanResult.sha256
    val rows = buildList {
        scanResult.typeDescription?.let { add("Type" to it) }
        scanResult.sizeBytes?.let { add("Size" to it.formatBytes()) }
        scanResult.timesSubmitted?.let { add("Times submitted" to it.toString()) }
        scanResult.firstSubmissionDate?.let { add("First seen" to formatScanDate(it)) }
        scanResult.lastAnalysisDate?.let { add("Last analyzed" to formatScanDate(it)) }
        if (scanResult is VirusTotalScanner.ScanResult.Malicious) {
            if (scanResult.scannedFiles != null && scanResult.fileName.isNotBlank()) {
                add("Flagged APK" to scanResult.fileName)
            }
        }
        scanResult.scannedFiles?.let { add("APKs scanned" to it.toString()) }
        scanResult.bundleName?.let { add("Bundle" to it) }
        if (scanResult is VirusTotalScanner.ScanResult.Clean) {
            if (scanResult.votesHarmless > 0 || scanResult.votesMalicious > 0) {
                add("Community votes" to "${scanResult.votesHarmless} harmless / ${scanResult.votesMalicious} malicious")
            }
            scanResult.reputation?.let { add("Reputation" to it.toString()) }
        }
    }
    if (rows.isEmpty() && sha256 == null) return
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        sha256?.let { hash ->
            // Tap to copy the full hash.
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable {
                        clipboard?.setPrimaryClip(ClipData.newPlainText("SHA-256", hash))
                        hashCopied = true
                        Toast.makeText(context, "SHA-256 copied", Toast.LENGTH_SHORT).show()
                    }
                    .padding(vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "SHA-256",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(0.38f)
                )
                Text(
                    text = hash.take(16) + "…",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(0.5f)
                )
                Icon(
                    imageVector = if (hashCopied) Icons.Outlined.CheckCircle else Icons.Outlined.ContentCopy,
                    contentDescription = "Copy SHA-256",
                    tint = if (hashCopied) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    modifier = Modifier.size(16.dp)
                )
            }
            // Jump to the full report on virustotal.com.
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { openVirusTotalPage(context, hash) }
                    .padding(vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Open in VirusTotal",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(0.38f)
                )
                Spacer(modifier = Modifier.weight(0.5f))
                Icon(
                    imageVector = Icons.Outlined.OpenInNew,
                    contentDescription = "Open in VirusTotal",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        rows.forEach { (label, value) ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(0.38f)
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(0.62f)
                )
            }
        }
    }
}

private fun formatScanDate(epochSeconds: Long): String {
    val date = java.util.Date(epochSeconds * 1000L)
    return java.text.SimpleDateFormat("MMM d, yyyy", Locale.US).format(date)
}

private fun formatTransferSpeed(bytesPerSec: Double): String {
    if (bytesPerSec <= 0.0) return ""
    val mb = bytesPerSec / (1024.0 * 1024.0)
    if (mb >= 1.0) return String.format(Locale.US, "%.1f MB/s", mb)
    val kb = bytesPerSec / 1024.0
    if (kb >= 1.0) return String.format(Locale.US, "%.0f KB/s", kb)
    return String.format(Locale.US, "%.0f B/s", bytesPerSec)
}

private fun formatTransferEta(ms: Long): String {
    val totalSec = (ms / 1000L).coerceAtLeast(1L)
    val h = totalSec / 3600L
    val m = (totalSec % 3600L) / 60L
    val s = totalSec % 60L
    return if (h > 0L) {
        String.format(Locale.US, "%d:%02d:%02d", h, m, s)
    } else {
        String.format(Locale.US, "%d:%02d", m, s)
    }
}

@Composable
private fun ErrorState(message: String, onRefresh: () -> Unit, onCancel: () -> Unit) {
    HelperCard {
        Column(
            modifier = Modifier.padding(HelperDefaults.ContentPadding),
            verticalArrangement = Arrangement.spacedBy(HelperDefaults.ItemSpacing)
        ) {
        Text(text = message, color = MaterialTheme.colorScheme.error)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(HelperDefaults.ItemSpacing)
        ) {
            HelperButton(
                text = "Retry",
                onClick = onRefresh,
                modifier = Modifier.weight(1f)
            )
            HelperOutlinedButton(
                text = "Cancel",
                onClick = onCancel,
                modifier = Modifier.weight(1f)
            )
        }
        }
    }
}

internal data class HelperRequest(
    val callerPackage: String,
    val packageName: String,
    val appName: String,
    val versionName: String?,
    val versionCode: Long?,
    val versionCodes: Set<Long>,
    val compatibleVersionNames: Set<String>,
    val compatibleVersionCodes: Set<Long>,
    val supportedAbis: List<String>,
    val requestedFileType: String?,
    val allowSplitArchive: Boolean,
    val stockInstallRequired: Boolean,
    val fallbackWebUrl: String,
    val sourceHintUrls: List<String>
) {
    val availableAbis: List<String>
        get() = supportedAbis
            .ifEmpty { Build.SUPPORTED_ABIS.toList() }
            .mapNotNull { it.trim().takeIf(String::isNotBlank) }
            .distinct()

    val requestedVersionName: String?
        get() = versionName
            ?.withoutTrailingVersionCode()
            ?.takeIf(String::isNotBlank)

    val embeddedVersionCode: Long?
        get() = versionName?.trailingVersionCode()

    val abiSummary: String
        get() = availableAbis.joinToString().ifBlank { "Default" }

    val requestedFileKinds: Set<String>
        get() = requestedFileKindsFrom(requestedFileType, allowSplitArchive)

    val requestedFormatLabel: String
        get() = requestedFileKinds
            .orderedFileKinds()
            .joinToString("/") { it.uppercase(Locale.US) }

    val versionCodeSummary: String?
        get() = when {
            requestedVersionCodes.isEmpty() -> null
            requestedVersionCodes.size == 1 -> requestedVersionCodes.first().toString()
            else -> requestedVersionCodes.joinToString(limit = 3, truncated = "+${requestedVersionCodes.size - 3} more")
        }

    val requestedVersionCodes: Set<Long>
        get() = buildSet {
            versionCode?.takeIf { it > 0L }?.let(::add)
            embeddedVersionCode?.takeIf { it > 0L }?.let(::add)
            addAll(versionCodes.filter { it > 0L })
        }

    val knownVersionNames: List<String>
        get() = (listOfNotNull(requestedVersionName) + compatibleVersionNames.map { it.withoutTrailingVersionCode() })
            .mapNotNull { it.trim().takeIf(String::isNotBlank) }
            .distinctBy { it.normalizedVersionName() }

    val requestedVersionNames: List<String>
        get() = listOfNotNull(requestedVersionName)
            .mapNotNull { it.trim().takeIf(String::isNotBlank) }
            .distinctBy { it.normalizedVersionName() }

    val hasRequestedVersionRequest: Boolean
        get() = requestedVersionName != null || requestedVersionCodes.isNotEmpty()

    val hasKnownVersionRequest: Boolean
        get() = requestedVersionName != null ||
            requestedVersionCodes.isNotEmpty() ||
            compatibleVersionNames.isNotEmpty() ||
            compatibleVersionCodes.any { it > 0L }

    val requestedVersionLabel: String
        get() = listOfNotNull(
            requestedVersionName,
            versionCodeSummary?.let { "build $it" }
        ).joinToString(" ").ifBlank { "any compatible version" }

    fun isRequestedMatch(candidate: DownloadCandidate): Boolean =
        matchesRequestedVersion(candidate.versionName, candidate.versionCode)

    fun versionStatus(candidateVersionName: String?, candidateVersionCode: Long?): VersionStatus {
        if (matchesRequestedVersion(candidateVersionName, candidateVersionCode)) return VersionStatus.REQUESTED

        val compatibleName = candidateVersionName != null &&
            compatibleVersionNames.any { candidateVersionName.versionNameEquals(it.withoutTrailingVersionCode()) }
        val compatibleCode = candidateVersionCode != null &&
            candidateVersionCode > 0L &&
            candidateVersionCode in compatibleVersionCodes
        return if (compatibleName || compatibleCode) VersionStatus.COMPATIBLE else VersionStatus.LATEST
    }

    fun acceptsFormat(fileKind: String): Boolean {
        // Some parsers tag a candidate with the whole requested label (e.g.
        // "APK/APKM/APKS/XAPK") instead of a single kind. Split on '/' so any
        // listed kind counts as a match instead of a false "Format mismatch".
        val kinds = fileKind.lowercase(Locale.US).split('/')
        return kinds.any { it in requestedFileKinds }
    }

    fun matchesKnownVersion(candidateVersionName: String?, candidateVersionCode: Long?): Boolean =
        if (!hasKnownVersionRequest) {
            false
        } else {
            matchesRequestedVersion(candidateVersionName, candidateVersionCode) ||
                (
                    candidateVersionName != null &&
                        compatibleVersionNames.any { candidateVersionName.versionNameEquals(it.withoutTrailingVersionCode()) }
                    ) ||
                (
                    candidateVersionCode != null &&
                        candidateVersionCode > 0L &&
                        candidateVersionCode in compatibleVersionCodes
                    )
        }

    fun matchesRequestedVersion(candidateVersionName: String?, candidateVersionCode: Long?): Boolean {
        val requestedCodes = requestedVersionCodes
        if (versionName == null && requestedCodes.isEmpty()) return false

        val nameMatches = requestedVersionName != null && candidateVersionName.versionNameEquals(requestedVersionName)
        val codeMatches = requestedCodes.isNotEmpty() &&
            candidateVersionCode != null &&
            candidateVersionCode > 0L &&
            candidateVersionCode in requestedCodes
        return nameMatches || codeMatches
    }

    /**
     * Strict matcher used by Fast Mode, where the exact version name AND the
     * exact version code are both required. Unlike [matchesRequestedVersion]
     * (which accepts a name-only match), a candidate that reports a version
     * code outside the requested set is rejected up front  so Fast Mode
     * doesn't download a file that validation would reject anyway. When the
     * source doesn't report a code, the name match is accepted (validation is
     * still the backstop for the downloaded artifact).
     */
    fun matchesRequestedVersionStrict(candidateVersionName: String?, candidateVersionCode: Long?): Boolean {
        if (versionName == null && requestedVersionCodes.isEmpty()) return false

        val nameRequired = requestedVersionName != null
        val nameMatches = candidateVersionName != null &&
            candidateVersionName.versionNameEquals(requestedVersionName)
        val codeRequired = requestedVersionCodes.isNotEmpty() && candidateVersionCode != null
        val codeMatches = requestedVersionCodes.isEmpty() ||
            candidateVersionCode == null ||
            (candidateVersionCode > 0L && candidateVersionCode in requestedVersionCodes)

        return (!nameRequired || nameMatches) && (!codeRequired || codeMatches)
    }

    fun sourceHintUrlsFor(source: DownloadSource): List<String> {
        val needles = when (source) {
            DownloadSource.AURORA,
            DownloadSource.PLAY -> listOf("play.google.com")
            DownloadSource.APK_MIRROR -> listOf("apkmirror.com", "google.com/search")
            DownloadSource.APK_COMBO -> listOf("apkcombo.com")
            DownloadSource.APTOIDE -> listOf("aptoide.com")
            DownloadSource.APK_PURE -> listOf("apkpure.com")
            DownloadSource.UPTODOWN -> listOf("uptodown.com")
            DownloadSource.EVOZI -> listOf("apkcube.com", "evozi.com")
            DownloadSource.MI9 -> listOf("mi9.com")
            DownloadSource.APK_DOWNLOADER -> listOf("apkdownloader.pages.dev")
        }

        return (sourceHintUrls + fallbackWebUrl).distinct().filter { url ->
            needles.any { needle -> url.contains(needle, ignoreCase = true) }
        }
    }

    companion object {
        fun from(intent: Intent): HelperRequest? {
            if (intent.action != DownloadHelperContract.ACTION_DOWNLOAD_ORIGINAL_APK) return null
            val packageName = intent.getStringExtra(DownloadHelperContract.EXTRA_PACKAGE_NAME)
                ?.takeIf { it.isNotBlank() }
                ?: return null

            return HelperRequest(
                callerPackage = intent.getStringExtra(DownloadHelperContract.EXTRA_CALLER_PACKAGE)
                    ?: "app.morphe.manager",
                packageName = packageName,
                appName = intent.getStringExtra(DownloadHelperContract.EXTRA_APP_NAME) ?: packageName,
                versionName = intent.getStringExtra(DownloadHelperContract.EXTRA_VERSION_NAME),
                versionCode = if (intent.hasExtra(DownloadHelperContract.EXTRA_VERSION_CODE)) {
                    // Callers send the version code as either an Int (am --ei, some
                    // app stores) or a Long. getLongExtra silently returns the
                    // default for Int extras, so try Long first and fall back to Int.
                    intent.getLongExtra(DownloadHelperContract.EXTRA_VERSION_CODE, 0L)
                        .takeIf { it > 0L }
                        ?: intent.getIntExtra(DownloadHelperContract.EXTRA_VERSION_CODE, 0)
                            .toLong()
                            .takeIf { it > 0L }
                } else {
                    null
                },
                versionCodes = intent
                    .getLongArrayExtra(DownloadHelperContract.EXTRA_VERSION_CODES)
                    ?.filter { it > 0L }
                    ?.toSet()
                    .orEmpty(),
                compatibleVersionNames = intent
                    .getStringArrayListExtra(DownloadHelperContract.EXTRA_COMPATIBLE_VERSION_NAMES)
                    ?.toSet()
                    .orEmpty(),
                compatibleVersionCodes = intent
                    .getLongArrayExtra(DownloadHelperContract.EXTRA_COMPATIBLE_VERSION_CODES)
                    ?.filter { it > 0L }
                    ?.toSet()
                    .orEmpty(),
                supportedAbis = intent
                    .getStringArrayExtra(DownloadHelperContract.EXTRA_SUPPORTED_ABIS)
                    ?.toList()
                    .orEmpty(),
                requestedFileType = intent.getStringExtra(DownloadHelperContract.EXTRA_FILE_TYPE)
                    ?: intent.getStringExtra(DownloadHelperContract.EXTRA_REQUESTED_FILE_TYPE),
                allowSplitArchive = intent.getBooleanExtra(DownloadHelperContract.EXTRA_ALLOW_SPLIT_ARCHIVE, false),
                stockInstallRequired = intent.getBooleanExtra(
                    DownloadHelperContract.EXTRA_STOCK_INSTALL_REQUIRED,
                    intent.getBooleanExtra(DownloadHelperContract.EXTRA_INSTALL_STOCK_AFTER_DOWNLOAD, false)
                ),
                fallbackWebUrl = intent.getStringExtra(DownloadHelperContract.EXTRA_FALLBACK_WEB_URL)
                    ?: "https://www.apkmirror.com/?post_type=app_release&searchtype=app&s=$packageName",
                sourceHintUrls = intent
                    .getStringArrayListExtra(DownloadHelperContract.EXTRA_SOURCE_HINT_URLS)
                    .orEmpty()
            )
        }
    }
}

private data class CandidateResult(
    val sourceGroups: List<SourceCandidateGroup>
) {
    fun withResolveState(
        source: DownloadSource,
        option: CandidateOption,
        state: ResolveState
    ): CandidateResult = copy(
        sourceGroups = sourceGroups.map { group ->
            if (group.source != source) {
                group
            } else {
                when (option) {
                    CandidateOption.REQUESTED -> group.copy(recommended = state)
                    CandidateOption.LATEST -> group.copy(latest = state)
                    CandidateOption.MANUAL -> group
                }
            }
        }
    )

    fun withHistoryState(
        source: DownloadSource,
        state: VersionHistoryState
    ): CandidateResult = copy(
        sourceGroups = sourceGroups.map { group ->
            if (group.source == source) group.copy(history = state) else group
        }
    )

    fun markHistoryCandidateNoDirectDownload(
        source: DownloadSource,
        candidateKey: String
    ): CandidateResult = copy(
        sourceGroups = sourceGroups.map { group ->
            if (group.source == source && group.history is VersionHistoryState.Done) {
                val done = group.history as VersionHistoryState.Done
                group.copy(
                    history = done.copy(
                        noDirectDownloadKeys = done.noDirectDownloadKeys + candidateKey
                    )
                )
            } else {
                group
            }
        }
    )
}

private data class SourceCandidateGroup(
    val source: DownloadSource,
    val manual: List<DownloadCandidate>,
    val recommended: ResolveState,
    val latest: ResolveState,
    val history: VersionHistoryState = VersionHistoryState.Idle
)

private data class ResolveOutcome(
    val candidates: List<DownloadCandidate>,
    val errorMessage: String? = null,
    val fallbackCandidate: DownloadCandidate? = null,
    val notFoundMessage: String? = null
)

private sealed interface ResolveState {
    data object Idle : ResolveState
    data object Loading : ResolveState
    data class Done(val candidates: List<DownloadCandidate>) : ResolveState
    data class Error(
        val message: String,
        val fallbackCandidate: DownloadCandidate? = null
    ) : ResolveState
}

private sealed interface VersionHistoryState {
    data object Idle : VersionHistoryState
    data object Loading : VersionHistoryState
    data class Done(
        val candidates: List<DownloadCandidate>,
        // Identity keys of candidates whose direct download could not be
        // resolved; those rows render an "Open link" action instead of
        // "Download" so the user is not stuck in a resolve-then-error loop.
        val noDirectDownloadKeys: Set<String> = emptySet()
    ) : VersionHistoryState
    data class Error(val message: String) : VersionHistoryState
}



internal data class ApkMirrorLatestInfo(
    val versionName: String?,
    val openUrl: String
)

internal data class ApkMirrorVariant(
    val url: String,
    val type: String,
    val fileKind: String,
    val arch: String?,
    val dpi: String?,
    val isBundle: Boolean
)

internal data class UptodownVersionResponse(
    val data: List<UptodownVersionEntry> = emptyList()
)

internal data class UptodownVersionEntry(
    @SerializedName("fileID")
    val fileId: Long? = null,
    val version: String? = null,
    @SerializedName("kindFile")
    val kindFile: String? = null,
    @SerializedName("titleKindFile")
    val titleKindFile: String? = null,
    @SerializedName("versionURL")
    val versionUrl: UptodownVersionUrl? = null
)

internal data class UptodownVersionUrl(
    val url: String? = null,
    @SerializedName("extraURL")
    val extraUrl: String? = null,
    @SerializedName("versionID")
    val versionId: Long? = null
)

internal data class UptodownVariantResponse(
    val content: String? = null
)

internal data class UptodownVariantFile(
    val fileId: String,
    val fileKind: String,
    val archLabel: String?
)

internal data class DownloadCandidate(
    val source: DownloadSource,
    val name: String,
    val packageName: String,
    val versionName: String?,
    val versionCode: Long?,
    val url: String,
    val fileKind: String,
    val option: CandidateOption,
    val directDownload: Boolean,
    val versionStatus: VersionStatus,
    val formatMatches: Boolean,
    val note: String? = null,
    val variantLabel: String? = null,
    val files: List<CandidateDownloadFile> = emptyList(),
    // When set, the source gates the file behind a captcha/JS challenge that
    // only a real browser can pass. The card then offers a "Solve captcha in
    // app" action that opens this URL in an embedded WebView and captures the
    // download the page produces.
    val captchaUrl: String? = null
) {
    val sortIndex: Int get() = source.sortIndex
    val versionDisplay: String
        get() = when {
            versionName != null && versionCode != null -> "$versionName ($versionCode)"
            versionName != null -> versionName
            option == CandidateOption.MANUAL -> "Manual"
            option == CandidateOption.REQUESTED -> "Requested search"
            else -> "Latest"
        }
}

private data class CandidateMatchSummary(
    val matches: Boolean,
    val title: String,
    val details: List<String>
)

private fun DownloadCandidate.matchSummary(request: HelperRequest): CandidateMatchSummary {
    val requestedVersionNames = request.requestedVersionNames
    val versionNameMatches = requestedVersionNames.isEmpty() ||
        (versionName != null && requestedVersionNames.any { versionName.versionNameEquals(it) })
    val requestedVersionCodes = request.requestedVersionCodes
    val versionCodeMatches = requestedVersionCodes.isEmpty() ||
        versionCode == null ||
        versionCode in requestedVersionCodes
    val formatMatches = fileKind.equals("web", ignoreCase = true) || request.acceptsFormat(fileKind)

    val mismatchNames = buildList {
        if (!versionNameMatches) add("Version")
        if (!versionCodeMatches) add("Version code")
        if (!formatMatches) add("Format")
    }
    val details = buildList {
        if (!versionNameMatches) {
            add("Version: requested ${requestedVersionNames.joinToString().ifBlank { "Any" }}, found ${versionName ?: "Unknown"}")
        }
        if (!versionCodeMatches) {
            add("Version code: requested ${requestedVersionCodes.joinToString()}, found $versionCode")
        }
        if (!formatMatches) {
            add("Format: requested ${request.requestedFormatLabel}, found ${fileKind.uppercase()}")
        }
    }
    val matches = mismatchNames.isEmpty()

    return CandidateMatchSummary(
        matches = matches,
        title = if (matches) {
            "Same as recommended version"
        } else {
            "${mismatchNames.joinToString(" / ")} mismatch"
        },
        details = details
    )
}

internal fun DownloadCandidate.identityKey(): String =
    "${source.name}:$versionName:$versionCode:$fileKind:$variantLabel:$url"

internal data class CandidateDownloadFile(
    val url: String,
    val fileName: String,
    val size: Long? = null,
    val referer: String? = null,
    val cookieHeader: String? = null,
    // The SHA-256 the source publishes for the file, when it does. When set,
    // the downloaded bytes are checked against it before handoff, catching
    // corrupted or wrong builds outright instead of only a package/version
    // mismatch. (Sources that publish it: APKMirror variant page, Uptodown
    // download page.)
    val expectedSha256: String? = null
)

private data class BrowserDownloadCapture(
    val downloadUrl: String,
    val refererUrl: String? = null,
    val cookieHeader: String? = null
)

private fun capturedDownloadFileName(
    candidate: DownloadCandidate,
    downloadUrl: String,
    fileKind: String
): String {
    val decodedUrl = runCatching { Uri.decode(downloadUrl) }.getOrDefault(downloadUrl)
    val path = runCatching { java.net.URI(decodedUrl).path }.getOrDefault("")
    val fileName = path.substringAfterLast('/').takeIf { it.isNotBlank() && it != "/" }
    val extension = fileName?.substringAfterLast('.', "")?.takeIf { it.isNotBlank() } ?: fileKind
    val baseName = fileName?.takeIf { it.contains('.') }
        ?: "${candidate.packageName}-${candidate.versionName ?: "download"}.$extension"
    return baseName.sanitizeFileName()
}

internal data class DownloadedApkMetadata(
    val packageName: String,
    val versionName: String?,
    val versionCode: Long?
)

internal enum class DownloadSource(
    val label: String,
    val sortIndex: Int,
    val supportsManualArtifactPicker: Boolean = true
) {
    APK_MIRROR("APKMirror", 0),
    UPTODOWN("Uptodown", 1),
    APK_PURE("APKPure", 2),
    APK_COMBO("APKCombo", 3),
    APTOIDE("Aptoide", 4),
    EVOZI("Evozi", 5),
    MI9("Mi9", 6),
    APK_DOWNLOADER("APK Downloader", 7),
    AURORA("Aurora", 8, supportsManualArtifactPicker = false),
    PLAY("Play", 9, supportsManualArtifactPicker = false)
}

internal fun DownloadSource.searchDomain(): String? = when (this) {
    DownloadSource.APK_MIRROR -> "apkmirror.com"
    DownloadSource.UPTODOWN -> "uptodown.com"
    DownloadSource.APK_PURE -> "apkpure.com"
    DownloadSource.APK_COMBO -> "apkcombo.com"
    DownloadSource.APTOIDE -> "aptoide.com"
    DownloadSource.EVOZI -> "apkcube.com"
    DownloadSource.MI9 -> "mi9.com"
    DownloadSource.APK_DOWNLOADER -> "apkdownloader.pages.dev"
    DownloadSource.PLAY -> "play.google.com"
    DownloadSource.AURORA -> null
}

internal enum class CandidateOption {
    MANUAL,
    REQUESTED,
    LATEST
}

private val CandidateOption.labelForLogs: String
    get() = when (this) {
        CandidateOption.MANUAL -> "manual"
        CandidateOption.REQUESTED -> "recommended"
        CandidateOption.LATEST -> "latest"
    }

internal enum class VersionStatus(val label: String) {
    REQUESTED("Requested"),
    COMPATIBLE("Compatible"),
    LATEST("Latest")
}

@Composable
private fun LogLevel.color(): Color = when (this) {
    LogLevel.Info -> MaterialTheme.colorScheme.onSurfaceVariant
    LogLevel.Warning -> warningAccent()
    LogLevel.Error -> MaterialTheme.colorScheme.error
}

private sealed interface UiState {
    data object Idle : UiState
    data object Loading : UiState
    data class Ready(val result: CandidateResult) : UiState
    data class CheckingPickedFile(val candidate: DownloadCandidate) : UiState
    data class Downloading(
        val candidate: DownloadCandidate,
        val percent: Int,
        val speedBytesPerSec: Double = 0.0,
        val etaMs: Long? = null,
        val statusMessage: String? = null
    ) : UiState
    data class Error(val message: String) : UiState
    data class FastMode(val progress: FastModeProgress) : UiState
    data class ScanResult(
        val candidate: DownloadCandidate,
        val scanResult: VirusTotalScanner.ScanResult,
        val detail: String,
        val isMalicious: Boolean,
        /** True if the downloaded file's bytes matched the source-published SHA-256. */
        val shaVerified: Boolean = false
    ) : UiState
    data class ScanAsk(val candidate: DownloadCandidate) : UiState
}

private enum class FastModeChoice { USE, NEXT }

private sealed interface FastModeFindResult {
    data class Exact(val candidate: DownloadCandidate) : FastModeFindResult
    data class VersionMismatch(val candidate: DownloadCandidate) : FastModeFindResult
    object None : FastModeFindResult
}

private data class FastModeProgress(
    val sourceLabel: String? = null,
    val detail: String = "Starting…",
    val percent: Int? = null,
    val speedBytesPerSec: Double = 0.0,
    val etaMs: Long? = null,
    val done: Boolean = false,
    val succeeded: Boolean = false,
    // Present once Fast Mode finishes so the regular source list stays reachable.
    val result: CandidateResult? = null,
    // Set while Fast Mode waits for the user to accept a version-code mismatch.
    val awaitingDecision: Boolean = false,
    val mismatchDetail: String? = null,
    // True while Fast Mode (ALWAYS_ASK) asks which version to fetch.
    val awaitingVersionChoice: Boolean = false,
    val versionChoiceDetail: String? = null,
    // The requested version shown on the ALWAYS_ASK prompt, so the user can
    // compare it against the newest version each source offers.
    val versionChoiceRequested: String? = null,
    // True once the downloaded file's bytes matched the source-published SHA-256;
    // shown persistently on the card (not just the transient post-download status).
    val shaVerified: Boolean = false
)

internal object DownloadHelperContract {
    const val ACTION_DOWNLOAD_ORIGINAL_APK = "app.morphe.manager.action.DOWNLOAD_ORIGINAL_APK"
    const val EXTRA_PROTOCOL_VERSION = "app.morphe.manager.extra.PROTOCOL_VERSION"
    const val EXTRA_CALLER_PACKAGE = "app.morphe.manager.extra.CALLER_PACKAGE"
    const val EXTRA_PACKAGE_NAME = "app.morphe.manager.extra.PACKAGE_NAME"
    const val EXTRA_APP_NAME = "app.morphe.manager.extra.APP_NAME"
    const val EXTRA_VERSION_NAME = "app.morphe.manager.extra.VERSION_NAME"
    const val EXTRA_VERSION_CODE = "app.morphe.manager.extra.VERSION_CODE"
    const val EXTRA_VERSION_CODES = "app.morphe.manager.extra.VERSION_CODES"
    const val EXTRA_COMPATIBLE_VERSION_NAMES = "app.morphe.manager.extra.COMPATIBLE_VERSION_NAMES"
    const val EXTRA_COMPATIBLE_VERSION_CODES = "app.morphe.manager.extra.COMPATIBLE_VERSION_CODES"
    const val EXTRA_SUPPORTED_ABIS = "app.morphe.manager.extra.SUPPORTED_ABIS"
    const val EXTRA_FILE_TYPE = "app.morphe.manager.extra.FILE_TYPE"
    const val EXTRA_REQUESTED_FILE_TYPE = "app.morphe.manager.extra.REQUESTED_FILE_TYPE"
    const val EXTRA_ALLOW_SPLIT_ARCHIVE = "app.morphe.manager.extra.ALLOW_SPLIT_ARCHIVE"
    const val EXTRA_STOCK_INSTALL_REQUIRED = "app.morphe.manager.extra.STOCK_INSTALL_REQUIRED"
    const val EXTRA_INSTALL_STOCK_AFTER_DOWNLOAD = "app.morphe.manager.extra.INSTALL_STOCK_AFTER_DOWNLOAD"
    const val EXTRA_FALLBACK_WEB_URL = "app.morphe.manager.extra.FALLBACK_WEB_URL"
    const val EXTRA_SOURCE_HINT_URLS = "app.morphe.manager.extra.SOURCE_HINT_URLS"
    const val EXTRA_RESULT_USE_INSTALLED_APP = "app.morphe.manager.extra.RESULT_USE_INSTALLED_APP"
    const val EXTRA_RESULT_PACKAGE_NAME = "app.morphe.manager.extra.RESULT_PACKAGE_NAME"
    const val EXTRA_RESULT_VERSION_NAME = "app.morphe.manager.extra.RESULT_VERSION_NAME"
    const val EXTRA_RESULT_SOURCE_NAME = "app.morphe.manager.extra.RESULT_SOURCE_NAME"
    const val EXTRA_RESULT_FILE_NAME = "app.morphe.manager.extra.RESULT_FILE_NAME"
}

internal interface ApkPureApi {
    @Headers(
        "content-type: application/json",
        "ual-access-businessid: projecta"
    )
    @POST("v3/get_app_update")
    suspend fun getAppUpdate(
        @Header("ual-access-projecta") header: String,
        @Body request: ApkPureUpdateRequest
    ): ApkPureUpdateResponse
}

internal data class ApkPureUpdateRequest(
    val app_info_for_update: List<ApkPureAppInfo>,
    val android_id: String = Random.nextLong().toString(16),
    val application_id: String = "com.apkpure.aegon",
    val cached_size: Long = -1
)

internal data class ApkPureAppInfo(
    val package_name: String,
    val version_code: Long,
    val is_system: Boolean = false,
    val version_id: String = "",
    val cached_size: Int = -1
)

internal data class ApkPureUpdateResponse(
    val retcode: Int = 0,
    val app_update_response: List<ApkPureAppUpdate> = emptyList()
)

internal data class ApkPureAppUpdate(
    val package_name: String = "",
    val version_code: Long = 0L,
    val version_name: String = "",
    val label: String = "",
    val asset: ApkPureAsset = ApkPureAsset()
)

internal data class ApkPureAsset(
    val type: String = "",
    val url: String = ""
)

internal data class ApkPureVersionEntry(
    val versionName: String?,
    val versionCode: Long?,
    val downloadPageUrl: String,
    val fileKind: String
)

internal data class ApkPureDeviceHeader(
    val device_info: ApkPureDeviceInfo = ApkPureDeviceInfo()
)

internal data class ApkPureDeviceInfo(
    val abis: List<String> = runCatching { Build.SUPPORTED_ABIS.toList() }.getOrDefault(emptyList()),
    val android_id: String = Random.nextLong().toString(16),
    val os_ver: String = runCatching { Build.VERSION.SDK_INT.toString() }.getOrDefault(""),
    val os_ver_name: String = runCatching { Build.VERSION.RELEASE }.getOrNull() ?: "",
    val platform: Int = 1
)

internal interface AptoideApi {
    @POST("listSearchApps")
    suspend fun searchApps(@Body request: AptoideSearchRequest): AptoideSearchResponse

    @GET("getApp")
    suspend fun getAppByPackage(@Query("package_name") packageName: String): AptoideGetAppResponse

    @GET("getApp")
    suspend fun getAppById(@Query("app_id") appId: Long): AptoideGetAppResponse

    @GET("listAppVersions")
    suspend fun listAppVersionsByPackage(
        @Query("package_name") packageName: String,
        @Query("limit") limit: Long = 100L
    ): AptoideVersionListResponse

    @GET("listAppVersions")
    suspend fun listAppVersionsById(
        @Query("app_id") appId: Long,
        @Query("limit") limit: Long = 100L
    ): AptoideVersionListResponse
}

internal data class AptoideSearchRequest(
    val query: String = "",
    val limit: String = "10",
    val q: String? = null,
    val not_apk_tags: String = "alpha,beta",
    val store_ids: List<Long>? = listOf(15L, 711454L)
)

internal data class AptoideSearchResponse(
    val datalist: AptoideDataList = AptoideDataList()
)

internal data class AptoideDataList(
    val list: List<AptoideApp> = emptyList()
)

internal data class AptoideGetAppResponse(
    val nodes: AptoideNodes = AptoideNodes()
)

internal data class AptoideVersionListResponse(
    val list: List<AptoideApp> = emptyList()
)

internal data class AptoideNodes(
    val meta: AptoideMetaNode = AptoideMetaNode()
)

internal data class AptoideMetaNode(
    val data: AptoideApp = AptoideApp()
)

internal data class AptoideNextData(
    val props: AptoideNextProps = AptoideNextProps()
)

internal data class AptoideNextProps(
    val pageProps: AptoidePageProps = AptoidePageProps()
)

internal data class AptoidePageProps(
    val app: AptoideApp = AptoideApp(),
    val packageName: String = "",
    val versions: List<AptoideVersionItem> = emptyList()
)

internal data class AptoideVersionItem(
    val id: Long = 0L,
    val name: String = "",
    val vername: String = "",
    val vercode: Long = 0L
)

internal data class AptoideApp(
    val id: Long = 0L,
    val name: String = "",
    @SerializedName("package")
    val packageName: String = "",
    val file: AptoideFile = AptoideFile(),
    val urls: AptoideUrls = AptoideUrls()
)

internal data class AptoideFile(
    val vername: String = "",
    val vercode: String = "0",
    val path: String = "",
    @SerializedName(value = "path_alt", alternate = ["pathAlt"])
    val pathAlt: String = ""
)

internal data class AptoideUrls(
    val w: String = "",
    val m: String = ""
)

internal fun playStoreUrl(packageName: String): String =
    "https://play.google.com/store/apps/details?id=${URLEncoder.encode(packageName, "UTF-8")}"

internal fun fileKindFromTags(tags: List<String>, request: HelperRequest): String {
    val available = tags
        .map { it.trim().lowercase(Locale.US) }
        .filter { it in DOWNLOAD_FILE_KIND_SET }
        .distinct()
    val requested = available.firstOrNull { it in request.requestedFileKinds }

    return requested
        ?: available.firstOrNull { request.acceptsFormat(it) }
        ?: available.firstOrNull()
        ?: "web"
}

private fun Context.openPlayStoreListing(packageName: String, fallbackUrl: String) {
    val marketIntent = Intent(
        Intent.ACTION_VIEW,
        Uri.parse("market://details?id=${Uri.encode(packageName)}")
    ).apply {
        setPackage("com.android.vending")
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(fallbackUrl)).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    runCatching { startActivity(marketIntent) }
        .onFailure { startActivity(webIntent) }
}

private fun Context.isPackageInstalled(packageName: String): Boolean =
    runCatching {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
        } else {
            @Suppress("DEPRECATION")
            packageManager.getPackageInfo(packageName, 0)
        }
    }.isSuccess

internal fun String.normalizedHttpUrlOrNull(): String? {
    val normalized = trim().let { url ->
        when {
            url.startsWith("//") -> "https:$url"
            url.startsWith("http://", ignoreCase = true) ||
                url.startsWith("https://", ignoreCase = true) -> url
            else -> return null
        }
    }

    return runCatching {
        Request.Builder().url(normalized)
        normalized
    }.getOrNull()
}

private fun validateApkLikeStream(
    input: java.io.InputStream,
    contentType: String?
): java.io.InputStream {
    val header = ByteArray(4)
    val headerSize = input.read(header)
    val isZip = headerSize >= 2 &&
        header[0] == 'P'.code.toByte() &&
        header[1] == 'K'.code.toByte()

    check(isZip) {
        val type = contentType?.let { " ($it)" }.orEmpty()
        "Source did not return a valid APK/APKS/XAPK$type."
    }

    return SequenceInputStream(ByteArrayInputStream(header, 0, headerSize), input)
}

internal fun Context.readDownloadedApkMetadata(file: File): DownloadedApkMetadata? {
    if (file.extension.equals("apk", ignoreCase = true)) {
        return readApkMetadata(file)
    }

    return runCatching {
        val validationDir = File(cacheDir, "validation").apply { mkdirs() }
        ZipFile(file).use { zip ->
            // The base APK inside split containers is not always named "base.apk":
            // some sources (e.g. APKPure XAPKs) name it after the package. Trying
            // the first alphabetical .apk entry is wrong  config splits like
            // config.ar.apk carry no manifest and fail to parse. Prefer the real
            // base: exact base.apk, then a name matching the package (derived from
            // the output file name), then the largest entry, and take the first
            // entry that actually reads as an APK.
            val packageHint = file.nameWithoutExtension
                .substringBefore('-')
                .lowercase(Locale.US)
            val apkEntries = zip.entries()
                .asSequence()
                .filter { !it.isDirectory && it.name.endsWith(".apk", ignoreCase = true) }
                .sortedWith(
                    compareBy<java.util.zip.ZipEntry> { entry ->
                        val name = entry.name.substringAfterLast('/').lowercase(Locale.US)
                        when {
                            name == "base.apk" -> 0
                            packageHint.isNotEmpty() && name.contains(packageHint) -> 1
                            name.contains("base") -> 2
                            else -> 3
                        }
                    }
                        .thenByDescending { it.size }
                        .thenBy { it.name }
                )
                .toList()
            var metadata: DownloadedApkMetadata? = null
            for (entry in apkEntries) {
                val extracted = File(
                    validationDir,
                    "${file.nameWithoutExtension}-${entry.name.hashCode()}.apk".sanitizeFileName()
                )
                zip.getInputStream(entry).use { input ->
                    extracted.outputStream().use { output -> input.copyTo(output) }
                }
                val read = readApkMetadata(extracted)
                extracted.delete()
                if (read != null) {
                    metadata = read
                    break
                }
            }
            metadata
        }
    }.getOrNull()
}

@Suppress("DEPRECATION")
private fun Context.readApkMetadata(file: File): DownloadedApkMetadata? {
    val info = packageManager.getPackageArchiveInfo(file.absolutePath, PackageManager.GET_META_DATA)
        ?: return null
    val versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        info.longVersionCode
    } else {
        info.versionCode.toLong()
    }.takeIf { it > 0L }

    return DownloadedApkMetadata(
        packageName = info.packageName,
        versionName = info.versionName,
        versionCode = versionCode
    )
}

internal fun parseInfoTableValue(doc: Document, label: String): String? =
    doc.select("tr")
        .firstOrNull { it.select("th").text().equals(label, ignoreCase = true) }
        ?.select("td")
        ?.lastOrNull()
        ?.text()
        ?.trim()
        ?.takeIf(String::isNotBlank)

internal fun fileKindFromUrl(url: String): String {
    val decoded = URLDecoder.decode(url, StandardCharsets.UTF_8.name()).lowercase(Locale.US)
    // Kind detection must be scoped to the file name (final path segment): CDN
    // hosts and APKMirror's own download.php path embed "apkmirror", so a bare
    // substring check for "apkm" wrongly matches and labels plain APKs as APKM
    // bundles, which then fail validation as containers.
    val path = runCatching { java.net.URI(decoded).path }.getOrDefault("")
    val fileName = path.substringAfterLast('/')
    val extension = fileName.substringAfterLast('.', "")
    val fileNameKind = Regex("""filename[^.]*\.(apk|apks|apkm|xapk)""")
        .find(decoded)
        ?.groupValues
        ?.getOrNull(1)
    // Some CDNs expose a bundle only in the path segment rather than the file
    // name (e.g. APKPure's d.apkpure.com/b/XAPK/<pkg>?versionCode=N), where the
    // last segment is the package, not the archive. Match those segments too so
    // an XAPK/APKS/APKM link isn't mislabelled as a plain APK (which later
    // fails archive validation). Scoped to whole segments to avoid the
    // apkmirror/dowload.php substring false-positives the extension check
    // already avoids.
    val segments = path.split('/').filter { it.isNotBlank() }
    val segmentKinds = setOf("apks", "apkm", "xapk")
    val segmentKind = segments.asReversed().firstOrNull { it in segmentKinds }
    return when {
        extension in setOf("apk", "apks", "apkm", "xapk") -> extension
        fileNameKind != null -> fileNameKind
        segmentKind != null -> segmentKind
        "xapk" in fileName -> "xapk"
        "apks" in fileName -> "apks"
        "apkm" in fileName -> "apkm"
        else -> "apk"
    }
}

internal fun String.slugForUrl(): String =
    lowercase(Locale.US)
        .replace("&", " and ")
        .replace("'", "")
        .replace(Regex("[^a-z0-9]+"), "-")
        .trim('-')
        .ifBlank { "app" }

internal fun String.apkMirrorVersionSlug(): String =
    lowercase(Locale.US)
        .replace(".", "-")
        .replace("_", "-")
        .replace(Regex("[^a-z0-9-]+"), "-")
        .replace(Regex("-+"), "-")
        .trim('-')

internal fun String?.versionNameEquals(other: String?): Boolean {
    if (this == null || other == null) return false
    val left = normalizedVersionName()
    val right = other.normalizedVersionName()
    if (left.isBlank() || right.isBlank()) return false
    if (left == right) return true

    val leftParts = left.versionNumberParts()
    val rightParts = right.versionNumberParts()
    return leftParts.isNotEmpty() && leftParts == rightParts
}

/**
 * A downloaded result belongs to a request only when the package matches and
 * the file's version satisfies the request (or the request didn't pin one).
 * Used to stop a stale result  from a previous session's pending file or a
 * replayed [DownloadJobManager.Event.Completed]  from being handed to a
 * different request.
 */
internal fun PendingDownloadResult.belongsTo(request: HelperRequest?): Boolean {
    if (request == null) return false
    if (requestPackage != request.packageName) return false
    val requestedName = request.requestedVersionName ?: return true
    val candidateName = versionName ?: return true
    return candidateName.versionNameEquals(requestedName)
}

/**
 * A live completion event belongs to the current session when the package
 * matches the request on screen and the event was produced by the session
 * currently in flight (see [DownloadJobManager.currentEpoch]). Unlike
 * [belongsTo], the version is deliberately not compared: within the same
 * session the user may have chosen a different version (Latest/History tab)
 * and that file must still be returned to the caller instead of being
 * silently discarded, which previously left the app frozen at 100%.
 */
internal fun PendingDownloadResult.belongsToCurrentSession(
    request: HelperRequest?,
    epoch: Long
): Boolean =
    request != null &&
        requestPackage == request.packageName &&
        epoch == DownloadJobManager.currentEpoch

internal fun String.withoutTrailingVersionCode(): String =
    replace(Regex("""\s*\(\s*\d+\s*\)\s*$"""), "")
        .trim()

internal fun String.trailingVersionCode(): Long? =
    Regex("""\(\s*(\d+)\s*\)\s*$""")
        .find(this)
        ?.groupValues
        ?.getOrNull(1)
        ?.toLongOrNull()

internal fun String.normalizedVersionName(): String =
    withoutTrailingVersionCode()
        .lowercase(Locale.US)
        .replace(Regex("""\b(version|ver|v|release|stable|apk|xapk|apkm|apks|bundle)\b"""), " ")
        .replace(Regex("""[^\p{Alnum}]+"""), ".")
        .trim('.')

internal fun String.withManualModeHint(): String {
    if (contains("Manual mode", ignoreCase = true)) return this
    val message = trimEnd()
    val hint = "Use Manual mode for this source instead."
    return if (message.contains('\n')) "$message\n$hint" else "$message $hint"
}

internal fun sourceFailureMessage(source: DownloadSource, error: Throwable): String =
    sourceFailureMessage(source.label, error, action = "check")

internal fun downloadFailureMessage(candidate: DownloadCandidate, error: Throwable): String =
    sourceFailureMessage(candidate.source.label, error, action = "download")

internal fun sourceFailureMessage(sourceLabel: String, error: Throwable, action: String): String {
    val details = error.failureDetails()
    val httpCode = Regex("""\bHTTP\s+(\d{3})\b""", RegexOption.IGNORE_CASE)
        .find(details)
        ?.groupValues
        ?.getOrNull(1)
    val actionText = if (action == "download") "download" else "check"

    return when {
        httpCode == "403" -> {
            "$sourceLabel blocked automated access (HTTP 403), likely due to bot protection. Open the link and download manually."
        }
        httpCode == "429" -> {
            "$sourceLabel rate-limited the helper (HTTP 429). Try again later or use Manual mode."
        }
        httpCode == "404" -> {
            "$sourceLabel did not have the requested page (HTTP 404). Use Manual mode for this source instead."
        }
        httpCode != null -> {
            "$sourceLabel returned HTTP $httpCode during $actionText. Use Manual mode for this source instead."
        }
        details.contains("cloudflare", ignoreCase = true) -> {
            "$sourceLabel showed a browser verification page, so direct access is blocked. Open the link and download manually."
        }
        details.contains("timeout", ignoreCase = true) -> {
            "$sourceLabel took too long to respond. Try again or use Manual mode."
        }
        details.contains("Unable to resolve host", ignoreCase = true) ||
            details.contains("failed to connect", ignoreCase = true) -> {
            "Could not connect to $sourceLabel. Check your connection or use Manual mode."
        }
        else -> {
            "Could not $actionText $sourceLabel: ${details.ifBlank { "unknown error" }}".withManualModeHint()
        }
    }
}

internal fun Throwable.failureDetails(): String =
    generateSequence(this) { it.cause }
        .mapNotNull { it.message?.trim()?.takeIf(String::isNotBlank) }
        .firstOrNull()
        ?: javaClass.simpleName

internal fun sourceVersionFromText(text: String): String? =
    Regex("""\b(v?\d+(?:[._-]\d+)+(?:[-.][A-Za-z0-9]+)?)\b""", RegexOption.IGNORE_CASE)
        .find(text)
        ?.value
        ?.trim()

internal fun compareVersionNames(left: String?, right: String?): Int {
    if (left == right) return 0
    if (left == null) return -1
    if (right == null) return 1

    val leftParts = left.versionNumberParts()
    val rightParts = right.versionNumberParts()
    val size = maxOf(leftParts.size, rightParts.size)
    for (index in 0 until size) {
        val leftPart = leftParts.getOrElse(index) { 0 }
        val rightPart = rightParts.getOrElse(index) { 0 }
        if (leftPart != rightPart) return leftPart.compareTo(rightPart)
    }

    return left.compareTo(right, ignoreCase = true)
}

internal fun String.versionNumberParts(): List<Int> =
    Regex("""\d+""")
        .findAll(this)
        .mapNotNull { it.value.toIntOrNull() }
        .toList()

private fun requestedFileKindsFrom(rawFileType: String?, allowSplitArchive: Boolean): Set<String> {
    val normalized = rawFileType?.lowercase(Locale.US).orEmpty()
    val explicitKinds = DOWNLOAD_FILE_KIND_REGEX
        .findAll(normalized)
        .map { it.value.lowercase(Locale.US) }
        .toMutableSet()
    if (explicitKinds.isEmpty() && "package-archive" in normalized) {
        explicitKinds.add("apk")
    }

    if (explicitKinds.isEmpty()) {
        return if (allowSplitArchive) DOWNLOAD_FILE_KIND_SET else setOf("apk")
    }

    if (allowSplitArchive && "apk" in explicitKinds) {
        explicitKinds.addAll(SPLIT_ARCHIVE_FILE_KINDS)
    }

    return explicitKinds
}

private fun Collection<String>.orderedFileKinds(): List<String> {
    val knownKinds = DOWNLOAD_FILE_KIND_ORDER.filter { it in this }
    val extraKinds = filter { it !in DOWNLOAD_FILE_KIND_SET }.distinct()
    return knownKinds + extraKinds
}

internal fun String?.variantFileSuffix(): String =
    this
        ?.lowercase(Locale.US)
        ?.replace(Regex("""[^a-z0-9._-]+"""), "-")
        ?.trim('-')
        ?.takeIf(String::isNotBlank)
        ?.let { "-$it" }
        .orEmpty()

internal fun String.sanitizeFileName(): String =
    replace(Regex("[^A-Za-z0-9._-]"), "_")
