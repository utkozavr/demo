package my.demo.exchangeservice.gateway

import kotlinx.coroutines.flow.Flow
import my.demo.exchangeservice.domen.Event


interface EventGateway {

    suspend fun getFlow(): Flow<Event>

}