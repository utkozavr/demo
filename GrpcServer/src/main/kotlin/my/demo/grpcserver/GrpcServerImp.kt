package my.demo.grpcserver

import io.grpc.Server
import org.slf4j.Logger

class GrpcServerImp(
    private val server: Server,
    private val logger: Logger
): GrpcServer {

    private fun setShutdownHook(callback: ()->Unit) {
        Runtime.getRuntime().addShutdownHook(
            Thread { callback() }
        )
    }

    override fun start(){
        server.start()
        setShutdownHook { stop() }
    }

    override fun stop() {
        logger.info("*** shutting down gRPC server since JVM is shutting down")
        server.shutdown()
        logger.info("*** server shut down")
    }

    override fun blockUntilShutdown() {
        server.awaitTermination()
    }
}