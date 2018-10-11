package com.github.caoddx.appupdater

enum class DownloadMode {
    /**
     * 只允许wifi网络下进行下载，且不进行下载询问
     */
    WifiOnlyAndNoAsk,
    /**
     * 只允许wifi网络下进行下载，且需要下载询问
     */
    WifiOnlyAndAsk,
    /**
     * 允许所有网络下进行下载，且wifi网络下不询问
     */
    AllAllowAndWifiNoAsk,
    /**
     * 允许所有网络下进行下载，且均需要下载询问
     */
    AllAllowAndAsk
}