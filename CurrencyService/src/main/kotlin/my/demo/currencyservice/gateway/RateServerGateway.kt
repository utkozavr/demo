package my.demo.currencyservice.gateway

import my.demo.currencyservice.domain.CurrencyAdaptor

interface RateServerGateway {

    suspend fun getCurrencies(): List<CurrencyAdaptor>

}