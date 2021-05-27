package my.demo.exchangeservice.gateway

import kotlinx.coroutines.flow.Flow
import my.demo.exchangeservice.domen.RateAdapter
import java.math.BigDecimal


interface RateGateway {

    suspend fun getFlow(baseCurrencyCode: String): Flow<Map<String, BigDecimal>>

}