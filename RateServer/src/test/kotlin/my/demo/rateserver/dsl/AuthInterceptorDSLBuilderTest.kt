package my.demo.rateserver.dsl

import io.mockk.clearAllMocks
import my.demo.rateserver.authProviderMockk
import my.demo.rateserver.loggerMockk
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream


internal class AuthInterceptorDSLBuilderTest {

    @AfterEach
    fun tearDown() {
        clearAllMocks()
    }

    companion object {

        fun block(b: AuthInterceptorDSLBuilder.() -> Unit): AuthInterceptorDSLBuilder.() -> Unit = b


        @JvmStatic
        fun variants(): Stream<Arguments> = Stream.of(
            arguments(false, block {
                authProvider { authProviderMockk }
                tokenKey { "tokenKey" }
                logger { loggerMockk }
            }),
            arguments(true, block {
                tokenKey { "tokenKey" }
                logger { loggerMockk }
            }),
            arguments(true, block {
                authProvider { authProviderMockk }
                logger { loggerMockk }
            }),
            arguments(true, block {
                authProvider { authProviderMockk }
                tokenKey { "tokenKey" }
            }),
            arguments(true, block {
            })

        )

    }


    @ParameterizedTest
    @MethodSource("variants")
    fun build(shouldThrow: Boolean, block: AuthInterceptorDSLBuilder.() -> Unit) {
        if(shouldThrow){
            org.junit.jupiter.api.assertThrows<IllegalStateException> {
                AuthInterceptorDSLBuilder().apply(block).build()
            }
        } else {
            assertDoesNotThrow {
                AuthInterceptorDSLBuilder().apply(block).build()
            }
        }
    }

}