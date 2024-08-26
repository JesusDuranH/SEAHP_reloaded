package com.aem.sheap_reloaded.ui.project.assess.select_y

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SelectYViewModel: ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is Select Y Fragment"
    }
    val text: LiveData<String> = _text
}