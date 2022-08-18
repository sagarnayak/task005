@file:Suppress("unused")

package com.example.task005.contracts

import androidx.lifecycle.MediatorLiveData

class SuperMediatorLiveData<T> {

    private var success: MediatorLiveData<Event<T>> = MediatorLiveData()
    private var fail: MediatorLiveData<Event<com.example.task005.models.core.Result>> =
        MediatorLiveData()

    private lateinit var shouldPushData: () -> Boolean

    @Suppress("MemberVisibilityCanBePrivate")
    fun setShouldPushData(method: () -> Boolean) {
        this.shouldPushData = method
    }

    fun getSuccess() = success

    fun getFail() = fail

    fun initialise(
        mutableLiveData: SuperMutableLiveData<T>,
        shouldPushData: (() -> Boolean)? = null
    ): SuperMediatorLiveData<T> {
        shouldPushData?.let {
            this.setShouldPushData(it)
        }
        success.addSource(
            mutableLiveData.getSuccess()
        ) { t ->
            if (this::shouldPushData.isInitialized) {
                if (shouldPushData()) {
                    success.postValue(t)
                }
            } else
                success.postValue(t)
        }
        fail.addSource(
            mutableLiveData.getFail()
        ) { t ->
            if (this::shouldPushData.isInitialized) {
                if (shouldPushData()) {
                    fail.postValue(t)
                }
            } else
                fail.postValue(t)
        }

        return this
    }
}