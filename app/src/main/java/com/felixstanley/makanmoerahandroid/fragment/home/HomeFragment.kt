package com.felixstanley.makanmoerahandroid.fragment.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.felixstanley.makanmoerahandroid.constants.Constants
import com.felixstanley.makanmoerahandroid.databinding.FragmentHomeBinding
import com.felixstanley.makanmoerahandroid.fragment.AbstractFragment
import com.felixstanley.makanmoerahandroid.network.api.NetworkApi
import com.felixstanley.makanmoerahandroid.view.restaurantcardcarousel.RestaurantCardCarousel

class HomeFragment : AbstractFragment() {

    private lateinit var viewModel: HomeViewModel
    private lateinit var viewModelFactory: HomeViewModelFactory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate Home Fragment
        val binding = FragmentHomeBinding.inflate(inflater)

        // Initialize ViewModelFactory & ViewModel
        viewModelFactory = HomeViewModelFactory(NetworkApi.restaurantService)
        viewModel = ViewModelProvider(this, viewModelFactory)
            .get(HomeViewModel::class.java)

        // Initialize Data Binding Variables
        binding.lifecycleOwner = this
        binding.homeViewModel = viewModel
        viewModel.restaurantCriteriaWithValues.observe(viewLifecycleOwner, { it ->
            it.forEach {
                // Add Restaurant Card Carousel Component for each Obtained Criteria
                val restaurantCardCarousel = RestaurantCardCarousel(requireContext())
                restaurantCardCarousel.criteria = it.criteria.name
                // Transform Null CriteriaValue to "NULL" String as RestaurantCardCarousel
                // does not accept NULL Parameter
                restaurantCardCarousel.criteriaValue = it.value ?: "NULL"
                binding.homeLinearLayout.addView(
                    restaurantCardCarousel,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    getPixelFromDp(Constants.RESTAURANT_CARD_CAROUSEL_HEIGHT_DP)
                )
            }
        })
        return binding.root
    }

}
