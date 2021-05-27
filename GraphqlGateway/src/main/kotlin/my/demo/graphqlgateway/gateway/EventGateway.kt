package my.demo.graphqlgateway.gateway

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import my.demo.client.eventservice.EventClientImp
import my.demo.config.EventServiceConfig
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
open class EventGateway @Autowired constructor(
    config: EventServiceConfig
): EventClientImp(config.host, config.port, LoggerFactory.getLogger("CurrencyGateway")) {

    suspend fun getFlow(): Flow<Map<String, String>> {
        return getEventFlow().map { it.map() }
    }

}