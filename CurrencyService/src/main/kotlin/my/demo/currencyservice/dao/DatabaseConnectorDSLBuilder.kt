package my.demo.currencyservice.dao

import org.jetbrains.exposed.sql.Table

class DatabaseConnectorDSLBuilder {

    private val tables: MutableList<Table> = mutableListOf()

    fun tables(vararg tables: Table) { tables.forEach { this.tables.add(it) } }

    fun build():DatabaseConnector =
        DatabaseConnectorImp(
            tables = tables,
        )
}

fun dataBaseConnector(
    builder: DatabaseConnectorDSLBuilder = DatabaseConnectorDSLBuilder(),
    lambda: DatabaseConnectorDSLBuilder.() -> Unit
) = builder.apply(lambda).build()