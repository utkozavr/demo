package my.demo.graphqlgateway.gateway

import my.demo.currencyservice.CurrencyServiceOuterClass
import my.demo.eventqueueservice.EventQueueServiceOuterClass
import my.demo.exchangeservice.ExchangeServiceOuterClass

fun CurrencyServiceOuterClass.Currency.map() = mapOf(
    "code" to this.code.toString(),
    "name" to this.name,
    "isActive" to this.isActive.toString()
)

fun EventQueueServiceOuterClass.EventMessage.map() = mapOf(
    "timestamp" to this.timestamp.seconds.toString(),
    "sender" to this.sender.name,
    "message" to this.message.name
)

fun ExchangeServiceOuterClass.Rates.map() = this.ratesList.map {
    mapOf(
        "source" to it.source,
        "destination" to it.destination,
        "rate" to it.rate.toFloat()
    )
}