package com.felixstanley.makanmoerahandroid.fragment.restaurantdetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.felixstanley.makanmoerahandroid.databinding.FragmentRestaurantDetailsBinding
import com.felixstanley.makanmoerahandroid.fragment.AbstractFragment

class RestaurantDetailsFragment : AbstractFragment() {

    private lateinit var binding: FragmentRestaurantDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate RestaurantDetails Fragment
        binding = FragmentRestaurantDetailsBinding.inflate(inflater)

        // Obtain Arguments From Bundle
        val args = RestaurantDetailsFragmentArgs.fromBundle(requireArguments())
        binding.demoText.text = args.restaurantId.toString()

        return binding.root
    }

}