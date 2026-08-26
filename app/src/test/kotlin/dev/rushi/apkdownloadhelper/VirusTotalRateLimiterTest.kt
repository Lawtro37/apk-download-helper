package dev.rushi.apkdownloadhelper

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.concurrent.CancellationException
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class VirusTotalRateLimiterTest {

    @Test
    fun `first call is immediate and records the slot`() {
        val limiter = VirusTotalScanner.RateLimiter(minGapMs = 16_000L)
        val start = System.currentTimeMillis()
        limiter.awaitSlot { false }
        // First slot should be granted with no sleep.
        assertTrue(System.currentTimeMillis() - start < 1500)
        assertTrue(limiter.millisUntilNextSlot() in 1..16_000L)
    }

    @Test
    fun `successive calls wait for the gap`() {
        val limiter = VirusTotalScanner.RateLimiter(minGapMs = 200L)
        limiter.awaitSlot { false }
        assertTrue(limiter.millisUntilNextSlot() in 1..200L)
        val start = System.currentTimeMillis()
        limiter.awaitSlot { false }
        // Second slot must wait out the gap.
        assertTrue(System.currentTimeMillis() - start >= 180L)
    }

    @Test
    fun `cancellation is honoured promptly`() {
        val limiter = VirusTotalScanner.RateLimiter(minGapMs = 5_000L)
        limiter.awaitSlot { false }
        val start = System.currentTimeMillis()
        var cancel = false
        // Flip cancel shortly after the wait starts.
        Thread {
            Thread.sleep(300)
            cancel = true
        }.start()
        assertTrue(
            try {
                limiter.awaitSlot { cancel }
                false
            } catch (e: CancellationException) {
                true
            }
        )
        // Cancellation should land in well under the full 5s gap.
        assertTrue(System.currentTimeMillis() - start < 4000)
    }

    @Test
    fun `skip request releases a pending wait promptly`() {
        val limiter = VirusTotalScanner.RateLimiter(minGapMs = 5_000L)
        limiter.awaitSlot { false }
        val released = CountDownLatch(1)
        val waiter = Thread {
            limiter.awaitSlot { false }
            released.countDown()
        }
        waiter.start()

        Thread.sleep(150)
        limiter.requestSkip()

        assertTrue(released.await(2, TimeUnit.SECONDS))
        waiter.join(500)
    }

    @Test
    fun `millisUntilNextSlot reports zero when a slot is available`() {
        val limiter = VirusTotalScanner.RateLimiter(minGapMs = 16_000L)
        // Fresh limiter: no calls yet.
        assertEquals(0L, limiter.millisUntilNextSlot())
        limiter.awaitSlot { false }
        assertTrue(limiter.millisUntilNextSlot() > 0L)
    }

    @Test
    fun `pace surfaces the wait as progress`() {
        VirusTotalScanner.rateLimiter.awaitSlot { false }
        val messages = mutableListOf<String>()
        // A second pace call must wait; assert the progress callback fired.
        VirusTotalScanner.pace(onProgress = { messages += it })
        assertEquals(1, messages.size)
        assertTrue(messages.first().contains("rate limit"))
    }
}