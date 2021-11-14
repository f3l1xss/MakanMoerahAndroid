package com.felixstanley.makanmoerahandroid.fragment.explore

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.felixstanley.makanmoerahandroid.databinding.RestaurantListItemBinding
import com.felixstanley.makanmoerahandroid.entity.restaurant.Restaurant
import com.felixstanley.makanmoerahandroid.view.restaurantcardcarousel.RestaurantCardAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RestaurantListItemAdapter :
    ListAdapter<Restaurant, RecyclerView.ViewHolder>(RestaurantCardAdapter.RestaurantDiffCallback()) {

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    class RestaurantListItemHolder private constructor(val binding: RestaurantListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        companion object {
            // RestaurantListItemHolder Builder Function
            fun from(parent: ViewGroup): RestaurantListItemHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RestaurantListItemBinding.inflate(layoutInflater, parent, false)

                // Modify Each List Item Height to be 50 percent of Parent's Height
                val view = binding.root
                val restaurantListItemHeight = (parent.height * 0.5).toInt()
                val marginLayoutParams = ViewGroup.MarginLayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    restaurantListItemHeight
                )
                marginLayoutParams.setMargins(16, 16, 16, 16)
                view.layoutParams = marginLayoutParams

                // Modify Each List Item Top Part Height to be 60 percent of
                // restaurantListItem's Height
                val topPart = binding.restaurantListItemTopPart
                topPart.layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, (restaurantListItemHeight * 0.6).toInt()
                )

                // Modify Each List Item Bottom Part Height to be 40 percent of
                // restaurantListItem's Height
                val bottomPart = binding.restaurantListItemBottomPart
                val bottomPartHeight = (restaurantListItemHeight * 0.4).toInt()
                bottomPart.layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, bottomPartHeight
                )

                // Modify Restaurant Name Height to match 40% Bottom Part Height
                val restaurantName = binding.restaurantListItemName
                restaurantName.layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, (bottomPartHeight * 0.4).toInt()
                )

                // Modify Address City & Rating Horizontal Tab Height to match 30% Bottom Part Height
                val addressCityRatingTab = binding.restaurantListItemAddressCityRatingTab
                addressCityRatingTab.layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, (bottomPartHeight * 0.3).toInt()
                )

                // Modify Food Category & Price Horizontal Tab Height to match 30% Bottom Part Height
                val foodCategoryPriceTab = binding.restaurantListItemFoodCategoryPriceTab
                foodCategoryPriceTab.layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, (bottomPartHeight * 0.3).toInt()
                )

                return RestaurantListItemHolder(binding)
            }
        }

        fun bind(restaurant: Restaurant) {
            binding.restaurant = restaurant
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return RestaurantListItemHolder.from(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is RestaurantListItemHolder -> {
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