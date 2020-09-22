package com.safesoft.safemobile.ui.providers

import android.view.ContextMenu
import android.view.View
import androidx.recyclerview.widget.DiffUtil
import com.safesoft.safemobile.R
import com.safesoft.safemobile.backend.db.entity.Providers
import com.safesoft.safemobile.databinding.FragmentProviderListItemBinding
import com.safesoft.safemobile.ui.generics.ContactRecycler
import com.safesoft.safemobile.ui.generics.adapter.GenericPagedListAdapter
import com.safesoft.safemobile.ui.generics.viewholder.BindingViewHolder
import javax.inject.Inject

class ProvidersRecyclerAdapter @Inject constructor() :
    GenericPagedListAdapter<Providers, FragmentProviderListItemBinding>(DIFF_CALLBACK),
    ContactRecycler {

    override var layout = R.layout.fragment_provider_list_item
    override var holderDrawable = R.drawable.ic_no_factory
    override var holderText = R.string.no_provider_yet

    override fun onBindViewHolder(
        holder: BindingViewHolder<FragmentProviderListItemBinding>,
        position: Int
    ) {
        if (getItemViewType(position) != emptyView) {
            holder.binding?.provider = getItem(position)
            if (!getItem(position)?.phones.isNullOrEmpty() && !getItem(position)?.phones.isNullOrBlank()) {
                if (getItem(position)?.getAllPhones()?.size!! > 1) {
                    holder.binding?.makeCall?.setOnClickListener {
                        showPhonesDialog(
                            holder.binding?.root?.context!!,
                            getItem(position)?.getAllPhones()!!
                        )
                    }
                } else {
                    holder.binding?.makeCall?.setOnClickListener {
                        makeCall(holder.binding?.root?.context!!, getItem(position)?.phones!!)
                    }
                }
            }
        }
        super.onBindViewHolder(holder, position)
    }


    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Providers>() {
            override fun areItemsTheSame(oldItem: Providers, newItem: Providers) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Providers, newItem: Providers) =
                oldItem == newItem

        }
    }
}