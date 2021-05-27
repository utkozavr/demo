package my.demo.currencyservice.domain

interface Cached<T> {
    fun clean()
    suspend fun get(): T
}