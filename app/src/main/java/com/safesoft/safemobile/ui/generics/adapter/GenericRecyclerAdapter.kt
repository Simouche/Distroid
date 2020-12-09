package com.safesoft.safemobile.ui.generics.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.safesoft.safemobile.R
import com.safesoft.safemobile.databinding.NoProductsPlaceholderBinding
import com.safesoft.safemobile.ui.generics.listeners.OnItemClickListener
import com.safesoft.safemobile.ui.generics.listeners.OnItemLongClickListener
import com.safesoft.safemobile.ui.generics.viewholder.BindingViewHolder

abstract class GenericRecyclerAdapter<O, T : ViewDataBinding> :
    RecyclerView.Adapter<BindingViewHolder<T>>() {

    lateinit var onNormalClickListener: OnItemClickListener
    lateinit var onLongClickListener: OnItemLongClickListener
    lateinit var items: MutableList<O>
    var layout: Int = 0
    protected val emptyView = 777777
    protected var empty_drawable: Int = 0
    protected var empty_text: Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder<T> {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        return if (viewType == emptyView) {
            BindingViewHolder(
                layoutInflater.inflate(
                    R.layout.no_products_placeholder,
                    parent,
                    false
                )
            )
        } else
            BindingViewHolder(
                layoutInflater
                    .inflate(layout, parent, false)
            )
    }


    override fun onBindViewHolder(holder: BindingViewHolder<T>, position: Int) {
        if (getItemViewType(position) == emptyView) {
            val mHolder = holder as BindingViewHolder<NoProductsPlaceholderBinding>
            mHolder.binding?.image?.setImageResource(empty_drawable)
            mHolder.binding?.text?.setText(empty_text)
            return
        } else {
            doOnBindViewHolder(holder, position)
            if (this::onLongClickListener.isInitialized) holder.setClickListeners(
                position,
                onNormalClickListener,
                onLongClickListener
            )
        }
        holder.binding?.executePendingBindings()
    }

    override fun getItemCount(): Int {
        return if (items.isEmpty()) 1 else items.size
    }

    protected abstract fun doOnBindViewHolder(holder: BindingViewHolder<T>, position: Int)

    override fun getItemViewType(position: Int): Int {
        if (items.isEmpty())
            return emptyView
        return super.getItemViewType(position)
    }


    fun addItem(item: O) {
        items.add(item)
        notifyDataSetChanged()
    }


}

