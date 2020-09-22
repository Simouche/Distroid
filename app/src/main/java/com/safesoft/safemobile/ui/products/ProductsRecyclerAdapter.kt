package com.safesoft.safemobile.ui.products

import androidx.recyclerview.widget.DiffUtil
import com.safesoft.safemobile.R
import com.safesoft.safemobile.backend.db.entity.AllAboutAProduct
import com.safesoft.safemobile.backend.db.entity.ProductWithBarcodes
import com.safesoft.safemobile.backend.db.entity.Products
import com.safesoft.safemobile.databinding.ProductListItemBinding
import com.safesoft.safemobile.ui.generics.adapter.GenericPagedListAdapter
import com.safesoft.safemobile.ui.generics.viewholder.BindingViewHolder
import com.squareup.picasso.Picasso
import java.io.File
import javax.inject.Inject

class ProductsRecyclerAdapter @Inject constructor() :
    GenericPagedListAdapter<AllAboutAProduct, ProductListItemBinding>(DIFF_CALLBACK) {

    override var layout = R.layout.product_list_item
    override var holderDrawable = R.drawable.ic_no_item
    override var holderText = R.string.no_item_yet

    override fun onBindViewHolder(
        holder: BindingViewHolder<ProductListItemBinding>,
        position: Int
    ) {
        if (getItemViewType(position) != emptyView) {
            holder.binding?.product = getItem(position)
            Picasso
                .get()
                .load(File(getItem(position)?.product?.photo ?: " "))
                .error(R.drawable.ic_product)
                .placeholder(R.drawable.ic_product)
                .fit()
                .into(holder.binding?.productImage)
        }
        super.onBindViewHolder(holder, position)
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<AllAboutAProduct>() {
            override fun areItemsTheSame(oldItem: AllAboutAProduct, newItem: AllAboutAProduct) =
                oldItem.product.id == newItem.product.id

            override fun areContentsTheSame(oldItem: AllAboutAProduct, newItem: AllAboutAProduct) =
                oldItem == newItem

        }
    }
}