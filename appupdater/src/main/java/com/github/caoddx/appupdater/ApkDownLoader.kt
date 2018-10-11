package com.github.caoddx.appupdater

import com.github.caoddx.appupdater.util.Response
import io.reactivex.Single

interface ApkDownLoader {

    // todo 下载进度

    fun download(url: String, fileName: String): Single<Response<String>>

}
