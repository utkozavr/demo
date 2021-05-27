package my.demo.rateserver.dsl

import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.verify
import my.demo.rateserver.authInterceptorDSLBuilderMockk
import my.demo.rateserver.authInterceptorMockk
import my.demo.rateserver.authProviderMockk
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class AuthInterceptorDSLBuilderKtTest {

    @AfterEach
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun authInterceptorTest() {
        every { authInterceptorDSLBuilderMockk.build() } returns authInterceptorMockk
        authInterceptor(authInterceptorDSLBuilderMockk){}
        verify(exactly = 1) { authInterceptorDSLBuilderMockk.build() }
    }
}