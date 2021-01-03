package com.safesoft.safemobile.ui.scanner

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.zxing.Result
import com.safesoft.safemobile.viewmodel.ScannerViewModel
import dagger.hilt.android.AndroidEntryPoint
import me.dm7.barcodescanner.zxing.ZXingScannerView


@AndroidEntryPoint
class ScannerFragment : Fragment(), ZXingScannerView.ResultHandler {
    private val TAG = ScannerFragment::class.simpleName

    private lateinit var mScannerView: ZXingScannerView
    private val viewModel: ScannerViewModel by viewModels(this::requireActivity)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mScannerView = ZXingScannerView(activity)
        mScannerView.flash = viewModel.useFlash
        mScannerView.setAutoFocus(viewModel.autoFocus)

        viewModel.scannedCode.observe(viewLifecycleOwner, {
            Log.d(TAG, "onCreateView: observing scan $it")
            if (it.isNotEmpty())
                findNavController().popBackStack()
        })

        return mScannerView
    }

    override fun onResume() {
        super.onResume()
        mScannerView.setResultHandler(this) // Register ourselves as a handler for scan results.
        mScannerView.startCamera() // Start camera on resume
    }

    override fun onPause() {
        super.onPause()
        mScannerView.stopCamera() // Stop camera on pause
    }

    override fun handleResult(rawResult: Result?) {
        Log.d(TAG, "Scanned: ${rawResult?.text}")
        viewModel.scannedCode.value = rawResult?.text

    }
}