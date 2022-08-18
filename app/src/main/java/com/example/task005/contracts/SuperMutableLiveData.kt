package com.example.task005.contracts

import androidx.lifecycle.MutableLiveData
import com.example.task005.models.core.Result

class SuperMutableLiveData<T> {

    private var success: MutableLiveData<Event<T>> = MutableLiveData()
    private var fail: MutableLiveData<Event<Result>> = MutableLiveData()

    fun getSuccess() = success

    fun getFail() = fail
}