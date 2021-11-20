package com.felixstanley.makanmoerahandroid.fragment.explore

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.felixstanley.makanmoerahandroid.databinding.FragmentExploreBinding
import com.felixstanley.makanmoerahandroid.fragment.AbstractFragment
import com.felixstanley.makanmoerahandroid.network.api.NetworkApi

class ExploreFragment : AbstractFragment() {

    private lateinit var viewModel: ExploreViewModel
    private lateinit var viewModelFactory: ExploreViewModelFactory

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
        return binding.root
    }

}