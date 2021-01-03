package com.safesoft.safemobile.ui.generics

import android.util.Log
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.safesoft.safemobile.R
import com.safesoft.safemobile.viewmodel.ScannerViewModel

abstract class BaseScannerFragment : BaseFragment() {
    protected val scannerViewModel: ScannerViewModel by viewModels(this::requireActivity)
    private val SCANNER_REQUEST = 100
    protected var isScanning = false


    protected fun launchScanner() {
        isScanning = true
        findNavController().navigate(R.id.action_global_nav_start_scanner)
    }

    protected open fun handleScannerResult(text: String) {
        Log.d(TAG, "handleScannerResult: handling scan result with value: $text")
        scannerViewModel.scannedCode.value = ""
    }


    override fun onResume() {
        super.onResume()
        if (isScanning && scannerViewModel.scannedCode.value?.isNotEmpty() == true)
            handleScannerResult(scannerViewModel.scannedCode.value!!)
    }


}