package io.github.breninsul.okhttp.logging

interface OkHttpBodyMasking {
    fun mask(message: String?): String
}

interface OkHttpRequestBodyMasking : OkHttpBodyMasking

interface OkHttpResponseBodyMasking : OkHttpBodyMasking

open class OkHttpRequestBodyMaskingDelegate(
    protected open val delegate: OkHttpBodyMasking,
) : OkHttpRequestBodyMasking {
    override fun mask(message: String?): String = delegate.mask(message)
}
open class OkHttpResponseBodyMaskingDelegate(
    protected open val delegate: OkHttpBodyMasking
) : OkHttpResponseBodyMasking {
    override fun mask(message: String?): String = delegate.mask(message)
}
open class OkHttpRegexJsonBodyMasking(
    protected open val fields: List<String> = listOf("password", "pass", "code", "token", "secret"),
) : OkHttpBodyMasking {
    protected open val emptyBody: String = ""
    protected open val maskedBody: String = "<MASKED>"

    protected open val fieldsGroup get() = fields.joinToString("|")
    protected open val regex: Regex = "\"($fieldsGroup)\":\"((\\\\\"|[^\"])*)\"".toRegex()

    override fun mask(message: String?): String {
        if (message == null) {
            return emptyBody
        }
        val ranges = regex.findAll(message).map { it.groups[2]!!.range }

        val maskedMessage = StringBuilder(message)
        ranges.forEach { range ->
            maskedMessage.replace(range.first,range.last+1, maskedBody)
        }
        return maskedMessage.toString()
    }
}

open class OkHttpRegexFormUrlencodedBodyMasking(
    protected open val fields: List<String> = listOf("password", "pass", "code", "token", "secret"),
) : OkHttpBodyMasking {
    protected open val emptyBody: String = ""
    protected open val maskedBody: String = "<MASKED>"

    protected open val fieldsGroup get() = fields.joinToString("|")
    protected open val regex: Regex = "($fieldsGroup)(=)([^&]*)(?:&|\$)".toRegex()

    override fun mask(message: String?): String {
        if (message == null) {
            return emptyBody
        }
        val ranges = regex.findAll(message).map { it.groups[3]!!.range }

        val maskedMessage = StringBuilder(message)
        ranges.forEach { range ->
            maskedMessage.replace(range.first,range.last+1, maskedBody)
        }
        return maskedMessage.toString()
    }
}
