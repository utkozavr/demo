package my.demo.currencyservice.dao


import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory
import javax.sql.DataSource

class DatabaseConnectorImp(
    private val tables: List<Table>,
): DatabaseConnector {

    private val logger = LoggerFactory.getLogger(this::class.simpleName)

    private fun attemptToConnect(
        host: String,
        dbName: String,
        driver: String,
        user: String,
        password: String
    ): Boolean {

        logger.info("Attempt to connect: $host$dbName user: $user")

        return try {
            Database.connect("$host$dbName", driver, user, password ).connector().schema
            true
        } catch (e: Exception) {
            logger.warn(e.message)
            false
        }
    }

    private fun connectWithWaiting(
        host: String,
        dbName: String,
        user: String,
        password: String
    ) {
        var isConnected: Boolean = false

        while (!isConnected){
            runBlocking {
                isConnected = attemptToConnect(
                    host,
                    dbName,
                    "org.postgresql.Driver",
                    user,
                    password
                )
                if(!isConnected){
                    delay(10000)
                }
            }
        }
    }

    private fun onConnected() {
        transaction { addLogger(Slf4jSqlDebugLogger) }
        transaction {
            SchemaUtils.createMissingTablesAndColumns(tables = tables.toTypedArray())
        }
    }

    override fun connect(
        host: String,
        dbName: String,
        user: String,
        password: String
    ) {
        connectWithWaiting(host, dbName, user, password)
        onConnected()
    }

}




