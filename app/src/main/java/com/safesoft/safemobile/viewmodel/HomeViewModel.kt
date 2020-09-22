package com.safesoft.safemobile.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.safesoft.safemobile.backend.model.DashBoard
import com.safesoft.safemobile.backend.repository.AuthRepository
import com.safesoft.safemobile.backend.repository.HomeRepository
import com.safesoft.safemobile.backend.utils.Resource

class HomeViewModel @ViewModelInject constructor(
    private val homeRepository: HomeRepository
) :
    BaseViewModel() {

    val counts = MutableLiveData<DashBoard>()

    fun getCount(): LiveData<Resource<DashBoard>> {
        val data = MutableLiveData<Resource<DashBoard>>()
        enqueue(homeRepository.getCounts(), data)
        return data
    }
}