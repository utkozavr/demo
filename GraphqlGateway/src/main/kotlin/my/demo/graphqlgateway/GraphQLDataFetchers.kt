package my.demo.graphqlgateway

import graphql.schema.AsyncDataFetcher
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.reactive.asPublisher
import kotlinx.coroutines.runBlocking
import my.demo.currencyservice.CurrencyServiceOuterClass
import my.demo.graphqlgateway.gateway.CurrencyGateway
import my.demo.graphqlgateway.gateway.EventGateway
import my.demo.graphqlgateway.gateway.ExchangeRateGateway
import my.demo.graphqlgateway.gateway.map
import org.reactivestreams.Publisher
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import kotlin.coroutines.CoroutineContext


@Component
class GraphQLDataFetchers @Autowired constructor(
    private val currencyGateway: CurrencyGateway,
    private val eventGateway: EventGateway,
    private val exchangeRateGateway: ExchangeRateGateway
): CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + SupervisorJob()

    fun getCurrenciesDataFetcher(): AsyncDataFetcher<List<Map<String, String>>> {
        return AsyncDataFetcher { _: DataFetchingEnvironment ->
            runBlocking(coroutineContext) {
                currencyGateway.getCurrencies()
            }
        }
    }

    fun getToggleDisabledDataFetcher(): AsyncDataFetcher<Map<String, String>> {
        return AsyncDataFetcher { dataFetchingEnvironment: DataFetchingEnvironment ->
            runBlocking(coroutineContext) {
                val code: String = dataFetchingEnvironment.getArgument("code")
                currencyGateway.toggle(code)
            }
        }
    }

    fun getRateCurrenciesDataFetcher(key: String): AsyncDataFetcher<Map<String, String>> {
        return AsyncDataFetcher { dataFetchingEnvironment: DataFetchingEnvironment ->
            dataFetchingEnvironment
                .getSource<Map<String, Any>>()
                .get(key)
                .let {
                    if(it is CurrencyServiceOuterClass.Currency){
                        it.map()
                    } else {
                        null
                    }
                }
        }
    }

    fun getEventDataFetcher(): DataFetcher<Publisher<Map<String, String>>> {
        return DataFetcher { _: DataFetchingEnvironment ->
            runBlocking(coroutineContext) {
                eventGateway.getFlow()
            }.asPublisher(coroutineContext)
        }
    }

    fun getExchangeRateDataFetcher(): DataFetcher<Publisher<List<Map<String, Any>>>> {
        return DataFetcher { _: DataFetchingEnvironment ->
            runBlocking(coroutineContext) {
                exchangeRateGateway.getFlow()
            }.asPublisher(coroutineContext)
        }
    }


}