package com.felixstanley.makanmoerahandroid.fragment.explore

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.felixstanley.makanmoerahandroid.databinding.NoRestaurantFoundHeaderBinding
import com.felixstanley.makanmoerahandroid.databinding.RestaurantListItemBinding
import com.felixstanley.makanmoerahandroid.entity.restaurant.Restaurant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val ITEM_VIEW_TYPE_RESTAURANT = 0
private const val ITEM_VIEW_TYPE_NO_RESTAURANT_FOUND_HEADER = 1

class RestaurantListItemAdapter :
    ListAdapter<RestaurantListItemDataItem, RecyclerView.ViewHolder>(
        RestaurantListItemDataItemDiffCallback()
    ) {

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    class RestaurantListItemDataItemDiffCallback :
        DiffUtil.ItemCallback<RestaurantListItemDataItem>() {
        override fun areItemsTheSame(
            oldItem: RestaurantListItemDataItem,
            newItem: RestaurantListItemDataItem
        ): Boolean {
            return oldItem.restaurant?.id == newItem.restaurant?.id
        }

        override fun areContentsTheSame(
            oldItem: RestaurantListItemDataItem,
            newItem: RestaurantListItemDataItem
        ): Boolean {
            // Since Restaurant is a data class, equals sign will compare all fields
            // declared at constructor params
            return oldItem.restaurant == newItem.restaurant
        }
    }

    // No Restaurant Found Header View Holder
    class NoRestaurantFoundHeaderViewHolder private constructor(val binding: NoRestaurantFoundHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        companion object {
            // NoRestaurantFoundHeaderViewHolder Builder Function
            fun from(parent: ViewGroup): NoRestaurantFoundHeaderViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding =
                    NoRestaurantFoundHeaderBinding.inflate(layoutInflater, parent, false)
                return NoRestaurantFoundHeaderViewHolder(binding)
            }
        }
    }

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
        return when (viewType) {
            ITEM_VIEW_TYPE_RESTAURANT -> RestaurantListItemHolder.from(parent)
            ITEM_VIEW_TYPE_NO_RESTAURANT_FOUND_HEADER -> NoRestaurantFoundHeaderViewHolder.from(
                parent
            )
            else -> throw ClassCastException("Unknown viewType ${viewType}")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is RestaurantListItemHolder -> {
                getItem(position).restaurant?.let { holder.bind(it) }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is RestaurantListItemDataItem.RestaurantDataItem -> ITEM_VIEW_TYPE_RESTAURANT
            is RestaurantListItemDataItem.NoRestaurantFoundHeader -> ITEM_VIEW_TYPE_NO_RESTAURANT_FOUND_HEADER
        }
    }

    fun addList(restaurantList: List<Restaurant>?) {
        // If Restaurant List is Empty / null, display No Restaurant Found Header
        val modifiedItems = if (restaurantList == null || restaurantList.isEmpty())
            listOf(RestaurantListItemDataItem.NoRestaurantFoundHeader)
        else
            restaurantList.map { RestaurantListItemDataItem.RestaurantDataItem(it) }

        adapterScope.launch {
            withContext(Dispatchers.Main) {
                submitList(modifiedItems)
            }
        }
    }

}

sealed class RestaurantListItemDataItem {

    abstract val restaurant: Restaurant?

    data class RestaurantDataItem(private val someRestaurant: Restaurant) :
        RestaurantListItemDataItem() {
        override val restaurant = someRestaurant
    }

    object NoRestaurantFoundHeader : RestaurantListItemDataItem() {
        // override restaurant value to null as it is not used
        override val restaurant: Nothing? = null
    }
}