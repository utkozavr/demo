package my.demo.config

data class ExchangeServiceConfig(
    val host: String,
    val port: Int,
    val currencyServiceHost: String,
    val currencyServicePort: Int,
    val eventServiceHost: String,
    val eventServicePort: Int,
    val rateServerHost: String,
    val rateServerPort: Int,
    val rateServerTokenKey: String,
    val rateServerToken: String,
    val baseCurrency: String,
    val loggerName: String,
)
