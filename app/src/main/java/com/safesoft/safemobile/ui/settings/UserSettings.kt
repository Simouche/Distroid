package com.safesoft.safemobile.ui.settings

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.safesoft.safemobile.R
import com.safesoft.safemobile.databinding.FragmentUserSettingsBinding
import com.safesoft.safemobile.databinding.FragmentUsersSettingsBinding
import com.safesoft.safemobile.ui.generics.BaseFragment
import com.safesoft.safemobile.ui.generics.addDivider
import com.safesoft.safemobile.ui.generics.listeners.OnItemClickListener
import com.safesoft.safemobile.ui.generics.listeners.OnItemLongClickListener
import com.safesoft.safemobile.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class UserSettings : BaseFragment() {

    private val authViewModel: AuthViewModel by viewModels(this::requireActivity)
    private lateinit var binding: FragmentUsersSettingsBinding

    @Inject
    lateinit var recyclerAdapter: UsersRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!authViewModel.isAdmin()) {
            findNavController().popBackStack()
            warning(R.string.access_denied)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_users_settings,
                container,
                false
            )
        return binding.root
    }

    override fun setUpViews() {
        super.setUpViews()
        setUpViewsForAdmin()
    }

    override fun setUpObservers() {
        super.setUpObservers()
        setUpObserversForAdmin()
    }

    private fun setUpViewsForAdmin() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.usersList.adapter = recyclerAdapter
        binding.usersList.addDivider()
        recyclerAdapter.onNormalClickListener = OnItemClickListener { position, _ ->
            Log.d(
                TAG,
                "setUpForAdmin: $position"
            )
        }
        registerForContextMenu(binding.usersList)
        recyclerAdapter.onLongClickListener = OnItemLongClickListener { position, view ->
            currentItemPosition = position
            view.showContextMenu()
            true
        }
        binding.addNewUser.setOnClickListener { showAddNewUserDialog() }

    }

    private fun setUpObserversForAdmin() {
        authViewModel.getAllUsers().observe(viewLifecycleOwner, Observer {
            when (it.state) {
                loading -> return@Observer
                success -> {
                    recyclerAdapter.items = it.data!!.toMutableList()
                    recyclerAdapter.notifyDataSetChanged()
                }
                error -> {
                    error(R.string.error_loading_users)
                }
            }
        })

    }

    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        val inflater = requireActivity().menuInflater
        inflater.inflate(R.menu.user_context_menu, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_details_user -> toast(message = "Should show user details")
            R.id.action_update_user -> toast(message = "should show update user")
            R.id.action_revoke_user -> toast(message = "user is revoked")
            R.id.action_delete_user -> toast(message = "user is deleted")
        }
        return super.onContextItemSelected(item)
    }

    private fun showAddNewUserDialog() {
        val dialogBinding: FragmentUserSettingsBinding = DataBindingUtil.inflate(
            LayoutInflater.from(requireContext()),
            R.layout.fragment_user_settings,
            null,
            false
        )
        dialogBinding.viewModel = authViewModel
        dialogBinding.isAdmin = authViewModel.isAdmin()

        val dialog =
            AlertDialog
                .Builder(requireContext())
                .setView(dialogBinding.root)
                .setCancelable(true)
                .create()

        dialogBinding.createUserIsAdmin.setOnCheckedChangeListener { _, isChecked ->
            authViewModel.userForm.fields.isAdmin.value = isChecked
            dialogBinding.createUserSalesPerm.isChecked = isChecked
            dialogBinding.createUserPurchasePerm.isChecked = isChecked
            dialogBinding.createUserInventoryPerm.isChecked = isChecked
        }

        dialogBinding.createUserSalesPerm.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                authViewModel.userForm.fields.permission.value?.add("S")
            } else {
                authViewModel.userForm.fields.permission.value?.remove("S")
                dialogBinding.createUserIsAdmin.isChecked = false
            }
        }

        dialogBinding.createUserPurchasePerm.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                authViewModel.userForm.fields.permission.value?.add("B")
            } else {
                authViewModel.userForm.fields.permission.value?.remove("B")
                dialogBinding.createUserIsAdmin.isChecked = false
            }
        }

        dialogBinding.createUserInventoryPerm.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                authViewModel.userForm.fields.permission.value?.add("I")
            } else {
                authViewModel.userForm.fields.permission.value?.remove("I")
                dialogBinding.createUserIsAdmin.isChecked = false
            }
        }

        dialogBinding.saveUserButton.setOnClickListener {
            authViewModel.saveButton().observe(viewLifecycleOwner, {
                when (it.state) {
                    loading -> return@observe
                    success -> Log.d(TAG, "showAddNewUserDialog: added!")
                    error -> Log.d(TAG, "showAddNewUserDialog: wtf")
                }
            })
        }

        dialog.show()

    }

}