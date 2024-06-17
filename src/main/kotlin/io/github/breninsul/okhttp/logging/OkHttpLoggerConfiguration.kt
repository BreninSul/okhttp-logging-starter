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

import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean

/**
 * Configuration class for OkHttpLogger.
 *
 * This class is responsible for configuring the OkHttpLogger library.
 * It provides a bean for registering the OKLoggingInterceptor as an interceptor in OkHttp client.
 *
 * To enable OkHttpLogger, the property `okhttp.logging-interceptor.enabled` should be set to `true`.
 * The default value is `true`, so if the property is not specified, it will be enabled.
 *
 * The properties for configuring the logger are provided through the OkHttpLoggerProperties class,
 * which is annotated with @ConfigurationProperties("okhttp.logging-interceptor").
 *
 * Note: Example code is not included in this documentation.
 */
@ConditionalOnProperty(value = ["okhttp.logging-interceptor.enabled"], havingValue = "true", matchIfMissing = true)
@AutoConfiguration
@EnableConfigurationProperties(OkHttpLoggerProperties::class)
open class OkHttpLoggerConfiguration {
    /**
     * Registers the OKLoggingInterceptor as an interceptor in the OkHttp client.
     *
     * @param properties The OkHttpLoggerProperties used for configuring the interceptor.
     * @return The registered OKLoggingInterceptor.
     */
    @Bean
    open fun registerOKLoggingInterceptor(properties: OkHttpLoggerProperties):OKLoggingInterceptor{
        return OKLoggingInterceptor(properties)
    }

}