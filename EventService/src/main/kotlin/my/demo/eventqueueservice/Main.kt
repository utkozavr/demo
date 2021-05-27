package my.demo.eventqueueservice

import io.grpc.BindableService
import io.grpc.ServerInterceptor
import my.demo.config.Config
import my.demo.grpcserver.MainGrpcServer
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class Main(
    portDefault: Int,
    args: Array<String>,
    logger: Logger
): MainGrpcServer(portDefault, args, logger) {
    override fun getService(): BindableService = EventQueueServiceImp()
    override fun getInterceptors(): List<ServerInterceptor> = emptyList()
}

fun main(args: Array<String>) {

    val config = Config.load().eventServiceConfig
    val logger = LoggerFactory.getLogger(config.loggerName)

    Main(config.port, args, logger).start()

}