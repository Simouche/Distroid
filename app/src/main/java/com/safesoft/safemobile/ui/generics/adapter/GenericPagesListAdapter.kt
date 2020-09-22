package com.safesoft.safemobile.ui.generics.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.safesoft.safemobile.R
import com.safesoft.safemobile.databinding.NoProductsPlaceholderBinding
import com.safesoft.safemobile.ui.generics.listeners.OnItemClickListener
import com.safesoft.safemobile.ui.generics.listeners.OnItemLongClickListener
import com.safesoft.safemobile.ui.generics.viewholder.BindingViewHolder


open class GenericPagedListAdapter<O, T : ViewDataBinding>(DIFF_CALLBACK: DiffUtil.ItemCallback<O>) :
    PagedListAdapter<O, BindingViewHolder<T>>(DIFF_CALLBACK) {

    var onNormalClickListener: OnItemClickListener? = null
    var onLongClickListener: OnItemLongClickListener? = null
    open var layout: Int = 0
    open var holderDrawable: Int = 0
    open var holderText: Int = 0

    protected val emptyView = 777777

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder<T> {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        return if (viewType == emptyView) BindingViewHolder(
            layoutInflater.inflate(
                R.layout.no_products_placeholder,
                parent,
                false
            )
        ) else
            BindingViewHolder(
                layoutInflater
                    .inflate(layout, parent, false)
            )
    }

    override fun onBindViewHolder(holder: BindingViewHolder<T>, position: Int) {
        if (getItemViewType(position) == emptyView) {
            val mHolder = holder as BindingViewHolder<NoProductsPlaceholderBinding>
            mHolder.binding?.image?.setImageResource(holderDrawable)
            mHolder.binding?.text?.setText(holderText)
            return
        }
        if (onLongClickListener != null || onNormalClickListener != null) holder.setClickListeners(
            position,
            onNormalClickListener,
            onLongClickListener
        )
        holder.binding?.executePendingBindings()
    }

    override fun getItemCount(): Int {
        return if (super.getItemCount() == 0) 1 else super.getItemCount()
    }

    fun getItemAt(position: Int) = super.getItem(position)

    override fun getItemViewType(position: Int): Int {
        if (super.getItemCount() == 0) return emptyView
        return super.getItemViewType(position)
    }


}