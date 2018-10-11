package com.github.caoddx.appupdater.updatesource

import com.github.caoddx.appupdater.UpdateSource
import com.github.caoddx.appupdater.util.Response
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

class FirImSource(private val appId: String, private val apiToken: String) : UpdateSource {

    // see https://fir.im/docs/version_detection

    override fun getLatestInfo(): Single<Response<UpdateSource.LatestInfo>> {

        val url = HttpUrl.get("http://api.fir.im/apps/latest/").newBuilder()
                .addPathSegment(appId)
                .addQueryParameter("token", apiToken)
                .build()

        val request = Request.Builder()
                .get()
                .url(url)
                .build()

        val call = OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()
                .newCall(request)

        return Single.fromCallable {
            try {
                call.execute().use { response ->
                    val body = response.body()
                    if (response.isSuccessful && body != null) {
                        Response.success(body.string())
                    } else {
                        Response.failure(response.message())
                    }
                }
            } catch (ioe: IOException) {
                Response.failure<String>(ioe.localizedMessage)
            }
        }
                .subscribeOn(Schedulers.io())
                .map {
                    if (it.success) {
                        try {
                            val json = JSONObject(it.data)
                            val info = UpdateSource.LatestInfo(
                                    versionCode = json.getString("build").toIntOrNull() ?: 0,
                                    versionName = json.getString("versionShort"),
                                    changeLog = json.getString("changelog"),
                                    downloadUrl = json.getString("install_url"),
                                    apkSize = json.getJSONObject("binary").getLong("fsize")
                            )
                            Response.success(info)
                        } catch (je: JSONException) {
                            Response.failure<UpdateSource.LatestInfo>(je.localizedMessage)
                        }
                    } else {
                        Response.failure(it.failureInfo)
                    }
                }
    }
}