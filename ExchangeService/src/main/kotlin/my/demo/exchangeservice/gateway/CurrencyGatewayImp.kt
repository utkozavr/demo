package my.demo.exchangeservice.gateway

import my.demo.client.currencyservice.CurrencyClientImp
import my.demo.currencyservice.CurrencyServiceOuterClass
import org.slf4j.Logger

class CurrencyGatewayImp(
    host: String,
    port: Int,
    logger: Logger
): CurrencyGateway, CurrencyClientImp(host, port, logger) {

    private fun List<CurrencyServiceOuterClass.Currency>.filter(onlyActive: Boolean): List<CurrencyServiceOuterClass.Currency> =
        if (onlyActive) {
            this.filter { it.isActive }
        } else {
            this
        }

    override suspend fun requestCurrencies(onlyActive: Boolean): Map<String, CurrencyServiceOuterClass.Currency> =
        requestCurrencies()
            .filter(onlyActive)
            .map { it.code to it }.toMap()
}