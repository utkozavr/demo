package my.demo.rateserver

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class DummyDataProviderTest {

    val dataProvider = DummyDataProvider()

    @Test
    fun getCurrencies() {
        val resp = dataProvider.getCurrencies()

        assertTrue { resp.isNotEmpty() }
    }

    @Test
    fun getRates() {
        val baseCurrency =  dataProvider.getCurrencies().first()

        val respFirst = runBlocking { dataProvider.getRates(baseCurrency.code) }
        val respSecond = runBlocking { dataProvider.getRates(baseCurrency.code) }

        val baseCurrRateFirst = respFirst.find { it.currency == baseCurrency }?.value
        val baseCurrRateSecond = respSecond.find { it.currency == baseCurrency }?.value

        assertTrue { respFirst.isNotEmpty() }
        assertTrue { respSecond.isNotEmpty() }
        assertNotNull(baseCurrRateFirst)
        assertNotNull(baseCurrRateSecond)
        assertEquals(baseCurrRateFirst, baseCurrRateSecond)
    }
}