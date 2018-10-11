package com.github.caoddx.appupdater

import io.reactivex.Single

interface UI {

    fun showError(error: String)

    fun askDownload(info: UpdateSource.LatestInfo): Single<AskResult>

    fun askInstall(info: UpdateSource.LatestInfo): Single<AskResult>

    enum class AskResult {
        Ok,
        Later,
        IgnoreThisVersion
    }
}