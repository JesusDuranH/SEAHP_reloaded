package com.aem.sheap_reloaded.ui.project.assess.select_x

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SelectXViewModel: ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is Selection A Fragment"
    }
    val text: LiveData<String> = _text
}