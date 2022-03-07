package com.felixstanley.makanmoerahandroid.fragment.restaurantdetails

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.felixstanley.makanmoerahandroid.databinding.ReviewListItemBinding
import com.felixstanley.makanmoerahandroid.entity.restaurant.ReviewElastic
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ReviewListItemAdapter :
    ListAdapter<ReviewElastic, RecyclerView.ViewHolder>(ReviewElasticDiffCallback()) {

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    class ReviewElasticDiffCallback : DiffUtil.ItemCallback<ReviewElastic>() {
        override fun areItemsTheSame(oldItem: ReviewElastic, newItem: ReviewElastic): Boolean {
            return oldItem.bookingId == newItem.bookingId
        }

        override fun areContentsTheSame(oldItem: ReviewElastic, newItem: ReviewElastic): Boolean {
            // Since Review Elastic is a data class, equals sign will compare all fields
            // declared at constructor params
            return oldItem == newItem
        }
    }

    class ReviewListItemViewHolder private constructor(val binding: ReviewListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        companion object {
            // ReviewListItemViewHolder Builder Function
            fun from(parent: ViewGroup): ReviewListItemViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ReviewListItemBinding.inflate(layoutInflater, parent, false)

                val sectionWidth = (parent.width * 0.33).toInt()
                val sectionLayoutParams = LinearLayout.LayoutParams(
                    sectionWidth, ViewGroup.LayoutParams.MATCH_PARENT
                )

                // Modify Each Section Width to match 1/3 of Parent's Width
                val avatarFileName = binding.avatarFileName
                val reviewContentSection = binding.reviewContentSection
                val reviewStar = binding.reviewStar
                avatarFileName.layoutParams = sectionLayoutParams
                reviewContentSection.layoutParams = sectionLayoutParams
                reviewStar.layoutParams = sectionLayoutParams

                return ReviewListItemViewHolder(binding)
            }
        }

        fun bind(reviewElastic: ReviewElastic) {
            binding.reviewElastic = reviewElastic
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ReviewListItemViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ReviewListItemViewHolder -> {
                holder.bind(getItem(position))
            }
        }
    }

    fun addList(reviewList: List<ReviewElastic>?) {
        adapterScope.launch {
            withContext(Dispatchers.Main) {
                submitList(reviewList)
            }
        }
    }

}