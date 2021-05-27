package my.demo.exchangeservice.domen

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
import my.demo.currencyservice.CurrencyServiceOuterClass
import my.demo.exchangeservice.gateway.RateGateway
import java.math.BigDecimal
import kotlin.coroutines.CoroutineContext

class RateProviderImp(
    private val baseCurrencyCode: String,
    private val activeCurrencyCodesProvider: ActiveCurrencyCodesProvider,
    private val rateGateway: RateGateway
): RateProvider, CoroutineScope {

    override val coroutineContext: CoroutineContext = Dispatchers.IO + SupervisorJob()

    private val dataFlow: MutableSharedFlow<List<Rate>> = MutableSharedFlow()
    private var activeCurrencies: Map<String, CurrencyServiceOuterClass.Currency> = emptyMap()

    override fun getFlow(): Flow<List<Rate>> = dataFlow.asSharedFlow()

    init {
        async {
            collectActiveCurrencyCodes()
        }
        async {
            collectRates()
        }
    }

    private suspend fun collectActiveCurrencyCodes() {
        activeCurrencyCodesProvider.getFlow().collectLatest {
            synchronized(activeCurrencies){
                activeCurrencies = it
            }
        }
    }

    private fun Map<String, BigDecimal>.asRate():List<Rate> {
        val activeCurrencies = synchronized(activeCurrencies){ activeCurrencies }
        val source = activeCurrencies.getOrElse(baseCurrencyCode) {
            return emptyList()
        }


        return this.entries.mapNotNull { pair ->
            activeCurrencies[pair.key]?.let { destination ->
                RateImp(
                    source = source,
                    destination = destination,
                    rate = pair.value
                )
            }
        }
    }


    private suspend fun collectRates() {
        rateGateway.getFlow(baseCurrencyCode)
            .collectLatest {
                it.asRate().apply {
                    dataFlow.emit(this)
                }
            }
    }



}