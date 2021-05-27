package my.demo.rateserver.dsl

import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.verify
import my.demo.rateserver.exceptionInterceptorDSLBuilderMockk
import my.demo.rateserver.exceptionInterceptorMockk
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class ExceptionInterceptorDSLBuilderKtTest {

    @AfterEach
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun exceptionInterceptorTest() {
        every { exceptionInterceptorDSLBuilderMockk.build() } returns exceptionInterceptorMockk
        exceptionInterceptor(exceptionInterceptorDSLBuilderMockk){}
        verify(exactly = 1) { exceptionInterceptorDSLBuilderMockk.build() }
    }
}