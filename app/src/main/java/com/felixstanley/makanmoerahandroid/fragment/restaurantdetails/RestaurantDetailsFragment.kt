package com.felixstanley.makanmoerahandroid.fragment.restaurantdetails

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.felixstanley.makanmoerahandroid.R
import com.felixstanley.makanmoerahandroid.constants.Constants
import com.felixstanley.makanmoerahandroid.databinding.FragmentRestaurantDetailsBinding
import com.felixstanley.makanmoerahandroid.entity.restaurant.RestaurantCapacity
import com.felixstanley.makanmoerahandroid.fragment.AbstractFragment
import com.felixstanley.makanmoerahandroid.fragment.explore.ExposedDropdownMenu
import com.felixstanley.makanmoerahandroid.network.api.NetworkApi
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class RestaurantDetailsFragment : AbstractFragment() {

    private lateinit var binding: FragmentRestaurantDetailsBinding
    private lateinit var viewModel: RestaurantDetailsViewModel
    private lateinit var viewModelFactory: RestaurantDetailsViewModelFactory

    private lateinit var facilityRecyclerViewAdapter: FacilityPaymentModeListItemAdapter
    private lateinit var paymentModeRecyclerViewAdapter: FacilityPaymentModeListItemAdapter
    private lateinit var hourRecyclerViewAdapter: OpeningHourListItemAdapter
    private lateinit var menuRecyclerViewAdapter: MenuListItemAdapter
    private lateinit var reviewRecyclerViewAdapter: ReviewListItemAdapter

    private lateinit var discountSelectionDropdownMenu: ExposedDropdownMenu

    private lateinit var locationMap: MapView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate RestaurantDetails Fragment
        binding = FragmentRestaurantDetailsBinding.inflate(inflater)

        // Obtain Arguments From Bundle
        val args = RestaurantDetailsFragmentArgs.fromBundle(requireArguments())
        val restaurantId = args.restaurantId

        // Initialize ViewModelFactory & ViewModel
        viewModelFactory =
            RestaurantDetailsViewModelFactory(NetworkApi.restaurantService, restaurantId)
        viewModel =
            ViewModelProvider(this, viewModelFactory).get(RestaurantDetailsViewModel::class.java)

        // Initialize Data Binding Variables
        binding.lifecycleOwner = this
        binding.restaurantDetailsViewModel = viewModel

        initializeRestaurantImageViewLayout(container)
        initializeFacilityListRecyclerView()
        initializePaymentModeListRecyclerView()
        initializeOpeningHourListRecyclerView()
        initializeMenuListRecyclerView()
        initializeReviewListRecyclerView()
        initializeDiscountSelectionDropdown()
        initializeLocationMap(savedInstanceState)
        initializeRestaurant()

        return binding.root
    }

    private fun initializeRestaurantImageViewLayout(container: ViewGroup?) {
        val parentHeight = container!!.height
        val restaurantImageViewHeight = (parentHeight * 0.3).toInt()
        val restaurantImageView = binding.restaurantDetailsCardRestaurantImageView

        // Modify Restaurant Image View Height to match 30 % of Parent's height
        restaurantImageView.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            restaurantImageViewHeight
        )
    }

    private fun initializeRestaurant() {
        viewModel.restaurant.observe(viewLifecycleOwner) { it ->
            binding.restaurant = it

            // Populate Data for Facility, Payment Mode, OpeningHour, Menu, Review Recycler View
            facilityRecyclerViewAdapter.addList(it.facilities)
            paymentModeRecyclerViewAdapter.addList(it.paymentModes)
            hourRecyclerViewAdapter.addList(it.restaurantHours)
            menuRecyclerViewAdapter.addList(it.menus)
            reviewRecyclerViewAdapter.addList(it.reviewElastics)

            // Make Review Average Star Visible if There are existing reviews
            val averageStar = binding.restaurantDetailsCardAverageStar
            val averageStarValue = binding.restaurantDetailsCardAverageStarValue
            val numberOfReviews = binding.restaurantDetailsCardNumberOfReviews
            if (it.reviewElastics != null && it.reviewElastics.isNotEmpty()) {
                averageStar.visibility = View.VISIBLE
                averageStarValue.text = it.averageStar.toString()
                averageStarValue.visibility = View.VISIBLE
                numberOfReviews.text = it.reviewElastics.size.toString()
            } else {
                numberOfReviews.text = "0"
            }

            // Make Holiday Hour Notice Visible if there is Holiday Hour Entry
            val holidayHourNoticeCard = binding.restaurantDetailsCardHolidayHourNoticeCard
            if (it.restaurantHolidayHours != null && it.restaurantHolidayHours.isNotEmpty()) {
                holidayHourNoticeCard.visibility = View.VISIBLE
            }

            // Update Discount Table Discount Selection Exposed Dropdown Value
            if (it.restaurantCapacities != null && it.restaurantCapacities.isNotEmpty()) {
                // TODO: Determine Current Capacity Selection Through Selection Via Booking Card
                updateDiscountSelectionDropdownListItems(it.restaurantCapacities[0])
            }

            // Add Marker of Current Restaurant Coordinate to LocationMap
            locationMap.getMapAsync { googleMap ->
                val currentRestaurantCoordinate = LatLng(it.coordinate.lat, it.coordinate.lon)
                val markerOptions =
                    MarkerOptions().position(currentRestaurantCoordinate)
                        .title(it.name).snippet("${it.address} ${it.city}")
                googleMap.addMarker(markerOptions)

                // Zoom in to the added marker
                val cameraPosition = CameraPosition.fromLatLngZoom(
                    currentRestaurantCoordinate,
                    Constants.RESTAURANT_DETAILS_FRAGMENT_LOCATION_MAP_ZOOM_LEVEL
                )
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
            }

            binding.executePendingBindings()
        }
    }

    private fun initializeFacilityListRecyclerView() {
        val adapter = FacilityPaymentModeListItemAdapter()

        binding.facilityList.adapter = adapter
        binding.facilityList.layoutManager =
            GridLayoutManager(context, 1, GridLayoutManager.VERTICAL, false)
        facilityRecyclerViewAdapter = adapter
    }

    private fun initializePaymentModeListRecyclerView() {
        val adapter = FacilityPaymentModeListItemAdapter()

        binding.paymentModeList.adapter = adapter
        binding.paymentModeList.layoutManager =
            GridLayoutManager(context, 1, GridLayoutManager.VERTICAL, false)
        paymentModeRecyclerViewAdapter = adapter
    }

    private fun initializeOpeningHourListRecyclerView() {
        val adapter = OpeningHourListItemAdapter()

        binding.openingHourList.adapter = adapter
        binding.openingHourList.layoutManager =
            GridLayoutManager(context, 1, GridLayoutManager.VERTICAL, false)
        hourRecyclerViewAdapter = adapter
    }

    private fun initializeMenuListRecyclerView() {
        val adapter = MenuListItemAdapter()

        binding.menuList.adapter = adapter
        binding.menuList.layoutManager =
            GridLayoutManager(context, 1, GridLayoutManager.VERTICAL, false)
        menuRecyclerViewAdapter = adapter
    }

    private fun initializeReviewListRecyclerView() {
        val adapter = ReviewListItemAdapter()

        binding.reviewList.adapter = adapter
        binding.reviewList.layoutManager =
            GridLayoutManager(context, 1, GridLayoutManager.VERTICAL, false)
        reviewRecyclerViewAdapter = adapter
    }

    private fun initializeDiscountSelectionDropdown() {
        // Initialize Dropdown Menu with N/A Value
        val initialDiscountSelectionItemAdapter = ArrayAdapter(
            requireContext(),
            R.layout.discount_selection_list_item,
            Constants.RESTAURANT_DETAILS_FRAGMENT_DISCOUNT_SELECTION_INITIAL_DROPDOWN_ITEMS
        )
        discountSelectionDropdownMenu =
            binding.restaurantDetailsCardDiscountTableDiscountSelectionTextView
        discountSelectionDropdownMenu.setAdapter(initialDiscountSelectionItemAdapter)
        discountSelectionDropdownMenu.setText(
            Constants.RESTAURANT_DETAILS_FRAGMENT_DISCOUNT_SELECTION_INITIAL_DROPDOWN_ITEM_NOT_AVAILABLE,
            false
        )
    }

    private fun updateDiscountSelectionDropdownListItems(restaurantCapacity: RestaurantCapacity) {
        // Update Discount Selection Item List to restaurantCapacity List of Minimum Spend
        val discountListItems =
            restaurantCapacity.restaurantMinimumSpends.map { minimumSpend -> "${minimumSpend.discountAmount}%" }
        val discountSelectionItemAdapter = ArrayAdapter(
            requireContext(),
            R.layout.discount_selection_list_item,
            discountListItems
        )
        discountSelectionDropdownMenu.setAdapter(discountSelectionItemAdapter)

        // Initialize DiscountList Item Selection with first Minimum Spend
        discountSelectionDropdownMenu.setText(discountListItems[0], false)
        updateMenuListRecyclerViewCurrentDiscount(substringToPercentage(discountListItems[0]))

        // Update Menu List Item on new Discount Value Change whenever Discount Selection Changes
        discountSelectionDropdownMenu.doAfterTextChanged { text: Editable? ->
            updateMenuListRecyclerViewCurrentDiscount(substringToPercentage(text!!))
        }
    }

    private fun substringToPercentage(text: CharSequence): Int {
        // Remove Percentage Symbol from dropdown text
        val percentageSymbolIndex = text.lastIndexOf("%");
        return text.substring(0, percentageSymbolIndex).toInt()
    }

    private fun updateMenuListRecyclerViewCurrentDiscount(currentDiscount: Int) {
        menuRecyclerViewAdapter.currentDiscount = currentDiscount
        menuRecyclerViewAdapter.notifyDataSetChanged()
    }

    private fun initializeLocationMap(savedInstanceState: Bundle?) {
        locationMap = binding.restaurantDetailsCardLocationMap
        locationMap.onCreate(savedInstanceState)
        // Needed for map to start showing
        locationMap.onResume()
    }


}