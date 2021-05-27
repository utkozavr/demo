package my.demo.grpcserver

import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import kotlin.math.min

internal class GrpcServerImpTest {

    val grpcServer: GrpcServer
    get() = GrpcServerImp(serverMockk, loggerMockk)

    @BeforeEach
    fun setUp() {
        every { loggerMockk.info(any()) } returns Unit
    }

    @AfterEach
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun start() {
        every { loggerMockk.info(any()) } returns Unit
        every { serverMockk.start() } returns serverMockk
        //grpcServer.start()
        //verify(exactly = 1){ serverMockk.start() }
    }

    @Test
    fun stop() {
        every { serverMockk.shutdown() } returns serverMockk
        grpcServer.stop()
        verify{ loggerMockk.info(any()) }
        verify(exactly = 1){ serverMockk.shutdown() }
    }

    @Test
    fun blockUntilShutdown() {
        every { serverMockk.awaitTermination() } returns Unit
        grpcServer.blockUntilShutdown()
        verify(exactly = 1){ serverMockk.awaitTermination() }
    }
}