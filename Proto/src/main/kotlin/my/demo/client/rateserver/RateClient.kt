package my.demo.client.rateserver

import kotlinx.coroutines.flow.Flow
import my.demo.rateserver.RateServiceOuterClass


interface RateClient {
    suspend fun requestRates(request: RateServiceOuterClass.RateRequest): Flow<RateServiceOuterClass.RatesResponse>
    suspend fun requestCurrencies(): RateServiceOuterClass.CurrenciesResponse
}