package my.demo.rateserver

import com.google.protobuf.Timestamp
import io.grpc.*
import io.mockk.mockk
import my.demo.grpcserver.GrpcServer
import my.demo.rateserver.dsl.*
import org.slf4j.Logger
import kotlin.coroutines.CoroutineContext

val authProviderMockk = mockk<AuthProvider>()
val loggerMockk = mockk<Logger>()
val rateProviderMockk = mockk<RateProvider>()
val rateMockk = mockk<RateServiceOuterClass.Rate>()
val rateServiceImpResponseBuildersMockk = mockk<RateServiceImpResponseBuilders>()
val rateRequestMockk = mockk<RateServiceOuterClass.RateRequest>()
val ratesResponseMockk = mockk<RateServiceOuterClass.RatesResponse>()
val timestampMockk = mockk<Timestamp>()
val rpcStatusMockk = mockk<com.google.rpc.Status>()
val currencyMockk = mockk<RateServiceOuterClass.Currency>()
val serverCallMockk = mockk<ServerCall<Any, Any>>()
val metadataMockk = mockk<Metadata>()
val serverCallHandlerMockk = mockk<ServerCallHandler<Any, Any>>()
val grpcServerMockk = mockk<GrpcServer>()
val authInterceptorDSLBuilderMockk = mockk<AuthInterceptorDSLBuilder>()
val authInterceptorMockk = mockk<AuthInterceptor>()
val exceptionInterceptorDSLBuilderMockk = mockk<ExceptionInterceptorDSLBuilder>()
val exceptionInterceptorMockk = mockk<ExceptionInterceptor>()
val currenciesResponseMockk = mockk<RateServiceOuterClass.CurrenciesResponse>()
val rateServiceImpDSLBuilderMockk = mockk<RateServiceImpDSLBuilder>()
val rateServiceImpMockk = mockk<RateServiceImp>()