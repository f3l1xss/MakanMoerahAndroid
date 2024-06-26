package com.felixstanley.makanmoerahandroid.fragment.restaurantdetails

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.felixstanley.makanmoerahandroid.databinding.MenuListItemBinding
import com.felixstanley.makanmoerahandroid.entity.restaurant.Menu
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MenuListItemAdapter(var currentDiscount: Int = 0) :
    ListAdapter<Menu, RecyclerView.ViewHolder>(MenuDiffCallback()) {

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    class MenuDiffCallback : DiffUtil.ItemCallback<Menu>() {
        override fun areItemsTheSame(oldItem: Menu, newItem: Menu): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: Menu, newItem: Menu): Boolean {
            // Since Menu is a data class, equals sign will compare all fields
            // declared at constructor params
            return oldItem == newItem
        }
    }

    class MenuListItemViewHolder private constructor(val binding: MenuListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        companion object {
            // MenuListItemViewHolder Builder Function
            fun from(parent: ViewGroup): MenuListItemViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = MenuListItemBinding.inflate(layoutInflater, parent, false)

                return MenuListItemViewHolder(binding)
            }
        }

        fun bind(menu: Menu, currentDiscount: Int) {
            binding.menu = menu

            // Calculate Menu After Price by applying Current Discount to Menu Price
            binding.menuAfterPrice.text =
                (menu.price * getCurrentDiscountPercentage(currentDiscount)).toString()
            binding.executePendingBindings()
        }

        private fun getCurrentDiscountPercentage(currentDiscount: Int): Double {
            return (100 - currentDiscount) / 100.0;
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MenuListItemViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MenuListItemViewHolder -> {
                holder.bind(getItem(position), currentDiscount)
            }
        }
    }

    fun addList(menuList: List<Menu>?) {
        adapterScope.launch {
            withContext(Dispatchers.Main) {
                submitList(menuList)
            }
        }
    }

}