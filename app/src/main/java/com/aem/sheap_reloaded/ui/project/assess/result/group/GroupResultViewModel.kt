package com.aem.sheap_reloaded.ui.project.assess.result.group

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GroupResultViewModel: ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        //value = "This is Result Group Fragment"
        value = ""
    }
    val text: LiveData<String> = _text
}