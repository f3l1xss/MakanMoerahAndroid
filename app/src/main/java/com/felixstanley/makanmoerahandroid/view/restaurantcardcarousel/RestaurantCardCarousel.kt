package com.felixstanley.makanmoerahandroid.view.restaurantcardcarousel

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.withStyledAttributes
import androidx.core.view.doOnAttach
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import androidx.recyclerview.widget.GridLayoutManager
import com.felixstanley.makanmoerahandroid.R
import com.felixstanley.makanmoerahandroid.databinding.RestaurantCardCarouselBinding
import com.felixstanley.makanmoerahandroid.entity.enums.RestaurantCriteria
import com.felixstanley.makanmoerahandroid.network.api.NetworkApi
import timber.log.Timber

/**
 * Restaurant Card Carousel
 */
class RestaurantCardCarousel @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private lateinit var viewModel: RestaurantCardCarouselViewModel
    private lateinit var viewModelFactory: RestaurantCardCarouselViewModelFactory

    init {
        val layoutInflater = LayoutInflater.from(context)
        val binding = RestaurantCardCarouselBinding.inflate(layoutInflater, this, true)

        var criteria: String? = null
        var criteriaValue: String? = null

        // Obtain Necessary Attributes
        context.withStyledAttributes(attrs, R.styleable.RestaurantCardCarousel) {
            criteria = getString(R.styleable.RestaurantCardCarousel_criteria)
            criteriaValue = getString(R.styleable.RestaurantCardCarousel_criteriaValue)
        }

        // Validate that required attributes are not null
        if (criteria == null || RestaurantCriteria.values()
                .firstOrNull { it.name.equals(criteria) } == null || criteriaValue == null
        ) {
            Timber.e("Required Attributes missing / Invalid at RestaurantCardCarousel")
            throw NullPointerException()
        }

        doOnAttach {
            // Initialize ViewModelFactory & ViewModel
            viewModelFactory = RestaurantCardCarouselViewModelFactory(
                NetworkApi.restaurantService, RestaurantCriteria.valueOf(criteria!!),
                criteriaValue!!
            )
            viewModel = ViewModelProvider(findViewTreeViewModelStoreOwner()!!, viewModelFactory)
                .get(RestaurantCardCarouselViewModel::class.java)

            // Initialize Data Binding Variables
            // TODO: Find out if this is needed
            //binding.setLifecycleOwner(this)
            val manager = GridLayoutManager(context, 1, GridLayoutManager.HORIZONTAL, false)
            val adapter = RestaurantCardAdapter()
            binding.restaurantCardCarouselViewModel = viewModel
            binding.restaurantCards.adapter = adapter
            binding.restaurantCards.layoutManager = manager

            viewModel.restaurants.observe(
                findViewTreeLifecycleOwner()!!,
                { it -> adapter.addList(it) })
        }
    }
}