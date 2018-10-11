package com.github.caoddx.appupdater.util

import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

private inline fun <T> SharedPreferences.delegate(defaultValue: T, key: String? = null,
                                                  crossinline getter: SharedPreferences.(key: String, def: T) -> T,
                                                  crossinline setter: Editor.(key: String, value: T) -> Editor): ReadWriteProperty<Any, T> {
    return object : ReadWriteProperty<Any, T> {
        override fun getValue(thisRef: Any, property: KProperty<*>): T {
            return getter(key ?: property.name, defaultValue)
        }

        override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
            edit().setter(key ?: property.name, value).apply()
        }
    }
}

fun SharedPreferences.int(defaultValue: Int = 0, key: String? = null): ReadWriteProperty<Any, Int> {
    return delegate(defaultValue, key, SharedPreferences::getInt, Editor::putInt)
}

fun SharedPreferences.long(defaultValue: Long = 0L, key: String? = null): ReadWriteProperty<Any, Long> {
    return delegate(defaultValue, key, SharedPreferences::getLong, Editor::putLong)
}

fun SharedPreferences.string(defaultValue: String = "", key: String? = null): ReadWriteProperty<Any, String> {
    return delegate(defaultValue, key, SharedPreferences::getString, Editor::putString)
}

fun SharedPreferences.boolean(defaultValue: Boolean = false, key: String? = null): ReadWriteProperty<Any, Boolean> {
    return delegate(defaultValue, key, SharedPreferences::getBoolean, Editor::putBoolean)
}

fun SharedPreferences.float(defaultValue: Float = 0f, key: String? = null): ReadWriteProperty<Any, Float> {
    return delegate(defaultValue, key, SharedPreferences::getFloat, Editor::putFloat)
}

fun SharedPreferences.stringSet(defaultValue: MutableSet<String> = mutableSetOf(), key: String? = null): ReadWriteProperty<Any, MutableSet<String>> {
    return delegate(defaultValue, key, SharedPreferences::getStringSet, Editor::putStringSet)
}