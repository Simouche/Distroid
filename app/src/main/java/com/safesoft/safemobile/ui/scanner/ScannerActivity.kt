package com.safesoft.safemobile.ui.scanner

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import com.google.zxing.Result
import com.safesoft.safemobile.viewmodel.ScannerViewModel
import com.safesoft.safemobile.viewmodel.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint
import me.dm7.barcodescanner.zxing.ZXingScannerView

@AndroidEntryPoint
class ScannerActivity : AppCompatActivity(), ZXingScannerView.ResultHandler {
    private val TAG = ScannerActivity::class.simpleName
    private val viewModel: ScannerViewModel by viewModels()

    private lateinit var mScannerView: ZXingScannerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mScannerView = ZXingScannerView(this)
        setContentView(mScannerView)
        mScannerView.flash = viewModel.useFlash
        mScannerView.setAutoFocus(viewModel.autoFocus)
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
        val data = Intent()
        data.putExtra("scanned_code", rawResult?.text)
        viewModel.scannedCode.value = rawResult?.text
        setResult(RESULT_OK, data)
        finish()
    }
}