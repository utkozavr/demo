package my.demo.currencyservice.dao

interface DatabaseConnector {
    fun connect(
        host: String,
        dbName: String,
        user: String,
        password: String
    )
}