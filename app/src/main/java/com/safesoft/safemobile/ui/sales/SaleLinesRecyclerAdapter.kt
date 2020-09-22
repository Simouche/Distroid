package com.safesoft.safemobile.ui.sales

import com.safesoft.safemobile.R
import com.safesoft.safemobile.backend.db.entity.SaleLines
import com.safesoft.safemobile.databinding.SaleFormProductsListItemBinding
import com.safesoft.safemobile.ui.generics.adapter.GenericRecyclerAdapter
import com.safesoft.safemobile.ui.generics.listeners.OnItemClickListener
import com.safesoft.safemobile.ui.generics.viewholder.BindingViewHolder
import javax.inject.Inject

class SaleLinesRecyclerAdapter @Inject constructor() :
    GenericRecyclerAdapter<SaleLines, SaleFormProductsListItemBinding>() {

    lateinit var deleter: OnItemClickListener

    init {
        layout = R.layout.sale_form_products_list_item
        items = mutableListOf()
        empty_drawable = R.drawable.ic_empty_cart
        empty_text = R.string.no_product_added_yet
    }

    override fun doOnBindViewHolder(
        holder: BindingViewHolder<SaleFormProductsListItemBinding>,
        position: Int
    ) {
        holder.binding?.model = items[position]
        holder.binding?.deleteProduct?.setOnClickListener { deleter.onItemSelected(position, it) }
    }

}