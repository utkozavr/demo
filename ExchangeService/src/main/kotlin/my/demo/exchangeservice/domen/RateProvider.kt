package my.demo.exchangeservice.domen

import kotlinx.coroutines.flow.Flow

interface RateProvider {

    fun getFlow(): Flow<List<Rate>>

}