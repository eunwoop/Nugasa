package com.eee.www.nugasa.data

import android.content.Context

class SharedPreferenceManager(val context: Context) {

    fun getBooleanSharedPref(prefName:String, key:String) : Boolean {
        val pref = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        val value = pref.getBoolean(key, false);
        return value
    }

    fun setBooleanSharedPref(prefName: String, key:String, value:Boolean) {
        val pref = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        pref.edit().putBoolean(key, value).apply()
    }
}