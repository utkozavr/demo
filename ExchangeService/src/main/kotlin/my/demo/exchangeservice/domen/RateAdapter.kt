package my.demo.exchangeservice.domen

interface RateAdapter {
    fun get(baseCurrency: String): Rate
}