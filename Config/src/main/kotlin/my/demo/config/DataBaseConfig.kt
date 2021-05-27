package my.demo.config

data class DataBaseConfig(
    val socketPath: String,
    val dbName: String,
    val user: String,
    val password: String
)
