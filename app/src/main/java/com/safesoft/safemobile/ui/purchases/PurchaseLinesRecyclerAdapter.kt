package com.safesoft.safemobile.ui.purchases

import com.safesoft.safemobile.R
import com.safesoft.safemobile.backend.db.local.entity.PurchaseLines
import com.safesoft.safemobile.databinding.PurchaseFormProductsListItemBinding
import com.safesoft.safemobile.ui.generics.adapter.GenericRecyclerAdapter
import com.safesoft.safemobile.ui.generics.listeners.OnItemClickListener
import com.safesoft.safemobile.ui.generics.viewholder.BindingViewHolder
import javax.inject.Inject

class PurchaseLinesRecyclerAdapter @Inject constructor() :
    GenericRecyclerAdapter<PurchaseLines, PurchaseFormProductsListItemBinding>() {

    lateinit var deleter: OnItemClickListener

    init {
        layout = R.layout.purchase_form_products_list_item
        items = mutableListOf()
        empty_drawable = R.drawable.ic_empty_cart
        empty_text = R.string.no_product_added_yet
    }


    override fun doOnBindViewHolder(
        holder: BindingViewHolder<PurchaseFormProductsListItemBinding>,
        position: Int
    ) {
        holder.binding?.model = items[position]
        holder.binding?.deleteProduct?.setOnClickListener { deleter.onItemSelected(position, it) }
    }
}