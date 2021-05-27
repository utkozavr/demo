package my.demo.client.exchangeservice

import kotlinx.coroutines.flow.Flow
import my.demo.exchangeservice.ExchangeServiceOuterClass

interface ExchangeClient {
    suspend fun getRatesFlow(): Flow<ExchangeServiceOuterClass.Rates>
}