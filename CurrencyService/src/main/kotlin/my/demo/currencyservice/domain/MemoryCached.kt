package my.demo.currencyservice.domain

import java.time.Instant
import java.time.temporal.TemporalAmount
import java.time.temporal.TemporalUnit
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@ExperimentalTime
abstract class MemoryCached<T: Any>(
    invalidateAfter: Long,
    invalidateTimeUnit: TimeUnit
): Cached<T> {

    abstract suspend fun dataProvider(): T

    private val sharedCounterLock = ReentrantLock()
    private lateinit var data: T
    private var updatedAt: Long = 0

    private val invalidateAfterMills = invalidateTimeUnit.toMillis(invalidateAfter)

    private fun now(): Long = Instant.now().toEpochMilli()

    private suspend fun requestAndSetData(): T {
        val newData = dataProvider()

        try {
            sharedCounterLock.lock()
            data = newData
            updatedAt = now() + invalidateAfterMills
        } finally {
            sharedCounterLock.unlock()
        }


        return newData
    }

    override suspend fun get(): T {

        val updatedAt = try {
            sharedCounterLock.lock()
            updatedAt
        } finally {
            sharedCounterLock.unlock()
        }

        return when {
            now() > updatedAt -> {
                requestAndSetData()
            }
            !::data.isInitialized -> {
                requestAndSetData()
            }
            else -> {
                try {
                    sharedCounterLock.lock()
                    data
                } finally {
                    sharedCounterLock.unlock()
                }
            }
        }
    }

    override fun clean() {
        try {
            sharedCounterLock.lock()
            updatedAt = 0
        } finally {
            sharedCounterLock.unlock()
        }
    }
}