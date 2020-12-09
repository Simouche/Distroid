package com.safesoft.safemobile.ui.inventory

import androidx.recyclerview.widget.DiffUtil
import com.safesoft.safemobile.R
import com.safesoft.safemobile.backend.db.local.entity.InventoryWithLines
import com.safesoft.safemobile.databinding.FragmentInventoryListItemBinding
import com.safesoft.safemobile.ui.generics.adapter.GenericPagedListAdapter
import com.safesoft.safemobile.ui.generics.viewholder.BindingViewHolder
import javax.inject.Inject

class InventoriesRecyclerAdapter @Inject constructor() :
    GenericPagedListAdapter<InventoryWithLines, FragmentInventoryListItemBinding>(DIFF_CALLBACK) {

    override var layout = R.layout.fragment_inventory_list_item
    override var holderDrawable = R.drawable.ic_no_inventory
    override var holderText = R.string.no_inventory_yet

    override fun onBindViewHolder(
        holder: BindingViewHolder<FragmentInventoryListItemBinding>,
        position: Int
    ) {
        if (getItemViewType(position) != emptyView)
            holder.binding?.inventory = getItem(position)
        super.onBindViewHolder(holder, position)
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<InventoryWithLines>() {
            override fun areItemsTheSame(oldItem: InventoryWithLines, newItem: InventoryWithLines) =
                oldItem.inventory.id == newItem.inventory.id

            override fun areContentsTheSame(
                oldItem: InventoryWithLines,
                newItem: InventoryWithLines
            ) =
                oldItem == newItem

        }
    }
}