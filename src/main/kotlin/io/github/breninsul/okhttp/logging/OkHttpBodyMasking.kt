package io.github.breninsul.okhttp.logging

import io.github.breninsul.logging.HttpBodyMasking
import io.github.breninsul.logging.HttpRequestBodyMasking
import io.github.breninsul.logging.HttpResponseBodyMasking

interface OkHttpRequestBodyMasking : HttpRequestBodyMasking
interface OkHttpResponseBodyMasking : HttpResponseBodyMasking

open class OkHttpRequestBodyMaskingDelegate(
    protected open val delegate: HttpBodyMasking,
) : OkHttpRequestBodyMasking {
    override fun mask(message: String?): String = delegate.mask(message)
}
open class OkHttpResponseBodyMaskingDelegate(
    protected open val delegate: HttpBodyMasking
) : OkHttpResponseBodyMasking {
    override fun mask(message: String?): String = delegate.mask(message)
}
