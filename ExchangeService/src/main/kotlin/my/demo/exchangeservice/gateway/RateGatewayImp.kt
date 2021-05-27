package my.demo.exchangeservice.gateway

import com.google.rpc.Status
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import my.demo.client.rateserver.RateClientImp
import my.demo.rateserver.RateServiceOuterClass
import org.slf4j.Logger
import java.math.BigDecimal

class RateGatewayImp(
    host: String,
    port: Int,
    tokenKey: String,
    token: String,
    override val logger: Logger
): RateGateway, RateClientImp(host, port, logger, tokenKey, token) {

    private val codeOK = Status.getDefaultInstance().code
    private val className = this.javaClass.simpleName


    override suspend fun getFlow(baseCurrencyCode: String): Flow<Map<String, BigDecimal>> =
        requestRates(
            RateServiceOuterClass.RateRequest
                .newBuilder()
                .setBaseCurrencyCode(baseCurrencyCode)
                .build()
        ).map {
            when(it.status.code){
                codeOK -> {
                    it.ratesList
                        .map { it.currency.code to it.value.toBigDecimal() }
                        .toMap()
                }
                else -> {
                    logger.warn("$className get status code: ${it.status.code} message: ${it.status.message}")
                    emptyMap()
                }
            }
        }

}