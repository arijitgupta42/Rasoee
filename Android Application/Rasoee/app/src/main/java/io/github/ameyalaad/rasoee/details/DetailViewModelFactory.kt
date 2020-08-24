package io.github.ameyalaad.rasoee.details

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class DetailViewModelFactory(
    private val recipeProperty: io.github.ameyalaad.rasoee.network.RecipeProperty,
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(io.github.ameyalaad.rasoee.details.DetailViewModel::class.java)) {
            return io.github.ameyalaad.rasoee.details.DetailViewModel(
                recipeProperty,
                application
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
