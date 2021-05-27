package my.demo.currencyservice.dao

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import javax.sql.DataSource

class DataSourceDSLBuilder {

    private var dbName: String? = null
    private var socketPath: String? = null
    private var user: String? = null
    private var password: String? = null

    fun dbName(lambda: ()->String) { dbName = lambda() }
    fun socketPath(lambda: ()->String) { socketPath = lambda() }
    fun user(lambda: ()->String) { user = lambda() }
    fun password(lambda: ()->String) { password = lambda() }


    fun build(): DataSource = HikariConfig()
        .apply {

            // Configure which instance and what database user to connect with.
            this.jdbcUrl = java.lang.String.format("jdbc:postgresql:///%s", checkNotNull(dbName))
            this.username = checkNotNull(user) // e.g. "root", "postgres"
            this.password = checkNotNull(password) // e.g. "my-password"

            // For Java users, the Cloud SQL JDBC Socket Factory can provide authenticated connections.
            // See https://github.com/GoogleCloudPlatform/cloud-sql-jdbc-socket-factory for details.
            addDataSourceProperty("socketFactory", "com.google.cloud.sql.postgres.SocketFactory")
            addDataSourceProperty("cloudSqlInstance", checkNotNull(socketPath))

            // ... Specify additional connection properties here.
            // [START_EXCLUDE]

            // [START cloud_sql_postgres_servlet_limit]
            // maximumPoolSize limits the total number of concurrent connections this pool will keep. Ideal
            // values for this setting are highly variable on app design, infrastructure, and database.
            maximumPoolSize = 10
            // minimumIdle is the minimum number of idle connections Hikari maintains in the pool.
            // Additional connections will be established to meet this value unless the pool is full.
            minimumIdle = 2
            // [END cloud_sql_postgres_servlet_limit]

            // [START cloud_sql_postgres_servlet_timeout]
            // setConnectionTimeout is the maximum number of milliseconds to wait for a connection checkout.
            // Any attempt to retrieve a connection from this pool that exceeds the set limit will throw an
            // SQLException.
            //connectionTimeout = 10000 // 10 seconds
            // idleTimeout is the maximum amount of time a connection can sit in the pool. Connections that
            // sit idle for this many milliseconds are retried if minimumIdle is exceeded.
            //idleTimeout = 5000 // 10 minutes
            // [END cloud_sql_postgres_servlet_timeout]

            // [START cloud_sql_postgres_servlet_backoff]
            // Hikari automatically delays between failed connection attempts, eventually reaching a
            // maximum delay of `connectionTimeout / 2` between attempts.
            // [END cloud_sql_postgres_servlet_backoff]

            // [START cloud_sql_postgres_servlet_lifetime]
            // maxLifetime is the maximum possible lifetime of a connection in the pool. Connections that
            // live longer than this many milliseconds will be closed and reestablished between uses. This
            // value should be several minutes shorter than the database's timeout value to avoid unexpected
            // terminations.
            //maxLifetime = 1800000 // 30 minutes
            // [END cloud_sql_postgres_servlet_lifetime]

            // [END_EXCLUDE]

        }.let {
            // Initialize the connection pool using the configuration object.
            // [END cloud_sql_postgres_servlet_create]
            HikariDataSource(it)
        }
}