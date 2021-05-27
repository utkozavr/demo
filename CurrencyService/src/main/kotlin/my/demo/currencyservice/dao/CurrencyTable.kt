package my.demo.currencyservice.dao

import my.demo.currencyservice.domain.Currency
import my.demo.currencyservice.domain.CurrencyAdaptor
import my.demo.currencyservice.domain.CurrencyImp
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.`java-time`.timestamp
import org.jetbrains.exposed.sql.transactions.transaction

import java.time.Instant

object CurrencyTable: Table(), CurrencyDAO {

    val code = varchar("code", 5)
    val name = varchar("name", 300)
    val isActive = bool("isActive")
    val isDisabledManually = bool("isDisabledManually").default(false)

    val createdAt = timestamp("createdAt")
    val updatedAt = timestamp("updatedAt")

    override val primaryKey = PrimaryKey(code, name = "pk_cur")



    private fun fromRow(rows: List<ResultRow>): List<CurrencyAdaptor> = rows.map { CurrencyAdaptorImp(it) }

    private fun fromRowOrThrow(row: ResultRow?): CurrencyAdaptor = CurrencyAdaptorImp(checkNotNull(row))

    private fun getByCode(code: String): ResultRow? = transaction {
        select { CurrencyTable.code eq code }.toList().firstOrNull()
    }

    private fun insertInternal(currency: Currency): Currency {
        transaction {
            insertIgnore {
                it[code] = currency.code
                it[name] = currency.name
                it[isActive] = currency.isActive
                it[isDisabledManually] = currency.isDisabledManually
                it[createdAt] = Instant.now()
                it[updatedAt] = Instant.now()
            }
        }

        return currency
    }

    private fun updateInternal(currency: Currency): Currency {
        transaction {
            CurrencyTable.update({CurrencyTable.code eq currency.code}){
                it[name] = currency.name
                it[isActive] = currency.isActive
                it[isDisabledManually] = currency.isDisabledManually
                it[updatedAt] = Instant.now()
            }
        }

        return currency
    }

    override fun insert(currency: Currency) {
        insertInternal(currency)
    }

    override fun update(currency: Currency) {
        updateInternal(currency)
    }

    override fun insertAndGet(currency: Currency): CurrencyAdaptor =
        insertInternal(currency)
            .let { getByCode(it.code) }
            .let { fromRowOrThrow(it) }

    override fun updateAndGet(currency: Currency): CurrencyAdaptor =
        updateInternal(currency)
            .let { getByCode(it.code) }
            .let { fromRowOrThrow(it) }


    override fun select(): List<CurrencyAdaptor>  = transaction {
        CurrencyTable.selectAll().toList()
    }.let { fromRow(it) }

    override fun selectBy(code: String): CurrencyAdaptor? = transaction {
        select { CurrencyTable.code eq code }.toList()
    }.let { fromRow(it) }.firstOrNull()

    class CurrencyAdaptorImp(private val currencyFromDAO: ResultRow): CurrencyAdaptor {

        private fun fromRow(row: ResultRow): Currency = CurrencyImp(
            code = row[code],
            name = row[name],
            isActive = row[isActive],
            isDisabledManually = row[isDisabledManually]
        )

        override fun get(): Currency = fromRow(currencyFromDAO)

    }

}