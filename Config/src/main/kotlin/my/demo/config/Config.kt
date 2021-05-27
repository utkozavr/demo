package my.demo.config

import com.typesafe.config.ConfigFactory
import io.github.config4k.extract

data class Config(
    val rateServerConfig: RateServerConfig,
    val currencyServiceConfig: CurrencyServiceConfig,
    val exchangeServiceConfig: ExchangeServiceConfig,
    val eventServiceConfig: EventServiceConfig,
) {
    companion object {
        fun load(resName: String = "app.conf"): Config  {
            return javaClass.classLoader.getResource(resName).readText()
                .let {
                    ConfigFactory.parseString(it).resolve().extract()
                }

        }
    }
}