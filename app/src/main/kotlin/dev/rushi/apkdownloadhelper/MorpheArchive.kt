package dev.rushi.apkdownloadhelper

import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

/**
 * Models + fetcher for the Morphe archive index that powers the "Find New
 * Apps" browser. The index is deliberately NOT cached: every open fetches the
 * current JSON live so the list always reflects the archive right now.
 */
internal data class MorpheArchiveData(
    @SerializedName("generatedAt") val generatedAt: String? = null,
    @SerializedName("apps") val apps: List<ArchiveApp> = emptyList()
)

internal data class ArchiveApp(
    @SerializedName("packageName") val packageName: String = "",
    @SerializedName("name") val name: String = "",
    @SerializedName("patches") val patches: List<String> = emptyList(),
    @SerializedName("patchDetails") val patchDetails: List<ArchivePatch> = emptyList(),
    @SerializedName("versions") val versions: List<String> = emptyList(),
    @SerializedName("sources") val sources: List<ArchiveSource> = emptyList(),
    @SerializedName("iconColor") val iconColor: String? = null,
    @SerializedName("iconUrl") val iconUrl: String? = null
) {
    val patchCount: Int
        get() = patches.size

    /** Number of distinct source repos/bundles offering patches for this app. */
    val sourceCount: Int
        get() = sources.size
}

internal data class ArchivePatch(
    @SerializedName("name") val name: String = "",
    @SerializedName("description") val description: String? = null
)

internal data class ArchiveSource(
    @SerializedName("repo") val repo: String = "",
    @SerializedName("host") val host: String? = null,
    @SerializedName("webUrl") val webUrl: String? = null,
    @SerializedName("addUrl") val addUrl: String? = null,
    @SerializedName("patches") val patches: List<ArchivePatch> = emptyList(),
    /** The repo's latest release, used to rank otherwise identical sources. */
    @SerializedName("latestChanges") val latestChanges: ArchiveChanges? = null
)

/** A repo's most recent release, as published in its patch bundle. */
internal data class ArchiveChanges(
    @SerializedName("title") val title: String = "",
    @SerializedName("date") val date: String? = null
)

/** One patch set for an app: the repo carrying it, plus any repos carrying the same one. */
internal data class ArchiveSourceGroup(
    val primary: ArchiveSource,
    val mirrors: List<ArchiveSource>
)

/**
 * Collapses [sources] into one entry per distinct patch set.
 *
 * A fork re-releases its upstream's patches under its own name, so the index
 * lists both and a mirror ends up looking like an independent choice. The set of
 * patch names is what identifies a group, so identical sets in a different order
 * still count as one.
 *
 * The newest release leads each group, and the groups, so the maintained copy is
 * the one on top. Ties keep the index's own order, the sorts being stable.
 */
internal fun groupArchiveSources(sources: List<ArchiveSource>): List<ArchiveSourceGroup> {
    val byPatchSet = LinkedHashMap<String, MutableList<ArchiveSource>>()
    sources.forEach { source ->
        val key = source.patches.map { it.name }.sorted().joinToString("\u0000")
        byPatchSet.getOrPut(key) { mutableListOf() }.add(source)
    }
    return byPatchSet.values
        .map { members ->
            val ranked = members.sortedByDescending { it.latestChanges?.date.orEmpty() }
            ArchiveSourceGroup(primary = ranked.first(), mirrors = ranked.drop(1))
        }
        .sortedByDescending { it.primary.latestChanges?.date.orEmpty() }
}

/**
 * Fetches the Morphe archive index over the network on every call.
 * Returns the apps list, or throws so the caller can surface an error state.
 */
internal object MorpheArchive {

    const val INDEX_URL =
        "https://raw.githubusercontent.com/rushiforai/morphe-archive/refs/heads/main/docs/data.json"

    private const val TAG = "MorpheArchive"
    private const val CONNECT_TIMEOUT_S = 10L
    private const val READ_TIMEOUT_S = 45L

    /** Shared client: used for the index fetch and for source avatars. */
    val http = OkHttpClient.Builder()
        .connectTimeout(CONNECT_TIMEOUT_S, TimeUnit.SECONDS)
        .readTimeout(READ_TIMEOUT_S, TimeUnit.SECONDS)
        .followRedirects(true)
        .build()

    suspend fun fetchApps(): List<ArchiveApp> = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url(INDEX_URL)
            .header(
                "User-Agent",
                "Mozilla/5.0 (Linux; Android 14) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/125.0 Mobile Safari/537.36"
            )
            .header("Accept", "application/json")
            .build()
        http.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                error("Index request failed: HTTP ${response.code}")
            }
            val body = response.body?.string().orEmpty()
            val parsed = gson.fromJson(body, MorpheArchiveData::class.java)
            parsed.apps
        }
    }

    /**
     * A source's avatar image URL. GitHub exposes {owner}.png directly;
     * GitLab needs an ID-based URL we can't derive, so it falls back to null
     * (the UI then shows a letter tile).
     */
    fun avatarUrlFor(source: ArchiveSource): String? {
        val owner = source.repo.substringBefore('/').takeIf { it.isNotBlank() } ?: return null
        return when (source.host?.lowercase()) {
            "github.com" -> "https://github.com/$owner.png?size=96"
            else -> null
        }
    }
}
