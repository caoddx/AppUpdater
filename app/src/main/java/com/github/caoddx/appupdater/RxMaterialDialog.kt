package com.github.caoddx.appupdater

import android.content.Context

import com.afollestad.materialdialogs.MaterialDialog

import io.reactivex.Single


class RxMaterialDialog private constructor(private val builder: MaterialDialog.Builder, private val builderSetter: (MaterialDialog.Builder) -> Unit = {}) {

    init {
        builder.cancelable(false)
    }

    fun yesOrNo(): Single<Boolean> {
        return Single.create { e ->
            builderSetter(builder)
            val d = builder
                    .onPositive { _, _ -> e.onSuccess(true) }
                    .onNegative { _, _ -> e.onSuccess(false) }
                    .dismissListener {
                        e.onSuccess(false) // 如果已经发射过值了，会忽略此发射的值
                    }
                    .show()
            // Observable 被 dispose 时调用
            e.setCancellable { d.dismiss() }
        }
    }

    fun yesOrNoWithPrompt(): Single<Pair<Boolean, Boolean>> {
        return Single.create { e ->
            builderSetter(builder)
            val d = builder
                    .onPositive { md, _ -> e.onSuccess(true to md.isPromptCheckBoxChecked) }
                    .onNegative { md, _ -> e.onSuccess(false to md.isPromptCheckBoxChecked) }
                    .dismissListener {
                        // 如果已经发射过值了，会忽略此发射的值
                        e.onSuccess(false to ((it as? MaterialDialog)?.isPromptCheckBoxChecked
                                ?: false))
                    }
                    .show()
            // Observable 被 dispose 时调用
            e.setCancellable { d.dismiss() }
        }
    }

    companion object {

        fun create(context: Context, builderSetter: (MaterialDialog.Builder) -> Unit = {}): RxMaterialDialog {
            return RxMaterialDialog(MaterialDialog.Builder(context), builderSetter)
        }

        fun from(builder: MaterialDialog.Builder): RxMaterialDialog {
            return RxMaterialDialog(builder)
        }
    }

}

fun MaterialDialog.Builder.toRxMaterialDialog(): RxMaterialDialog {
    return RxMaterialDialog.from(this)
}