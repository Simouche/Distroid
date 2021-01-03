package com.safesoft.safemobile.viewmodel

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import com.safesoft.safemobile.backend.repository.PreferencesRepository

class ScannerViewModel @ViewModelInject constructor(
    private val preferencesRepository: PreferencesRepository,
) : BaseViewModel() {

    val useFlash = preferencesRepository.getUseFlash()

    val autoFocus = preferencesRepository.getAutoFocus()

    val scannedCode = MutableLiveData<String>().apply { value = "" }

}