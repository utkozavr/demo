package my.demo.client.exchangeservice

import kotlinx.coroutines.flow.Flow
import my.demo.client.ClientAbs
import my.demo.exchangeservice.ExchangeServiceGrpcKt
import my.demo.exchangeservice.ExchangeServiceOuterClass
import org.slf4j.Logger

open class ExchangeClientImp(
    host: String,
    port: Int,
    logger: Logger
): ClientAbs(host, port, logger), ExchangeClient {

    private val stub = ExchangeServiceGrpcKt.ExchangeServiceCoroutineStub(channel)

    override suspend fun getRatesFlow(): Flow<ExchangeServiceOuterClass.Rates> =
        runWithRecovery("ExchangeClientImp.getRatesFlow") { stub.getRates(emptyRequest) }
}