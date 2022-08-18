package com.example.task005.di

import android.content.Context
import android.content.SharedPreferences
import com.example.task005.core.KeywordsAndConstants.SHARED_PREF_DB

class SharedPrefModule(context: Context) {

    var pref: SharedPreferences = context.getSharedPreferences(
        SHARED_PREF_DB,
        Context.MODE_PRIVATE
    )
}