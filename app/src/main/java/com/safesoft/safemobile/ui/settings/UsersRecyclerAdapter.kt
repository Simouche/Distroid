package com.safesoft.safemobile.ui.settings

import com.safesoft.safemobile.R
import com.safesoft.safemobile.backend.db.local.entity.Users
import com.safesoft.safemobile.databinding.UsersListItemBinding
import com.safesoft.safemobile.ui.generics.adapter.GenericRecyclerAdapter
import com.safesoft.safemobile.ui.generics.viewholder.BindingViewHolder
import javax.inject.Inject

class UsersRecyclerAdapter @Inject constructor() :
    GenericRecyclerAdapter<Users, UsersListItemBinding>() {

    init {
        items = mutableListOf()
        layout = R.layout.users_list_item
        empty_drawable = R.drawable.ic_no_clients
        empty_text = R.string.no_users_yet
    }

    override fun doOnBindViewHolder(
        holder: BindingViewHolder<UsersListItemBinding>,
        position: Int
    ) {
        holder.binding?.user = items[position]
    }
}