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

import io.github.breninsul.logging.HttpLoggingHelper
import okhttp3.*
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer
import org.springframework.core.Ordered
import java.util.*
import java.util.concurrent.atomic.AtomicReference
import java.util.function.Supplier
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
open class OKLoggingInterceptor(
    protected open val properties: OkHttpLoggerProperties,
    protected open val requestBodyMaskers: List<OkHttpRequestBodyMasking>,
    protected open val responseBodyMaskers: List<OkHttpResponseBodyMasking>,
) : Interceptor, Ordered {
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
     * Helper variable for logging HTTP requests and responses.
     *
     * This variable is an instance of the `HttpLoggingHelper` class, which is responsible for logging requests and responses in the OkHttp library.
     * It is protected and open, allowing subclasses to override it if necessary.
     *
     * @property helper The `HttpLoggingHelper` instance for logging requests and responses.
     */
    protected open val helper = HttpLoggingHelper("OkHTTP", properties, requestBodyMaskers, responseBodyMaskers)

    /**
     * Intercepts the OkHttp request chain and logs the request and response.
     *
     * @param chain The OkHttp interceptor chain.
     * @return The intercepted OkHttp response.
     */
    override fun intercept(chain: Interceptor.Chain): Response =
        try {
            val startTime = System.currentTimeMillis()
            val rqId = helper.getIdString()
            val request = chain.request()
            logRequest(rqId, request, startTime)
            val technicalHeadersBackup = request.getTechnicalHeaders()
            // Have to remove technical headers before request
            val response = chain.proceed(request.removeTechnicalHeaders())
            // And set them back
            logResponse(rqId, response, request.setTechnicalHeaders(technicalHeadersBackup), startTime)
        } catch (e: Throwable) {
            logger.log(Level.SEVERE, "Exception in OkHttp logging interceptor", e)
            throw e
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
        if (helper.loggingLevel == Level.OFF) {
            return response
        }
        val contentType = response.body?.contentType()
        val cachedBodyString=AtomicReference<String?>(null)
        val logBody = constructRsBody(rqId, response, request, time){
            val contentLength = response.body?.contentLength() ?: 0L
            val emptyBody = ((contentLength == 0L) || response.body == null)
            val haveToLogBody = request.logResponseBody() ?: properties.response.bodyIncluded
            val tooBigBody = contentLength > properties.maxBodySize
            if (tooBigBody) {
                helper.constructTooBigMsg(contentLength)
            } else if (!haveToLogBody) {
                ""
            } else if (emptyBody) {
                ""
            } else {
                val body=response.body.toBodyString()
                cachedBodyString.set(body)
                return@constructRsBody  body
            }

        }
        logger.log(helper.loggingLevel, logBody)
        val cachedBody=cachedBodyString.get()
        return if (cachedBody==null) {
            response
        } else {
            response.newBuilder().body((cachedBody).toResponseBody(contentType)).build()
        }
    }
    protected open fun ResponseBody?.toBodyString()=this?.string() ?: ""
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
        bodySupplier: Supplier<String?>,
    ): String {
        val type = HttpLoggingHelper.Type.REQUEST
        val message =
            listOf(
                helper.getHeaderLine(type),
                helper.getIdString(rqId, type),
                helper.getUriString(request.logResponseUri(), "${request.method} ${request.url}", type),
                helper.getTookString(request.logRequestTookTime(), time, type),
                helper.getHeadersString(request.logRequestHeaders(), request.headers.toMultimap(), type),
                helper.getBodyString(request.logRequestBody(), bodySupplier, type),
                helper.getFooterLine(type),
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
     * @param contentSupplier The response body content Supplier.
     * @return The formatted response body.
     */
    protected open fun constructRsBody(
        rqId: String,
        response: Response,
        request: Request,
        time: Long,
        contentSupplier: Supplier<String?>,
    ): String {
        val type = HttpLoggingHelper.Type.RESPONSE
        val message =
            listOf(
                helper.getHeaderLine(type),
                helper.getIdString(rqId,type),
                helper.getUriString(request.logResponseUri(), "${response.code} ${request.method} ${request.url}", type),
                helper.getTookString(request.logResponseTookTime(), time, type),
                helper.getHeadersString(request.logResponseHeaders(), response.headers.toMultimap(), type),
                helper.getBodyString(request.logResponseBody(), contentSupplier, type),
                helper.getFooterLine(type),
            ).filter { !it.isNullOrBlank() }
                .joinToString("\n")
        return message
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
        startTime: Long,
    ) {
        if (helper.loggingLevel == Level.OFF) {
            return
        }
        val requestBuffer = Buffer()

        val contentLength = request.body?.contentLength() ?: 0L
        val emptyBody = ((contentLength == 0L) || request.body == null)
        val haveToLogBody = request.logRequestBody() ?: properties.request.bodyIncluded
        if (!emptyBody && haveToLogBody) {
            request.body!!.writeTo(requestBuffer)
        }
        val logString = constructRqBody(rqId, request, startTime){
            if (contentLength > properties.maxBodySize) {
                helper.constructTooBigMsg(contentLength)
            } else if (!haveToLogBody) {
                ""
            } else if (emptyBody) {
                ""
            } else {
                requestBuffer.readUtf8()
            }
        }
        logger.log(helper.loggingLevel, logString)
    }


    /**
     * Retrieves the order value of the `OKLoggingInterceptor`.
     *
     * @return The order value of the `OKLoggingInterceptor`.
     */
    override fun getOrder(): Int = properties.order
}
