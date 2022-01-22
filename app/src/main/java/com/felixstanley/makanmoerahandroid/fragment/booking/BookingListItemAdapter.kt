package com.felixstanley.makanmoerahandroid.fragment.booking

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.felixstanley.makanmoerahandroid.databinding.BookingListItemBinding
import com.felixstanley.makanmoerahandroid.databinding.NoBookingAvailableHeaderBinding
import com.felixstanley.makanmoerahandroid.entity.booking.Booking
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val ITEM_VIEW_TYPE_BOOKING = 0
private const val ITEM_VIEW_TYPE_NO_BOOKING_AVAILABLE_HEADER = 1

class BookingListItemAdapter : ListAdapter<BookingListItemDataItem, RecyclerView.ViewHolder>(
    BookingListItemDataItemDiffCallback()
) {

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    class BookingListItemDataItemDiffCallback : DiffUtil.ItemCallback<BookingListItemDataItem>() {

        override fun areItemsTheSame(
            oldItem: BookingListItemDataItem,
            newItem: BookingListItemDataItem
        ): Boolean {
            return oldItem.booking?.id == newItem.booking?.id
        }

        override fun areContentsTheSame(
            oldItem: BookingListItemDataItem,
            newItem: BookingListItemDataItem
        ): Boolean {
            // Since Booking is a data class, equals sign will compare all fields
            // declared at constructor params
            return oldItem.booking == newItem.booking
        }
    }

    // No Booking Available Header View Holder
    class NoBookingAvailableHeaderViewHolder private constructor(val binding: NoBookingAvailableHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        companion object {
            // NoBookingAvailableHeaderViewHolder Builder Function
            fun from(parent: ViewGroup): NoBookingAvailableHeaderViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = NoBookingAvailableHeaderBinding.inflate(layoutInflater, parent, false)
                return NoBookingAvailableHeaderViewHolder(binding)
            }
        }
    }

    class BookingListItemViewHolder private constructor(val binding: BookingListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        companion object {
            // BookingListItemViewHolder Builder Function
            fun from(parent: ViewGroup): BookingListItemViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = BookingListItemBinding.inflate(layoutInflater, parent, false)

                // Modify Each List Item Height to be 80 percent of Parent's Height
                val view = binding.root
                val bookingListItemHeight = (parent.height * 0.8).toInt()
                val marginLayoutParams = ViewGroup.MarginLayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    bookingListItemHeight
                )
                marginLayoutParams.setMargins(16, 16, 16, 16)
                view.layoutParams = marginLayoutParams

                // Modify Each List Item Top Part Height to be 40 percent of
                // bookingListItem's Height
                val topPart = binding.bookingListItemTopPart
                topPart.layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, (bookingListItemHeight * 0.4).toInt()
                )

                // Modify Each List Item Middle Part Height to be 30 percent of
                // bookingListItem's Height
                val middlePart = binding.bookingListItemMiddlePart
                val middlePartHeight = (bookingListItemHeight * 0.3).toInt()
                middlePart.layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, middlePartHeight
                )

                // Modify Each List Item Bottom Part Height to be 30 percent of
                // bookingListItem's Height
                val bottomPart = binding.bookingListItemBottomPart
                val bottomPartHeight = (bookingListItemHeight * 0.3).toInt()
                bottomPart.layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, bottomPartHeight
                )

                // Modify Restaurant Name Height to match 40% Middle Part Height
                val restaurantName = binding.bookingListItemRestaurantName
                restaurantName.layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, (middlePartHeight * 0.4).toInt()
                )

                // Modify Address City & Rating Horizontal Tab Height to match 30% Middle Part Height
                val addressCityRatingTab = binding.bookingListItemRestaurantAddressCityRatingTab
                addressCityRatingTab.layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, (middlePartHeight * 0.3).toInt()
                )

                // Modify Food Category & Price Horizontal Tab Height to match 30% Middle Part Height
                val foodCategoryPriceTab = binding.bookingListItemRestaurantFoodCategoryPriceTab
                foodCategoryPriceTab.layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, (middlePartHeight * 0.3).toInt()
                )

                // Modify Booking Confirmed Date & Timeslot Height to match 30% Bottom Part Height
                val bookingConfirmedDateTimeslot = binding.bookingListItemConfirmedDateTimeslot
                bookingConfirmedDateTimeslot.layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, (bottomPartHeight * 0.3).toInt()
                )

                // Modify Booking Details Tab Height to match 70% Bottom Part Height
                val bookingDetailsTab = binding.bookingListItemBookingDetailsTab
                bookingDetailsTab.layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, (bottomPartHeight * 0.7).toInt()
                )

                return BookingListItemViewHolder(binding)
            }
        }

        fun bind(booking: Booking) {
            binding.booking = booking
            binding.executePendingBindings()
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_BOOKING -> BookingListItemViewHolder.from(parent)
            ITEM_VIEW_TYPE_NO_BOOKING_AVAILABLE_HEADER -> NoBookingAvailableHeaderViewHolder.from(
                parent
            )
            else -> throw ClassCastException("Unknown viewType ${viewType}")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is BookingListItemViewHolder -> {
                getItem(position).booking?.let { holder.bind(it) }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is BookingListItemDataItem.BookingDataitem -> ITEM_VIEW_TYPE_BOOKING
            is BookingListItemDataItem.NoBookingAvailableHeader -> ITEM_VIEW_TYPE_NO_BOOKING_AVAILABLE_HEADER
        }
    }

    fun addList(bookingList: List<Booking>?) {
        // If Booking List is Empty / null, display No Booking Available Header
        val modifiedItems = if (bookingList == null || bookingList.isEmpty())
            listOf(BookingListItemDataItem.NoBookingAvailableHeader)
        else
            bookingList.map { BookingListItemDataItem.BookingDataitem(it) }

        adapterScope.launch {
            withContext(Dispatchers.Main) {
                submitList(modifiedItems)
            }
        }
    }

}

sealed class BookingListItemDataItem {

    abstract val booking: Booking?

    data class BookingDataitem(private val someBooking: Booking) : BookingListItemDataItem() {
        override val booking = someBooking
    }

    object NoBookingAvailableHeader : BookingListItemDataItem() {
        // override booking value to null as it is not used
        override val booking: Nothing? = null
    }
}
