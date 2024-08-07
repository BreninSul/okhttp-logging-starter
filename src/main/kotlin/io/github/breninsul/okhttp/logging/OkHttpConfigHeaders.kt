package io.github.breninsul.okhttp.logging

import io.github.breninsul.logging.HttpConfigHeaders
import io.github.breninsul.logging.HttpConfigHeaders.TECHNICAL_HEADERS
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
 * Retrieves the value of the "LOG_REQUEST_URI" header from the current HTTP request headers and
 * converts it to a Boolean.
 *
 * @return The Boolean value of the "LOG_REQUEST_URI" header, or null if the header is not present
 * or cannot be converted to a Boolean.
 */
fun Request.logRequestUri(): Boolean? = headers[HttpConfigHeaders.LOG_REQUEST_URI]?.toBoolean()

/**
 * Retrieves the value of the "LOG_REQUEST_HEADERS" header from the HttpRequest headers.
 *
 * @return The value of the "LOG_REQUEST_HEADERS" header as a Boolean, or null if the header is not present or cannot be parsed as a Boolean.
 */
fun Request.logRequestHeaders(): Boolean? = headers[HttpConfigHeaders.LOG_REQUEST_HEADERS]?.toBoolean()

/**
 * Retrieves the value of the "Log-Request-Body" header from the HttpRequest instance.
 *
 * @return The boolean value of the "Log-Request-Body" header, or null if the header is not present or cannot be parsed as a boolean.
 */
fun Request.logRequestBody(): Boolean? = headers[HttpConfigHeaders.LOG_REQUEST_BODY]?.toBoolean()

/**
 * Retrieves the value of the LOG_REQUEST_TOOK_TIME header and converts it to a Boolean.
 *
 * @return The Boolean value of the LOG_REQUEST_TOOK_TIME header, or null if the header is not present or cannot be parsed as a Boolean.
 */
fun Request.logRequestTookTime(): Boolean? = headers[HttpConfigHeaders.LOG_REQUEST_TOOK_TIME]?.toBoolean()

/**
 * Retrieves the value of the 'LOG_RESPONSE_URI' header from the HttpRequest headers and converts it to a Boolean.
 *
 * @return The Boolean value of the 'LOG_RESPONSE_URI' header, or null if the header is not present or the value cannot be converted to Boolean.
 */
fun Request.logResponseUri(): Boolean? = headers[HttpConfigHeaders.LOG_RESPONSE_URI]?.toBoolean()

/**
 * Retrieves the value of the "LOG_RESPONSE_HEADERS" header and converts it to a Boolean.
 *
 * @return The Boolean value of the "LOG_RESPONSE_HEADERS" header, or null if the header is not present or cannot be converted to Boolean.
 */
fun Request.logResponseHeaders(): Boolean? = headers[HttpConfigHeaders.LOG_RESPONSE_HEADERS]?.toBoolean()

/**
 * Retrieves the value of the "log_response_body" header and converts it to a Boolean value.
 *
 * @return The Boolean value of the "log_response_body" header, or null if the header is not present or cannot be converted to Boolean.
 */
fun Request.logResponseBody(): Boolean? = headers[HttpConfigHeaders.LOG_RESPONSE_BODY]?.toBoolean()

/**
 * Retrieves the value of the "Log Response Took Time" header and converts it to a Boolean.
 *
 * @return The value of the "Log Response Took Time" header as a Boolean, or null if the header is not present or cannot be converted to a Boolean.
 */
fun Request.logResponseTookTime(): Boolean? = headers[HttpConfigHeaders.LOG_RESPONSE_TOOK_TIME]?.toBoolean()
