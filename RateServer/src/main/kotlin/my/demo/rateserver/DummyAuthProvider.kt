package my.demo.rateserver

class DummyAuthProvider(
    override val tokenKey: String,
    val validToken: String
): AuthProvider {
    override fun isTokenValid(token: String): Boolean = token == validToken
}