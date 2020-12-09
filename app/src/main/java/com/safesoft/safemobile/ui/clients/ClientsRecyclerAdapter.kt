package com.safesoft.safemobile.ui.clients

import androidx.recyclerview.widget.DiffUtil
import com.safesoft.safemobile.R
import com.safesoft.safemobile.backend.db.local.entity.Clients
import com.safesoft.safemobile.databinding.FragmentClientListItemBinding
import com.safesoft.safemobile.ui.generics.ContactRecycler
import com.safesoft.safemobile.ui.generics.adapter.GenericPagedListAdapter
import com.safesoft.safemobile.ui.generics.viewholder.BindingViewHolder
import javax.inject.Inject

class ClientsRecyclerAdapter @Inject constructor() :
    GenericPagedListAdapter<Clients, FragmentClientListItemBinding>(DIFF_CALLBACK),
    ContactRecycler {

    override var layout = R.layout.fragment_client_list_item
    override var holderDrawable = R.drawable.ic_no_clients
    override var holderText = R.string.no_client_yet

    override fun onBindViewHolder(
        holder: BindingViewHolder<FragmentClientListItemBinding>,
        position: Int
    ) {
        if (getItemViewType(position) != emptyView) {
            holder.binding?.client = getItem(position)
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
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Clients>() {
            override fun areItemsTheSame(oldItem: Clients, newItem: Clients) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Clients, newItem: Clients) =
                oldItem == newItem
        }
    }

}