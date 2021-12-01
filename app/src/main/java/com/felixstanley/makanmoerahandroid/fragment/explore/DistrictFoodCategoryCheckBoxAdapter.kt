package com.felixstanley.makanmoerahandroid.fragment.explore

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.felixstanley.makanmoerahandroid.databinding.CheckboxBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DistrictFoodCategoryCheckBoxAdapter :
    ListAdapter<String, RecyclerView.ViewHolder>(CityFilterRadioButtonAdapter.StringDiffCallback()) {

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    class DistrictFoodCategoryCheckBoxViewHolder private constructor(val binding: CheckboxBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val DISTRICT_FOOD_CATEGORY_FILTER_CHECKBOX_ID_PREFIX =
            "DISTRICT_FOOD_CATEGORY_FILTER_"

        companion object {
            // DistrictFoodCategoryCheckBoxViewHolder Builder Function
            fun from(parent: ViewGroup): DistrictFoodCategoryCheckBoxViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = CheckboxBinding.inflate(layoutInflater, parent, false)

                return DistrictFoodCategoryCheckBoxViewHolder(binding)
            }
        }

        fun bind(item: String) {
            // Set Root ID to each individual string item hashcode
            binding.root.id = (DISTRICT_FOOD_CATEGORY_FILTER_CHECKBOX_ID_PREFIX + item).hashCode()
            binding.text = item
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return DistrictFoodCategoryCheckBoxViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is DistrictFoodCategoryCheckBoxViewHolder -> {
                holder.bind(getItem(position))
            }
        }
    }

    fun addList(items: List<String>?) {
        adapterScope.launch {
            withContext(Dispatchers.Main) {
                submitList(items)
            }
        }
    }

}