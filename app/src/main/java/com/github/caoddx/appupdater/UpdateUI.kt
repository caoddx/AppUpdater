package com.github.caoddx.appupdater

import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import io.reactivex.Single

class UpdateUI(val activity: AppCompatActivity) : UI {

    override fun showError(error: String) {
        Toast.makeText(activity, error, Toast.LENGTH_LONG).show()
    }

    override fun askDownload(info: UpdateSource.LatestInfo): Single<UI.AskResult> {
        return MaterialDialog.Builder(activity)
                .title("更新v${info.versionName}")
                .content(info.changeLog)
                .checkBoxPrompt("此版本不再提示", false) { _, _ ->
                }
                .positiveText("下载")
                .negativeText("稍后")
                .toRxMaterialDialog()
                .yesOrNoWithPrompt()
                .map {
                    if (it.first) {
                        UI.AskResult.Ok
                    } else {
                        if (it.second) {
                            UI.AskResult.IgnoreThisVersion
                        } else {
                            UI.AskResult.Later
                        }
                    }
                }
    }

    override fun askInstall(info: UpdateSource.LatestInfo): Single<UI.AskResult> {
        return MaterialDialog.Builder(activity)
                .title("安装v${info.versionName}")
                .content(info.changeLog)
                .checkBoxPrompt("此版本不再提示", false) { _, _ ->
                }
                .positiveText("安装")
                .negativeText("稍后")
                .toRxMaterialDialog()
                .yesOrNoWithPrompt()
                .map {
                    if (it.first) {
                        UI.AskResult.Ok
                    } else {
                        if (it.second) {
                            UI.AskResult.IgnoreThisVersion
                        } else {
                            UI.AskResult.Later
                        }
                    }
                }
    }
}