/*
 * MIT License
 *
 * Copyright (c) 2024 BreninSul
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.github.breninsul.okhttp.logging


import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer
import org.springframework.core.Ordered
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger

/**
 * OKLoggingInterceptor is an implementation of the Interceptor interface
 * used in OkHttp library for logging requests and responses. It logs
 * the request and response details including headers, body, and other
 * metadata.
 *
 * @param properties The configuration properties for the
 *     OkLoggingInterceptor.
 */
open class OKLoggingInterceptor(protected open val properties: OkHttpLoggerProperties) : Interceptor, Ordered {
    /**
     * This variable represents a logger used for logging messages in the
     * OkHttpLogger class.
     *
     * @property logger An instance of the Logger class provided by the Java
     *     Logging API.
     * @see Logger
     */
    protected open val logger = Logger.getLogger(this.javaClass.name)

    /**
     * Represents the logging level used for logging. The value is retrieved
     * from the properties loggingLevel of the containing class.
     *
     * @see OKLoggingInterceptor
     */
    protected open val loggingLevel = properties.loggingLevel.javaLevel

    /**
     * The `headerFormat` is a property of type `String` that
     * represents the format of the header for logging. By default,
     * it is set to "===========================CLIENT OKHttp %type%
     * begin===========================". This format string is used in
     * log functions to construct the header part of the log message.
     * The placeholder "%type%" is replaced by either "Request" or
     * "Response" depending on the type of log message being constructed.
     *
     * @property headerFormat The format string for the header.
     */
    protected open val headerFormat: String = "\n===========================CLIENT OKHttp %type% begin==========================="

    /**
     * The `footerFormat` property represents the format string used to
     * construct the footer for log messages. This property is used in the
     * `constructRqBody` and `constructRsBody` functions to append the footer
     * to the log message. The format string can contain the placeholder
     * `%type%`, which will be replaced with the corresponding `Type` value
     * ("Request" or "Response"). The default value of the `footerFormat`
     * property is "===========================CLIENT OKHttp %type%
     * end===========================".
     */
    protected open val footerFormat: String = "\n===========================CLIENT OKHttp %type% end==========================="

    /**
     * Represents the format string for new lines in the log output.
     *
     * The `newLineFormat` property is used to specify the format string for
     * new lines in the log output. It is used in the `formatLine` function to
     * pad the line start with the specified format string.
     *
     * The default value for `newLineFormat` is `"="`.
     */
    protected open val newLineFormat: String = "="

    /** A random number generator. */
    protected open val random = Random()

    /**
     * Enum class representing the type of the log message. It can be either
     * Request or Response.
     */
    protected enum class Type {
        Request, Response
    }

    /**
     * Intercepts the OkHttp request chain and logs the request and response.
     *
     * @param chain The OkHttp interceptor chain.
     * @return The intercepted OkHttp response.
     */
    override fun intercept(chain: Interceptor.Chain): Response {
        return try {
            val startTime = System.currentTimeMillis()
            val rqId = getIdString()
            val request = chain.request()
            logRequest(rqId, request, startTime)
            val technicalHeadersBackup=request.getTechnicalHeaders()
            //Have to remove technical headers before request
            val response = chain.proceed(request.removeTechnicalHeaders())
            //And set them back
            logResponse(rqId, response, request.setTechnicalHeaders(technicalHeadersBackup), startTime)
        } catch (e: Throwable) {
            logger.log(Level.SEVERE, "Exception in OkHttp logging interceptor", e)
            throw e
        }
    }

    /**
     * Logs the response of an OkHttp request and returns the intercepted
     * response.
     *
     * @param rqId The ID of the request.
     * @param response The original response from the server.
     * @param request The original request sent to the server.
     * @param time The time taken for the request and response.
     * @return The intercepted response.
     */
    protected open fun logResponse(
        rqId: String,
        response: Response,
        request: Request,
        time: Long,
    ): Response {
        if (loggingLevel == Level.OFF) {
            return response
        }
        val contentType = response.body?.contentType()
        val contentLength = response.body?.contentLength() ?: 0L
        val emptyBody = ((contentLength == 0L) || response.body == null)
        val haveToLogBody = request.logResponseBody() ?:properties.response.bodyIncluded
        val tooBigBody = contentLength > properties.maxBodySize
        val body = if (tooBigBody) constructTooBigMsg(contentLength)
        else if (!haveToLogBody) ""
        else if (emptyBody) ""
        else response.body?.string()?:""

        val logBody = constructRsBody(rqId, response, request, time,body)
        logger.log(loggingLevel, logBody)
        return if (tooBigBody||!haveToLogBody||emptyBody){
            response
        } else{
            response.newBuilder().body((body).toResponseBody(contentType)).build()
        }
    }

    /**
     * Constructs the request body log message.
     *
     * @param rqId The request ID.
     * @param request The request object.
     * @param time The time taken for the request.
     * @param body The request body.
     * @return The constructed request body log message.
     */
    protected open fun constructRqBody(
        rqId: String,
        request: Request,
        time: Long,
        body: String,
    ): String {
        val message = listOf(
            headerFormat.replace("%type%", Type.Request.name),
            getIdString(rqId, Type.Request),
            getUriString(request.logResponseUri(),"${request.method} ${request.url}", Type.Request),
            getTookString(request.logRequestTookTime(),time, Type.Request),
            getHeadersString(request.logRequestHeaders(),request.headers, Type.Request),
            getBodyString(request.logRequestBody(),body, Type.Request),
            footerFormat.replace("%type%", Type.Request.name),
        ).filter { !it.isNullOrBlank() }
            .joinToString("\n")
        return message
    }

    /**
     * Constructs the response body for logging.
     *
     * @param rqId The request ID.
     * @param response The OkHttp response.
     * @param request The OkHttp request.
     * @param time The time taken for the request/response.
     * @param content The response body content.
     * @return The formatted response body.
     */
    protected open fun constructRsBody(
        rqId: String,
        response: Response,
        request: Request,
        time: Long,
        content: String?,
    ): String {
        val message = listOf(
            headerFormat.replace("%type%", Type.Response.name),
            getIdString(rqId, Type.Response),
            getUriString(request.logResponseUri(),"${response.code} ${request.method} ${request.url}", Type.Response),
            getTookString(request.logResponseTookTime(),time, Type.Response),
            getHeadersString(request.logResponseHeaders(),response.headers, Type.Response),
            getBodyString(request.logResponseBody(),content, Type.Response),
            footerFormat.replace("%type%", Type.Response.name),
        ).filter { !it.isNullOrBlank() }
            .joinToString("\n")
        return message
    }

    /**
     * Retrieves the ID string for logging purposes.
     *
     * @param rqId The request ID.
     * @param type The type of the log message.
     * @return The formatted ID string if it is included in logging, otherwise
     *     null.
     */
    protected open fun getIdString(rqId: String, type: Type): String? {
        return if (type.properties().idIncluded) formatLine("ID", rqId)
        else null

    }

    /**
     * Retrieves the URI string for logging purposes.
     *
     * This method checks if the URI should be included in the log message based on the value of logEnabledForRequest.
     * If logEnabledForRequest is null, it uses the value of uriIncluded property from the Type enum.
     * If logEnabledForRequest is false or uriIncluded is false, it returns null.
     * Otherwise, it formats a line with name "URI" and value uri using the formatLine method.
     *
     * @param logEnabledForRequest Whether logging is enabled for the request. If null, the value from the Type enum will be used.
     * @param uri The URI string to be included in the log message.
     * @param type The type of the log message (Request or Response).
     * @return The formatted URI string if it is included in logging, otherwise null.
     */
    protected open fun getUriString(logEnabledForRequest:Boolean?,uri: String, type: Type): String? {
        return if (logEnabledForRequest?:type.properties().uriIncluded) formatLine("URI", uri)
        else null
    }

    /**
     * Retrieves the result of the "Took" operation as a formatted string.
     *
     * @param logEnabledForRequest Whether logging is enabled for the request. If null, the value from the Type enum will be used.
     * @param startTime The start time of the operation.
     * @param type The type of the log message.
     * @return The formatted "Took" string if it is included in logging,
     *     otherwise null.
     */
    protected open fun getTookString(logEnabledForRequest:Boolean?,startTime: Long, type: Type): String? {
        return if (type.properties().tookTimeIncluded) formatLine("Took", "${System.currentTimeMillis() - startTime} ms")
        else null
    }

    /**
     * Retrieves the formatted headers string based on the given Headers object
     * and type.
     *
     * @param logEnabledForRequest Whether logging is enabled for the request. If null, the value from the Type enum will be used.
     * @param headers The Headers object containing the headers information.
     * @param type The Type of the log message (Request or Response).
     * @return The formatted headers string if headersIncluded is true for the
     *     given type, otherwise null.
     */
    protected open fun getHeadersString(logEnabledForRequest:Boolean?,headers: Headers, type: Type): String? {
        return if (type.properties().bodyIncluded) formatLine("Headers", headers.getHeadersString())
        else null
    }

    /**
     * Retrieves the body string based on the given body and type.
     *
     * @param logEnabledForRequest Whether logging is enabled for the request. If null, the value from the Type enum will be used.
     * @param body The body string to be included in the log message.
     * @param type The type of the log (Request or Response).
     * @return The formatted body string if bodyIncluded is true for the given
     *     type, otherwise null.
     */
    protected open fun getBodyString(logEnabledForRequest:Boolean?,body: String?, type: Type): String? {
        return if (type.properties().bodyIncluded) formatLine("Body", body)
        else null
    }

    /**
     * Formats a line of log message with name and value.
     *
     * @param name The name of the line.
     * @param value The value of the line.
     * @return The formatted line.
     */
    protected open fun formatLine(name: String, value: String?): String {
        val lineStart = "${newLineFormat}${name}".padEnd(properties.newLineColumnSymbols, ' ')
        return "${lineStart}: $value"
    }

    /**
     * Retrieves the log settings for the given type.
     *
     * @return The log settings for the type.
     */
    protected open fun Type.properties(): OkHttpLoggerProperties.LogSettings {
        return when (this) {
            Type.Request -> properties.request
            Type.Response -> properties.response
        }
    }

    /**
     * Logs the request.
     *
     * @param rqId The ID of the request.
     * @param request The request object.
     * @param startTime The start time of the request.
     */
    protected open fun logRequest(
        rqId: String,
        request: Request,
        startTime: Long
    ) {
        if (loggingLevel == Level.OFF) {
            return
        }
        val requestBuffer = Buffer()

        val contentLength = request.body?.contentLength() ?: 0L
        val emptyBody = ((contentLength == 0L) || request.body == null)
        val haveToLogBody = request.logRequestBody() ?:properties.request.bodyIncluded
        if (!emptyBody && haveToLogBody) {
            request.body!!.writeTo(requestBuffer)
        }
        val body = if (contentLength > properties.maxBodySize) constructTooBigMsg(contentLength)
            else if (!haveToLogBody) ""
            else if (emptyBody) ""
            else requestBuffer.readUtf8()

        val logString = constructRqBody(rqId, request, startTime, body)
        logger.log(loggingLevel, logString)
    }

    /**
     * Constructs a too big message indicating the size of the content.
     *
     * @param contentLength The length of the content in bytes.
     * @return The constructed too big message.
     */
    protected open fun constructTooBigMsg(contentLength: Long) = "<TOO BIG $contentLength bytes>"

    /**
     * Retrieves the ID string for logging purposes.
     *
     * This method generates a random integer between 0 and 10,000,000, pads
     * the integer with leading zeros to reach a length of 7 digits, and
     * returns a substring of the padded integer. The returned ID string
     * consists of the first four characters of the substring, followed by a
     * hyphen, and then the fifth and subsequent characters of the substring.
     *
     * @return The formatted ID string.
     */
    protected open fun getIdString(): String {
        val integer = random.nextInt(10000000)
        val leftPad = integer.toString().padStart(7, '0')
        return leftPad.substring(0, 4) + '-' + leftPad.substring(5)
    }

    /**
     * Retrieves the order value of the `OKLoggingInterceptor`.
     *
     * @return The order value of the `OKLoggingInterceptor`.
     */
    override fun getOrder(): Int {
        return properties.order
    }
}
