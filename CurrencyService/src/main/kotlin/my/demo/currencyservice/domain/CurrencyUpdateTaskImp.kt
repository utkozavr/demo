package my.demo.currencyservice.domain

import kotlinx.coroutines.*
import my.demo.currencyservice.dao.CurrencyDAO
import my.demo.currencyservice.gateway.RateServerGateway
import my.demo.currencyservice.gateway.EventGateway
import org.slf4j.Logger
import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@ExperimentalTime
class CurrencyUpdateTaskImp(
    private val logger: Logger,
    private val currencyDAO: CurrencyDAO,
    private val currencyGateway: RateServerGateway,
    private val eventGateway: EventGateway,
    private val delayDuration: Duration,
    private val delayDurationOnError: Duration,
    private val cacheCleaner: ()->Unit
): CurrencyUpdateTask, CoroutineScope {

    private val className = this::class.simpleName

    override val coroutineContext: CoroutineContext = Dispatchers.IO + SupervisorJob()

    private var process: Deferred<Unit>? = null

    private fun Currency.compare(other: Currency): Boolean {
        return this.code == other.code && this.name == other.name && this.isActive == other.isActive
    }


    private fun combine(currentData: List<Currency>, newData: List<Currency>, compare: Currency.(other: Currency)->Boolean): Pair<List<Currency>,List<Currency>> {
        val toUpdate: MutableList<Currency> = mutableListOf()
        var toCreate: List<Currency> = newData

        currentData.forEach { current ->
            when(val found = toCreate.find { it.code == current.code }) {
                null -> {
                    if(current.isActive) {
                        toUpdate.add( current.getCopy(isActive = false) )
                    }
                }
                else -> {
                    toCreate = toCreate.minus(found)

                    if(!current.compare(found)){
                        toUpdate.add(found.getCopy(isDisabledManually = current.isDisabledManually))
                    }
                }
            }
        }

        return Pair(toUpdate, toCreate)
    }


    private suspend fun recollectData() {

        val currentData: List<Currency> = currencyDAO.select().map { it.get() }
        val newData = currencyGateway.getCurrencies().map { it.get() }

        val (update, create) = combine(currentData, newData) { other -> compare(other) }

        update.forEach {
            currencyDAO.update(it)
        }

        create.forEach {
            currencyDAO.insert(it)
        }

        if (update.isNotEmpty() || create.isNotEmpty()){
            cacheCleaner()
            eventGateway.publishUpdatedEvent()
        }

    }


    private fun restartOnError() {
        CoroutineScope(coroutineContext).async {
            delay(delayDurationOnError)
            logger.info("$className attempt to restart after error")
            start()
        }

    }

    override fun start() {
        if(process?.isActive != true){
            val process = CoroutineScope(coroutineContext).async {
                while (isActive){
                    recollectData()
                    delay(delayDuration)
                }
            }
            process.invokeOnCompletion{
                when(it) {
                    null, is CancellationException -> {
                        logger.info("$className is stopped")
                    }
                    else -> {
                        logger.error("$className is stopped with exception", it)
                        restartOnError()
                    }
                }
            }
            this.process = process
            logger.info("$className is started")
        } else {
            logger.warn("$className already running")
        }
    }

    override fun stop() {
        val process = this.process
        if(process?.isActive == true){
            process.cancel()
        }
    }



}