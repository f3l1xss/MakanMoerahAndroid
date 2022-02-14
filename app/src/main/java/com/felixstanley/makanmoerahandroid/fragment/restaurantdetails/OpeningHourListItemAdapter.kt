package com.felixstanley.makanmoerahandroid.fragment.restaurantdetails

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.felixstanley.makanmoerahandroid.databinding.OpeningHourListItemBinding
import com.felixstanley.makanmoerahandroid.entity.restaurant.RestaurantHours
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OpeningHourListItemAdapter :
    ListAdapter<RestaurantHours, RecyclerView.ViewHolder>(RestaurantHoursDiffCallback()) {

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    class RestaurantHoursDiffCallback : DiffUtil.ItemCallback<RestaurantHours>() {
        override fun areItemsTheSame(oldItem: RestaurantHours, newItem: RestaurantHours): Boolean {
            return oldItem.dayOfWeek == newItem.dayOfWeek
        }

        override fun areContentsTheSame(
            oldItem: RestaurantHours,
            newItem: RestaurantHours
        ): Boolean {
            // Since Restaurant Hours is a data class, equals sign will compare all fields
            // declared at constructor params
            return oldItem == newItem
        }
    }

    class OpeningHourListItemViewHolder private constructor(val binding: OpeningHourListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        companion object {
            // OpeningHourListItemViewHolder Builder Function
            fun from(parent: ViewGroup): OpeningHourListItemViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = OpeningHourListItemBinding.inflate(layoutInflater, parent, false)

                return OpeningHourListItemViewHolder(binding)
            }
        }

        fun bind(restaurantHours: RestaurantHours) {
            binding.restaurantHours = restaurantHours
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return OpeningHourListItemViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is OpeningHourListItemViewHolder -> {
                holder.bind(getItem(position))
            }
        }
    }

    fun addList(RestaurantHoursList: List<RestaurantHours>?) {
        adapterScope.launch {
            withContext(Dispatchers.Main) {
                submitList(RestaurantHoursList)
            }
        }
    }

}