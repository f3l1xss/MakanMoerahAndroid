package com.felixstanley.makanmoerahandroid.fragment.explore

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.Editable
import android.view.*
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.RadioGroup
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.felixstanley.makanmoerahandroid.R
import com.felixstanley.makanmoerahandroid.constants.Constants
import com.felixstanley.makanmoerahandroid.databinding.FragmentExploreBinding
import com.felixstanley.makanmoerahandroid.entity.enums.SortCriteria
import com.felixstanley.makanmoerahandroid.fragment.AbstractFragment
import com.felixstanley.makanmoerahandroid.network.api.NetworkApi
import com.felixstanley.makanmoerahandroid.utility.Utility
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.slider.RangeSlider
import com.google.android.material.slider.Slider
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.util.*

class ExploreFragment : AbstractFragment() {

    private lateinit var binding: FragmentExploreBinding
    private lateinit var viewModel: ExploreViewModel
    private lateinit var viewModelFactory: ExploreViewModelFactory

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private lateinit var restaurantFilterBottomSheetDateEditText: EditText
    private lateinit var restaurantFilterBottomSheetTimeEditText: EditText

    // Stores Latest Value of Date selected via Date Edit Text
    private val dateEditTextCalendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("dd-MM-yyyy")

    // Stores Latest Value of Time selected via Time Edit Text
    private var timeEditTextTime = LocalTime.now()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate Explore Fragment
        binding = FragmentExploreBinding.inflate(inflater)

        // Initialize ViewModelFactory & ViewModel
        viewModelFactory = ExploreViewModelFactory(NetworkApi.restaurantService)
        viewModel = ViewModelProvider(this, viewModelFactory)
            .get(ExploreViewModel::class.java)

        // Initialize Data Binding Variables
        binding.lifecycleOwner = this
        binding.exploreViewModel = viewModel

        initializeRestaurantListRecyclerView()
        initializeBottomSheetBehavior()
        initializeSortRadioGroup()
        initializeCityFilterRecyclerView()
        initializeDistrictFilterRecyclerView()
        initializeFoodCategoryFilterRecyclerView()
        initializePriceSlider()
        initializeDiscountSlider()
        initializeRatingSlider()
        initializeNumOfPeopleDropdown()
        initializeDateEditText()
        initializeTimeEditText()

        // Initialize Options Menu
        setHasOptionsMenu(true)
        return binding.root
    }

    private fun initializeRestaurantListRecyclerView() {
        // TODO: Find out if we can use context for layout manager below
        val manager = GridLayoutManager(context, 1, GridLayoutManager.VERTICAL, false)
        val adapter = RestaurantListItemAdapter()

        binding.restaurantList.adapter = adapter
        binding.restaurantList.layoutManager = manager

        // Initialize RecyclerView OnScrollListener
        // to implement endless / infinite scrolling
        var loading = false
        binding.restaurantList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                // Reached Bottom Of List, Fetch New Set of Items if Available
                if (!loading && !recyclerView.canScrollVertically(1)) {
                    loading = true
                    viewModel.getNextDataSet()
                }
            }
        })

        // Observe Adapter Data to implement scroll to top upon data load finish
        // (Scrolling to top cannot be done after adapter.submitList as there will be race
        // condition (adapter.submitList() is executed asynchronously))
        adapter.registerAdapterDataObserver(
            RestaurantListItemAdapterDataObserver(
                viewModel,
                manager
            )
        )

        // Observe to ViewModel Live Data Variables
        viewModel.restaurantEntitiesPage.observe(viewLifecycleOwner, { it ->
            // If Refresh Is Triggered By Infinite Scroll, Append New Restaurants to Old List.
            // Otherwise (Due to Apply filter button) overwrite old List and go to page 1
            if (viewModel.isRefreshTriggeredByInfiniteScroll()) {
                // Reset Refresh Triggered By Infinite Scroll Flag
                viewModel.resetRefreshTriggeredByInfiniteScroll()
                val oldList = adapter.currentList.toList().map { it.restaurant!! }.toMutableList()
                // Append New Restaurants to oldList
                oldList.addAll(it.entities)
                adapter.addList(oldList)
            } else {
                adapter.addList(it.entities)
            }

            // New Data Set is ready, Set Loading Flag to false
            // So that Endless Scrolling is allowed again
            loading = false
            // Also Hide Bottom Sheet & Scroll Bottom Sheet to the top whenever New Data is fetched
            hideBottomSheet()
            binding.restaurantFilterBottomSheetContentNestedScrollView.scrollTo(0, 0)
        })
    }

    private fun initializeBottomSheetBehavior() {
        val restaurantFilterBottomSheet = binding.restaurantFilterBottomSheet
        bottomSheetBehavior = BottomSheetBehavior.from(restaurantFilterBottomSheet)
        bottomSheetBehavior.isHideable = true
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        // Set Skip Collapsed to True as we don't need Half Expanded & Collapsed State
        bottomSheetBehavior.skipCollapsed = true
        viewModel.hideBottomSheet.observe(viewLifecycleOwner, { it ->
            if (it == true) {
                // Bottom Sheet Close Button Is Clicked, Hide Bottom Sheet
                hideBottomSheet()
                viewModel.resetHideBottomSheetFlag()
            }
        })
    }

    private fun initializeSortRadioGroup() {
        // Add Sort Radio Group On Click Listener
        val sortRadioGroup = binding.restaurantFilterBottomSheetSortRadioGroup
        sortRadioGroup.setOnCheckedChangeListener { radioGroup: RadioGroup, checkedId: Int ->
            val selectedSortCriteria = when (checkedId) {
                R.id.restaurant_filter_bottom_sheet_sort_radio_button_cheapest -> SortCriteria.CHEAPEST
                R.id.restaurant_filter_bottom_sheet_sort_radio_button_most_rating -> SortCriteria.MOST_RATING
                R.id.restaurant_filter_bottom_sheet_sort_radio_button_a_to_z -> SortCriteria.ALPHABET
                R.id.restaurant_filter_bottom_sheet_sort_radio_button_most_discount -> SortCriteria.MOST_DISCOUNT
                R.id.restaurant_filter_bottom_sheet_sort_radio_button_nearest -> SortCriteria.NEAREST
                else -> SortCriteria.NEAREST
            }
            // Update ViewModel Current Sort according to checked Radio Button
            viewModel.updateCurrentSort(selectedSortCriteria)
        }
    }

    private fun initializeCityFilterRecyclerView() {
        val cityFilterRadioButtonAdapter =
            CityFilterRadioButtonAdapter { buttonView: CompoundButton, isChecked: Boolean ->
                // Update ViewModel Current City ID upon Checkbox Change
                viewModel.updateCurrentCityId(buttonView.text.toString(), isChecked)
            }
        binding.restaurantFilterBottomSheetCityFilters.adapter = cityFilterRadioButtonAdapter
        binding.restaurantFilterBottomSheetCityFilters.layoutManager =
            GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
        viewModel.cityFilters.observe(viewLifecycleOwner, { it ->
            cityFilterRadioButtonAdapter.addList(it)
        })
    }

    private fun initializeDistrictFilterRecyclerView() {
        val districtFilterCheckboxAdapter =
            DistrictFoodCategoryCheckBoxAdapter { buttonView: CompoundButton, isChecked: Boolean ->
                // Update ViewModel Current Districts upon Checkbox Change
                viewModel.updateCurrentDistricts(buttonView.text.toString(), isChecked)
            }
        binding.restaurantFilterBottomSheetDistrictFilters.adapter = districtFilterCheckboxAdapter
        binding.restaurantFilterBottomSheetDistrictFilters.layoutManager =
            GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
        viewModel.districtFilters.observe(viewLifecycleOwner, { it ->
            districtFilterCheckboxAdapter.addList(it, true)
        })
    }

    private fun initializeFoodCategoryFilterRecyclerView() {
        val foodCategoryFilterCheckboxAdapter =
            DistrictFoodCategoryCheckBoxAdapter { buttonView: CompoundButton, isChecked: Boolean ->
                // Update ViewModel Current Food Categories upon Checkbox Change
                viewModel.updateCurrentFoodCategories(buttonView.text.toString(), isChecked)
            }
        binding.restaurantFilterBottomSheetFoodCategoryFilters.adapter =
            foodCategoryFilterCheckboxAdapter
        binding.restaurantFilterBottomSheetFoodCategoryFilters.layoutManager =
            GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
        viewModel.foodCategoryFilters.observe(viewLifecycleOwner, { it ->
            foodCategoryFilterCheckboxAdapter.addList(it, false)
        })
    }

    private fun initializePriceSlider() {
        val priceSlider = binding.restaurantFilterBottomSheetPriceSlider
        // Add Price Slider On Change Listener
        priceSlider.addOnChangeListener { slider: Slider, value: Float, fromUser: Boolean ->
            // Update ViewModel Current Price according to slider value
            viewModel.updatePrice(value.toInt())
        }
        // Adjust Price Slider Label Formatter
        priceSlider.setLabelFormatter { labelValue ->
            when (labelValue.toInt()) {
                1 -> "$"
                2 -> "$$"
                3 -> "$$$"
                4 -> "$$$$"
                5 -> "$$$$$"
                else -> "$$$$$"
            }
        }
    }

    private fun initializeDiscountSlider() {
        val discountSlider = binding.restaurantFilterBottomSheetDiscountSlider
        // Add Discount Slider On Change Listener
        discountSlider.addOnChangeListener { slider: RangeSlider, value: Float, fromUser: Boolean ->
            // Update ViewModel Current Discount according to slider value
            viewModel.updateDiscount(slider.values[0].toInt(), slider.values[1].toInt())
        }
        // Adjust Discount Slider Label Formatter
        discountSlider.setLabelFormatter { labelValue ->
            "${labelValue.toInt()}%"
        }
    }

    private fun initializeRatingSlider() {
        val ratingSlider = binding.restaurantFilterBottomSheetRatingSlider
        // Add Rating Slider On Change Listener
        ratingSlider.addOnChangeListener { slider: RangeSlider, value: Float, fromUser: Boolean ->
            // Update ViewModel Current Rating according to slider value
            viewModel.updateRating(slider.values[0].toInt(), slider.values[1].toInt())
        }
        // Adjust Rating Slider Label Formatter
        ratingSlider.setLabelFormatter { labelValue ->
            when (labelValue.toInt()) {
                1 -> "★"
                2 -> "★★"
                3 -> "★★★"
                4 -> "★★★★"
                5 -> "★★★★★"
                else -> "★★★★★"
            }
        }
    }

    private fun initializeNumOfPeopleDropdown() {
        // Initialize Number Of People Exposed Dropdown List Item
        val numberOfPeopleItemAdapter =
            ArrayAdapter(
                requireContext(),
                R.layout.num_of_people_list_item,
                Constants.EXPLORE_FRAGMENT_NUM_OF_PEOPLE_DROPDOWN_ITEMS
            )
        val restaurantFilterBottomSheetNumOfPeopleTextView =
            binding.restaurantFilterBottomSheetNumOfPeopleTextView
        restaurantFilterBottomSheetNumOfPeopleTextView.setAdapter(numberOfPeopleItemAdapter)
        // Set Default Text
        restaurantFilterBottomSheetNumOfPeopleTextView.setText("2", false)
        // Add NumOfPeople Exposed Dropdown AfterTextChange Listener
        restaurantFilterBottomSheetNumOfPeopleTextView.doAfterTextChanged { text: Editable? ->
            // Update ViewModel current numOfPeople
            viewModel.updateCurrentNumOfPeople(text.toString().toShort())
        }
    }

    private fun initializeDateEditText() {
        // Initialize Date Edit Text to show Date Picker Dialog Upon Click
        restaurantFilterBottomSheetDateEditText = binding.restaurantFilterBottomSheetDateEditText
        val datePickerDialogListener =
            DatePickerDialog.OnDateSetListener { view, year, month, day ->
                dateEditTextCalendar.set(year, month, day)
                updateDateEditText()

                // Also Update ViewModel CurrentDate
                viewModel.updateCurrentDate(Utility.toLocalDate(dateEditTextCalendar))
            }
        restaurantFilterBottomSheetDateEditText.setOnClickListener { it ->
            // Show Date Picker Dialog Upon Edit Text Click
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                datePickerDialogListener,
                dateEditTextCalendar.get(Calendar.YEAR),
                dateEditTextCalendar.get(Calendar.MONTH),
                dateEditTextCalendar.get(Calendar.DAY_OF_MONTH)
            )
            // Only allow selecting Today's Date onwards
            val datePicker = datePickerDialog.datePicker
            datePicker.minDate = System.currentTimeMillis()
            datePickerDialog.show()
        }
        // Update Date Edit Text Once to set Text as current Date
        updateDateEditText()
    }

    private fun updateDateEditText() {
        // Update Date Edit Text Content to current value of dateEditTextCalendar
        restaurantFilterBottomSheetDateEditText.setText(dateFormat.format(dateEditTextCalendar.time))
    }

    private fun initializeTimeEditText() {
        // Initialize Time Edit Text to Show Time Picker Dialog Upon Click
        restaurantFilterBottomSheetTimeEditText = binding.restaurantFilterBottomSheetTimeEditText
        val timePickerDialogListener = TimePickerDialog.OnTimeSetListener { view, hour, minute ->
            timeEditTextTime = timeEditTextTime.withHour(hour).withMinute(minute)
            updateTimeEditText()

            // Also Update ViewModel CurrentTimeslot
            viewModel.updateCurrentTimeslot(Utility.getTimeslot(timeEditTextTime))
        }
        restaurantFilterBottomSheetTimeEditText.setOnClickListener { it ->
            // Show Time Picker Dialog Upon Edit Text Click
            TimePickerDialog(
                requireContext(),
                timePickerDialogListener,
                timeEditTextTime.hour,
                timeEditTextTime.minute,
                true
            ).show()
        }
        // Update Time Edit Text Once to set Text as current Time
        updateTimeEditText()
    }

    private fun updateTimeEditText() {
        // Prepend with 0 if either hour or minute consist only of single digit
        val hour =
            if (timeEditTextTime.hour >= 10) "${timeEditTextTime.hour}" else "0${timeEditTextTime.hour}"
        val minute =
            if (timeEditTextTime.minute >= 10) "${timeEditTextTime.minute}" else "0${timeEditTextTime.minute}"
        // Update Time Edit Text Content to current value of timeEditTextTime
        restaurantFilterBottomSheetTimeEditText.setText("$hour:$minute")
    }

    private fun expandBottomSheet() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun hideBottomSheet() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.explore_fragment_options_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            // Expand Bottom Sheet when Filter Option Menu is clicked
            R.id.options_menu_filter -> {
                expandBottomSheet()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}