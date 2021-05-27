package my.demo.client.eventservice

import kotlinx.coroutines.flow.Flow
import my.demo.client.ClientAbs
import my.demo.eventqueueservice.EventQueueServiceGrpcKt
import my.demo.eventqueueservice.EventQueueServiceOuterClass
import org.slf4j.Logger

open class EventClientImp constructor (
    host: String,
    port: Int,
    logger: Logger
): ClientAbs(host, port, logger), EventClient {

    private val stub = EventQueueServiceGrpcKt.EventQueueServiceCoroutineStub(channel)

    override suspend fun getEventFlow(): Flow<EventQueueServiceOuterClass.EventMessage> =
        runWithRecovery("EventClientImp.getEventFlow") { stub.listen(emptyRequest) }

    override suspend fun publish(message: EventQueueServiceOuterClass.EventMessage) {
        runWithRecovery("EventClientImp.publish") { stub.publish(message) }
    }
}