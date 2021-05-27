package my.demo.rateserver

import io.grpc.ForwardingServerCall
import io.grpc.ServerCall
import org.slf4j.Logger

class ExceptionTranslatingServerCall<ReqT, RespT>(
    delegate: ServerCall<ReqT, RespT>,
    private val logger: Logger
) : ForwardingServerCall.SimpleForwardingServerCall<ReqT, RespT>(delegate) {

    fun getStatusByCause(cause: Throwable?): io.grpc.Status =
        when (cause) {
        is IllegalArgumentException -> io.grpc.Status.INVALID_ARGUMENT
        is IllegalStateException -> io.grpc.Status.FAILED_PRECONDITION
        else -> io.grpc.Status.UNKNOWN
    }.withDescription(cause?.message).withCause(cause)

    fun getStatusForUnknown(incomeStatus: io.grpc.Status): io.grpc.Status =
        if (incomeStatus.code == io.grpc.Status.Code.UNKNOWN) {
            getStatusByCause(incomeStatus.cause)
        } else {
            incomeStatus
        }


    override fun close(status: io.grpc.Status, trailers: io.grpc.Metadata) {
        if (status.isOk) {
            return super.close(status, trailers)
        }

        logger.error("Error handling gRPC endpoint with status: $status", status.cause)

        super.close(getStatusForUnknown(status), trailers)
    }
}