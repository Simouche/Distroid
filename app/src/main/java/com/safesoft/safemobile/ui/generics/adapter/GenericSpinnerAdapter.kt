package com.safesoft.safemobile.ui.generics.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.annotation.Keep

@Keep
class GenericSpinnerAdapter<T : Any>(
    context: Context,
    resource: Int,
    var objects: List<T>
) : ArrayAdapter<T>(context, resource, objects) {

    override fun getCount(): Int {
        return objects.size
    }

    override fun getItem(position: Int): T? {
        return objects[position]
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val label = super.getView(position, convertView, parent) as TextView
        label.text = (getItem(position).toString())
        return label
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val label = super.getDropDownView(position, convertView, parent) as TextView
        label.text = (getItem(position).toString())
        return label
    }



}