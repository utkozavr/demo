package my.demo.rateserver.dsl

import my.demo.rateserver.ExceptionInterceptor
import org.slf4j.Logger

class ExceptionInterceptorDSLBuilder {
    private var logger: Logger? = null
    fun logger(lambda: () -> Logger) { this.logger = lambda() }
    fun build() = ExceptionInterceptor(checkNotNull(logger))
}

inline fun exceptionInterceptor(
    builder: ExceptionInterceptorDSLBuilder = ExceptionInterceptorDSLBuilder(),
    block: ExceptionInterceptorDSLBuilder.() -> Unit
) = builder.apply(block).build()