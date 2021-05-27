package my.demo.graphqlgateway

fun List<Any?>.listToJsonString(): String {
    val nodes = this.map { it.toJsonString() }
    return "[ " + nodes.joinToString(separator = ", ") + " ]"
}

fun Any?.toJsonString(): String  = when (this) {
    is String -> "\"$this\""
    is Map<*, *> -> this.mapToJsonString()
    is List<*> -> this.listToJsonString()
    null -> "null"
    else -> "$this"
}

fun Map<*, *>.mapToJsonString(): String {

    val nodes = this.map { (k, v) ->
        "\"$k\": ${v.toJsonString()}"
    }
    return "{ " + nodes.joinToString(separator = ", ") + " }"
}