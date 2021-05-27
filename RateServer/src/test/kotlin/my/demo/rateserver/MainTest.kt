package my.demo.rateserver

import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.spyk
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class MainTest {


    val main: Main
        get() = spyk(Main(
            portDefault = 1,
            args = emptyArray(),
            logger = loggerMockk,
            service = rateServiceImpMockk,
            interceptors = emptyList()
        ))



    @AfterEach
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun getService() {
        every { grpcServerMockk.start() } returns Unit
        every { grpcServerMockk.blockUntilShutdown() } returns Unit
        every { loggerMockk.info(any()) } returns Unit
        assertDoesNotThrow {
            main.start(grpcServerMockk, loggerMockk)
        }

        verify(exactly = 1) { grpcServerMockk.start() }
        verify(exactly = 1) { grpcServerMockk.blockUntilShutdown() }
        verify(exactly = 2) { loggerMockk.info(any()) }
    }

}