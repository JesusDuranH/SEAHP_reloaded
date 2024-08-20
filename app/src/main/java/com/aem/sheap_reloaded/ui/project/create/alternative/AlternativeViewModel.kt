package com.aem.sheap_reloaded.ui.project.create.alternative

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ProjectCreateAlternativeViewModel: ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is Project Create Alternative Fragment"
    }
    val text: LiveData<String> = _text
}