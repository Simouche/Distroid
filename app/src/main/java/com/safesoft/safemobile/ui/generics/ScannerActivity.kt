package com.safesoft.safemobile.ui.generics

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.zxing.Result
import com.safesoft.safemobile.R
import dagger.hilt.android.AndroidEntryPoint
import me.dm7.barcodescanner.zxing.ZXingScannerView
import javax.inject.Inject

@AndroidEntryPoint
class ScannerActivity : AppCompatActivity(), ZXingScannerView.ResultHandler {
    private val TAG = ScannerActivity::class.simpleName

    private lateinit var scannerView: ZXingScannerView

    @Inject
    lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scannerView = ZXingScannerView(this)
        setContentView(scannerView)
    }


    override fun onResume() {
        super.onResume()
        scannerView.setResultHandler(this)
        scannerView.flash = preferences.getBoolean("use_flash", false)
        scannerView.setAutoFocus(preferences.getBoolean("auto_focus", false))

        scannerView.startCamera()
    }

    override fun onPause() {
        super.onPause()
        scannerView.stopCamera()
    }

    override fun handleResult(rawResult: Result?) {
        Log.d(TAG, "handleResult: The scanned code is: ${rawResult?.text}")
        setResult(RESULT_OK, Intent().putExtra("code", rawResult?.text))
        finish()
    }
}