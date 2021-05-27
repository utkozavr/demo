package my.demo.currencyservice.domain

interface CurrencyProvider {
    suspend fun getCurrencies(): List<Currency>
    fun toggleDisabled(code: String): Currency
}