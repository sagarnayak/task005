@file:Suppress("unused")

package com.example.task005.utils

import androidx.lifecycle.ViewModel
import com.example.task005.contracts.Event
import com.example.task005.contracts.Repository
import com.example.task005.contracts.SuperMutableLiveData
import com.example.task005.enums.ResultType

open class SuperViewModel(private val repository: Repository) :
    ViewModel() {

    private var activityPaused = false
    private var shouldNotifyWhileActivityPaused = true

    val mutableLiveDataProcessing: SuperMutableLiveData<Boolean> = SuperMutableLiveData()
    val mutableLiveDataGenericError: SuperMutableLiveData<com.example.task005.models.core.Result> =
        SuperMutableLiveData()
    val mutableLiveDataCriticalError: SuperMutableLiveData<com.example.task005.models.core.Result> =
        SuperMutableLiveData()

    fun loading() = mutableLiveDataProcessing.getSuccess().postValue(Event(true))

    fun doneLoading() = mutableLiveDataProcessing.getSuccess().postValue(Event(false))

    fun genericError(
        message: String? = null,
        result: com.example.task005.models.core.Result? = null
    ) {
        message?.let {
            mutableLiveDataGenericError.getSuccess().postValue(
                Event(
                    com.example.task005.models.core.Result(
                        StatusCode.Unknown.code,
                        message = message,
                        result = ResultType.FAIL
                    )
                )
            )
        }
        result?.let {
            mutableLiveDataGenericError.getSuccess().postValue(
                Event(
                    result
                )
            )
        }
    }

    fun criticalError(
        message: String? = null,
        result: com.example.task005.models.core.Result? = null
    ) {
        message?.let {
            mutableLiveDataCriticalError.getSuccess().postValue(
                Event(
                    com.example.task005.models.core.Result(
                        StatusCode.Unknown.code,
                        message = message,
                        result = ResultType.FAIL
                    )
                )
            )
        }
        result?.let {
            mutableLiveDataCriticalError.getSuccess().postValue(
                Event(
                    result
                )
            )
        }
    }

    fun notifyDuringPaused(shouldNotifyWhileActivityPaused: Boolean) {
        this.shouldNotifyWhileActivityPaused = shouldNotifyWhileActivityPaused
    }

    fun activityPaused() {
        activityPaused = true
    }

    fun activityResumed() {
        activityPaused = false
    }

    fun canPushData(): Boolean {
        if (shouldNotifyWhileActivityPaused)
            return true
        return !activityPaused
    }

    fun giveRepository(): Repository {
        return repository
    }
}