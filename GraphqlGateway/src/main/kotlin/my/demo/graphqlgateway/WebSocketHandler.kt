package my.demo.graphqlgateway

import graphql.ExecutionInput
import graphql.ExecutionResult
import graphql.GraphQL
import org.reactivestreams.Publisher
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.atomic.AtomicReferenceArray


class WebSocketHandler(private val graphQL: GraphQL): TextWebSocketHandler() {

    private val log: Logger = LoggerFactory.getLogger(WebSocketHandler::class.java)

    @Throws(Exception::class)
    override fun afterConnectionEstablished(session: WebSocketSession?) {
        log.info("Websocket connection established")
    }

    @Throws(Exception::class)
    override fun afterConnectionClosed(session: WebSocketSession?, status: CloseStatus?) {
        log.info("afterConnectionClosed")
        session?.close()
    }

    @Throws(Exception::class)
    override fun handleTextMessage(webSocketSession: WebSocketSession, message: TextMessage) {
        val graphqlQuery = message.payload
        log.info("Websocket said {}", graphqlQuery)
        val parameters: QueryParameters = QueryParameters.from(graphqlQuery)
        val executionInput = ExecutionInput.newExecutionInput()
            .query(parameters.query)
            .variables(parameters.variables)
            .operationName(parameters.operationName)
            .build()


        val executionResult: ExecutionResult = graphQL.execute(executionInput)
        val stream: Publisher<ExecutionResult> = executionResult.getData()

        stream.subscribe(object : Subscriber<ExecutionResult> {

            var subscription: Subscription? = null

            override fun onSubscribe(s: Subscription) {
                subscription = s
                subscription?.request(1)
            }

            override fun onError(t: Throwable?) {
                log.error("Subscription threw an exception", t)
                try {
                    webSocketSession.close()
                } catch (e: IOException) {
                    log.error("Unable to close websocket session", e)
                }
            }

            override fun onComplete() {
                subscription = null
            }

            override fun onNext(p0: ExecutionResult) {
                try {
                    val data = p0.getData<Map<String, Any?>>()
                    val jsonString = data.toJsonString()

                    if (webSocketSession.isOpen) {
                        webSocketSession.sendMessage(TextMessage(jsonString))
                    } else {
                        subscription?.cancel()
                    }

                } catch (e: IOException) {
                    e.printStackTrace()
                    log.error("onNext error", e)
                }

                subscription?.request(1)
            }
        })

    }

}