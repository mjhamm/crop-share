package com.hamm.cropshare.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hamm.cropshare.data.AccountListItem
import com.hamm.cropshare.databinding.LayoutAccountListItemBinding
import com.hamm.cropshare.listeners.AccountListItemListener

class AccountListAdapter(
    private val listener: AccountListItemListener
): ListAdapter<AccountListItem, AccountListAdapter.AccountListViewHolder>(DIFF_CALLBACK) {

    private lateinit var item: AccountListItem

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<AccountListItem>() {
            override fun areItemsTheSame(oldItem: AccountListItem, newItem: AccountListItem) =
                oldItem == newItem

            override fun areContentsTheSame(oldItem: AccountListItem, newItem: AccountListItem) =
                oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = AccountListViewHolder(
        LayoutAccountListItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: AccountListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class AccountListViewHolder(
        private val binding: LayoutAccountListItemBinding
    ): RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        fun bind(accountItem: AccountListItem) {
            item = accountItem
            with(binding) {
                accountListImage.setImageResource(accountItem.itemImage)
                accountListTitle.text = accountItem.itemTitle
            }
        }

        init {
            binding.root.setOnClickListener(this)
        }
        override fun onClick(p0: View?) {
            listener.onAccountListItemClicked(item, adapterPosition)
        }
    }
}