package com.safesoft.safemobile.ui.sales

import androidx.recyclerview.widget.DiffUtil
import com.safesoft.safemobile.R
import com.safesoft.safemobile.backend.db.entity.Sales
import com.safesoft.safemobile.databinding.FragmentSalesListItemBinding
import com.safesoft.safemobile.ui.generics.adapter.GenericPagedListAdapter
import com.safesoft.safemobile.ui.generics.viewholder.BindingViewHolder
import javax.inject.Inject

class SalesRecyclerAdapter @Inject constructor() :
    GenericPagedListAdapter<Sales, FragmentSalesListItemBinding>(DIFF_CALLBACK) {

    override var layout = R.layout.fragment_sales_list_item
    override var holderDrawable = R.drawable.ic_empty_invoice
    override var holderText = R.string.no_sales_yet

    override fun onBindViewHolder(
        holder: BindingViewHolder<FragmentSalesListItemBinding>,
        position: Int
    ) {
        if (getItemViewType(position) != emptyView)
            holder.binding?.sale = getItem(position)
        super.onBindViewHolder(holder, position)
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Sales>() {
            override fun areItemsTheSame(oldItem: Sales, newItem: Sales) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Sales, newItem: Sales) =
                oldItem == newItem

        }
    }

}