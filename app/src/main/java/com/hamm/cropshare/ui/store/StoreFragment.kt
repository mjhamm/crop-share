package com.hamm.cropshare.ui.store

import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.hamm.cropshare.R
import com.hamm.cropshare.adapters.MyStoreAdapter
import com.hamm.cropshare.data.Location
import com.hamm.cropshare.data.Store
import com.hamm.cropshare.data.StoreItem
import com.hamm.cropshare.databinding.FragmentStoreBinding
import com.hamm.cropshare.databinding.LayoutEditStoreItemBottomSheetBinding
import com.hamm.cropshare.databinding.LayoutEnterLocationBottomSheetBinding
import com.hamm.cropshare.extensions.*
import com.hamm.cropshare.helpers.FirebaseHelper
import com.hamm.cropshare.listeners.StoreItemClickListener
import com.hamm.cropshare.models.StoreViewModel
import com.hamm.cropshare.models.UserViewModel
import com.hamm.cropshare.prefs


class StoreFragment : Fragment(), StoreItemClickListener {

    private var _binding: FragmentStoreBinding? = null
    private var adapter: MyStoreAdapter? = null

    private var cachedStoreName: String? = null

    private var userHasStore = false
    private var storeItems = mutableListOf<StoreItem>()

    private val storeViewModel: StoreViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()

    private lateinit var swipeHelper: ItemTouchHelper

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentStoreBinding.inflate(inflater, container, false)
        val root: View = binding.root

        adapter = MyStoreAdapter(this)

        setupNavigateToLoginFlow()
        setupButtons()

        observeData()

        storeViewModel.getStore()

        return root
    }

    private fun setupButtons() {
        // Create a new store for user
        binding.createNewStoreContainer.createStoreButton.setOnClickListener {
            val newStoreName = binding.createNewStoreContainer.newStoreName.text.toString()
            if (prefs.userStoreAddressPref?.isNotEmpty() == true && prefs.userStoreZipCodePref?.isNotEmpty() == true) {
                prefs.userStoreAddressPref?.let { address ->
                    prefs.userStoreZipCodePref?.let { zip ->
                        val location = Location(address, zip)
                        storeViewModel.createNewStore(Store(newStoreName, emptyList(), location))
                        userHasStore = true
                        binding.storeNameEdittext.clearFocus()
                        hideKeyboard()
                    }
                }
            } else {
                binding.storeNameEdittext.clearFocus()
                hideKeyboard()
                showEnterLocationDialog(newStoreName)
            }
        }

        binding.createNewStoreContainer.newStoreName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                binding.createNewStoreContainer.createStoreButton.isEnabled = p0?.isNotEmpty() == true
            }
        })

        // Add new item to store
        binding.addStoreItem.setOnClickListener {
            showAddNewItemDialog()
        }
    }

    private fun observeData() {
        FirebaseHelper().firebaseAuth.addAuthStateListener {
            try {
                if (it.currentUser == null) {
                    userHasStore = false
                    updateScreen(false)
                } else {
                    updateScreen(true)
                }
            } catch (exception: Exception) {
                Log.d("StoreFragment", exception.toString())
            }
        }

        storeViewModel.store.observe(viewLifecycleOwner) {
            cachedStoreName = it.storeName
            userHasStore = true
            adapter?.submitList(it.storeItems)
            updateScreen(true)
            binding.storeNameEdittext.setText(cachedStoreName)
        }

        storeViewModel.items.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                binding.emptyStoreTextview.hide()
            } else {
                binding.emptyStoreTextview.show()
            }
            storeItems = it.toMutableList()
            updateData(it)
        }
    }

    private fun updateData(storeItems: List<StoreItem>) {
        adapter?.submitList(storeItems)
    }

    private fun setupEdittextCursor() {
        binding.storeNameEdittext.setOnEditorActionListener(object : OnEditorActionListener {

            override fun onEditorAction(p0: TextView?, p1: Int, p2: KeyEvent?): Boolean {
                when(p1) {
                    EditorInfo.IME_ACTION_DONE -> {
                        return if (p0?.text?.isEmpty() == true) {
                            binding.storeNameEdittext.setText(cachedStoreName)
                            requireContext().createToast("Your store name cannot be empty")
                            binding.storeNameEdittext.clearFocus()
                            false
                        } else {
                            if (binding.storeNameEdittext.text.toString() == cachedStoreName) {
                                binding.storeNameEdittext.clearFocus()
                                false
                            } else {
                                requireContext().createToast("Store name has been updated")
                                cachedStoreName = binding.storeNameEdittext.text.toString()
                                binding.storeNameEdittext.clearFocus()
                                //storeViewModel.updateStoreName(binding.storeNameEdittext.text.toString())
                                false
                            }
                        }
                    }
                }
                return false
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Displays a bottom dialog sheet that allows you to make changes to your item
     */
    override fun storeItemClicked(storeItem: StoreItem, position: Int) {
        val bottomSheet = BottomSheetDialog(requireContext(), R.style.DialogStyle)
        val dialogBinding = LayoutEditStoreItemBottomSheetBinding.inflate(layoutInflater)
        bottomSheet.setContentView(dialogBinding.root)
        bottomSheet.show()

        dialogBinding.saveItemChangesButton.setOnClickListener {
            val itemName = dialogBinding.updateItemName.text.toString()
            val itemPrice = dialogBinding.updateItemPrice.text.toString()
            val itemQuantityType = dialogBinding.updateItemQuantityType.text.toString()
            if (itemPrice.convertPriceToLong()) {
                val updatedItem = StoreItem(
                    itemName.ifEmpty { storeItem.itemName },
                    itemQuantityType,
                    itemPrice.toLong())
                if (isUpdatedItemValid(updatedItem)) {
                    storeItems.add(updatedItem)
                    storeViewModel.updateStoreItem(storeItems)
                    adapter?.notifyItemChanged(position)
                    bottomSheet.dismiss()
                } else {
                    requireContext().createToast("There is an issue updating your item. Please check empty fields.")
                }
            } else {
                requireContext().createToast("There is an issue with your price.")
            }
        }
    }

    private fun showEnterLocationDialog(storeName: String) {
        val bottomSheet = BottomSheetDialog(requireContext(), R.style.DialogStyle)
        val dialogBinding = LayoutEnterLocationBottomSheetBinding.inflate(layoutInflater)

        var newZip = ""
        var newAddress = ""

        bottomSheet.setContentView(dialogBinding.root)
        bottomSheet.show()

        dialogBinding.enterZipCodeEdittext.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                newZip = p0.toString()
                dialogBinding.saveStoreLocationButton.isEnabled = newAddress.isNotEmpty() && isZipCodeValid(newZip)
            }
        })

        dialogBinding.enterStreetAddressEdittext.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                newAddress = p0.toString()
                dialogBinding.saveStoreLocationButton.isEnabled = newAddress.isNotEmpty() && isZipCodeValid(newZip)
            }
        })

        dialogBinding.saveStoreLocationButton.setOnClickListener {
            prefs.userStoreAddressPref = newAddress
            prefs.userStoreZipCodePref = newZip
            userViewModel.updateUserStoreLocation(Location(newAddress, newZip))
            storeViewModel.createNewStore(Store(storeName, emptyList(), Location(newAddress, newZip)))
            bottomSheet.dismiss()
            hideKeyboard()
        }
    }

    private fun isZipCodeValid(zipCode: String): Boolean {
        if (zipCode.isNotEmpty() && zipCode.length == 5) {
            return try {
                zipCode.toLong()
                true
            } catch (exception: Exception) {
                false
            }
        }
        return false
    }

    private fun showAddNewItemDialog() {
        val bottomSheet = BottomSheetDialog(requireContext(), R.style.DialogStyle)
        val dialogBinding = LayoutEditStoreItemBottomSheetBinding.inflate(layoutInflater)

        dialogBinding.saveItemChangesButton.text = "Add New Item"
        dialogBinding.updateItemName.hint = "Item Name"
        dialogBinding.updateItemPrice.hint = "Item Price"
        dialogBinding.updateItemQuantityType.hint = "Quantity Type (ex. Per Lb., Per oz., Each)"

        bottomSheet.setContentView(dialogBinding.root)
        bottomSheet.show()

        dialogBinding.saveItemChangesButton.setOnClickListener {
            val itemName = dialogBinding.updateItemName.text.toString()
            val itemPrice = dialogBinding.updateItemPrice.text.toString()
            val itemQuantityType = dialogBinding.updateItemQuantityType.text.toString()
            if (itemPrice.convertPriceToLong()) {
                val addedItem = StoreItem(itemName, itemQuantityType, itemPrice.toLong())
                if (isUpdatedItemValid(addedItem)) {
                    storeItems.add(addedItem)
                    storeViewModel.addNewStoreItem(storeItems)
                    adapter?.notifyDataSetChanged()
                    bottomSheet.dismiss()
                    binding.emptyStoreTextview.visibility = View.GONE
                } else {
                    requireContext().createToast("There is an issue adding your item. Please check empty fields.")
                }
            } else {
                requireContext().createToast("There is an issue with you price.")
            }
        }
    }

    private fun setupSwipeRecyclerView() {
        val simpleCallback = object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.LEFT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                when(direction) {
                    ItemTouchHelper.LEFT -> {
                        // TODO
//                        storeViewModel.removeStoreItem(position)
                        adapter?.notifyItemRemoved(position)
                        if (adapter?.itemCount == 0) {
                            binding.emptyStoreTextview.visibility = View.VISIBLE
                        } else {
                            binding.emptyStoreTextview.visibility = View.GONE
                        }
                    }
                }
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                val itemView = viewHolder.itemView
                val backgroundCornerOffset = 20
                val background = ColorDrawable(ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark))
                val icon = ResourcesCompat.getDrawable(resources, R.drawable.baseline_delete_24, null)

                icon?.let {

                    val iconMargin: Int = (itemView.height - it.intrinsicHeight) / 2
                    val iconTop: Int = itemView.top + (itemView.height - it.intrinsicHeight) / 2
                    val iconBottom: Int = iconTop + it.intrinsicHeight

                    if (dX < 0) { // Swiping to the left
                        val iconLeft: Int = itemView.right - iconMargin - it.intrinsicWidth
                        val iconRight = itemView.right - iconMargin
                        it.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                        background.setBounds(
                            itemView.right + dX.toInt() - backgroundCornerOffset,
                            itemView.top, itemView.right, itemView.bottom
                        )
                    } else { // view is unSwiped
                        background.setBounds(0, 0, 0, 0)
                    }
                    background.draw(c)
                    icon.draw(c)
                }
            }
        }
        swipeHelper = ItemTouchHelper(simpleCallback)
        swipeHelper.attachToRecyclerView(binding.storeItemsList)
    }

    private fun updateScreen(isUserLoggedIn: Boolean) {
        if (isUserLoggedIn) {
            if (userHasStore) {
                binding.storeLoginFlowContainer.root.hide()
                binding.createNewStoreContainer.root.hide()
                binding.storeLayoutContainer.show()
                binding.storeItemsList.layoutManager = LinearLayoutManager(this.context)
                binding.storeItemsList.adapter = adapter
                binding.storeNameEdittext.setText(cachedStoreName)
                setupSwipeRecyclerView()
                setupEdittextCursor()
            } else {
                binding.storeLoginFlowContainer.root.hide()
                binding.createNewStoreContainer.root.show()
                binding.storeLayoutContainer.hide()
            }
        } else {
            binding.storeLoginFlowContainer.root.show()
            binding.storeLayoutContainer.hide()
            binding.createNewStoreContainer.root.hide()
        }
    }

    private fun setupNavigateToLoginFlow() {
        binding.storeLoginFlowContainer.loginButtonStore.setOnClickListener {
            val navigateToLogin = StoreFragmentDirections.actionNavigationStoreToNavigationLogin()
            findNavController().navigate(navigateToLogin)
        }
        binding.storeLoginFlowContainer.registerButtonStore.setOnClickListener {
            val navigateToRegister = StoreFragmentDirections.actionNavigationStoreToNavigationRegister()
            findNavController().navigate(navigateToRegister)
        }
    }

}