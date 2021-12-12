package com.felixstanley.makanmoerahandroid.fragment.explore

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class RestaurantListItemAdapterDataObserver(
    private val viewModel: ExploreViewModel,
    private val layoutManager: LinearLayoutManager
) :
    RecyclerView.AdapterDataObserver() {

    override fun onChanged() {
        super.onChanged()
        scrollToTop()
    }

    override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
        super.onItemRangeChanged(positionStart, itemCount)
        scrollToTop()
    }

    override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
        super.onItemRangeChanged(positionStart, itemCount, payload)
        scrollToTop()
    }

    override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
        super.onItemRangeInserted(positionStart, itemCount)
        scrollToTop()
    }

    override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
        super.onItemRangeRemoved(positionStart, itemCount)
        scrollToTop()
    }

    override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
        super.onItemRangeMoved(fromPosition, toPosition, itemCount)
        scrollToTop()
    }

    private fun scrollToTop() {
        // Scroll To Top Upon Item Change if we are still at first page
        // (Implying that data change is due to Apply Filter Button / Initial Page Load)
        if (viewModel.isAtFirstPage()) {
            layoutManager.scrollToPositionWithOffset(0, 0)
        }
    }
}