package my.demo.eventqueueservice

import com.google.protobuf.Empty
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.subscribe

class EventQueueServiceImp(): EventQueueServiceGrpcKt.EventQueueServiceCoroutineImplBase() {

    private val sharedFlow: MutableSharedFlow<EventQueueServiceOuterClass.EventMessage> = MutableSharedFlow()

    override suspend fun publish(request: EventQueueServiceOuterClass.EventMessage): Empty {
        sharedFlow.emit(request)
        return Empty.getDefaultInstance()
    }

    override fun listen(request: Empty): Flow<EventQueueServiceOuterClass.EventMessage> {
        return sharedFlow.asSharedFlow()
    }
}