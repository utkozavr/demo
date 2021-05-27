package my.demo.grpcserver.dsl

import io.mockk.clearAllMocks
import io.mockk.every
import my.demo.grpcserver.serverInterceptorMockk
import my.demo.grpcserver.serverServiceDefinitionMockk
import my.demo.grpcserver.serviceDescriptorMockk
import my.demo.grpcserver.serviceMockk
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

internal class ServerDSLBuilderTest {

    @AfterEach
    fun tearDown() {
        clearAllMocks()
    }

    companion object {

        fun block(b: ServerDSLBuilder.() -> Unit): ServerDSLBuilder.() -> Unit = b


        @JvmStatic
        fun variants(): Stream<Arguments> = Stream.of(
            Arguments.arguments(false, block {
                port { 100 }
                service { serviceMockk }
                intercept { serverInterceptorMockk }
                intercept { serverInterceptorMockk }
            }),
            Arguments.arguments(true, block {
                service { serviceMockk }
                intercept { serverInterceptorMockk }
                intercept { serverInterceptorMockk }
                interceptAll { listOf(serverInterceptorMockk, serverInterceptorMockk) }
            }),
            Arguments.arguments(true, block {
                port { 100 }
                intercept { serverInterceptorMockk }
                intercept { serverInterceptorMockk }
                interceptAll { listOf(serverInterceptorMockk, serverInterceptorMockk) }
            }),
            Arguments.arguments(true, block {
                port { 100 }
                interceptAll { listOf(serverInterceptorMockk, serverInterceptorMockk) }
            }),
            Arguments.arguments(false, block {
                port { 100 }
                service { serviceMockk }
            }),
            Arguments.arguments(true, block {
            })

        )

    }


    @ParameterizedTest
    @MethodSource("variants")
    fun build(shouldThrow: Boolean, block: ServerDSLBuilder.() -> Unit) {

        every { serviceMockk.bindService() } returns serverServiceDefinitionMockk
        every { serverServiceDefinitionMockk.serviceDescriptor } returns serviceDescriptorMockk
        every { serverServiceDefinitionMockk.methods } returns emptyList()
        every { serviceDescriptorMockk.name } returns "name"
        every { serviceDescriptorMockk.methods } returns emptyList()
        every { serviceDescriptorMockk.schemaDescriptor } returns object {}

        if (shouldThrow) {
            org.junit.jupiter.api.assertThrows<IllegalStateException> {
                ServerDSLBuilder().apply(block).build()
            }
        } else {
            assertDoesNotThrow {
                ServerDSLBuilder().apply(block).build()
            }
        }
    }

    @Test
    fun serviceTest() {
        assertThrows<IllegalStateException> {
            ServerDSLBuilder().apply {
                service { serviceMockk }
            }.build()
        }
    }
}