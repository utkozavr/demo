package my.demo.graphqlgateway

import graphql.GraphQL
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry

@Configuration
@EnableWebSocket
open class WebSocketConfigImp: WebSocketConfigurer {
    @Autowired
    private lateinit var graphQl: GraphQL

    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry.addHandler(WebSocketHandler(graphQl), "/subscription")
            .setAllowedOrigins("*")
    }

}