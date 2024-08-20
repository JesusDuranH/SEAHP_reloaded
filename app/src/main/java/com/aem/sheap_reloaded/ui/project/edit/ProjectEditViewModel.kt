package com.aem.sheap_reloaded.ui.project.edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ProjectEditViewModel:ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is Project Edit Fragment"
    }
    val text: LiveData<String> = _text
}