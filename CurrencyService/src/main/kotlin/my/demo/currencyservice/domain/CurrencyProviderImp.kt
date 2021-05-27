package my.demo.currencyservice.domain

import my.demo.currencyservice.dao.CurrencyDAO
import java.util.concurrent.TimeUnit
import kotlin.time.ExperimentalTime

@ExperimentalTime
class CurrencyProviderImp(
    private val currencyDAO: CurrencyDAO,
    invalidateAfter: Long,
    invalidateTimeUnit: TimeUnit,
): CurrencyProvider, MemoryCached<List<Currency>>(invalidateAfter, invalidateTimeUnit, ) {

    override suspend fun dataProvider(): List<Currency> {
        return currencyDAO.select().map { it.get() }
    }

    override suspend fun getCurrencies(): List<Currency> = get()

    override fun toggleDisabled(code: String): Currency {
        val currency = currencyDAO.selectBy(code)?.get() ?: throw IllegalArgumentException("Currency was not found by code: $code")
        return currencyDAO
            .updateAndGet(currency.getCopy(isDisabledManually = !currency.isDisabledManually))
            .get()
            .let {
                this@CurrencyProviderImp.clean()
                it
            }
    }
}