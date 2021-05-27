package my.demo.grpcserver

import com.google.protobuf.Timestamp
import io.grpc.*
import io.mockk.mockk
import my.demo.grpcserver.dsl.GrpcServerDSLBuilder
import my.demo.grpcserver.dsl.ServerDSLBuilder
import org.slf4j.Logger
import kotlin.coroutines.CoroutineContext

val loggerMockk = mockk<Logger>(relaxed = true)
val timestampMockk = mockk<Timestamp>()
val rpcStatusMockk = mockk<com.google.rpc.Status>()
val serverCallMockk = mockk<ServerCall<Any, Any>>()
val metadataMockk = mockk<Metadata>()
val serverCallHandlerMockk = mockk<ServerCallHandler<Any, Any>>()
val serverMockk = mockk<Server>()
val coroutineContextMockk = mockk<CoroutineContext>()
val serverDSLBuilderMockk = mockk<ServerDSLBuilder>()
val serverInterceptorMockk = mockk<ServerInterceptor>()
val serverServiceDefinitionMockk = mockk<ServerServiceDefinition>()
val serviceDescriptorMockk = mockk<ServiceDescriptor>()
val grpcServerDSLBuilderMockk = mockk<GrpcServerDSLBuilder>()
val grpcServerMockk = mockk<GrpcServer>()
val serviceMockk = mockk<BindableService>()
