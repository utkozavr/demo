package my.demo.grpcserver.dsl

import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.verify
import my.demo.grpcserver.grpcServerDSLBuilderMockk
import my.demo.grpcserver.grpcServerMockk
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

internal class GrpcServerDSLBuilderKtTest {

    @AfterEach
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun rateServerTest() {
        every { grpcServerDSLBuilderMockk.build() } returns grpcServerMockk
        grpcServer(grpcServerDSLBuilderMockk){}
        verify(exactly = 1) { grpcServerDSLBuilderMockk.build() }
    }
}