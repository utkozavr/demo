package my.demo.currencyservice

import com.google.protobuf.Empty
import com.google.rpc.Status
import my.demo.currencyservice.domain.Currency
import my.demo.currencyservice.domain.CurrencyProvider


class CurrencyServiceImp(
    val currencyProvider: CurrencyProvider
): CurrencyServiceGrpcKt.CurrencyServiceCoroutineImplBase() {

    private fun status(code: Int, message: String): Status= Status.newBuilder().apply {
        this.code = code
        this.message = message
    }.build()

    private fun statusOK(): Status = status(0, "")

    private fun statusError(message: String): Status = status(1, message)

    private fun Currency.asResponseCurrency(): CurrencyServiceOuterClass.Currency =
        CurrencyServiceOuterClass.Currency.newBuilder().apply {
            this.name = this@asResponseCurrency.name
            this.code = this@asResponseCurrency.code
            this.isActive = this@asResponseCurrency.isActive && !this@asResponseCurrency.isDisabledManually
        }.build()

    private fun List<Currency>.asResponse(): List<CurrencyServiceOuterClass.Currency> =
        this.map { it.asResponseCurrency() }

    private suspend fun getCurrenciesAndStatus(): Pair<List<CurrencyServiceOuterClass.Currency>, Status> = try {
        Pair(currencyProvider.getCurrencies().asResponse(), statusOK())
    } catch (e: Exception) {
        Pair(emptyList(), statusError(e.message ?: ""))
    }

    override suspend fun getCurrency(request: Empty): CurrencyServiceOuterClass.CurrenciesResponse {
        return getCurrenciesAndStatus().let {
            CurrencyServiceOuterClass.CurrenciesResponse.newBuilder().apply {
                this.addAllCurrencies(it.first)
                this.status = it.second
            }.build()
        }
    }

    override suspend fun toggleDisabled(request: CurrencyServiceOuterClass.Currency): CurrencyServiceOuterClass.Currency =
        currencyProvider
            .toggleDisabled(request.code.toUpperCase())
            .asResponseCurrency()

}