package com.aem.sheap_reloaded.ui.project.assess.result.progress

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ProgressViewModel: ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is Fragment Progress"
    }
    val text: LiveData<String> = _text
}