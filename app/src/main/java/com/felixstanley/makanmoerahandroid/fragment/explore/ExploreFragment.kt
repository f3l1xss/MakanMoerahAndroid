package com.felixstanley.makanmoerahandroid.fragment.explore

import android.os.Bundle
import android.view.*
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.felixstanley.makanmoerahandroid.R
import com.felixstanley.makanmoerahandroid.databinding.FragmentExploreBinding
import com.felixstanley.makanmoerahandroid.fragment.AbstractFragment
import com.felixstanley.makanmoerahandroid.network.api.NetworkApi
import com.google.android.material.bottomsheet.BottomSheetBehavior

class ExploreFragment : AbstractFragment() {

    private lateinit var viewModel: ExploreViewModel
    private lateinit var viewModelFactory: ExploreViewModelFactory

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

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

        // Initialize Options Menu
        setHasOptionsMenu(true)
        return binding.root
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