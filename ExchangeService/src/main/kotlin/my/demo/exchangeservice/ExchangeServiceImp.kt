package my.demo.exchangeservice

import com.google.protobuf.Empty
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import my.demo.currencyservice.CurrencyServiceOuterClass
import my.demo.exchangeservice.domen.Rate
import my.demo.exchangeservice.domen.RateProvider

class ExchangeServiceImp(private val rateProvider: RateProvider): ExchangeServiceGrpcKt.ExchangeServiceCoroutineImplBase() {

    private fun Rate.asResp(): ExchangeServiceOuterClass.ExchangePair = ExchangeServiceOuterClass.ExchangePair.newBuilder().apply {
        this.source = this@asResp.source
        this.destination = this@asResp.destination
        this.rate = this@asResp.rate.toDouble()
    }.build()

    private fun List<Rate>.asResp(): ExchangeServiceOuterClass.Rates = ExchangeServiceOuterClass.Rates.newBuilder()
        .apply {
            addAllRates(this@asResp.map { it.asResp() })
        }.build()

    override fun getRates(request: Empty): Flow<ExchangeServiceOuterClass.Rates> {
        return rateProvider.getFlow().map { it.asResp() }
    }
}