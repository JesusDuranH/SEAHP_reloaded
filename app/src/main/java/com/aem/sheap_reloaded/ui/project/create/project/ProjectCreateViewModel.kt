package com.aem.sheap_reloaded.ui.project.create.project

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ProjectCreateViewModel: ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is Project Create Fragment"
    }
    val text: LiveData<String> = _text
}