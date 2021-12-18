package com.felixstanley.makanmoerahandroid.fragment.explore

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.felixstanley.makanmoerahandroid.databinding.CheckboxBinding
import com.felixstanley.makanmoerahandroid.databinding.NoDistrictFilterAvailableHeaderBinding
import com.felixstanley.makanmoerahandroid.databinding.NoFoodCategoryFilterAvailableHeaderBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val ITEM_VIEW_TYPE_CHECKBOX_VALUE = 0
private const val ITEM_VIEW_TYPE_NO_DISTRICT_AVAILABLE_HEADER = 1
private const val ITEM_VIEW_TYPE_NO_FOOD_CATEGORY_AVAILABLE_HEADER = 2

class DistrictFoodCategoryCheckBoxAdapter(
    var currentCheckboxSelection: List<String>,
    private val onCheckedChangeListener: CompoundButton.OnCheckedChangeListener
) :
    ListAdapter<DistrictFoodCategoryCheckboxDataItem, RecyclerView.ViewHolder>(
        DistrictFoodCategoryCheckboxDataItemDiffCallback()
    ) {

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    class DistrictFoodCategoryCheckboxDataItemDiffCallback :
        DiffUtil.ItemCallback<DistrictFoodCategoryCheckboxDataItem>() {
        override fun areItemsTheSame(
            oldItem: DistrictFoodCategoryCheckboxDataItem,
            newItem: DistrictFoodCategoryCheckboxDataItem
        ): Boolean {
            return oldItem.value == newItem.value
        }

        override fun areContentsTheSame(
            oldItem: DistrictFoodCategoryCheckboxDataItem,
            newItem: DistrictFoodCategoryCheckboxDataItem
        ): Boolean {
            return oldItem.value == newItem.value
        }
    }

    // No District Filter Available Header View Holder
    class NoDistrictFilterAvailableHeaderViewHolder private constructor(val binding: NoDistrictFilterAvailableHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        companion object {
            // NoDistrictFilterAvailableHeaderViewHolder Builder Function
            fun from(parent: ViewGroup): NoDistrictFilterAvailableHeaderViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding =
                    NoDistrictFilterAvailableHeaderBinding.inflate(layoutInflater, parent, false)
                return NoDistrictFilterAvailableHeaderViewHolder(binding)
            }
        }
    }

    // No Food Category Filter Available Header View Holder
    class NoFoodCategoryFilterAvailableHeaderViewHolder private constructor(val binding: NoFoodCategoryFilterAvailableHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        companion object {
            // NoFoodCategoryFilterAvailableHeaderViewHolder Builder Function
            fun from(parent: ViewGroup): NoFoodCategoryFilterAvailableHeaderViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding =
                    NoFoodCategoryFilterAvailableHeaderBinding.inflate(
                        layoutInflater,
                        parent,
                        false
                    )
                return NoFoodCategoryFilterAvailableHeaderViewHolder(binding)
            }
        }
    }

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

        fun bind(
            item: String,
            onCheckedChangeListener: CompoundButton.OnCheckedChangeListener,
            currentCheckboxSelection: List<String>
        ) {
            // Set Root ID to each individual string item hashcode
            binding.root.id = (DISTRICT_FOOD_CATEGORY_FILTER_CHECKBOX_ID_PREFIX + item).hashCode()
            // Set Checkbox Is Checked Value on whether current Item is preselected already
            // as part of currentCheckboxSelection Parameters (CurrentCheckboxSelection will
            // be populated when User make a selection and then performing screen rotation)
            binding.checkbox.isChecked = currentCheckboxSelection.contains(item)
            // Set Checkbox On Checked Change Listener (need to do it after checked state is determined,
            // otherwise setting checked state will trigger the listener)
            binding.checkbox.setOnCheckedChangeListener(onCheckedChangeListener)
            binding.text = item
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_CHECKBOX_VALUE -> DistrictFoodCategoryCheckBoxViewHolder.from(parent)
            ITEM_VIEW_TYPE_NO_DISTRICT_AVAILABLE_HEADER -> NoDistrictFilterAvailableHeaderViewHolder.from(
                parent
            )
            ITEM_VIEW_TYPE_NO_FOOD_CATEGORY_AVAILABLE_HEADER -> NoFoodCategoryFilterAvailableHeaderViewHolder.from(
                parent
            )
            else -> throw ClassCastException("Unknown viewType ${viewType}")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is DistrictFoodCategoryCheckBoxViewHolder -> {
                holder.bind(
                    getItem(position).value,
                    onCheckedChangeListener,
                    currentCheckboxSelection
                )
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is DistrictFoodCategoryCheckboxDataItem.DistrictFoodCategoryCheckboxValue -> ITEM_VIEW_TYPE_CHECKBOX_VALUE
            is DistrictFoodCategoryCheckboxDataItem.NoDistrictAvailableHeader -> ITEM_VIEW_TYPE_NO_DISTRICT_AVAILABLE_HEADER
            is DistrictFoodCategoryCheckboxDataItem.NoFoodCategoryAvailableHeader -> ITEM_VIEW_TYPE_NO_FOOD_CATEGORY_AVAILABLE_HEADER
        }
    }

    fun addList(items: List<String>?, isDistrictItem: Boolean) {
        var modifiedItems: List<DistrictFoodCategoryCheckboxDataItem>
        // If Items is empty, display No District / No Food Category Available Header
        if (items == null || items.isEmpty()) {
            // Inserting District Item
            if (isDistrictItem) {
                modifiedItems =
                    listOf(DistrictFoodCategoryCheckboxDataItem.NoDistrictAvailableHeader)
            } else {
                // Is District Item is false, it implies that we are inserting Food Category Item
                modifiedItems =
                    listOf(DistrictFoodCategoryCheckboxDataItem.NoFoodCategoryAvailableHeader)
            }
        } else {
            modifiedItems = items.map {
                DistrictFoodCategoryCheckboxDataItem.DistrictFoodCategoryCheckboxValue(it)
            }
        }

        adapterScope.launch {
            withContext(Dispatchers.Main) {
                submitList(modifiedItems)
            }
        }
    }

}

sealed class DistrictFoodCategoryCheckboxDataItem {

    // Representing CheckBox Value / Empty String if Data Item is a header
    abstract val value: String

    data class DistrictFoodCategoryCheckboxValue(private val checkboxValue: String) :
        DistrictFoodCategoryCheckboxDataItem() {
        override val value = checkboxValue
    }

    object NoDistrictAvailableHeader : DistrictFoodCategoryCheckboxDataItem() {
        // override value to empty string as it is not used
        override val value = ""
    }

    object NoFoodCategoryAvailableHeader : DistrictFoodCategoryCheckboxDataItem() {
        // override value to empty string as it is not used
        override val value = ""
    }

}