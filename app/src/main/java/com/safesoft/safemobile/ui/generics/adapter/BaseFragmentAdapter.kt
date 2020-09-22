package com.safesoft.safemobile.ui.generics.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class BaseFragmentAdapter(fragment: Fragment, private val fragments: Array<Fragment>) :
    FragmentStateAdapter(fragment) {


    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}