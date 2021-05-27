package my.demo.rateserver

import com.google.protobuf.Empty
import io.mockk.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class RateServiceImpTest {

    val defaultBaseCurrencyCode = "USD"

    val rateServiceImp: RateServiceImp
        get() = spyk(RateServiceImp(
            rateProvider = rateProviderMockk,
            minDelay = 100,
            maxDelay = 300,
            defaultBaseCurrencyCode = defaultBaseCurrencyCode,
            builders = rateServiceImpResponseBuildersMockk
        ))

    @BeforeEach
    fun setUp() {
        every { rateServiceImpResponseBuildersMockk.buildRatesResponse(any(), any(), any()) } returns ratesResponseMockk
    }

    @AfterEach
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun getSleepDuration() {
        rateServiceImp.getSleepDuration(0, 100).apply {
            assertTrue { this in 1..99 }
        }
        rateServiceImp.getSleepDuration(0, 0).apply {
            assertEquals(0, this)
        }
        rateServiceImp.getSleepDuration(100, 0).apply {
            assertEquals(0, this)
        }
    }



    @Test
    fun getCurrency() {
        val currencies = listOf(currencyMockk, currencyMockk)
        every { rateProviderMockk.getCurrencies() } returns currencies
        every { rateServiceImpResponseBuildersMockk.buildCurrenciesResponse(any(), any(), any()) } returns currenciesResponseMockk

        runBlocking { rateServiceImp.getCurrency(Empty.getDefaultInstance()) }

        verify(exactly = 1) { rateServiceImpResponseBuildersMockk.buildCurrenciesResponse(any(), any(), any()) }
    }

    @Test
    fun getRatesWrongBaseCurrency() {

        every { rateRequestMockk.baseCurrencyCode } returns "USD1"
        var resp: RateServiceOuterClass.RatesResponse? = null

        runBlocking {
            rateServiceImp.getRates(rateRequestMockk).collect {
                resp = it
            }
        }

        assertEquals(ratesResponseMockk, resp)

        verify(exactly = 1){ rateServiceImpResponseBuildersMockk.buildRatesResponse(emptyList(), Status.ERROR, match { it.isNotBlank() }) }

    }

    @Test
    fun getRates() {

        every { rateRequestMockk.baseCurrencyCode } returns defaultBaseCurrencyCode
        every { rateServiceImpResponseBuildersMockk.buildRatesResponse(any(), any(), any()) } returns ratesResponseMockk
        every { rateServiceImp.getSleepDuration(any(), any()) } returns 1L
        coEvery { rateServiceImp.sleep(any()) } returns Unit
        coEvery { rateProviderMockk.getRates(any()) } returns listOf(rateMockk)

        val resp: MutableList<RateServiceOuterClass.RatesResponse> = mutableListOf()


        runBlocking {
            rateServiceImp.getRates(rateRequestMockk).onEach {
                resp.add(it)
            }.catch {
                println(it.message)
            }.take(2).collect()
        }

        assertEquals(listOf(ratesResponseMockk, ratesResponseMockk), resp)

        verify(exactly = 2){ rateServiceImpResponseBuildersMockk.buildRatesResponse(listOf(rateMockk), Status.OK, match { it.isBlank() }) }

    }

    @Test
    fun getRatesException() {

        val excMessage = "test"

        every { rateRequestMockk.baseCurrencyCode } returns defaultBaseCurrencyCode
        every { rateServiceImpResponseBuildersMockk.buildRatesResponse(any(), any(), any()) } returns ratesResponseMockk
        every { rateServiceImp.getSleepDuration(any(), any()) } returns 1L
        coEvery { rateServiceImp.sleep(any()) } returns Unit
        coEvery { rateProviderMockk.getRates(any()) } throws Exception(excMessage)

        val resp: MutableList<RateServiceOuterClass.RatesResponse> = mutableListOf()


        runBlocking {
            rateServiceImp.getRates(rateRequestMockk).onEach {
                resp.add(it)
            }.catch {
                println(it.message)
            }.take(2).collect()
        }

        assertEquals(listOf(ratesResponseMockk), resp)

        verify(exactly = 1){ rateServiceImpResponseBuildersMockk.buildRatesResponse(emptyList(), Status.ERROR, excMessage) }

    }
}