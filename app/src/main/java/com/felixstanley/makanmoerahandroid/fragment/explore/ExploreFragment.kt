package com.felixstanley.makanmoerahandroid.fragment.explore

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.felixstanley.makanmoerahandroid.R
import com.felixstanley.makanmoerahandroid.constants.Constants
import com.felixstanley.makanmoerahandroid.databinding.FragmentExploreBinding
import com.felixstanley.makanmoerahandroid.fragment.AbstractFragment
import com.felixstanley.makanmoerahandroid.network.api.NetworkApi
import com.google.android.material.bottomsheet.BottomSheetBehavior
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.util.*

class ExploreFragment : AbstractFragment() {

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
        val binding = FragmentExploreBinding.inflate(inflater)

        // Initialize ViewModelFactory & ViewModel
        viewModelFactory = ExploreViewModelFactory(NetworkApi.restaurantService)
        viewModel = ViewModelProvider(this, viewModelFactory)
            .get(ExploreViewModel::class.java)

        // Initialize Data Binding Variables
        // TODO: Find out if we can use context for layout manager below
        val manager = GridLayoutManager(context, 1, GridLayoutManager.VERTICAL, false)
        val adapter = RestaurantListItemAdapter()
        binding.lifecycleOwner = this
        binding.exploreViewModel = viewModel
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

        // Observe to ViewModel Live Data Variables
        viewModel.restaurantEntitiesPage.observe(viewLifecycleOwner, { it ->
            val oldList = adapter.currentList.toMutableList()
            // Append New Restaurants to oldList
            oldList.addAll(it.entities)
            adapter.addList(oldList)
        })
        viewModel.newDataSetFetched.observe(viewLifecycleOwner, { it ->
            if (it == true) {
                // New Data Set is ready, Set Loading Flag to false
                // So that Endless Scrolling is allowed again
                loading = false
                viewModel.resetNewDataSetFetchedFlag()
            }
        })

        // Initialize Bottom Sheet Behavior
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

        // Initialize City Filter RecyclerViews
        val cityFilterRadioButtonAdapter = CityFilterRadioButtonAdapter()
        binding.restaurantFilterBottomSheetCityFilters.adapter = cityFilterRadioButtonAdapter
        binding.restaurantFilterBottomSheetCityFilters.layoutManager =
            GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
        viewModel.cityFilters.observe(viewLifecycleOwner, { it ->
            cityFilterRadioButtonAdapter.addList(it)
        })

        // Initialize District Filter RecyclerViews
        val districtFilterCheckboxAdapter = DistrictFoodCategoryCheckBoxAdapter()
        binding.restaurantFilterBottomSheetDistrictFilters.adapter = districtFilterCheckboxAdapter
        binding.restaurantFilterBottomSheetDistrictFilters.layoutManager =
            GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
        viewModel.districtFilters.observe(viewLifecycleOwner, { it ->
            districtFilterCheckboxAdapter.addList(it)
        })

        // Initialize Food Category Filter RecyclerViews
        val foodCategoryFilterCheckboxAdapter = DistrictFoodCategoryCheckBoxAdapter()
        binding.restaurantFilterBottomSheetFoodCategoryFilters.adapter =
            foodCategoryFilterCheckboxAdapter
        binding.restaurantFilterBottomSheetFoodCategoryFilters.layoutManager =
            GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
        viewModel.foodCategoryFilters.observe(viewLifecycleOwner, { it ->
            foodCategoryFilterCheckboxAdapter.addList(it)
        })

        // Adjust Price Slider Label Formatter
        val priceSlider = binding.restaurantFilterBottomSheetPriceSlider
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

        // Adjust Discount Slider Label Formatter
        val discountSlider = binding.restaurantFilterBottomSheetDiscountSlider
        discountSlider.setLabelFormatter { labelValue ->
            "${labelValue.toInt()}%"
        }

        // Adjust Rating Slider Label Formatter
        val ratingSlider = binding.restaurantFilterBottomSheetRatingSlider
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

        // Initialize Date Edit Text to show Date Picker Dialog Upon Click
        restaurantFilterBottomSheetDateEditText = binding.restaurantFilterBottomSheetDateEditText
        val datePickerDialogListener =
            DatePickerDialog.OnDateSetListener { view, year, month, day ->
                dateEditTextCalendar.set(year, month, day)
                updateDateEditText()
            }
        restaurantFilterBottomSheetDateEditText.setOnClickListener { it ->
            // Show Date Picker Dialog Upon Edit Text Click
            DatePickerDialog(
                requireContext(),
                datePickerDialogListener,
                dateEditTextCalendar.get(Calendar.YEAR),
                dateEditTextCalendar.get(Calendar.MONTH),
                dateEditTextCalendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
        // Update Date Edit Text Once to set Text as current Date
        updateDateEditText()

        // Initialize Time Edit Text to Show Time Picker Dialog Upon Click
        restaurantFilterBottomSheetTimeEditText = binding.restaurantFilterBottomSheetTimeEditText
        val timePickerDialogListener = TimePickerDialog.OnTimeSetListener { view, hour, minute ->
            timeEditTextTime = timeEditTextTime.withHour(hour).withMinute(minute)
            updateTimeEditText()
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

        // Initialize Options Menu
        setHasOptionsMenu(true)
        return binding.root
    }

    private fun updateDateEditText() {
        // Update Date Edit Text Content to current value of dateEditTextCalendar
        restaurantFilterBottomSheetDateEditText.setText(dateFormat.format(dateEditTextCalendar.time))
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