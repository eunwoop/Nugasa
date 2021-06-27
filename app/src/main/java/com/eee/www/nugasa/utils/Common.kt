package com.eee.www.nugasa.utils

val Any.TAG: String
    get() {
        val tag = "[Nugasa] ${javaClass.simpleName}"
        return if (tag.length <= 23) tag else tag.substring(0, 23)
    }