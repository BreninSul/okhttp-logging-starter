package io.github.breninsul.okhttp.logging

import io.github.breninsul.logging.HttpBodyMasking
import io.github.breninsul.logging.HttpRequestBodyMasking
import io.github.breninsul.logging.HttpResponseBodyMasking
import io.github.breninsul.logging.HttpUriMasking

interface OkHttpUriMasking : HttpUriMasking

interface OkHttpRequestBodyMasking : HttpRequestBodyMasking

interface OkHttpResponseBodyMasking : HttpResponseBodyMasking

open class OkHttpRequestBodyMaskingDelegate(
    protected open val delegate: HttpBodyMasking,
) : OkHttpRequestBodyMasking {
    override fun mask(message: String?): String = delegate.mask(message)
}

open class OkHttpResponseBodyMaskingDelegate(
    protected open val delegate: HttpBodyMasking,
) : OkHttpResponseBodyMasking {
    override fun mask(message: String?): String = delegate.mask(message)
}

open class OkHttpUriMaskingDelegate(
    protected open val delegate: HttpUriMasking,
) : OkHttpUriMasking {
    override fun mask(uri: String?): String = delegate.mask(uri)
}
