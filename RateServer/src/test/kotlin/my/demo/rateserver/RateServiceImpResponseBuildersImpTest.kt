package my.demo.rateserver

import io.mockk.spyk
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class RateServiceImpResponseBuildersImpTest {

    val responseBuilders = RateServiceImpResponseBuildersImp()

    @Test
    fun buildTimestamp() {
        val sec = 500L
        val resp = responseBuilders.buildTimestamp(sec)

        assertEquals(sec, resp.seconds)
    }

    @Test
    fun buildTimestampCurrentTime() {
        val sec = System.currentTimeMillis() / 1000
        val resp = responseBuilders.buildTimestamp()

        assertTrue { resp.seconds >= sec }
    }

    @Test
    fun buildRatesResponseWithTimestamp() {

        val rates = listOf(rateMockk, rateMockk)

        val resp = responseBuilders.buildRatesResponse(
            rates,
            rpcStatusMockk,
            timestampMockk
        )

        assertEquals(rates, resp.ratesList)
        assertEquals(rpcStatusMockk, resp.status)
        assertEquals(timestampMockk, resp.timestamp)
    }

    @Test
    fun buildRatesResponse() {

        val rates = listOf(rateMockk, rateMockk)

        val resp = responseBuilders.buildRatesResponse(
            rates,
            Status.OK,
            ""
        )

        assertEquals(rates, resp.ratesList)
        assertEquals(Status.OK.code, resp.status.code)
        assertTrue { resp.timestamp.seconds > 0 }
    }

    @Test
    fun buildStatusByStatus() {

        val status = Status.OK
        val message = "111"
        val resp = responseBuilders.buildStatus(status, message)

        assertEquals(status.code, resp.code)
        assertEquals(message, resp.message)
    }

    @Test
    fun buildStatus() {

        val code = 5
        val message = ""
        val resp = responseBuilders.buildStatus(code, message)

        assertEquals(code, resp.code)
        assertEquals(message, resp.message)

    }

    @Test
    fun buildCurrenciesResponse() {

        val currencies = listOf(currencyMockk, currencyMockk)
        val status = Status.OK
        val message = "111"

        val resp = responseBuilders.buildCurrenciesResponse(
            currencies,
            status,
            message
        )

        assertEquals(currencies, resp.currenciesList)
        assertEquals(status.code, resp.status.code)
        assertEquals(message, resp.status.message)
    }

    @Test
    fun buildCurrenciesResponseBuRpcStatus() {

        val currencies = listOf(currencyMockk, currencyMockk)

        val resp = responseBuilders.buildCurrenciesResponse(
            currencies,
            rpcStatusMockk
        )

        assertEquals(currencies, resp.currenciesList)
        assertEquals(rpcStatusMockk, resp.status)

    }
}