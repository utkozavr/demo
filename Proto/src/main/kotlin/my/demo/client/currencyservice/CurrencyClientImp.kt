package my.demo.client.currencyservice

import my.demo.client.ClientAbs
import my.demo.currencyservice.CurrencyServiceGrpcKt
import my.demo.currencyservice.CurrencyServiceOuterClass
import org.slf4j.Logger

open class CurrencyClientImp(
    host: String,
    port: Int,
    logger: Logger
): ClientAbs(host, port, logger), CurrencyClient {

    private val stub = CurrencyServiceGrpcKt.CurrencyServiceCoroutineStub(channel)

    override suspend fun requestCurrencies(): List<CurrencyServiceOuterClass.Currency> =
        runWithRecovery("CurrencyClientImp.requestCurrencies") {
            stub.getCurrency(emptyRequest)
                .currenciesList
        }

    override suspend fun toggleDisabled(currency: CurrencyServiceOuterClass.Currency): CurrencyServiceOuterClass.Currency =
        runWithRecovery("CurrencyClientImp.requestCurrencies") {
            stub.toggleDisabled(currency)
        }



}