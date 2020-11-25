package com.safesoft.safemobile.ui.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.safesoft.safemobile.R
import com.safesoft.safemobile.ui.generics.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ApplicationSettings : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_application_settings, container, false)
    }

}