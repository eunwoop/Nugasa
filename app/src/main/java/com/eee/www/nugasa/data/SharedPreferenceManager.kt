package com.eee.www.nugasa.data

import android.content.Context

fun getBooleanSharedPref(context:Context, prefName:String, key:String) : Boolean {
    val pref = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
    val value = pref.getBoolean(key, false);
    return value
}

fun setBooleanSharedPref(context:Context, prefName: String, key:String, value:Boolean) {
    val pref = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
    pref.edit().putBoolean(key, value).apply()
}
