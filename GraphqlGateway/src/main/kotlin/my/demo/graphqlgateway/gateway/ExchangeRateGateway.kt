package my.demo.graphqlgateway.gateway

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import my.demo.client.exchangeservice.ExchangeClientImp
import my.demo.config.ExchangeServiceConfig
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.io.Serializable

@Component
open class ExchangeRateGateway @Autowired constructor(
    config: ExchangeServiceConfig
): ExchangeClientImp(config.host, config.port, LoggerFactory.getLogger("ExchangeRateGateway")) {

    suspend fun getFlow(): Flow<List<Map<String, Serializable>>> {
        return getRatesFlow().map { it.map() }
    }

}