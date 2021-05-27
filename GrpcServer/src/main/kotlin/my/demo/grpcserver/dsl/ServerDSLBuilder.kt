package my.demo.grpcserver.dsl

import io.grpc.BindableService
import io.grpc.Server
import io.grpc.ServerBuilder
import io.grpc.ServerInterceptor

class ServerDSLBuilder {
    private var port: Int? = null
    private var service: BindableService? = null
    private val interceptors: MutableList<ServerInterceptor> = mutableListOf()

    fun port(lambda: () -> Int) { this.port = lambda() }

    fun service(lambda: () -> BindableService) { this.service = lambda() }

    fun intercept(lambda: () -> ServerInterceptor) { this.interceptors.add(lambda()) }

    fun interceptAll(lambda: () -> List<ServerInterceptor>) { this.interceptors.addAll(lambda()) }

    fun build(): Server =
        ServerBuilder.forPort(checkNotNull(port))
            .addService(checkNotNull(service))
            .apply { interceptors.forEach { this.intercept(it) } }
            .build()
}
