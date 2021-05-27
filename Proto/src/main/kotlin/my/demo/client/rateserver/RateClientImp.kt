package my.demo.client.rateserver

import com.google.protobuf.Empty
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.grpc.Metadata
import io.grpc.stub.MetadataUtils
import kotlinx.coroutines.flow.Flow
import my.demo.client.ClientAbs
import my.demo.rateserver.RateServiceGrpcKt
import my.demo.rateserver.RateServiceOuterClass
import org.slf4j.Logger

open class RateClientImp(
    host: String,
    port: Int,
    logger: Logger,
    private val tokenKey: String,
    private val token: String
): ClientAbs(host, port, logger), RateClient {

    private val metadata = Metadata().apply {
        this.put(Metadata.Key.of(tokenKey, Metadata.ASCII_STRING_MARSHALLER),  token)
    }

    final override val channel: ManagedChannel = ManagedChannelBuilder
        .forAddress(host, port)
        .intercept(MetadataUtils.newAttachHeadersInterceptor(metadata))
        .usePlaintext()
        .build()

    private val stub = RateServiceGrpcKt.RateServiceCoroutineStub(channel)

    override suspend fun requestRates(request: RateServiceOuterClass.RateRequest): Flow<RateServiceOuterClass.RatesResponse> =
        runWithRecovery("RateClientImp.requestRates") { stub.getRates(request) }

    override suspend fun requestCurrencies(): RateServiceOuterClass.CurrenciesResponse =
        runWithRecovery("RateClientImp.requestCurrencies") { stub.getCurrency(Empty.getDefaultInstance()) }

}