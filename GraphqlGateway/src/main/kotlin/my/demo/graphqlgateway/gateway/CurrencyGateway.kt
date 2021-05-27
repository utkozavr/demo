package my.demo.graphqlgateway.gateway

import my.demo.client.currencyservice.CurrencyClientImp
import my.demo.config.CurrencyServiceConfig
import my.demo.currencyservice.CurrencyServiceOuterClass
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
open class CurrencyGateway @Autowired constructor(
    config: CurrencyServiceConfig
): CurrencyClientImp(config.host, config.port, LoggerFactory.getLogger("CurrencyGateway")) {

    suspend fun getCurrencies (): List<Map<String, String>> {
            return requestCurrencies().map { it.map() }
    }

    suspend fun toggle(code: String): Map<String, String> {
        return toggleDisabled(
            CurrencyServiceOuterClass.Currency.newBuilder()
                .setCode(code)
                .build()
        ).map()
    }

}