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

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * Configuration properties for OkHttpLogger.
 *
 * This class represents the properties required for configuring the OkHttpLogger library.
 * It is annotated with `@ConfigurationProperties("okhttp.logging-interceptor")` to specify the configuration prefix.
 *
 * @property enabled Whether the OkHttpLogger is enabled or not. Default value is `true`.
 * @property loggingLevel The logging level to be used for logging. Default value is `JavaLoggingLevel.INFO`.
 * @property request The settings for logging request. Default value is `LogSettings(tookTimeIncluded = false)`.
 * @property response The settings for logging response. Default value is `LogSettings(tookTimeIncluded = false)`.
 * @property maxBodySize The maximum body size allowed for logging. Default value is `Int.MAX_VALUE`.
 * @property order The order of the OkHttpLogger in the interceptor chain. Default value is `0`.
 * @property newLineColumnSymbols The number of column symbols for the newline character in log output. Default value is `14`.
 */
@ConfigurationProperties("okhttp.logging-interceptor")
data class OkHttpLoggerProperties (
    var enabled:Boolean=true,
    var loggingLevel: JavaLoggingLevel=JavaLoggingLevel.INFO,
    var request:LogSettings=LogSettings(tookTimeIncluded = false),
    var response:LogSettings=LogSettings(tookTimeIncluded = true),
    var maxBodySize:Int=Int.MAX_VALUE,
    var order:Int=0,
    var newLineColumnSymbols:Int=14
){
    /**
     * Represents the log settings for a logging operation.
     *
     * @property idIncluded Whether to include the request ID in the log output. Default value is true.
     * @property uriIncluded Whether to include the URI in the log output. Default value is true.
     * @property tookTimeIncluded Whether to include the time taken for the request in the log output. Default value is true.
     * @property headersIncluded Whether to include the headers in the log output. Default value is true.
     * @property bodyIncluded Whether to include the body in the log output. Default value is true.
     * @property mask The mask settings for logging requests and responses.
     */
    data class LogSettings(
        var idIncluded:Boolean =true,
        var uriIncluded:Boolean =true,
        var tookTimeIncluded:Boolean=true,
        var headersIncluded:Boolean =true,
        var bodyIncluded:Boolean =true,
        var mask:MaskSettings=MaskSettings()
    )
    /**
     * Represents the mask settings for logging requests and responses.
     *
     * @property maskHeaders The list of headers to be masked in the log output. Default value is ["Authorization"].
     * @property maskJsonBodyKeys The list of keys to be masked in the JSON body of the log output. Default value is ["password", "pass", "code", "token", "secret"].
     * @property maskFormUrlencodedBodyKeys The list of keys to be masked in the form-urlencoded body of the log output. Default value is ["password", "pass", "code", "token", "secret
     * "].
     */
    data class MaskSettings(
        var maskHeaders: List<String> = listOf("Authorization"),
        var maskJsonBodyKeys: List<String> = listOf("password", "pass", "code", "token", "secret"),
        var maskFormUrlencodedBodyKeys: List<String> = listOf("password", "pass", "code", "token", "secret"),
    )
}