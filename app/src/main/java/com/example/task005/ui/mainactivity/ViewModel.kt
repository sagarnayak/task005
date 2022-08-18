package com.example.task005.ui.mainactivity

import com.example.task005.contracts.Repository
import com.example.task005.contracts.SuperMediatorLiveData
import com.example.task005.models.business.Card
import com.example.task005.utils.SuperViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ViewModel @Inject constructor(private val repository: Repository) :
    SuperViewModel(repository) {

    val mediatorLiveDataApiResult: SuperMediatorLiveData<ArrayList<Card>> =
        SuperMediatorLiveData()

    init {
        mediatorLiveDataApiResult.initialise(repository.mutableLiveDataApiResult)
    }

    fun getData() {
        repository.getDataFromApi()
    }
}