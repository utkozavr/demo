package my.demo.rateserver

import io.grpc.ServerCall
import io.mockk.every
import io.mockk.spyk
import io.mockk.verify
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import kotlin.reflect.typeOf

internal class ExceptionInterceptorTest {

    @Test
    fun interceptCall() {

        val exceptionInterceptor = spyk(ExceptionInterceptor(loggerMockk))
        every { serverCallHandlerMockk.startCall(any(), metadataMockk) } returns object : ServerCall.Listener<Any?>() {}

        exceptionInterceptor.interceptCall(serverCallMockk, metadataMockk, serverCallHandlerMockk)

        verify(exactly = 1){ serverCallHandlerMockk.startCall(match { it is ExceptionTranslatingServerCall }, metadataMockk) }

    }
}