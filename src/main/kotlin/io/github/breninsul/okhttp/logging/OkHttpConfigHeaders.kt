package io.github.breninsul.okhttp.logging


import io.github.breninsul.logging2.HttpBodyType
import io.github.breninsul.logging2.HttpConfigHeaders
import io.github.breninsul.logging2.HttpConfigHeaders.TECHNICAL_HEADERS
import okhttp3.Request

/**
 * Returns a list of technical headers present in the HTTP request.
 *
 * @return A list of pairs representing the technical headers, where each pair consists of a header name and its corresponding value.
 */
fun Request.getTechnicalHeaders(): List<Pair<String, String>> = TECHNICAL_HEADERS.map { it to this.headers[it] }.filter { it.second != null } as List<Pair<String, String>>

/**
 * Removes the technical headers from the current request and returns a new request without the technical headers.
 *
 * @return A new [Request] object without the technical headers.
 */
fun Request.removeTechnicalHeaders(): Request {
    val builder=TECHNICAL_HEADERS.fold(this.newBuilder()) { b,h ->b.removeHeader(h) }
   return builder.build()
}

/**
 * Sets the technical headers of the request.
 *
 * @param list a list of pairs representing the header name and value.
 * @return the modified request object with the technical headers set.
 */
fun Request.setTechnicalHeaders(list: List<Pair<String, String>>): Request {
    val builder=list.fold(this.newBuilder()) { b,h ->b.header(h.first,h.second) }
   return  builder.build()
}

/**
 * Extracts the `LOG_REQUEST_ID` header from the HTTP request and converts its value to a Boolean.
 *
 * @return The boolean value of the `LOG_REQUEST_ID` header if present, or null if the header is not found.
 */
fun Request.logRequestId(): Boolean? = headers[HttpConfigHeaders.LOG_REQUEST_ID]?.toBoolean()

/**
 * Adds or removes a header to handle the logging of the request ID.
 * When the `log` parameter is non-null, the method adds a header with the name defined in `HttpConfigHeaders.LOG_REQUEST_ID`
 * and the string representation of the `log` parameter as its value. If the `log` parameter is null, the method removes
 * the header with the name `HttpConfigHeaders.LOG_REQUEST_ID`.
 *
 * @param log A Boolean value that determines whether to add or remove the request ID log header. If true or false,
 * a header is added with the respective string value. If null, the header is removed.
 * @return The instance of `Request.Builder` to allow for method chaining.
 */
fun Request.Builder.logRequestId(log:Boolean?): Request.Builder {
    val headerName = HttpConfigHeaders.LOG_REQUEST_ID
    if (log!=null ) {
        this.header(headerName, log.toString())
    } else {
        this.removeHeader(headerName)
    }
    return this
}
/**
 * Retrieves the value of the "LOG_REQUEST_URI" header from the current HTTP request headers and
 * converts it to a Boolean.
 *
 * @return The Boolean value of the "LOG_REQUEST_URI" header, or null if the header is not present
 * or cannot be converted to a Boolean.
 */
fun Request.logRequestUri(): Boolean? = headers[HttpConfigHeaders.LOG_REQUEST_URI]?.toBoolean()

/**
 * Allows logging configuration for the request URI by adding or removing a specific header.
 *
 * @param log A Boolean specifying whether to log the request URI.
 *            If true, the header for logging the URI is added.
 *            If null, the header for logging the URI is removed.
 * @return The updated [Request.Builder] instance.
 */
fun Request.Builder.logRequestUri(log:Boolean?): Request.Builder {
    val headerName = HttpConfigHeaders.LOG_REQUEST_URI
    if (log!=null ) {
        this.header(headerName, log.toString())
    } else {
        this.removeHeader(headerName)
    }
    return this
}
/**
 * Retrieves and parses the `LOG_REQUEST_MASK_QUERY_PARAMETERS` header to obtain a list of query
 * parameter names that should have their values masked during request logging.
 *
 * The header `LOG_REQUEST_MASK_QUERY_PARAMETERS` should contain a comma-separated list of query
 * parameter names. If the header is not present or is empty, this method will return `null`.
 *
 * @return A list of query parameter names to mask, or `null` if the header is not present or is empty.
 */
fun Request.logRequestMaskQueryParameters(): List<String>? = headers[HttpConfigHeaders.LOG_REQUEST_MASK_QUERY_PARAMETERS]?.split(",")

/**
 * Configures the request builder to log request query parameters with masking applied.
 * Adds or removes a header used to specify parameters to be masked in the request logging process.
 *
 * @param params A collection of query parameter names to be masked. If null, the masking header is removed.
 * @return The updated `Request.Builder` instance.
 */
fun Request.Builder.logRequestMaskQueryParameters(params:Collection<String>?): Request.Builder {
    val headerName = HttpConfigHeaders.LOG_REQUEST_MASK_QUERY_PARAMETERS
    if (params!=null ) {
        this.header(headerName, params.joinToString(","))
    } else {
        this.removeHeader(headerName)
    }
    return this
}

/**
 * Retrieves the value of the "LOG_REQUEST_HEADERS" header from the HttpRequest headers.
 *
 * @return The value of the "LOG_REQUEST_HEADERS" header as a Boolean, or null if the header is not present or cannot be parsed as a Boolean.
 */
fun Request.logRequestHeaders(): Boolean? = headers[HttpConfigHeaders.LOG_REQUEST_HEADERS]?.toBoolean()

/**
 * Adds or removes a header for logging request headers in the HTTP request.
 *
 * If the `log` parameter is set to a non-null value, it will add a header
 * indicating whether to log the request headers or not. If `log` is null,
 * the header will be removed.
 *
 * @param log A nullable Boolean indicating whether to log request headers.
 *            If true, request headers will be logged; if false, they will
 *            not be logged. Passing null removes the logging header.
 * @return The modified [Request.Builder] instance with the logging header added or removed.
 */
fun Request.Builder.logRequestHeaders(log:Boolean?): Request.Builder {
    val headerName = HttpConfigHeaders.LOG_REQUEST_HEADERS
    if (log!=null ) {
        this.header(headerName, log.toString())
    } else {
        this.removeHeader(headerName)
    }
    return this
}
/**
 * Retrieves and parses the headers associated with the `LOG_REQUEST_MASK_HEADERS` configuration from the request.
 * If the header is present, it splits its value into a list of strings. Otherwise, returns null.
 *
 * @return a list of header values split by a comma, or null if the `LOG_REQUEST_MASK_HEADERS` header is not present in the request.
 */
fun Request.logRequestMaskHeaders(): List<String>? = headers[HttpConfigHeaders.LOG_REQUEST_MASK_HEADERS]?.split(",")

/**
 * Updates the request's headers to include or remove a custom header for masked logging of specified request headers.
 *
 * @param params A collection of header names to be masked in the logs. If null, the masking header is removed.
 * @return The modified request builder with updated headers.
 */
fun Request.Builder.logRequestMaskHeaders(params:Collection<String>?): Request.Builder {
    val headerName = HttpConfigHeaders.LOG_REQUEST_MASK_HEADERS
    if (params!=null ) {
        this.header(headerName, params.joinToString(","))
    } else {
        this.removeHeader(headerName)
    }
    return this
}

/**
 * Retrieves the value of the "Log-Request-Body" header from the HttpRequest instance.
 *
 * @return The boolean value of the "Log-Request-Body" header, or null if the header is not present or cannot be parsed as a boolean.
 */
fun Request.logRequestBody(): Boolean? = headers[HttpConfigHeaders.LOG_REQUEST_BODY]?.toBoolean()

/**
 * Configures the request to log or suppress the request body details in the logs by adding or removing a specific header.
 *
 * @param log A boolean indicating whether to log the request body (true to log, false to suppress).
 *            If `null`, the header controlling logging is removed.
 * @return The modified Request.Builder instance.
 */
fun Request.Builder.logRequestBody(log:Boolean?): Request.Builder {
    val headerName = HttpConfigHeaders.LOG_REQUEST_BODY
    if (log!=null ) {
        this.header(headerName, log.toString())
    } else {
        this.removeHeader(headerName)
    }
    return this
}
/**
 * Retrieves a mapping of HTTP body types to a set of keys that should be masked in the request body,
 * based on the `LOG_REQUEST_MASK_BODY_KEYS` header. The header is expected to contain mappings in
 * the format `type:key1,key2;type2:key3,key4`.
 *
 * Each entry in the mapping consists of the body type and a set of keys to be masked for that type.
 *
 * @return A map where the key is of type [HttpBodyType] and the value is a set of strings representing
 * the keys to be masked, or null if the header is not present or improperly formatted.
 */
fun Request.logRequestMaskBodyKeys(): Map<HttpBodyType,Set<String>>? = headers[HttpConfigHeaders.LOG_REQUEST_MASK_BODY_KEYS]?.let{ val keyTypes = it.split(";")
    val mapped=keyTypes.map { keyType ->
        val split = keyType.split(":")
        return@map if (split.size == 2) {
            HttpBodyType(split[0]) to split[1].split(",").toSet()
        } else {
            null
        }
    }.filterNotNull()
    return@let mapped.toMap()
}

/**
 * Enhances the `Request.Builder` by adding or removing a custom header used for logging
 * masked keys from the request body.
 *
 * @param params A map where the key is the `HttpBodyType` and the value is a set of string
 * keys that should be masked. If null, the header will be removed.
 * @return The modified `Request.Builder` with the added or removed header used for
 * logging masked keys.
 */
fun Request.Builder.logRequestMaskBodyKeys(params:Map<HttpBodyType,Set<String>>?): Request.Builder {
    val headerName = HttpConfigHeaders.LOG_REQUEST_MASK_BODY_KEYS
    if (params!=null ) {
        this.header(headerName, params.entries.joinToString(";") { "${it.key}:${it.value.joinToString(",")}}" })
    } else {
        this.removeHeader(headerName)
    }
    return this
}
/**
 * Retrieves the value of the LOG_REQUEST_TOOK_TIME header and converts it to a Boolean.
 *
 * @return The Boolean value of the LOG_REQUEST_TOOK_TIME header, or null if the header is not present or cannot be parsed as a Boolean.
 */
fun Request.logRequestTookTime(): Boolean? = headers[HttpConfigHeaders.LOG_REQUEST_TOOK_TIME]?.toBoolean()

/**
 * Adds or removes a header that enables logging of the request's duration.
 *
 * @param log A nullable Boolean indicating whether to log the request took time.
 *            If true, the header is added with the value "true". If false, the
 *            header is added with the value "false". If null, the header is removed.
 * @return The updated [Request.Builder] with the appropriate header modifications.
 */
fun Request.Builder.logRequestTookTime(log:Boolean?): Request.Builder {
    val headerName = HttpConfigHeaders.LOG_REQUEST_TOOK_TIME
    if (log!=null ) {
        this.header(headerName, log.toString())
    } else {
        this.removeHeader(headerName)
    }
    return this
}

fun Request.logResponseId(): Boolean? = headers[HttpConfigHeaders.LOG_RESPONSE_ID]?.toBoolean()

/**
 * Adds or removes a header from the request to log the response ID based on the provided flag.
 *
 * @param log A nullable Boolean indicating whether to log the response ID. If true, the log response ID header is added;
 * if false or null, the header is removed.
 * @return The updated Request.Builder instance with the appropriate logging configuration.
 */
fun Request.Builder.logResponseId(log:Boolean?): Request.Builder {
    val headerName = HttpConfigHeaders.LOG_RESPONSE_ID
    if (log!=null ) {
        this.header(headerName, log.toString())
    } else {
        this.removeHeader(headerName)
    }
    return this
}
/**
 * Retrieves the value of the 'LOG_RESPONSE_URI' header from the HttpRequest headers and converts it to a Boolean.
 *
 * @return The Boolean value of the 'LOG_RESPONSE_URI' header, or null if the header is not present or the value cannot be converted to Boolean.
 */
fun Request.logResponseUri(): Boolean? = headers[HttpConfigHeaders.LOG_RESPONSE_URI]?.toBoolean()

/**
 * Adds or removes a header responsible for logging the response URI to the request builder.
 *
 * @param log A nullable Boolean indicating whether to enable (true), disable (false), or remove (null)
 * the log header for response URI.
 * @return The modified [Request.Builder] instance.
 */
fun Request.Builder.logResponseUri(log:Boolean?): Request.Builder {
    val headerName = HttpConfigHeaders.LOG_RESPONSE_URI
    if (log!=null ) {
        this.header(headerName, log.toString())
    } else {
        this.removeHeader(headerName)
    }
    return this
}
/**
 * Retrieves the list of query parameter names that should be masked in the response
 * based on the configuration specified in the request headers.
 *
 * @return A list of query parameter names to be masked if configured, or null if no such configuration exists.
 */
fun Request.logResponseMaskQueryParameters(): List<String>? = headers[HttpConfigHeaders.LOG_RESPONSE_MASK_QUERY_PARAMETERS]?.split(",")

/**
 * Adds or removes a custom header to log masked query parameters in the response.
 *
 * If the collection of query parameters is provided, the method sets a header with those parameters.
 * If the collection is null, it removes the corresponding header, effectively disabling the logging of the masked query parameters.
 *
 * @param params A collection of query parameter keys to be masked in the logged response. Pass null to remove the header.
 * @return The updated instance of the [Request.Builder].
 */
fun Request.Builder.logResponseMaskQueryParameters(params:Collection<String>?): Request.Builder {
    val headerName = HttpConfigHeaders.LOG_RESPONSE_MASK_QUERY_PARAMETERS
    if (params!=null ) {
        this.header(headerName, params.joinToString(","))
    } else {
        this.removeHeader(headerName)
    }
    return this
}
/**
 * Retrieves the value of the "LOG_RESPONSE_HEADERS" header and converts it to a Boolean.
 *
 * @return The Boolean value of the "LOG_RESPONSE_HEADERS" header, or null if the header is not present or cannot be converted to Boolean.
 */
fun Request.logResponseHeaders(): Boolean? = headers[HttpConfigHeaders.LOG_RESPONSE_HEADERS]?.toBoolean()

/**
 * Adds or removes a header to log response headers in the HTTP request.
 *
 * @param log Boolean value indicating whether to log response headers. If true, the header is added.
 * If null, the header is removed.
 * @return The updated Request.Builder instance with the log response headers configuration.
 */
fun Request.Builder.logResponseHeaders(log:Boolean?): Request.Builder {
    val headerName = HttpConfigHeaders.LOG_RESPONSE_HEADERS
    if (log!=null ) {
        this.header(headerName, log.toString())
    } else {
        this.removeHeader(headerName)
    }
    return this
}
/**
 * Extracts and returns a list of response header names that should be masked during logging.
 * This information is retrieved from the `HttpConfigHeaders.LOG_RESPONSE_MASK_HEADERS` header of the request.
 *
 * @return a list of response header names to be masked, or null if the header is not present.
 */
fun Request.logResponseMaskHeaders(): List<String>? = headers[HttpConfigHeaders.LOG_RESPONSE_MASK_HEADERS]?.split(",")

/**
 * Adds or removes the `LOG_RESPONSE_MASK_HEADERS` header in the request, depending on whether the
 * provided collection is null or not. If the collection is not null, it sets the header with the
 * joined string representation of the collection elements. If the collection is null, it removes
 * the header.
 *
 * @param params A collection of strings to be included in the `LOG_RESPONSE_MASK_HEADERS` header.
 *               If null, the header will be removed.
 * @return The modified [Request.Builder] instance.
 */
fun Request.Builder.logResponseMaskHeaders(params:Collection<String>?): Request.Builder {
    val headerName = HttpConfigHeaders.LOG_RESPONSE_MASK_HEADERS
    if (params!=null ) {
        this.header(headerName, params.joinToString(","))
    } else {
        this.removeHeader(headerName)
    }
    return this
}
/**
 * Retrieves the value of the "log_response_body" header and converts it to a Boolean value.
 *
 * @return The Boolean value of the "log_response_body" header, or null if the header is not present or cannot be converted to Boolean.
 */
fun Request.logResponseBody(): Boolean? = headers[HttpConfigHeaders.LOG_RESPONSE_BODY]?.toBoolean()

/**
 * Adds or removes a header to enable or disable logging of the response body.
 *
 * @param log A boolean value indicating whether to log the response body. If `true`, the response body will be logged.
 *            If `false`, logging will be disabled. If `null`, the header will be removed.
 * @return The updated [Request.Builder] instance with the configured header for response body logging.
 */
fun Request.Builder.logResponseBody(log:Boolean?): Request.Builder {
    val headerName = HttpConfigHeaders.LOG_RESPONSE_BODY
    if (log!=null ) {
        this.header(headerName, log.toString())
    } else {
        this.removeHeader(headerName)
    }
    return this
}
/**
 * Parses and retrieves a mapping of HTTP body types to sets of keys that should be masked in responses,
 * based on the `LOG_RESPONSE_MASK_BODY_KEYS` header in the request.
 *
 * The header's value is expected to consist of semicolon-separated entries. Each entry represents
 * an HTTP body type and its associated keys, separated by a colon. The keys are further comma-separated.
 *
 * Example header value format: "bodyType1:key1,key2;bodyType2:key3,key4"
 *
 * @return A map where the keys are instances of [HttpBodyType] and the values are sets of strings
 *         representing the keys to be masked for that body type. Returns null if the header
 *         is absent or improperly formatted.
 */
fun Request.logResponseMaskBodyKeys(): Map<HttpBodyType,Set<String>>? = headers[HttpConfigHeaders.LOG_RESPONSE_MASK_BODY_KEYS]?.let{ val keyTypes = it.split(";")
    val mapped=keyTypes.map { keyType ->
        val split = keyType.split(":")
        return@map if (split.size == 2) {
            HttpBodyType(split[0]) to split[1].split(",").toSet()
        } else {
            null
        }
    }.filterNotNull()
    return@let mapped.toMap()
}

fun Request.Builder.logResponseMaskBodyKeys(params:Map<HttpBodyType,Set<String>>?): Request.Builder {
    val headerName = HttpConfigHeaders.LOG_RESPONSE_MASK_BODY_KEYS
    if (params!=null ) {
        this.header(headerName, params.entries.joinToString(";") { "${it.key}:${it.value.joinToString(",")}}" })
    } else {
        this.removeHeader(headerName)
    }
    return this
}
/**
 * Retrieves the value of the "Log Response Took Time" header and converts it to a Boolean.
 *
 * @return The value of the "Log Response Took Time" header as a Boolean, or null if the header is not present or cannot be converted to a Boolean.
 */
fun Request.logResponseTookTime(): Boolean? = headers[HttpConfigHeaders.LOG_RESPONSE_TOOK_TIME]?.toBoolean()

/**
 * Adds or removes a header to indicate whether the response time should be logged.
 *
 * @param log A nullable Boolean indicating whether response time logging is enabled.
 *            If true, adds the appropriate header to enable logging.
 *            If false, disables logging. If null, removes the logging header entirely.
 * @return The updated Request.Builder instance with the modified headers.
 */
fun Request.Builder.logResponseTookTime(log:Boolean?): Request.Builder {
    val headerName = HttpConfigHeaders.LOG_RESPONSE_TOOK_TIME
    if (log!=null ) {
        this.header(headerName, log.toString())
    } else {
        this.removeHeader(headerName)
    }
    return this
}