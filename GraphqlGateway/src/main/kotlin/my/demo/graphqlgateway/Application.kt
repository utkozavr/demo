package my.demo.graphqlgateway

import my.demo.config.Config
import my.demo.config.CurrencyServiceConfig
import my.demo.config.EventServiceConfig
import my.demo.config.ExchangeServiceConfig
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean


@SpringBootApplication
open class Application {

    private val config = Config.load()

    @Bean
    open fun getCurrencyServiceConfig(): CurrencyServiceConfig = config.currencyServiceConfig
    @Bean
    open fun getEventServiceConfig(): EventServiceConfig = config.eventServiceConfig
    @Bean
    open fun getExchangeServiceConfig(): ExchangeServiceConfig = config.exchangeServiceConfig

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication(Application::class.java)
                .apply {
                    this.setDefaultProperties(
                        mapOf("server.port" to "8088")
                    )
                }
                .run(*args)
        }
    }


}