package com.github.caoddx.appupdater.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import java.util.concurrent.atomic.AtomicBoolean

object RxBroadcastReceiver {

    fun create(context: Context, vararg actions: String): Observable<Intent> {
        val filter = IntentFilter()
        actions.forEach { filter.addAction(it) }
        return create(context.applicationContext, filter)
    }

    fun create(context: Context, filter: IntentFilter): Observable<Intent> =
            RxBroadcastReceiverObservable(context.applicationContext, filter)

    private class RxBroadcastReceiverObservable(private val context: Context, private val filter: IntentFilter) : Observable<Intent>() {

        override fun subscribeActual(observer: Observer<in Intent>) {
            val receiver = BroadcastReceiverDisposable(context, observer)

            context.registerReceiver(receiver, filter)

            observer.onSubscribe(receiver)
        }

    }

    private class BroadcastReceiverDisposable(private val context: Context, private val observer: Observer<in Intent>) : BroadcastReceiver(), Disposable {

        override fun onReceive(context: Context, intent: Intent) {
            observer.onNext(intent)
        }

        private fun onDispose() {
            context.unregisterReceiver(this)
        }

        private val unSubscribed = AtomicBoolean()

        override fun isDisposed(): Boolean {
            return unSubscribed.get()
        }

        override fun dispose() {
            if (unSubscribed.compareAndSet(false, true)) {
                onDispose()
                /*if (Looper.myLooper() == Looper.getMainLooper()) {
                    onDispose()
                } else {
                    AndroidSchedulers.mainThread().scheduleDirect { onDispose() }
                }*/
            }
        }

    }
}