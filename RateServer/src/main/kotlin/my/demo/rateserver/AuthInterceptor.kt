package my.demo.rateserver

import io.grpc.*
import org.slf4j.Logger


class AuthInterceptor(
    private val authProvider: AuthProvider,
    private val tokenKey: String,
    private val logger: Logger
): ServerInterceptor {

    val permissionDeniedWithNullToken = io.grpc.Status.PERMISSION_DENIED.augmentDescription("token is not provided")
    val permissionDeniedByToken = io.grpc.Status.PERMISSION_DENIED

    fun getMetadataKey(tokenKey: String = this.tokenKey): Metadata.Key<String> = Metadata.Key.of(tokenKey, Metadata.ASCII_STRING_MARSHALLER)

    private fun Metadata.asString(): String {
        return this.keys().map { it to this.get(getMetadataKey(it)) }.toMap().toString()
    }

    override fun <ReqT : Any, RespT : Any> interceptCall(
        call: ServerCall<ReqT, RespT>,
        headers: Metadata?,
        next: ServerCallHandler<ReqT, RespT>
    ): ServerCall.Listener<ReqT> {

        val token = headers?.get(getMetadataKey())

        return when{
            token == null -> {
                logger.warn("${permissionDeniedWithNullToken.toString()} Metadata: ${headers?.asString()}")
                call.close(permissionDeniedWithNullToken, Metadata())
                object : ServerCall.Listener<ReqT>() {}
            }
            !authProvider.isTokenValid(token) -> {
                logger.warn("$permissionDeniedByToken token: $token")
                call.close(permissionDeniedByToken, Metadata())
                object : ServerCall.Listener<ReqT>() {}
            }
            else -> next.startCall(call, headers)
        }



    }


}