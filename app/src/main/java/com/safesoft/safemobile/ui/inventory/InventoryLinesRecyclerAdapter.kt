package com.safesoft.safemobile.ui.inventory

import com.safesoft.safemobile.R
import com.safesoft.safemobile.backend.db.entity.InventoryLines
import com.safesoft.safemobile.databinding.InventoryFormProductsListItemBinding
import com.safesoft.safemobile.ui.generics.adapter.GenericRecyclerAdapter
import com.safesoft.safemobile.ui.generics.listeners.OnItemClickListener
import com.safesoft.safemobile.ui.generics.viewholder.BindingViewHolder
import javax.inject.Inject

class InventoryLinesRecyclerAdapter @Inject constructor() :
    GenericRecyclerAdapter<InventoryLines, InventoryFormProductsListItemBinding>() {

    lateinit var deleter:OnItemClickListener

    init {
        layout = R.layout.inventory_form_products_list_item
        items = mutableListOf()
        empty_drawable = R.drawable.ic_no_inventory
        empty_text = R.string.no_product_added_yet
    }

    override fun doOnBindViewHolder(
        holder: BindingViewHolder<InventoryFormProductsListItemBinding>,
        position: Int
    ) {
        holder.binding?.model = items[position]
        holder.binding?.deleteProduct?.setOnClickListener { deleter.onItemSelected(position, it) }
    }
}