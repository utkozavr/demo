package my.demo.graphqlgateway

import com.fasterxml.jackson.core.JsonParser
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.util.*

@Serializable
data class QueryParameters(
    val query: String? = null,
    val operationName: String? = null,
    val variables: Map<String, String> = emptyMap<String, String>()
) {
    companion object {
        fun from(queryMessage: String): QueryParameters {
            return Json.decodeFromString<QueryParameters>(QueryParameters.serializer(), queryMessage)
        }
    }
}