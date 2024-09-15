package com.aem.sheap_reloaded.ui.project.assess.select_y_alternative

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SelectYAViewModel: ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is Assess Criteria - Alternative Fragment"
    }
    val text: LiveData<String> = _text
}