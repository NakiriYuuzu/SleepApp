package com.example.sleep.library

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.preference.PreferenceManager
import com.example.sleep.util.Constant
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


class ShareHelper(val context: Context) {
    val pref: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    fun <T> put(`object`: T, key: String) {
        val jsonString = GsonBuilder().create().toJson(`object`)
        pref.edit().putString(key, jsonString).apply()
    }

    inline fun <reified T> get(key: String): T? {
        val value = pref.getString(key, null)
        return GsonBuilder().create().fromJson(value, T::class.java)
    }

    inline fun <reified T> isValueExist(key: String): Boolean {
        return get<T>(key) != null
    }

    fun remove(key: String) {
        pref.edit().remove(key).apply()
    }

    fun clear() {
        pref.edit().clear().apply()
    }

    private var createModels: ArrayList<MqttModel>? = null

    fun saveMqttArray(createModels: ArrayList<MqttModel>) {
        this.createModels = createModels
        if (createModels.size == 0) Log.e("TAG", "No Data Can Save!")
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(
            Constant.USER_DATA,
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(createModels)
        editor.putString(Constant.USER_DATA, json)
        editor.apply()
    }

    fun getMqttArray(): ArrayList<MqttModel>? {
            val sharedPreferences: SharedPreferences = context.getSharedPreferences(
                Constant.USER_DATA,
                Context.MODE_PRIVATE
            )
            val gson = Gson()
            val json = sharedPreferences.getString(Constant.USER_DATA, null)
            val type: Type = object : TypeToken<ArrayList<MqttModel?>?>() {}.type
            createModels = gson.fromJson(json, type)

            // FIXME: Fix this shit
            if (createModels == null) createModels = ArrayList()

        return createModels
    }
}