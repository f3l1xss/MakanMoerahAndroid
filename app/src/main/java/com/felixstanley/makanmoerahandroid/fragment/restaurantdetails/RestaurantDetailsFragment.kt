package com.felixstanley.makanmoerahandroid.fragment.restaurantdetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.felixstanley.makanmoerahandroid.databinding.FragmentRestaurantDetailsBinding
import com.felixstanley.makanmoerahandroid.fragment.AbstractFragment
import com.felixstanley.makanmoerahandroid.network.api.NetworkApi

class RestaurantDetailsFragment : AbstractFragment() {

    private lateinit var binding: FragmentRestaurantDetailsBinding
    private lateinit var viewModel: RestaurantDetailsViewModel
    private lateinit var viewModelFactory: RestaurantDetailsViewModelFactory

    private lateinit var facilityRecyclerViewAdapter: FacilityPaymentModeListItemAdapter
    private lateinit var paymentModeRecyclerViewAdapter: FacilityPaymentModeListItemAdapter
    private lateinit var hourRecyclerViewAdapter: OpeningHourListItemAdapter
    private lateinit var menuRecyclerViewAdapter: MenuListItemAdapter
    private lateinit var reviewRecyclerViewAdapter: ReviewListItemAdapter

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

}