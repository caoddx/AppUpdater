package com.github.caoddx.appupdater

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.support.v7.app.AppCompatActivity
import com.github.caoddx.appupdater.DownloadMode.*
import com.github.caoddx.appupdater.downloader.DefaultDownloader
import com.github.caoddx.rxlifecycle.bindTo
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.*

class Updater(private val activity: AppCompatActivity,
              private val ui: UI,
              private val remoteSource: UpdateSource,
              private val apkDownLoader: ApkDownLoader = DefaultDownloader(activity),
              private val checkIntervalInSecond: Long = 24 * 60 * 60,
              private val downloadMode: DownloadMode = AllAllowAndWifiNoAsk) {

    private val config = UpdateConfig(activity)
    private var versionCode: Int = config.appVersionCode
    private val installer = ApkInstaller(activity)

    fun undoVersionIgnore() {
        config.ignoreVersionCode = -1
    }

    fun startTest() {
        versionCode = 0
        start()
        versionCode = config.appVersionCode
    }

    fun start() {
        checkable()
                .filter { it }
                .doOnSuccess {
                    config.lastCheckTime = Date().time / 1000
                }
                .flatMap {
                    checkRemoteSource()
                }
                .filter { it.versionCode != config.ignoreVersionCode }
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap { info ->
                    checkCacheApk(info)
                            .flatMapMaybe {
                                if (it) {
                                    Maybe.just(config.apkFilePath)
                                } else {
                                    checkCanDownload(info)
                                            .flatMap { info ->
                                                download(info)
                                            }
                                }
                            }
                            .map {
                                info to it
                            }
                }
                .observeOn(AndroidSchedulers.mainThread())
                .flatMapSingleElement { (info, apkFilePath) ->
                    install(info, apkFilePath)
                }
                .subscribe()
                .bindTo(activity)
    }

    private fun checkable(): Single<Boolean> {
        return Single.just(isConnected() && Date().time / 1000 - config.lastCheckTime > checkIntervalInSecond)
    }

    private fun checkCacheApk(info: UpdateSource.LatestInfo): Single<Boolean> {
        if (info.versionCode == config.apkFileVersionCode) {
            val pi: PackageInfo? = activity.packageManager.getPackageArchiveInfo(config.apkFilePath, PackageManager.GET_META_DATA)
            if (pi != null) {
                if (pi.versionCode == info.versionCode && activity.packageName.equals(pi.packageName, true)) {
                    return Single.just(true)
                }
            }
        }
        return Single.just(false)
    }

    private fun checkCanDownload(info: UpdateSource.LatestInfo): Maybe<UpdateSource.LatestInfo> {
        return if (info.versionCode == config.ignoreVersionCode) {
            Single.just(false)
        } else {
            if (isWifi()) {
                when (downloadMode) {
                    WifiOnlyAndNoAsk -> Single.just(true)
                    WifiOnlyAndAsk -> askDownload(info)
                    AllAllowAndWifiNoAsk -> Single.just(true)
                    AllAllowAndAsk -> askDownload(info)
                }
            } else {
                when (downloadMode) {
                    WifiOnlyAndNoAsk -> Single.just(false)
                    WifiOnlyAndAsk -> Single.just(false)
                    AllAllowAndWifiNoAsk -> askDownload(info)
                    AllAllowAndAsk -> askDownload(info)
                }
            }
        }
                .filter { it }
                .map { info }
    }

    private fun checkRemoteSource(): Maybe<UpdateSource.LatestInfo> {
        return remoteSource.getLatestInfo()
                .observeOn(AndroidSchedulers.mainThread())
                .filter {
                    if (!it.success) {
                        ui.showError(it.failureInfo)
                    }
                    it.success
                }
                .doOnSuccess {
                    config.lastSuccessCheckTime = Date().time / 1000
                }
                .map {
                    it.data
                }
                .filter {
                    it.versionCode > versionCode
                }
    }

    private fun download(info: UpdateSource.LatestInfo): Maybe<String> {
        val url = info.downloadUrl
        val filename = "${info.versionName} ${info.versionCode}.apk"

        return apkDownLoader.download(url, filename)
                .observeOn(AndroidSchedulers.mainThread())
                .filter {
                    if (!it.success) {
                        ui.showError(it.failureInfo)
                    }
                    it.success
                }
                .map { it.data }
                .doOnSuccess { path ->
                    config.apkFilePath = path
                    config.apkFileName = filename
                    config.apkFileVersionCode = info.versionCode
                }
    }

    private fun install(info: UpdateSource.LatestInfo, apkFile: String): Single<Boolean> {
        return askInstall(info)
                .doOnSuccess {
                    if (it) {
                        installer.install(apkFile)
                    }
                }
    }

    private fun askDownload(info: UpdateSource.LatestInfo): Single<Boolean> {
        return ui.askDownload(info)
                .doOnSuccess {
                    if (it == UI.AskResult.IgnoreThisVersion) {
                        config.ignoreVersionCode = info.versionCode
                    }
                }
                .map { it == UI.AskResult.Ok }
    }

    private fun askInstall(info: UpdateSource.LatestInfo): Single<Boolean> {
        return ui.askInstall(info)
                .doOnSuccess {
                    if (it == UI.AskResult.IgnoreThisVersion) {
                        config.ignoreVersionCode = info.versionCode
                    }
                }
                .map { it == UI.AskResult.Ok }
    }

    private fun isConnected(): Boolean {
        val cm = activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo?.isConnected ?: false
    }

    private fun isWifi(): Boolean {
        val cm = activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeInfo = cm.activeNetworkInfo
        return activeInfo != null && activeInfo.type == ConnectivityManager.TYPE_WIFI
    }
}