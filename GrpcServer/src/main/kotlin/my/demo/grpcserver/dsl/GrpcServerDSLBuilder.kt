package my.demo.grpcserver.dsl

import io.grpc.Server
import my.demo.grpcserver.GrpcServer
import my.demo.grpcserver.GrpcServerImp
import org.slf4j.Logger

class GrpcServerDSLBuilder {
    private var server: Server? = null
    private var logger: Logger? = null

    fun server(
        builder: ServerDSLBuilder = ServerDSLBuilder(),
        lambda: ServerDSLBuilder.() -> Unit
    ) { this.server = builder.apply(lambda).build() }

    fun logger(lambda: () -> Logger) { this.logger = lambda() }

    fun build(): GrpcServer = GrpcServerImp(checkNotNull(server), checkNotNull(logger))
}

inline fun grpcServer(
    builder: GrpcServerDSLBuilder = GrpcServerDSLBuilder(),
    block: GrpcServerDSLBuilder.() -> Unit
) = builder.apply(block).build()