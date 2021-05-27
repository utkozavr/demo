package my.demo.exchangeservice.gateway

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import my.demo.client.eventservice.EventClientImp
import my.demo.eventqueueservice.EventQueueServiceOuterClass
import my.demo.exchangeservice.domen.Event
import org.slf4j.Logger

class EventGatewayImp constructor (
    host: String,
    port: Int,
    logger: Logger
): EventGateway, EventClientImp(host, port, logger) {

    private fun EventQueueServiceOuterClass.EventMessage.map(): Event {
        return when {
            this.sender == EventQueueServiceOuterClass.Service.CURRENCY_SERVICE &&
                    this.message == EventQueueServiceOuterClass.Event.UPDATED -> Event.CURRENCY_UPDATED
            else -> Event.UNKNOWN
        }
    }

    override suspend fun getFlow(): Flow<Event> = getEventFlow().map {
        it.map()
    }
}