package com.safesoft.safemobile.ui.generics

import android.util.Log
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.pranavpandey.android.dynamic.toasts.DynamicToast
import com.safesoft.safemobile.R
import com.safesoft.safemobile.backend.utils.Resource
import com.safesoft.safemobile.backend.utils.ResourceState
import com.safesoft.safemobile.ui.generics.adapter.BaseFragmentAdapter
import com.safesoft.safemobile.viewmodel.ScannerViewModel

abstract class BaseFragment : Fragment() {

    protected val TAG = this::class.simpleName

    protected var currentItemPosition: Int = -1

    protected val loading = ResourceState.LOADING
    protected val success = ResourceState.SUCCESS
    protected val error = ResourceState.ERROR

    protected lateinit var pagerAdapter: BaseFragmentAdapter
    protected lateinit var titles: Array<Int>
    protected lateinit var fragments: Array<Fragment>

    protected fun toast(@StringRes messageId: Int? = null, message: String? = null) {
        if (messageId != null)
            Toast.makeText(context, messageId, Toast.LENGTH_SHORT).show()
        else
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    protected fun success(@StringRes messageId: Int? = null, message: String? = null) {
        if (messageId != null)
            DynamicToast.makeSuccess(requireContext(), getString(messageId), Toast.LENGTH_LONG)
                .show()
        else
            DynamicToast.makeSuccess(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    protected fun error(@StringRes messageId: Int? = null, message: String? = null) {
        if (messageId != null)
            DynamicToast.makeError(requireContext(), getString(messageId), Toast.LENGTH_LONG).show()
        else
            DynamicToast.makeError(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    protected fun warning(@StringRes messageId: Int? = null, message: String? = null) {
        if (messageId != null)
            DynamicToast.makeWarning(requireContext(), getString(messageId), Toast.LENGTH_LONG)
                .show()
        else
            DynamicToast.makeWarning(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    protected fun <T> errorHandler(state: Resource<T>) {
        when {
            state.messageId != null -> toast(messageId = state.messageId)
            state.message != null -> {
                toast(message = state.message)
            }
            else -> {
                state.exception!!.printStackTrace()
            }
        }
    }

    protected open fun setUpViews() {}
    protected open fun setUpObservers() {}

    protected open fun setUp() {}

    override fun onResume() {
        super.onResume()
        setUp()
        setUpViews()
        setUpObservers()
    }


}