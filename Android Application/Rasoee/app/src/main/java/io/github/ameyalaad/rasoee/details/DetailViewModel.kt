package io.github.ameyalaad.rasoee.details

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData


class DetailViewModel (recipeProperty: io.github.ameyalaad.rasoee.network.RecipeProperty, app: Application): AndroidViewModel(app){
    private val _selectedProperty = MutableLiveData<io.github.ameyalaad.rasoee.network.RecipeProperty>()
    val selectedProperty: LiveData<io.github.ameyalaad.rasoee.network.RecipeProperty>
        get() = _selectedProperty

    init {
        _selectedProperty.value = recipeProperty
    }

}