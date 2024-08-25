package com.aem.sheap_reloaded.ui.project.create.criteria

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ProjectCreateCriteriaViewModel: ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is Project Create Criteria Fragment"
    }
    val text: LiveData<String> = _text
}