package my.demo.currencyservice.gateway


interface EventGateway {
    suspend fun publishUpdatedEvent()
}