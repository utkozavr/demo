package my.demo.exchangeservice

import io.grpc.BindableService
import io.grpc.ServerInterceptor
import my.demo.config.Config
import my.demo.exchangeservice.domen.ActiveCurrencyCodesProviderImp
import my.demo.exchangeservice.domen.RateProvider
import my.demo.exchangeservice.domen.RateProviderImp
import my.demo.exchangeservice.gateway.CurrencyGatewayImp
import my.demo.exchangeservice.gateway.EventGatewayImp
import my.demo.exchangeservice.gateway.RateGatewayImp
import my.demo.grpcserver.GrpcServer

import my.demo.grpcserver.MainGrpcServer
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class Main(
    portDefault: Int,
    args: Array<String>,
    logger: Logger,
    private val rateProvider: RateProvider,
) : MainGrpcServer(portDefault, args, logger) {
    override fun getService(): BindableService = ExchangeServiceImp(rateProvider)
    override fun getInterceptors(): List<ServerInterceptor> = emptyList()
}

fun main(args: Array<String>) {

    val config = Config.load().exchangeServiceConfig
    val logger = LoggerFactory.getLogger(config.loggerName)

    val eventGateway = EventGatewayImp(
        host = config.eventServiceHost,
        port = config.eventServicePort,
        logger = logger
    )

    val currencyGateway = CurrencyGatewayImp(
        host = config.currencyServiceHost,
        port = config.currencyServicePort,
        logger = logger
    )

    val activeCurrencyCodesProvider = ActiveCurrencyCodesProviderImp(
        currencyGateway,
        eventGateway
    )

    val rateGateway = RateGatewayImp(
        host = config.rateServerHost,
        port = config.rateServerPort,
        tokenKey = config.rateServerTokenKey,
        token = config.rateServerToken,
        logger = logger
    )

    val rateProvider = RateProviderImp(
        baseCurrencyCode = config.baseCurrency,
        activeCurrencyCodesProvider = activeCurrencyCodesProvider,
        rateGateway = rateGateway
    )

    Main(config.port, args, logger, rateProvider).start()
}