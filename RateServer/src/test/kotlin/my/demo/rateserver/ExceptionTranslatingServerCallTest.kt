package my.demo.rateserver

import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.spyk
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class ExceptionTranslatingServerCallTest {

    val exceptionTranslatingServerCall: ExceptionTranslatingServerCall<Any, Any>
        get() = spyk(ExceptionTranslatingServerCall(serverCallMockk, loggerMockk))

    @BeforeEach
    fun setUp() {
        every { serverCallMockk.close(any(), any()) } returns Unit
        every { loggerMockk.error(any(), any<Throwable>()) } returns Unit
    }

    @AfterEach
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun getStatusByCauseNotMatched() {

        val message = "111"
        val throwable = Exception(message)
        val excServerCall = exceptionTranslatingServerCall
        val resp = excServerCall.getStatusByCause(throwable)


        assertEquals(io.grpc.Status.UNKNOWN.code, resp.code)
        assertEquals(message, resp.description)
        assertEquals(throwable, resp.cause)

    }

    @Test
    fun getStatusByCauseMatched() {

        val message = "111"
        val throwable = IllegalArgumentException(message)
        val excServerCall = exceptionTranslatingServerCall
        val resp = excServerCall.getStatusByCause(throwable)


        assertEquals(io.grpc.Status.INVALID_ARGUMENT.code, resp.code)
        assertEquals(message, resp.description)
        assertEquals(throwable, resp.cause)

    }

    @Test
    fun getStatusForUnknown() {
        val throwable = Exception("")
        val status = io.grpc.Status.UNKNOWN.withCause(throwable)
        val excServerCall = exceptionTranslatingServerCall
        excServerCall.getStatusForUnknown(status)

        verify(exactly = 1) { excServerCall.getStatusByCause(throwable) }

    }

    @Test
    fun getStatusForUnknownWithNotUnknownStatus() {
        val status = io.grpc.Status.UNAVAILABLE
        val excServerCall = exceptionTranslatingServerCall
        val resp = excServerCall.getStatusForUnknown(status)

        verify(exactly = 0) { excServerCall.getStatusByCause(any()) }

        assertEquals(status, resp)
    }

    @Test
    fun closeWithStatusOk() {

        exceptionTranslatingServerCall.close(io.grpc.Status.OK, metadataMockk)

        verify(exactly = 0){ loggerMockk.error(any(), any<Throwable>()) }
        verify(exactly = 1){ serverCallMockk.close(io.grpc.Status.OK, metadataMockk) }
    }

    @Test
    fun closeWithStatusNotOk() {
        val excServerCall = exceptionTranslatingServerCall
        val status = io.grpc.Status.UNAVAILABLE

        every { excServerCall.getStatusForUnknown(status) } returns status

        excServerCall.close(status, metadataMockk)

        verify(exactly = 1){ loggerMockk.error(any(), any<Throwable>()) }
        verify(exactly = 1){ excServerCall.getStatusForUnknown(status) }
        verify(exactly = 1){ serverCallMockk.close(status, metadataMockk) }
    }
}