package my.demo.exchangeservice.domen

import my.demo.currencyservice.CurrencyServiceOuterClass
import java.math.BigDecimal

data class RateImp(
    override val source: CurrencyServiceOuterClass.Currency,
    override val destination: CurrencyServiceOuterClass.Currency,
    override val rate: BigDecimal = BigDecimal.ZERO
): Rate
