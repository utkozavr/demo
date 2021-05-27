package my.demo.rateserver

import io.grpc.ServerCall
import io.mockk.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class AuthInterceptorTest {

    val key = "key"
    val authInterceptor: AuthInterceptor
        get() = spyk(AuthInterceptor(authProviderMockk, key, loggerMockk))

    @BeforeEach
    fun setUp(){
        every { loggerMockk.warn(any()) } returns Unit
        every { serverCallMockk.close(any(), any()) } returns Unit
    }

    @AfterEach
    fun tearDown(){
        clearAllMocks()
    }

    @Test
    fun getMetadataKey() {
        assertEquals(key, authInterceptor.getMetadataKey(key).name())
    }


    @Test
    fun interceptCallWithNullToken() {

        every { metadataMockk.get<Any>(any()) } returns null
        every { metadataMockk.keys() } returns emptySet()
        authInterceptor.interceptCall(serverCallMockk, metadataMockk, serverCallHandlerMockk)

        val expectedStatus = authInterceptor.permissionDeniedWithNullToken

        verify(exactly = 1){ loggerMockk.warn(any()) }
        verify(exactly = 1){ serverCallMockk.close(match { it.code == expectedStatus.code && it.description == expectedStatus.description }, any()) }
    }

    @Test
    fun interceptCallWithBadToken() {

        val token = "111"

        every { authProviderMockk.isTokenValid(token) } returns false
        every { metadataMockk.get<Any>(any()) } returns token

        authInterceptor.interceptCall(serverCallMockk, metadataMockk, serverCallHandlerMockk)

        val expectedStatus = authInterceptor.permissionDeniedByToken

        verify(exactly = 1){ loggerMockk.warn(any()) }
        verify(exactly = 1){ serverCallMockk.close(expectedStatus, any()) }

    }

    @Test
    fun interceptCallWithCorrectToken() {

        val token = "111"

        every { authProviderMockk.isTokenValid(token) } returns true
        every { metadataMockk.get<Any>(any()) } returns token
        every { serverCallHandlerMockk.startCall(serverCallMockk, metadataMockk) } returns object : ServerCall.Listener<Any?>() {}

        authInterceptor.interceptCall(serverCallMockk, metadataMockk, serverCallHandlerMockk)

        val expectedStatus = authInterceptor.permissionDeniedByToken

        verify(exactly = 0){ loggerMockk.warn(any()) }
        verify(exactly = 0){ serverCallMockk.close(expectedStatus, any()) }
        verify(exactly = 1){ serverCallHandlerMockk.startCall(serverCallMockk, metadataMockk) }
    }

    @Test
    fun permissionStatus() {
        assertEquals(io.grpc.Status.PERMISSION_DENIED.code, authInterceptor.permissionDeniedByToken.code)
        assertEquals(io.grpc.Status.PERMISSION_DENIED.code, authInterceptor.permissionDeniedWithNullToken.code)
        assertTrue { authInterceptor.permissionDeniedWithNullToken.description?.isNotBlank() == true }
    }
}