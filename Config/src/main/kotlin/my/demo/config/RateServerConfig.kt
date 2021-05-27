package my.demo.config

data class RateServerConfig(
    val host: String,
    val port: Int,
    val minDelay: Long,
    val maxDelay: Long,
    val baseCurrency: String,
    val loggerName: String,
    val tokenKey: String,
    val token: String
)