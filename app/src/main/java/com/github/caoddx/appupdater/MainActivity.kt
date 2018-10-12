package com.github.caoddx.appupdater

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.github.caoddx.appupdater.updatesource.FirImSource
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // see https://fir.im/docs
        val source = FirImSource("your app id", "your api token")

        val updater = Updater(
                this,
                UpdateUI(this),
                source,
                checkIntervalInSecond = 12 * 3600,
                downloadMode = DownloadMode.AllAllowAndAsk
        )

        updater.start()

        button.setOnClickListener {
            updater.undoVersionIgnore()
        }
    }
}
