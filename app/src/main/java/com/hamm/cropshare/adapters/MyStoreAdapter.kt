package com.hamm.cropshare.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hamm.cropshare.data.StoreItem
import com.hamm.cropshare.databinding.LayoutStoreItemBinding
import com.hamm.cropshare.listeners.StoreItemClickListener

class MyStoreAdapter(
    private val listener: StoreItemClickListener
): ListAdapter<StoreItem, MyStoreAdapter.MyStoreItemViewHolder>(DIFF_CALLBACK) {

    private lateinit var item: StoreItem

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StoreItem>() {
            override fun areItemsTheSame(oldItem: StoreItem, newItem: StoreItem) =
                oldItem == newItem

            override fun areContentsTheSame(oldItem: StoreItem, newItem: StoreItem) =
                oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MyStoreItemViewHolder(
        LayoutStoreItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: MyStoreItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class MyStoreItemViewHolder(
        private val binding: LayoutStoreItemBinding
    ): RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        fun bind(storeItem: StoreItem) {
            item = storeItem
            with(binding) {
                storeItemName.text = storeItem.itemName
                storeItemPrice.text = storeItem.getPrice()
                storeItemQuantity.text = storeItem.itemSellAmount?.type
            }
        }

        init {
            binding.root.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            listener.storeItemClicked(item, adapterPosition)
        }
    }
}