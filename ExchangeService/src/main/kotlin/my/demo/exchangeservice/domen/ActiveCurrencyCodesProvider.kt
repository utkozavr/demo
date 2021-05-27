package my.demo.exchangeservice.domen

import kotlinx.coroutines.flow.Flow
import my.demo.currencyservice.CurrencyServiceOuterClass

interface ActiveCurrencyCodesProvider {
    fun getFlow(): Flow<Map<String, CurrencyServiceOuterClass.Currency>>
}