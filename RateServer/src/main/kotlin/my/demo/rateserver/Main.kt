package my.demo.rateserver

import io.grpc.BindableService
import io.grpc.Context
import io.grpc.ServerInterceptor
import kotlinx.coroutines.Dispatchers
import my.demo.config.Config
import my.demo.grpcserver.MainGrpcServer
import my.demo.rateserver.dsl.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.coroutines.CoroutineContext

class Main(
    portDefault: Int,
    args: Array<String>,
    logger: Logger,
    private val service: RateServiceImp,
    private val interceptors: List<ServerInterceptor>
) : MainGrpcServer(portDefault, args, logger) {
    override fun getService(): BindableService = service
    override fun getInterceptors(): List<ServerInterceptor> = interceptors
}

fun main(args: Array<String>) {

    val config = Config.load().rateServerConfig
    val logger = LoggerFactory.getLogger(config.loggerName)

    val rateServiceImp = service {
        rateProvider { DummyDataProvider() }
        minDelay { config.minDelay }
        maxDelay { config.maxDelay }
        defaultBaseCurrencyCode { config.baseCurrency }
        responseBuilders { RateServiceImpResponseBuildersImp() }
    }

    val interceptors = listOf(
        authInterceptor {
            authProvider { DummyAuthProvider(config.tokenKey, config.token) }
            tokenKey { config.tokenKey }
            logger { logger }
        },
        exceptionInterceptor {
            logger { logger }
        }
    )

    Main(
        config.port,
        args,
        logger,
        rateServiceImp,
        interceptors
    ).start()

}



