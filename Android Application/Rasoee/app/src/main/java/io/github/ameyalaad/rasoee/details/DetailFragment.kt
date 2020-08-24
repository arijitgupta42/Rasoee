package io.github.ameyalaad.rasoee.details

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import io.github.ameyalaad.rasoee.databinding.FragmentDetailBinding


class DetailFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val application = requireNotNull(activity).application
        val binding = FragmentDetailBinding.inflate(inflater)
        val recipeProperty = io.github.ameyalaad.rasoee.details.DetailFragmentArgs.Companion.fromBundle(
            requireArguments()
        ).selectedProperty

        val viewModelFactory = io.github.ameyalaad.rasoee.details.DetailViewModelFactory(
            recipeProperty,
            application
        )
        binding.viewModel =
            ViewModelProvider(this, viewModelFactory).get(io.github.ameyalaad.rasoee.details.DetailViewModel::class.java)

        binding.recipeLink.setOnClickListener{
            val browserIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse(recipeProperty.recipe_link))
            startActivity(browserIntent)
        }
        binding.setLifecycleOwner(this)
        return binding.root
    }
}