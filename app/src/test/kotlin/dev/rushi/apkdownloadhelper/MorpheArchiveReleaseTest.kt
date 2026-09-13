package dev.rushi.apkdownloadhelper

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class MorpheArchiveReleaseTest {

    @Test
    fun `a bundle release reads as version and date`() {
        val release = formatBundleRelease(
            ArchiveChanges(title = "1.21.5 (2026-09-08)", date = "2026-09-08")
        )

        assertEquals("v1.21.5 · 8 Sep 2026", release)
    }

    @Test
    fun `a single-digit day is not zero-padded`() {
        val release = formatBundleRelease(
            ArchiveChanges(title = "1.0.0 (2026-09-06)", date = "2026-09-06")
        )

        assertEquals("v1.0.0 · 6 Sep 2026", release)
    }

    @Test
    fun `the date falls back to the title when the bundle omits it`() {
        val release = formatBundleRelease(ArchiveChanges(title = "2.3.4 (2026-08-19)"))

        assertEquals("v2.3.4 · 19 Aug 2026", release)
    }

    @Test
    fun `a version that is not a plain number keeps its own text`() {
        // Bundles name releases freely; only a leading digit gets the `v` prefix.
        val release = formatBundleRelease(
            ArchiveChanges(title = "Fork 1 (2026-08-19)", date = "2026-08-19")
        )

        assertEquals("Fork 1 · 19 Aug 2026", release)
    }

    @Test
    fun `an unreadable date is shown as the bundle wrote it`() {
        // Better to surface whatever the changelog said than to silently drop it.
        val release = formatBundleRelease(ArchiveChanges(title = "1.0.0", date = "last Tuesday"))

        assertEquals("v1.0.0 · last Tuesday", release)
    }

    @Test
    fun `a bundle with only a version still reads`() {
        assertEquals("v1.0.0", formatBundleRelease(ArchiveChanges(title = "1.0.0")))
    }

    @Test
    fun `no declared release shows nothing`() {
        assertNull(formatBundleRelease(null))
        assertNull(formatBundleRelease(ArchiveChanges()))
    }
}
