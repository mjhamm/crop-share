package com.hamm.cropshare.ui.store

import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
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
import com.hamm.cropshare.data.SellAmount
import com.hamm.cropshare.data.StoreItem
import com.hamm.cropshare.databinding.FragmentStoreBinding
import com.hamm.cropshare.databinding.LayoutEditStoreItemBottomSheetBinding
import com.hamm.cropshare.extensions.*
import com.hamm.cropshare.listeners.StoreItemClickListener
import com.hamm.cropshare.models.UserViewModel


class StoreFragment : Fragment(), StoreItemClickListener {

    private var _binding: FragmentStoreBinding? = null
    private var adapter: MyStoreAdapter? = null

    private var cachedStoreName: String? = null

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

        updateScreen(isUserLoggedIn())
        setupNavigateToLoginFlow()

        observeData()

        return root
    }

    private fun reloadUI(isUserLoggedIn: Boolean) {
        if (isUserLoggedIn) {
            binding.storeLayoutContainer.show()
            binding.storeLoginFlowContainer.root.hide()
        } else {
            binding.storeLayoutContainer.hide()
            binding.storeLoginFlowContainer.root.show()
        }
        updateScreen(isUserLoggedIn)
    }

    private fun observeData() {
        userViewModel.isLoggedIn.observe(viewLifecycleOwner) { updateScreen(it) }
    }

    private fun setupEdittextCursor() {
        binding.storeNameEdittext.setOnEditorActionListener(object : OnEditorActionListener {

            override fun onEditorAction(p0: TextView?, p1: Int, p2: KeyEvent?): Boolean {
                when(p1) {
                    EditorInfo.IME_ACTION_DONE -> {
                        return if (p0?.text?.isEmpty() == true) {
                            binding.storeNameEdittext.setText(getStoreName())
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

    private fun getStoreName(): String {
        return "Hamm Homestead"
    }

    private fun updateStoreName() {
        // Send updated name to server as well as keep in cache
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
            val updatedItem = StoreItem(
                itemName.ifEmpty { storeItem.itemName },
                SellAmount(itemQuantityType),
                convertPriceToDouble(itemPrice) ?: storeItem.itemPrice)
            if (isUpdatedItemValid(updatedItem)) {
                storeViewModel.updateStoreItem(position, updatedItem)
                adapter?.notifyItemChanged(position)
                bottomSheet.dismiss()
            } else {
                requireContext().createToast("There is an issue updating your item. Please check empty fields.")
            }
        }
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
            if (itemPrice.isEmpty()) {
                requireContext().createToast("There is an issue adding your item. Please check empty fields.")
            } else {
                val addedItem = StoreItem(itemName, SellAmount(itemQuantityType), convertPriceToDouble(itemPrice))
                if (isUpdatedItemValid(addedItem)) {
                    storeViewModel.addNewStoreItem(addedItem)
                    adapter?.notifyDataSetChanged()
                    bottomSheet.dismiss()
                    binding.emptyStoreTextview.visibility = View.GONE
                } else {
                    requireContext().createToast("There is an issue adding your item. Please check empty fields.")
                }
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
                        storeViewModel.removeStoreItem(position)
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
            adapter = MyStoreAdapter(this)

            storeViewModel.getMyStoreItems().let {
                adapter?.submitList(it)
            }

            if (storeViewModel.getMyStoreItems().isEmpty()) {
                binding.emptyStoreTextview.visibility = View.VISIBLE
            }

            binding.storeItemsList.layoutManager = LinearLayoutManager(this.context)
            binding.storeItemsList.adapter = adapter
            cachedStoreName = getStoreName()
            binding.storeNameEdittext.setText(getStoreName())
            setupSwipeRecyclerView()
            setupEdittextCursor()

            binding.addStoreItem.setOnClickListener {
                showAddNewItemDialog()
            }
        } else {
            binding.storeLoginFlowContainer.root.show()
            binding.storeLayoutContainer.hide()
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