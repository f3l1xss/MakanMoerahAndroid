package com.felixstanley.makanmoerahandroid.fragment.restaurantdetails

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.felixstanley.makanmoerahandroid.databinding.FacilityPaymentmodeListItemBinding
import com.felixstanley.makanmoerahandroid.entity.facility.AbstractFacilityPaymentMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FacilityPaymentModeListItemAdapter :
    ListAdapter<FacilityPaymentModeListItem, RecyclerView.ViewHolder>(
        FacilityPaymentModeListItemDiffCallback()
    ) {

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    class FacilityPaymentModeListItemDiffCallback :
        DiffUtil.ItemCallback<FacilityPaymentModeListItem>() {
        override fun areItemsTheSame(
            oldItem: FacilityPaymentModeListItem,
            newItem: FacilityPaymentModeListItem
        ): Boolean {
            return oldItem.firstItem.name == newItem.firstItem.name
                    && oldItem.secondItem?.name == newItem.secondItem?.name
        }

        override fun areContentsTheSame(
            oldItem: FacilityPaymentModeListItem,
            newItem: FacilityPaymentModeListItem
        ): Boolean {
            return oldItem.firstItem.name == newItem.firstItem.name
                    && oldItem.secondItem?.name == newItem.secondItem?.name
        }
    }

    class FacilityPaymentModeListItemViewHolder private constructor(val binding: FacilityPaymentmodeListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        companion object {
            // FacilityPaymentModeListItemViewHolder Builder Function
            fun from(parent: ViewGroup): FacilityPaymentModeListItemViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding =
                    FacilityPaymentmodeListItemBinding.inflate(layoutInflater, parent, false)

                return FacilityPaymentModeListItemViewHolder(binding)
            }
        }

        fun bind(facilityPaymentModeListItem: FacilityPaymentModeListItem) {
            binding.facilityPaymentModeListItem = facilityPaymentModeListItem

            // Check whether or not secondItem at facilityPaymentModeListItem is null
            // If it is not, Make Second Item Text View Visible
            facilityPaymentModeListItem.secondItem?.let {
                binding.secondItemName.text = it.name
            }

            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return FacilityPaymentModeListItemViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is FacilityPaymentModeListItemViewHolder -> {
                holder.bind(getItem(position))
            }
        }
    }

    fun <T : AbstractFacilityPaymentMode> addList(facilityPaymentModeList: List<T>?) {
        val modifiedItems = ArrayList<FacilityPaymentModeListItem>()
        facilityPaymentModeList?.let {
            // Transform facilityPaymentMode List to FacilityPaymentModeListItem List
            // by traversing through facilityPaymentMode List 2 items at a time (processing them 2 by 2)
            for (index in it.indices step 2) {
                if (index == it.lastIndex) {
                    // On Last Index, add FacilityPaymentModeListItem with null secondItem
                    modifiedItems.add(FacilityPaymentModeListItem(it.get(index), null))
                } else {
                    modifiedItems.add(FacilityPaymentModeListItem(it.get(index), it.get(index + 1)))
                }
            }
        }

        adapterScope.launch {
            withContext(Dispatchers.Main) {
                submitList(modifiedItems)
            }
        }
    }

}