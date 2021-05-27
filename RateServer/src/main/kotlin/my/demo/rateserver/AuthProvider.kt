package my.demo.rateserver

interface AuthProvider {
    val tokenKey: String
    fun isTokenValid(token: String): Boolean
}