package my.demo.rateserver

import io.grpc.ServerCall
import io.grpc.ServerCallHandler
import io.grpc.ServerInterceptor
import org.slf4j.Logger

class ExceptionInterceptor(private val logger: Logger) : ServerInterceptor {

    override fun <ReqT : Any, RespT : Any> interceptCall(
        call: ServerCall<ReqT, RespT>,
        headers: io.grpc.Metadata?,
        next: ServerCallHandler<ReqT, RespT>
    ): ServerCall.Listener<ReqT> {
        return next.startCall(ExceptionTranslatingServerCall(call, logger), headers)
    }
}