package my.demo.exchangeservice.domen

import my.demo.currencyservice.CurrencyServiceOuterClass
import java.math.BigDecimal

interface Rate {
    val source: CurrencyServiceOuterClass.Currency
    val destination: CurrencyServiceOuterClass.Currency
    val rate: BigDecimal
}