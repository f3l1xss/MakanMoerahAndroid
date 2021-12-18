package com.felixstanley.makanmoerahandroid.fragment.explore

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.felixstanley.makanmoerahandroid.databinding.RadioButtonBinding
import com.felixstanley.makanmoerahandroid.utility.IntHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CityFilterRadioButtonAdapter(
    private val currentRadioButtonSelection: String,
    private val onCheckedChangeListener: CompoundButton.OnCheckedChangeListener
) :
    ListAdapter<String, RecyclerView.ViewHolder>(StringDiffCallback()) {

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    // Keep Track of Radio Button's Last Checked Position
    private var radioButtonLastCheckedPosition = IntHolder(-1)

    class StringDiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }

    class CityFilterRadioButtonViewHolder private constructor(val binding: RadioButtonBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val CITY_FILTER_RADIO_BUTTON_ID_PREFIX = "CITY_FILTER_"

        companion object {
            // CityFilterRadioButtonViewHolder Builder Function
            fun from(parent: ViewGroup): CityFilterRadioButtonViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RadioButtonBinding.inflate(layoutInflater, parent, false)

                return CityFilterRadioButtonViewHolder(binding)
            }
        }

        fun bind(
            cityFilter: String,
            currentItemPosition: Int,
            radioButtonLastCheckedPosition: IntHolder,
            currentRadioButtonSelection: String,
            onCheckedChangeListener: CompoundButton.OnCheckedChangeListener
        ) {
            // Set Root ID (RadioButton XML Element) with PREFIX + cityFilter ID
            binding.root.id = (CITY_FILTER_RADIO_BUTTON_ID_PREFIX + cityFilter).hashCode()
            binding.text = cityFilter

            // If radioButtonLastCheckedPosition == -1 (Meaning User has yet to change Radio Button
            // Selection / Has just performed screen rotation), Take into account currentRadioButtonSelection
            // which is populated before screen rotation, Otherwise
            // Update Radio Button Checked State to whether or not currentItemPosition is the same
            // as Last Checked Position (This check will occur again whenever each radio button
            // is selected (Triggered by NotifyItemChanged at RadioButton Listener))
            var isChecked = false
            if (radioButtonLastCheckedPosition.value == -1) {
                if (cityFilter.equals(currentRadioButtonSelection)) {
                    isChecked = true
                    // Also record radioButtonLastCheckedPosition so that
                    // next selection works correctly
                    radioButtonLastCheckedPosition.value = currentItemPosition
                }
            } else {
                isChecked = currentItemPosition == radioButtonLastCheckedPosition.value
            }
            binding.radioButton.isChecked = isChecked
            // Set Radio Button On Checked Change Listener (need to do it after checked state is determined,
            // otherwise setting checked state will trigger the listener)
            binding.radioButton.setOnCheckedChangeListener(onCheckedChangeListener)
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return CityFilterRadioButtonViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is CityFilterRadioButtonViewHolder -> {
                holder.bind(
                    getItem(position),
                    position,
                    radioButtonLastCheckedPosition,
                    currentRadioButtonSelection,
                    onCheckedChangeListener
                )

                // Add Listener to maintain single selection radio button
                if (!holder.binding.radioButton.hasOnClickListeners()) {
                    holder.binding.radioButton.setOnClickListener { it ->
                        // Track Last Checked Position and update to current Check Position
                        val lastCheckedPosition = radioButtonLastCheckedPosition.value
                        radioButtonLastCheckedPosition.value = holder.adapterPosition

                        // Notify Item Changed For Last And Current Checked Position
                        if (lastCheckedPosition >= 0) {
                            // Not First Time checking radio button hence lastCheckedPosition >= 0
                            notifyItemChanged(lastCheckedPosition)
                        }
                        notifyItemChanged(radioButtonLastCheckedPosition.value)
                    }
                }
            }
        }
    }

    fun addList(cityFilters: List<String>?) {
        adapterScope.launch {
            withContext(Dispatchers.Main) {
                submitList(cityFilters)
            }
        }
    }

}