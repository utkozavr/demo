package my.demo.currencyservice

import io.grpc.BindableService
import io.grpc.ServerInterceptor
import my.demo.config.Config
import my.demo.currencyservice.dao.CurrencyTable
import my.demo.currencyservice.dao.dataBaseConnector
import my.demo.currencyservice.domain.*
import my.demo.currencyservice.gateway.RateServerGatewayImp
import my.demo.currencyservice.gateway.EventGatewayImp
import my.demo.grpcserver.MainGrpcServer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.time.ExperimentalTime
import kotlin.time.seconds

class Main(
    portDefault: Int,
    args: Array<String>,
    logger: Logger,
    private val currencyProvider: CurrencyProvider
) : MainGrpcServer(portDefault, args, logger) {
    override fun getService(): BindableService = CurrencyServiceImp(currencyProvider)
    override fun getInterceptors(): List<ServerInterceptor> = emptyList()
}


@ExperimentalTime
fun main(args: Array<String>) {

    val config = Config.load().currencyServiceConfig
    val logger = LoggerFactory.getLogger(config.loggerName)

    dataBaseConnector {
        tables(CurrencyTable)
    }.connect(
        config.dataBaseConfig.socketPath,
        config.dataBaseConfig.dbName,
        config.dataBaseConfig.user,
        config.dataBaseConfig.password
    )

    val currencyGateway = RateServerGatewayImp(
        config.rateServerHost,
        config.rateServerPort,
        config.rateServerTokenKey,
        config.rateServerToken,
        logger
    )

    val eventGateway = EventGatewayImp(config.eventServiceHost, config.eventServicePort, logger)

    val currencyProvider = CurrencyProviderImp(
        CurrencyTable,
        config.invalidateCacheEach,
        config.invalidateCacheTimeUnit
    )

    CurrencyUpdateTaskImp(
        logger = logger,
        currencyDAO = CurrencyTable,
        currencyGateway = currencyGateway,
        eventGateway = eventGateway,
        delayDuration = config.updateTaskDelaySeconds.seconds,
        delayDurationOnError = config.updateTaskOnErrorDelaySeconds.seconds,
        cacheCleaner = { currencyProvider.clean() }
    ).start()




    Main(config.port, args, logger, currencyProvider).start()

}