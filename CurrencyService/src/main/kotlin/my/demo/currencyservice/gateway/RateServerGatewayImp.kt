package my.demo.currencyservice.gateway

import com.google.rpc.Status
import my.demo.currencyservice.domain.CurrencyAdaptor
import my.demo.currencyservice.domain.CurrencyImp
import my.demo.client.rateserver.RateClientImp
import my.demo.currencyservice.domain.Currency
import my.demo.rateserver.RateServiceOuterClass
import org.slf4j.Logger

class RateServerGatewayImp(
    host: String,
    port: Int,
    tokenKey: String,
    token: String,
    override val logger: Logger
): RateServerGateway, RateClientImp(host, port, logger, tokenKey, token) {

    private val codeOK = Status.getDefaultInstance().code

    private val className = this::class.java.simpleName

    override suspend fun getCurrencies(): List<CurrencyAdaptor> =
        requestCurrencies()
            .let { it ->
                if (it.status.code == codeOK ) {
                    it.currenciesList.map { CurrencyAdaptorImp(it) }
                } else {
                    logger.warn("$className: ${it.status}")
                    emptyList()
                }
            }

    class CurrencyAdaptorImp(private val currencyFromGateway: RateServiceOuterClass.Currency): CurrencyAdaptor {
        override fun get(): Currency = CurrencyImp(
            currencyFromGateway.code,
            currencyFromGateway.fullName,
            true,
            false
        )
    }

}