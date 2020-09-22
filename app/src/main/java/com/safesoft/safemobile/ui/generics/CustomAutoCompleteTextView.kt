package com.safesoft.safemobile.ui.generics

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.textfield.MaterialAutoCompleteTextView


class CustomAutoCompleteTextView(context: Context, attrs: AttributeSet) :
    MaterialAutoCompleteTextView(context, attrs) {

    // this is how to disable AutoCompleteTextView filter
    override fun performFiltering(text: CharSequence?, keyCode: Int) {
        adapter
        val filterText = ""
        super.performFiltering(filterText, keyCode)
    }

    /*
     * after a selection we have to capture the new value and append to the existing text
     */
    override fun replaceText(text: CharSequence?) {
        super.replaceText(text)
    }
}