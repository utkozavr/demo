package my.demo.client.currencyservice

import my.demo.currencyservice.CurrencyServiceOuterClass


interface CurrencyClient {
    suspend fun requestCurrencies(): List<CurrencyServiceOuterClass.Currency>
    suspend fun toggleDisabled(currency: CurrencyServiceOuterClass.Currency): CurrencyServiceOuterClass.Currency
}