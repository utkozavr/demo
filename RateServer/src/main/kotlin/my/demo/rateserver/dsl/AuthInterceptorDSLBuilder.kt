package my.demo.rateserver.dsl

import my.demo.rateserver.AuthInterceptor
import my.demo.rateserver.AuthProvider
import org.slf4j.Logger

class AuthInterceptorDSLBuilder {
    private var authProvider: AuthProvider? = null
    private var tokenKey: String? = null
    private var logger: Logger? = null

    fun authProvider(lambda: () -> AuthProvider) { this.authProvider = lambda() }
    fun tokenKey(lambda: () -> String) { this.tokenKey = lambda() }
    fun logger(lambda: () -> Logger) { this.logger = lambda() }


    fun build() = AuthInterceptor(
        checkNotNull(authProvider),
        checkNotNull(tokenKey),
        checkNotNull(logger)
    )

}

inline fun authInterceptor(
    builder: AuthInterceptorDSLBuilder = AuthInterceptorDSLBuilder(),
    block: AuthInterceptorDSLBuilder.() -> Unit
) = builder.apply(block).build()