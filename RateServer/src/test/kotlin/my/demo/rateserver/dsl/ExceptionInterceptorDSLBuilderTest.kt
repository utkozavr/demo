package my.demo.rateserver.dsl

import io.mockk.clearAllMocks
import my.demo.rateserver.loggerMockk
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class ExceptionInterceptorDSLBuilderTest {

    @AfterEach
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun build() {

        org.junit.jupiter.api.assertThrows<IllegalStateException> {
            ExceptionInterceptorDSLBuilder().apply {}.build()
        }

        assertDoesNotThrow {
            ExceptionInterceptorDSLBuilder().apply { logger { loggerMockk } }.build()
        }

    }
}