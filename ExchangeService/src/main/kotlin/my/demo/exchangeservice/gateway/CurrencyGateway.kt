package my.demo.exchangeservice.gateway

import my.demo.currencyservice.CurrencyServiceOuterClass


interface CurrencyGateway {
    suspend fun requestCurrencies(onlyActive: Boolean): Map<String, CurrencyServiceOuterClass.Currency>
}