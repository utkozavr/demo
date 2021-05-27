package my.demo.rateserver

import com.google.protobuf.Empty
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.coroutines.CoroutineContext
import kotlin.random.Random

class RateServiceImp(
    private val rateProvider: RateProvider,
    private val minDelay: Long,
    private val maxDelay: Long,
    private val defaultBaseCurrencyCode: String,
    private val builders: RateServiceImpResponseBuilders,
    override val coroutineContext: CoroutineContext = Dispatchers.IO + SupervisorJob()
) : RateServiceGrpcKt.RateServiceCoroutineImplBase(coroutineContext), CoroutineScope {

    fun getSleepDuration(minDelay: Long, maxDelay: Long): Long = if(maxDelay > minDelay){
        Random(System.currentTimeMillis()).nextLong(minDelay, maxDelay)
    } else {
        0L
    }

    suspend fun sleep(timeMillis: Long) = delay(timeMillis)

    override suspend fun getCurrency(request: Empty): RateServiceOuterClass.CurrenciesResponse = builders.buildCurrenciesResponse(
        rateProvider.getCurrencies(),
        Status.OK,
        ""
    )


    override fun getRates(request: RateServiceOuterClass.RateRequest): Flow<RateServiceOuterClass.RatesResponse> = flow {

        if(request.baseCurrencyCode != defaultBaseCurrencyCode){
            emit(builders.buildRatesResponse(emptyList(), Status.ERROR,"Only $defaultBaseCurrencyCode is supported as base currency"))
            return@flow
        }

        while (currentCoroutineContext().isActive){

            val response = try {
                val rates = rateProvider.getRates(defaultBaseCurrencyCode)
                builders.buildRatesResponse(rates, Status.OK, "")
            } catch (e: Exception) {
                emit(builders.buildRatesResponse(emptyList(), Status.ERROR,e.message ?: e.stackTraceToString()))
                break
            }

            emit(response)

            getSleepDuration(minDelay, maxDelay).apply {
                sleep(this)
            }

        }


    }
}