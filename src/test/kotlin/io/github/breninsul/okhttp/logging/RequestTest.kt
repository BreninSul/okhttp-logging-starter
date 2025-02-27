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

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody


class RequestTest {
    @org.junit.jupiter.api.Test
    fun testFormRequest(){
        val config=OkHttpLoggerConfiguration()
        val properties = OkHttpLoggerProperties()
        val client= OkHttpClient.Builder().addInterceptor(config.registerOKLoggingInterceptor(properties)).build()
        val mediaType: MediaType = "application/x-www-form-urlencoded".toMediaTypeOrNull()!!
        val body: RequestBody = RequestBody.create(mediaType, "awadad=sadasdsad&password=some&awadadsa=sadasdsad")
        val request: Request = Request.Builder()
            .url("https://test-c.free.beeceptor.com")
            .method("POST", body)
            .addHeader("Content-Type", "application/x-www-form-urlencoded")
            .build()
        val rs= client.newCall(request).execute()
    }

    @org.junit.jupiter.api.Test
    fun testRestRequest(){
        val config=OkHttpLoggerConfiguration()
        val properties = OkHttpLoggerProperties()
        val client= OkHttpClient.Builder().addInterceptor(config.registerOKLoggingInterceptor(properties)).build()
        val rq= Request.Builder().url("https://test-c.free.beeceptor.com")
            .header("HeaderFirst","HeaderValueFirst")
            .header("HeaderSecond","HeaderValueSecond")
            .post("{\"password\":\"someval\"}".toRequestBody())
            .build()
        val rs= client.newCall(rq).execute()
    }
}