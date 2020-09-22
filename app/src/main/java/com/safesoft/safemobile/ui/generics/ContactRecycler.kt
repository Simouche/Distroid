package com.safesoft.safemobile.ui.generics

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.google.android.material.textview.MaterialTextView
import com.safesoft.safemobile.R
import com.safesoft.safemobile.databinding.PhonesListBinding

interface ContactRecycler {
    fun makeCall(context: Context, phone: String) {
        val intent = Intent(Intent.ACTION_CALL)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.data = Uri.parse("tel:$phone")
        context.startActivity(intent)
    }

    fun showPhonesDialog(context: Context, phones: List<String>) {
        val fBinding: PhonesListBinding = DataBindingUtil
            .inflate(LayoutInflater.from(context), R.layout.phones_list, null, false)
        val dialog =
            AlertDialog.Builder(context).setView(fBinding.root).setCancelable(true)
                .create()
        for (phone in phones) {
            val view = LayoutInflater.from(context)
                .inflate(R.layout.phone_view_callable, null) as MaterialTextView
            view.text = phone
            view.setOnClickListener { makeCall(context, phone) }
            fBinding.phones.addView(view)
        }
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        fBinding.closeFiscalProvider.setOnClickListener { dialog.dismiss() }
    }
}