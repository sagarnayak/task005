package com.example.task005.contracts

import com.example.task005.models.business.Card
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

abstract class Repository {

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //Mutable Live data
    val mutableLiveDataApiResult: SuperMutableLiveData<ArrayList<Card>> =
        SuperMutableLiveData()
    ////////////////////////////////////////////////////////////////////////////////////////////////

    lateinit var networkCallTimeMaster: NetworkCallTimeMaster

    inline fun <reified T> fromJson(json: String): T {
        return Gson().fromJson(json, object : TypeToken<T>() {}.type)
    }

    fun toJson(argument: Any) = Gson().toJson(argument)!!

    abstract fun clearAllData(reasonNotAuthorised: Boolean = false)

    abstract fun getDataFromApi()
}