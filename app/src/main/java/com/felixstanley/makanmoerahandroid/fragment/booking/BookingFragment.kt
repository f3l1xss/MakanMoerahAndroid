package com.felixstanley.makanmoerahandroid.fragment.booking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.felixstanley.makanmoerahandroid.R
import com.felixstanley.makanmoerahandroid.databinding.FragmentBookingBinding
import com.felixstanley.makanmoerahandroid.entity.enums.BookingStatus
import com.felixstanley.makanmoerahandroid.fragment.AbstractFragment
import com.felixstanley.makanmoerahandroid.network.api.NetworkApi
import com.google.android.material.button.MaterialButtonToggleGroup

class BookingFragment : AbstractFragment() {

    private lateinit var binding: FragmentBookingBinding
    private lateinit var viewModel: BookingViewModel
    private lateinit var viewModelFactory: BookingViewModelFactory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate Booking Fragment
        binding = FragmentBookingBinding.inflate(inflater)

        // Initialize ViewModelFactory & ViewModel
        viewModelFactory = BookingViewModelFactory(NetworkApi.bookingService)
        viewModel = ViewModelProvider(this, viewModelFactory).get(BookingViewModel::class.java)

        // Initialize Data Binding Variables
        binding.lifecycleOwner = this
        binding.bookingViewModel = viewModel

        initializeBookingListRecyclerView()
        initializeBookingCategoryToggleButtonGroup()

        return binding.root
    }

    private fun initializeBookingListRecyclerView() {
        val manager = GridLayoutManager(context, 1, GridLayoutManager.VERTICAL, false)
        val adapter = BookingListItemAdapter()

        binding.bookingList.adapter = adapter
        binding.bookingList.layoutManager = manager

        // Initialize RecyclerView OnScrollListener
        // to implement endless / infinite scrolling
        var loading = false
        binding.bookingList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                // Reached Bottom Of List, Fetch New Set of Items if Available
                if (!loading && !recyclerView.canScrollVertically(1)) {
                    loading = true
                    viewModel.getNextDataSet()
                }
            }
        })

        // Observe to ViewModel Live Data Variable
        viewModel.bookingEntitiesPage.observe(viewLifecycleOwner, { it ->
            // If Refresh Is Triggered By Infinite Scroll, Append New Booking to Old List.
            // Otherwise (Due to Booking Category Change) overwrite old List and go to page 1
            if (viewModel.isRefreshTriggeredByInfiniteScroll()) {
                // Reset Refresh Triggered By Infinite Scroll Flag
                viewModel.resetRefreshTriggeredByInfiniteScroll()
                val oldList = adapter.currentList.toList().map { it.booking!! }.toMutableList()
                // Append New Bookings to oldList
                oldList.addAll(it.entities)
                adapter.addList(oldList)
            } else {
                adapter.addList(it.entities)
            }

            // New Data Set is ready, Set Loading Flag to false
            // So that Endless Scrolling is allowed again
            loading = false
        })
    }

    private fun initializeBookingCategoryToggleButtonGroup() {
        // Add Booking Category Toggle Button On Click Listener
        val bookingCategoryToggleButtonGroup = binding.bookingCategoryToggleButtonGroup
        bookingCategoryToggleButtonGroup.addOnButtonCheckedListener { group: MaterialButtonToggleGroup, checkedId: Int, isChecked: Boolean ->
            val selectedBookingCategory = when (checkedId) {
                R.id.booking_category_ongoing_toggle_button -> BookingStatus.ONGOING
                R.id.booking_category_cancelled_toggle_button -> BookingStatus.CANCELLED
                R.id.booking_category_completed_toggle_button -> BookingStatus.COMPLETED
                else -> BookingStatus.ONGOING
            }
            // Update ViewModel Current Booking Category Upon Toggle Button Click
            viewModel.updateCurrentBookingCategory(selectedBookingCategory)
        }
    }

}