package com.felixstanley.makanmoerahandroid.view.restaurantcardcarousel

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.felixstanley.makanmoerahandroid.R
import com.felixstanley.makanmoerahandroid.entity.enums.RestaurantCriteria

@BindingAdapter("imageUrl")
fun bindImage(imgView: ImageView, imgUrl: String?) {
    imgUrl?.let {
        Glide.with(imgView.context)
            .load(it)
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.loading_animation)
                    .error(R.drawable.ic_broken_image)
            )
            .into(imgView)
    }
}

@BindingAdapter("cardCarouselTitle")
fun bindCardCarouselTitle(
    textView: TextView,
    restaurantCardCarouselViewModel: RestaurantCardCarouselViewModel
) {
    // Only Assign Criteria Value as Title when Criteria is either FoodCategory, City, or Facility
    textView.text = when (restaurantCardCarouselViewModel.criteria) {
        RestaurantCriteria.FOOD_CATEGORY, RestaurantCriteria.CITY, RestaurantCriteria.FACILITY ->
            restaurantCardCarouselViewModel.criteriaValue
        else -> restaurantCardCarouselViewModel.criteria.title
    }
}