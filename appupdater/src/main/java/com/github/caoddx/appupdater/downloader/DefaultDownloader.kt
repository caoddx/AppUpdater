package com.github.caoddx.appupdater.downloader

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import com.github.caoddx.appupdater.ApkDownLoader
import com.github.caoddx.appupdater.util.Response
import com.github.caoddx.appupdater.util.RxBroadcastReceiver
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import java.io.File
import java.util.concurrent.TimeUnit

class DefaultDownloader(private val context: Context) : ApkDownLoader {

    private val dManager: DownloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

    private fun startDownload(url: String, fileName: String): Long {
        val request = DownloadManager.Request(Uri.parse(url))
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, fileName)
        request.setMimeType("application/vnd.android.package-archive")
        //加入下载队列
        return dManager.enqueue(request)
    }

    override fun download(url: String, fileName: String): Single<Response<String>> {
        val apkFile = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName)
        if (apkFile.exists()) apkFile.delete()

        val id = startDownload(url, fileName)

        return RxBroadcastReceiver.create(context, DownloadManager.ACTION_DOWNLOAD_COMPLETE)
                .subscribeOn(AndroidSchedulers.mainThread())
                .filter {
                    it.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1) == id
                }
                .map {
                    val uri = dManager.getUriForDownloadedFile(id)
                    if (uri != null) {
                        Response.success(apkFile.absolutePath)
                    } else {
                        Response.failure("uri is null")
                    }
                }
                .timeout(300, TimeUnit.SECONDS, Observable.just(Response.failure("download timeout")))
                .firstOrError()
    }
}