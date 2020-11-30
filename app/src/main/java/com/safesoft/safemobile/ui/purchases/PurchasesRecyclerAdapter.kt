package com.safesoft.safemobile.ui.purchases

import android.util.Log
import androidx.recyclerview.widget.DiffUtil
import com.safesoft.safemobile.R
import com.safesoft.safemobile.backend.db.entity.Purchases
import com.safesoft.safemobile.databinding.FragmentPurchasesListItemBinding
import com.safesoft.safemobile.ui.generics.adapter.GenericPagedListAdapter
import com.safesoft.safemobile.ui.generics.viewholder.BindingViewHolder
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*
import javax.inject.Inject

class PurchasesRecyclerAdapter @Inject constructor() :
    GenericPagedListAdapter<Purchases, FragmentPurchasesListItemBinding>(DIFF_CALLBACK) {

    override var layout = R.layout.fragment_purchases_list_item
    override var holderDrawable = R.drawable.ic_empty_invoice
    override var holderText = R.string.no_purchase_yet

    override fun onBindViewHolder(
        holder: BindingViewHolder<FragmentPurchasesListItemBinding>,
        position: Int
    ) {
        if (getItemViewType(position) != emptyView)
            holder.binding?.purchase = getItem(position)

        super.onBindViewHolder(holder, position)
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Purchases>() {
            override fun areItemsTheSame(oldItem: Purchases, newItem: Purchases) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Purchases, newItem: Purchases) =
                oldItem == newItem

        }
    }

}