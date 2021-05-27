package my.demo.exchangeservice.domen

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import my.demo.currencyservice.CurrencyServiceOuterClass
import my.demo.exchangeservice.gateway.CurrencyGateway
import my.demo.exchangeservice.gateway.EventGateway
import kotlin.coroutines.CoroutineContext

class ActiveCurrencyCodesProviderImp(
    val currencyGateway: CurrencyGateway,
    val eventGateway: EventGateway
): ActiveCurrencyCodesProvider, CoroutineScope {

    override val coroutineContext: CoroutineContext = Dispatchers.IO + SupervisorJob()

    private val sharedFlow: MutableSharedFlow<Map<String, CurrencyServiceOuterClass.Currency>> = MutableSharedFlow()

    init {
        async {
            collectData()
        }
    }

    override fun getFlow(): SharedFlow<Map<String, CurrencyServiceOuterClass.Currency>> = sharedFlow

    private suspend fun emitNewData() {
        sharedFlow.emit(currencyGateway.requestCurrencies(true))
    }

    private suspend fun collectData() {
        emitNewData()
        eventGateway.getFlow().collectLatest {
            when(it) {
                Event.CURRENCY_UPDATED -> emitNewData()
                else -> {}
            }
        }
    }


}