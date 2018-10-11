package com.github.caoddx.appupdater.util

/**
 * @property success 如果为 true 则 [data] 一定不为 null，如果为 false 则 [failureInfo] 一定包含错误信息
 * @property dataNullable
 * @property failureInfo
 * @property data 只有在 [success] 为 true 时才可以使用，否则会报异常
 */
class Response<T> private constructor(
        private val dataNullable: T? = null,
        val failureInfo: String = ""
) {
    val success: Boolean
        get() {
            return dataNullable != null
        }

    val data: T get() = dataNullable!!

    override fun toString(): String {
        return "Response(success=$success, dataNullable=$dataNullable, failureInfo='$failureInfo')"
    }

    companion object {
        fun <T> success(data: T): Response<T> {
            return Response(data)
        }

        fun <T> failure(failureInfo: String): Response<T> {
            return Response(null, failureInfo)
        }
    }
}