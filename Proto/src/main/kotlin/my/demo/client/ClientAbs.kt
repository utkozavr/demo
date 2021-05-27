package my.demo.client

import com.google.protobuf.Empty
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import kotlinx.coroutines.delay
import org.slf4j.Logger


abstract class ClientAbs(
    host: String,
    port: Int,
    protected open val logger: Logger
) {
    private var recoveryAttempts = 0
    private val recoveryAttemptsMax = 60
    private val delayOnRecovery = 3000L

    protected open val channel: ManagedChannel = ManagedChannelBuilder
        .forAddress(host, port)
        .usePlaintext()
        .build()

    protected val emptyRequest: Empty = Empty.getDefaultInstance()

    protected suspend fun <T>runWithRecovery(sourceName: String, execute: suspend ()->T): T {
        return try {
            execute()
        } catch (e: Exception) {

            if (recoveryAttempts < recoveryAttemptsMax){
                logger.warn("Execution $sourceName will be recovered after: ${e.message}")
            } else {
                logger.error("Execution $sourceName failed: ${e.message}", e)
                throw e
            }

            recoveryAttempts++
            delay(delayOnRecovery)
            runWithRecovery(sourceName, execute)
        }
    }

}