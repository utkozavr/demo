package my.demo.grpcserver

import io.grpc.Server
import org.slf4j.Logger

interface GrpcServer {
    fun start()
    fun stop()
    fun blockUntilShutdown()
}