package com.github.caoddx.appupdater

import com.github.caoddx.appupdater.util.Response
import io.reactivex.Single

interface UpdateSource {

    fun getLatestInfo(): Single<Response<LatestInfo>>

    data class LatestInfo(
            val versionCode: Int,
            val versionName: String,
            val changeLog: String,
            val downloadUrl: String,
            val apkSize: Long
    )
}