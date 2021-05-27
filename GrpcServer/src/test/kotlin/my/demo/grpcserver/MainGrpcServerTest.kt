package my.demo.grpcserver

import com.google.protobuf.Descriptors
import io.grpc.ServerServiceDefinition
import io.grpc.ServiceDescriptor
import io.mockk.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class MainGrpcServerTest {

    val mainGrpcServerAbstract: MainGrpcServer
        get() = spyk<MainGrpcServer>()

    val port = 81
    val defaultPort = 80

    @AfterEach
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `getPort$GrpcServer`() {
        assertEquals(defaultPort, mainGrpcServerAbstract.getPort(emptyArray(), defaultPort))
        assertEquals(port, mainGrpcServerAbstract.getPort(arrayOf("$port"), defaultPort))
        assertEquals(port, mainGrpcServerAbstract.getPort(arrayOf("$port", "fff"), defaultPort))
        org.junit.jupiter.api.assertThrows<Exception> {
            assertEquals(port, mainGrpcServerAbstract.getPort(arrayOf("port", "fff"), defaultPort))
        }
    }

    @Test
    fun `getGrpcServer$GrpcServer`() {


        every { serviceDescriptorMockk.name } returns ""
        every { serverServiceDefinitionMockk.methods } returns emptyList()
        every { serverServiceDefinitionMockk.serviceDescriptor } returns serviceDescriptorMockk
        every { serviceMockk.bindService() } returns serverServiceDefinitionMockk

        assertDoesNotThrow {
            mainGrpcServerAbstract.getGrpcServer(port, loggerMockk, serviceMockk, listOf(serverInterceptorMockk, serverInterceptorMockk))
        }
    }

    @Test
    fun main() {

        every { loggerMockk.info(any()) } returns  Unit
        every { grpcServerMockk.start() } returns Unit
        every { grpcServerMockk.blockUntilShutdown() } returns Unit

        assertDoesNotThrow {
            mainGrpcServerAbstract.start(grpcServerMockk, loggerMockk)
        }

        verify(exactly = 2) { loggerMockk.info(any()) }
        verify(exactly = 1) { grpcServerMockk.start() }
        verify(exactly = 1) { grpcServerMockk.blockUntilShutdown() }


    }
}