package my.demo.currencyservice.gateway

import com.google.protobuf.Timestamp
import my.demo.client.eventservice.EventClientImp
import my.demo.eventqueueservice.EventQueueServiceOuterClass
import org.slf4j.Logger
import java.time.Instant


class EventGatewayImp(
    host: String,
    port: Int,
    logger: Logger
): EventGateway, EventClientImp(host, port, logger) {

    private fun getRequest() = EventQueueServiceOuterClass.EventMessage
        .newBuilder()
        .setMessage(EventQueueServiceOuterClass.Event.UPDATED)
        .setSender(EventQueueServiceOuterClass.Service.CURRENCY_SERVICE)
        .setTimestamp(Timestamp.newBuilder().setSeconds(Instant.now().epochSecond).build())
        .build()

    override suspend fun publishUpdatedEvent() {
        publish(getRequest())
    }
}