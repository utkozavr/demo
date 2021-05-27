package my.demo.rateserver.dsl

import my.demo.rateserver.RateProvider
import my.demo.rateserver.RateServiceImp
import my.demo.rateserver.RateServiceImpResponseBuilders
import kotlin.coroutines.CoroutineContext

class RateServiceImpDSLBuilder {
    private var rateProvider: RateProvider? = null
    private var minDelay: Long? = null
    private var maxDelay: Long? = null
    private var defaultBaseCurrencyCode: String? = null
    private var responseBuilders: RateServiceImpResponseBuilders? = null

    fun rateProvider(lambda: () -> RateProvider) { this.rateProvider = lambda() }
    fun minDelay(lambda: () -> Long) { this.minDelay = lambda() }
    fun maxDelay(lambda: () -> Long) { this.maxDelay = lambda() }
    fun defaultBaseCurrencyCode(lambda: () -> String) { this.defaultBaseCurrencyCode = lambda() }
    fun responseBuilders(lambda: () -> RateServiceImpResponseBuilders) { this.responseBuilders = lambda() }

    fun build() = RateServiceImp(
        checkNotNull(rateProvider),
        checkNotNull(minDelay),
        checkNotNull(maxDelay),
        checkNotNull(defaultBaseCurrencyCode),
        checkNotNull(responseBuilders)
    )
}

fun service(
    builder: RateServiceImpDSLBuilder = RateServiceImpDSLBuilder(),
    lambda: RateServiceImpDSLBuilder.() -> Unit
) = builder.apply(lambda).build()