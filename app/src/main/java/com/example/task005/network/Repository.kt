package com.example.task005.network

import android.content.SharedPreferences
import com.example.task005.R
import com.example.task005.core.KeywordsAndConstants
import com.example.task005.utils.ResourcesMaster
import com.example.task005.utils.logutil.LogUtil

class Repository(
    pref: SharedPreferences,
    private val logUtil: LogUtil,
    resourcesMaster: ResourcesMaster,
) : APIsMaster(
    pref,
    logUtil,
    KeywordsAndConstants.BASE_URL
) {

    init {
        logUtil.logV("^^^${resourcesMaster.getString(R.string.app_name)} App started.^^^")
    }

    override fun restartApp() {
    }

    override fun mayUseLogsForServer(log: String) {
    }

    override fun clearAllData(reasonNotAuthorised: Boolean) {
    }

    override fun getDataFromApi() {
        makeApiCall(
            apiInterface.getData(),
            responseJsonKeyword = "cards",
            superMutableLiveData = mutableLiveDataApiResult
        )
    }
}