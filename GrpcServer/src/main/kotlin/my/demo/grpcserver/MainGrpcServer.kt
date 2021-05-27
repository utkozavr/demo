package my.demo.grpcserver

import io.grpc.BindableService
import io.grpc.ServerInterceptor
import my.demo.grpcserver.dsl.grpcServer

import org.slf4j.Logger
import org.slf4j.LoggerFactory

abstract class MainGrpcServer(
    portDefault: Int = 0,
    args: Array<String> = emptyArray(),
    private val logger: Logger = LoggerFactory.getLogger(MainGrpcServer::class.java),
) {

    private val port = getPort(args, portDefault)

    fun getPort(args: Array<String>, default: Int): Int =
        args.getOrElse(0) { "$default" }.toIntOrNull() ?: throw Exception("Unable to parse port number")

    fun getGrpcServer(
        port: Int,
        logger: Logger,
        service: BindableService,
        interceptors: List<ServerInterceptor>
    ):GrpcServer = grpcServer {
        server {
            port { port }
            service { service }
            interceptAll { interceptors }
        }
        logger {
            logger
        }
    }

    protected abstract fun getInterceptors(): List<ServerInterceptor>

    protected abstract fun getService(): BindableService

    fun start(
        grpcServer: GrpcServer = getGrpcServer(
            this.port,
            this.logger,
            this.getService(),
            this.getInterceptors()
        ),
        logger: Logger = this.logger
    ) {
        logger.info("Starting grpc server on port: ${this.port}")
        grpcServer.start()
        logger.info("GRPC server started")
        grpcServer.blockUntilShutdown()
    }

}







