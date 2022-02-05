package com.felixstanley.makanmoerahandroid.view.restaurantcardcarousel

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.felixstanley.makanmoerahandroid.databinding.RestaurantCardBinding
import com.felixstanley.makanmoerahandroid.entity.restaurant.Restaurant
import com.felixstanley.makanmoerahandroid.fragment.home.HomeFragmentDirections
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RestaurantCardAdapter :
    ListAdapter<Restaurant, RecyclerView.ViewHolder>(RestaurantDiffCallback()) {

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    class RestaurantDiffCallback : DiffUtil.ItemCallback<Restaurant>() {
        override fun areItemsTheSame(oldItem: Restaurant, newItem: Restaurant): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Restaurant, newItem: Restaurant): Boolean {
            // Since Restaurant is a data class, equals sign will compare all fields
            // declared at constructor params
            return oldItem == newItem
        }
    }

    class RestaurantCardViewHolder private constructor(val binding: RestaurantCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        companion object {
            // RestaurantCardViewHolder Builder Function
            fun from(parent: ViewGroup): RestaurantCardViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RestaurantCardBinding.inflate(layoutInflater, parent, false)

                // Modify Each Card Width to be 60 percent of Parent's Width
                val view = binding.root
                val marginLayoutParams = ViewGroup.MarginLayoutParams(
                    (parent.width * 0.6).toInt(),
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                marginLayoutParams.setMargins(16, 16, 16, 16)
                view.layoutParams = marginLayoutParams

                return RestaurantCardViewHolder(binding)
            }
        }

        fun bind(restaurant: Restaurant) {
            binding.restaurant = restaurant

            // Set RestaurantCard OnClickListener To Navigate to Restaurant Details Fragment
            binding.restaurantCard.setOnClickListener { view: View ->
                view.findNavController()
                    .navigate(
                        HomeFragmentDirections.actionHomeFragmentToRestaurantDetailsFragment(
                            restaurant.id
                        )
                    )
            }
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return RestaurantCardViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is RestaurantCardViewHolder -> {
                holder.bind(getItem(position))
            }
        }
    }

    fun addList(restaurantList: List<Restaurant>?) {
        adapterScope.launch {
            withContext(Dispatchers.Main) {
                submitList(restaurantList)
            }
        }
    }

}