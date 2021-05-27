package my.demo.client.eventservice

import kotlinx.coroutines.flow.Flow
import my.demo.eventqueueservice.EventQueueServiceOuterClass


interface EventClient {
    suspend fun getEventFlow(): Flow<EventQueueServiceOuterClass.EventMessage>
    suspend fun publish(message: EventQueueServiceOuterClass.EventMessage)
}