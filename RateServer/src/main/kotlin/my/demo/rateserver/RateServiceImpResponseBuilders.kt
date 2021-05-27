package my.demo.rateserver


interface RateServiceImpResponseBuilders {

    fun buildCurrenciesResponse(
        currencies: List<RateServiceOuterClass.Currency>,
        status: Status,
        message: String,
    ): RateServiceOuterClass.CurrenciesResponse

    fun buildRatesResponse(
        rates: List<RateServiceOuterClass.Rate>,
        status: Status,
        message: String
    ): RateServiceOuterClass.RatesResponse

}