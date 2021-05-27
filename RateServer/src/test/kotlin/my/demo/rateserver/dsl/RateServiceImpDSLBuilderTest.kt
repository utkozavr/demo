package my.demo.rateserver.dsl

import io.mockk.*
import my.demo.rateserver.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

internal class RateServiceImpDSLBuilderTest {

    @AfterEach
    fun tearDown() {
        clearAllMocks()
    }

    companion object {

        fun block(b: RateServiceImpDSLBuilder.() -> Unit): RateServiceImpDSLBuilder.() -> Unit = b


        @JvmStatic
        fun variants(): Stream<Arguments> = Stream.of(
            Arguments.arguments(false, block {
                rateProvider { rateProviderMockk }
                minDelay { 100 }
                maxDelay { 300 }
                defaultBaseCurrencyCode { "text" }
                responseBuilders { rateServiceImpResponseBuildersMockk }
            }),
            Arguments.arguments(true, block {
                minDelay { 100 }
                maxDelay { 300 }
                defaultBaseCurrencyCode { "text" }
                responseBuilders { rateServiceImpResponseBuildersMockk }
            }),
            Arguments.arguments(true, block {
                rateProvider { rateProviderMockk }
                maxDelay { 300 }
                defaultBaseCurrencyCode { "text" }
                responseBuilders { rateServiceImpResponseBuildersMockk }
            }),
            Arguments.arguments(true, block {
                rateProvider { rateProviderMockk }
                minDelay { 100 }
                defaultBaseCurrencyCode { "text" }
                responseBuilders { rateServiceImpResponseBuildersMockk }
            }),
            Arguments.arguments(true, block {
                rateProvider { rateProviderMockk }
                minDelay { 100 }
                maxDelay { 300 }
                responseBuilders { rateServiceImpResponseBuildersMockk }
            }),
            Arguments.arguments(true, block {
                rateProvider { rateProviderMockk }
                minDelay { 100 }
                maxDelay { 300 }
                defaultBaseCurrencyCode { "text" }
            }),
            Arguments.arguments(true, block {
            })

        )

    }


    @ParameterizedTest
    @MethodSource("variants")
    fun build(shouldThrow: Boolean, block: RateServiceImpDSLBuilder.() -> Unit) {
        if(shouldThrow){
            org.junit.jupiter.api.assertThrows<IllegalStateException> {
                RateServiceImpDSLBuilder().apply(block).build()
            }
        } else {
            assertDoesNotThrow {
                RateServiceImpDSLBuilder().apply(block).build()
            }
        }
    }

    @Test
    fun serviceFunTest() {

        val lambdaMockk = mockk<RateServiceImpDSLBuilder.() -> Unit>()
        every { rateServiceImpDSLBuilderMockk.apply(lambdaMockk) } returns rateServiceImpDSLBuilderMockk
        every { rateServiceImpDSLBuilderMockk.build() } returns rateServiceImpMockk

        service(builder = rateServiceImpDSLBuilderMockk, lambda = lambdaMockk)

        verify(exactly = 1) { rateServiceImpDSLBuilderMockk.apply(lambdaMockk) }
        verify(exactly = 1) { rateServiceImpDSLBuilderMockk.build() }

        org.junit.jupiter.api.assertThrows<IllegalStateException> {
            service{}
        }

    }
}