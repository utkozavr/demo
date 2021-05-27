package my.demo.rateserver

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class DummyAuthProviderTest {

    val authProvider = DummyAuthProvider("key", "token")

    @Test
    fun getTokenKey() {
        assertTrue { authProvider.tokenKey.isNotBlank() }
    }

    @Test
    fun getValidToken() {
        assertTrue { authProvider.validToken.isNotBlank() }
    }

    @Test
    fun isTokenValid() {
        assertTrue { authProvider.isTokenValid(authProvider.validToken) }
        assertTrue { authProvider.isTokenValid("") == false }
    }
}