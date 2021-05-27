package my.demo.grpcserver.dsl

import io.mockk.*
import my.demo.grpcserver.loggerMockk
import my.demo.grpcserver.serverDSLBuilderMockk
import my.demo.grpcserver.serverMockk
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

internal class RateServerDSLBuilderTest {

    @AfterEach
    fun tearDown() {
        clearAllMocks()
    }

    companion object {

        fun block(b: GrpcServerDSLBuilder.() -> Unit): GrpcServerDSLBuilder.() -> Unit = b


        @JvmStatic
        fun variants(): Stream<Arguments> = Stream.of(
            Arguments.arguments(false, block {
                server(serverDSLBuilderMockk) {  }
                logger { loggerMockk }
            }),
            Arguments.arguments(true, block {
                logger { loggerMockk }
            }),
            Arguments.arguments(true, block {
                server(serverDSLBuilderMockk) {  }
            }),
            Arguments.arguments(true, block {
            })

        )

    }


    @ParameterizedTest
    @MethodSource("variants")
    fun build(shouldThrow: Boolean, block: GrpcServerDSLBuilder.() -> Unit) {

        every { serverDSLBuilderMockk.build() } returns serverMockk

        if(shouldThrow){
            assertThrows<IllegalStateException> {
                GrpcServerDSLBuilder().apply(block).build()
            }
        } else {
            assertDoesNotThrow {
                GrpcServerDSLBuilder().apply(block).build()
            }
            verify { serverDSLBuilderMockk.build() }
        }
    }

    @Test
    fun serverTest() {
        assertThrows<IllegalStateException> {
            GrpcServerDSLBuilder().apply {
                server {}
            }.build()
        }
    }
}