package com.github.caoddx.appupdater

import android.content.Context
import com.github.caoddx.appupdater.util.int
import com.github.caoddx.appupdater.util.long
import com.github.caoddx.appupdater.util.string

class UpdateConfig(private val context: Context) {

    private val sp = context.getSharedPreferences("app_update", Context.MODE_PRIVATE)

    val appVersionCode = context.packageManager.getPackageInfo(context.packageName, 0).versionCode

    var lastCheckTime: Long by sp.long()
    var lastSuccessCheckTime: Long by sp.long()

    var ignoreVersionCode: Int by sp.int()

    //var versionCode: Int by sp.int()
    //var versionName: String by sp.string()
    //var changeLog: String by sp.string()
    //var downloadUrl: String by sp.string()
    //var apkSize: Long by sp.long()

    var apkFileName: String by sp.string()
    var apkFilePath: String by sp.string()
    var apkFileVersionCode: Int by sp.int(-1)

    /*var latestInfo: UpdateSource.LatestInfo
        get() {
            return UpdateSource.LatestInfo(
                    versionCode = versionCode,
                    versionName = versionName,
                    changeLog = changeLog,
                    downloadUrl = downloadUrl,
                    apkSize = apkSize
            )
        }
        set(value) {
            versionName = value.versionName
            changeLog = value.changeLog
            downloadUrl = value.downloadUrl
            apkSize = value.apkSize
            versionCode = value.versionCode
        }*/
}