package my.demo.rateserver

import com.google.protobuf.Timestamp

class RateServiceImpResponseBuildersImp: RateServiceImpResponseBuilders {

    fun buildTimestamp(seconds: Long = System.currentTimeMillis() / 1000): Timestamp =
        Timestamp.newBuilder().apply {
            this.seconds = seconds
        }.build()


    fun buildRatesResponse(
        rates: List<RateServiceOuterClass.Rate>,
        status: com.google.rpc.Status,
        timestamp: Timestamp = buildTimestamp()
    ): RateServiceOuterClass.RatesResponse = RateServiceOuterClass.RatesResponse.newBuilder()
        .apply {
            this.timestamp = timestamp
            this.addAllRates(rates)
            this.status = status
        }.build()


    override fun buildRatesResponse(
        rates: List<RateServiceOuterClass.Rate>,
        status: Status,
        message: String
    ): RateServiceOuterClass.RatesResponse =
        buildRatesResponse(
            rates,
            buildStatus(status, message)
        )

    fun buildStatus(status: Status, message: String): com.google.rpc.Status = buildStatus(status.code, message)

    fun buildStatus(code: Int, message: String): com.google.rpc.Status =
        com.google.rpc.Status.newBuilder()
            .apply {
                this.code = code
                this.message = message
            }.build()

    fun buildCurrenciesResponse(
        currencies: List<RateServiceOuterClass.Currency>,
        status: com.google.rpc.Status
    ): RateServiceOuterClass.CurrenciesResponse =
        RateServiceOuterClass.CurrenciesResponse.newBuilder().apply {
            this.addAllCurrencies(currencies)
            this.status = status
        }.build()

    override fun buildCurrenciesResponse(
        currencies: List<RateServiceOuterClass.Currency>,
        status: Status,
        message: String,
    ): RateServiceOuterClass.CurrenciesResponse =
        buildCurrenciesResponse(currencies, buildStatus(status, message))


}