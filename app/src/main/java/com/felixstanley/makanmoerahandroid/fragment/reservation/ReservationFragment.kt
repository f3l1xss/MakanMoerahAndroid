package com.felixstanley.makanmoerahandroid.fragment.reservation

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.felixstanley.makanmoerahandroid.R
import com.felixstanley.makanmoerahandroid.databinding.FragmentReservationBinding
import com.felixstanley.makanmoerahandroid.entity.enums.BookingStatus
import com.felixstanley.makanmoerahandroid.fragment.AbstractFragment
import com.felixstanley.makanmoerahandroid.fragment.booking.BookingListItemAdapter
import com.felixstanley.makanmoerahandroid.network.api.NetworkApi
import com.felixstanley.makanmoerahandroid.utility.Utility
import com.google.android.material.button.MaterialButtonToggleGroup
import java.util.*

class ReservationFragment : AbstractFragment() {

    private lateinit var binding: FragmentReservationBinding
    private lateinit var viewModel: ReservationViewModel
    private lateinit var viewModelFactory: ReservationViewModelFactory

    private lateinit var datePickerEditText: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate Reservation Fragment
        binding = FragmentReservationBinding.inflate(inflater)

        // Initialize ViewModelFactory & ViewModel
        viewModelFactory = ReservationViewModelFactory(NetworkApi.bookingService)
        viewModel = ViewModelProvider(this, viewModelFactory).get(ReservationViewModel::class.java)

        // Initialize Data Binding Variables
        binding.lifecycleOwner = this
        binding.reservationViewModel = viewModel

        initializeReservationListRecyclerView()
        initializeBookingCategoryToggleButtonGroup()
        initializeDateEditText()

        return binding.root
    }

    private fun initializeReservationListRecyclerView() {
        val manager = GridLayoutManager(context, 1, GridLayoutManager.VERTICAL, false)
        val adapter = BookingListItemAdapter()

        binding.reservationList.adapter = adapter
        binding.reservationList.layoutManager = manager

        // Initialize RecyclerView OnScrollListener
        // to implement endless / infinite scrolling
        var loading = false
        binding.reservationList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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
                adapter.addList(oldList, false)
            } else {
                adapter.addList(it.entities, false)
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
            // Only Care about Checked Event
            if (isChecked) {
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

    private fun initializeDateEditText() {
        // Initialize Date Edit Text to show Date Picker Dialog Upon Click
        datePickerEditText = binding.datePickerEditText
        val datePickerDialogListener =
            DatePickerDialog.OnDateSetListener { view, year, month, day ->
                viewModel.setDateEditTextCalendar(year, month, day)
                updateDateEditText()

                // Also Update ViewModel CurrentDate
                viewModel.updateCurrentDate(Utility.toLocalDate(viewModel.dateEditTextCalendar))
            }
        datePickerEditText.setOnClickListener { it ->
            // Show Date Picker Dialog Upon Edit Text Click
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                datePickerDialogListener,
                viewModel.dateEditTextCalendar.get(Calendar.YEAR),
                viewModel.dateEditTextCalendar.get(Calendar.MONTH),
                viewModel.dateEditTextCalendar.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.show()
        }
        // Update Date Edit Text Once to sync latest value from viewModel
        updateDateEditText()
    }

    private fun updateDateEditText() {
        // Update Date Edit Text Content to current value of dateEditTextCalendar
        datePickerEditText.setText(viewModel.getEditTextCalendarFormattedDate())
    }

}