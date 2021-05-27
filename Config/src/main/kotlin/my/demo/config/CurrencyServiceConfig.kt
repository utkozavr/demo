package my.demo.config

import java.util.concurrent.TimeUnit

data class CurrencyServiceConfig(
    val host: String,
    val port: Int,
    val updateTaskDelaySeconds: Int,
    val updateTaskOnErrorDelaySeconds: Int,
    val invalidateCacheEach: Long,
    val invalidateCacheTimeUnit: TimeUnit,
    val eventServiceHost: String,
    val eventServicePort: Int,
    val rateServerHost: String,
    val rateServerPort: Int,
    val rateServerTokenKey: String,
    val rateServerToken: String,
    val loggerName: String,
    val dataBaseConfig: DataBaseConfig
)
