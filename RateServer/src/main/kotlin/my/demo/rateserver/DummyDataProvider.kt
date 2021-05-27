package my.demo.rateserver

import kotlinx.coroutines.*
import my.demo.rateserver.RateServiceOuterClass.Rate
import my.demo.rateserver.RateServiceOuterClass.Currency
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.CoroutineContext
import kotlin.random.Random
import kotlin.time.Duration

class DummyDataProvider: RateProvider, CoroutineScope {

    var flickerIsActive: AtomicBoolean = AtomicBoolean(true)

    init {
        async {
            while (isActive) {
                delay(Random.nextLong(3000, 5000))
                flickerIsActive.set(!flickerIsActive.get())
            }
        }
    }

    private val rates = listOf<Rate>(
        makeRate("USD", "US Dollar", 1.00),
        makeRate("EUR", "Euro", 0.84),
        makeRate("GBP", "British Pound Sterling", 0.73),
        makeRate("JPY", "Japanese Yen", 109.58),
        makeRate("FLC", "Flicker coin", 50.58),
    )

    private fun makeRate(code: String, fullName: String, rate: Double): Rate = Rate.newBuilder().apply {
        this.currency = this.currencyBuilder.apply {
            this.code = code
            this.fullName = fullName
        }.build()
        this.value = rate
    }.build()

    private fun randDouble(from: Double, to: Double): Double = Random(System.currentTimeMillis()).nextDouble(from, to)

    private fun newRate(
        currentRate: Double,
        decrease: Double = 0.8,
        increase: Double = 1.2,
        bottomLimit: Double = 0.4
    ): Double = when(currentRate <= bottomLimit){
        true -> randDouble(currentRate * decrease, currentRate * increase)
        false -> randDouble(currentRate, currentRate * increase)
    }

    override fun getCurrencies(): List<Currency> = rates
        .filter { it.currency.code != "FLC" || flickerIsActive.get() }
        .map { it.currency }

    private fun newRates(baseCurrencyCode: String): List<Rate> = rates
        .filter { it.currency.code != "FLC" || flickerIsActive.get() }
        .map {
        it.toBuilder().apply {
            if(this.currency.code != baseCurrencyCode){
                this.value = newRate(this.value)
            }
        }.build()
    }

    override suspend fun getRates(baseCurrencyCode: String): List<Rate> {
        return newRates(baseCurrencyCode)
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + SupervisorJob()

}