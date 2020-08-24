package io.github.ameyalaad.rasoee.analyze

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import io.github.ameyalaad.rasoee.databinding.ListItemBinding


class RecipeListAdapter(private val onClickListener: io.github.ameyalaad.rasoee.analyze.RecipeListAdapter.OnClickListener) :
    ListAdapter<io.github.ameyalaad.rasoee.network.RecipeProperty, io.github.ameyalaad.rasoee.analyze.RecipeListAdapter.RecipePropertyViewHolder>(
        io.github.ameyalaad.rasoee.analyze.RecipeListAdapter.DiffCallBack
    ) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): io.github.ameyalaad.rasoee.analyze.RecipeListAdapter.RecipePropertyViewHolder {
        return io.github.ameyalaad.rasoee.analyze.RecipeListAdapter.RecipePropertyViewHolder(
            ListItemBinding.inflate(LayoutInflater.from(parent.context))
        )
    }

    override fun onBindViewHolder(
        holder: io.github.ameyalaad.rasoee.analyze.RecipeListAdapter.RecipePropertyViewHolder,
        position: Int
    ) {
        val recipeProperty = getItem(position)
        holder.itemView.setOnClickListener{
            onClickListener.onClick(recipeProperty)
        }
        holder.bind(recipeProperty)
    }

    class RecipePropertyViewHolder(private var binding: ListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(recipeProperty: io.github.ameyalaad.rasoee.network.RecipeProperty) {
            binding.recipe = recipeProperty
            binding.executePendingBindings()
        }
    }

    companion object DiffCallBack : DiffUtil.ItemCallback<io.github.ameyalaad.rasoee.network.RecipeProperty>() {
        override fun areItemsTheSame(oldItem: io.github.ameyalaad.rasoee.network.RecipeProperty, newItem: io.github.ameyalaad.rasoee.network.RecipeProperty): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: io.github.ameyalaad.rasoee.network.RecipeProperty, newItem: io.github.ameyalaad.rasoee.network.RecipeProperty): Boolean {
            return oldItem.recipe_link == newItem.recipe_link
        }
    }

    class OnClickListener(val clickListener: (recipeProperty: io.github.ameyalaad.rasoee.network.RecipeProperty) -> Unit){
        fun onClick(recipeProperty: io.github.ameyalaad.rasoee.network.RecipeProperty) = clickListener(recipeProperty)
    }
}