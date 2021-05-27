package my.demo.rateserver

interface RateProvider {
    fun getCurrencies(): List<RateServiceOuterClass.Currency>
    suspend fun getRates(baseCurrencyCode: String): List<RateServiceOuterClass.Rate>
}