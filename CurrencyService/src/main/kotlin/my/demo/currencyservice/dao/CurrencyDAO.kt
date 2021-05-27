package my.demo.currencyservice.dao

import my.demo.currencyservice.domain.Currency
import my.demo.currencyservice.domain.CurrencyAdaptor

interface CurrencyDAO {
    fun insert(currency: Currency): Unit
    fun update(currency: Currency): Unit
    fun insertAndGet(currency: Currency): CurrencyAdaptor
    fun updateAndGet(currency: Currency): CurrencyAdaptor
    fun select(): List<CurrencyAdaptor>
    fun selectBy(code: String): CurrencyAdaptor?
}