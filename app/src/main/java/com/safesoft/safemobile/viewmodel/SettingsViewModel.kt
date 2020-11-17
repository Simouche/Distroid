package com.safesoft.safemobile.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import com.safesoft.safemobile.backend.repository.PreferencesRepository

class SettingsViewModel @ViewModelInject constructor(
    private val preferencesRepository: PreferencesRepository
) : BaseViewModel() {

    val autoSync: MutableLiveData<Boolean> =
        MutableLiveData<Boolean>().apply { value = preferencesRepository.getAutomaticSync() }

}