package com.aem.sheap_reloaded.ui.project.assess.result.alone

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ResultViewModel: ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        //value = "This is Result Fragment"
        value = ""
    }
    val text: LiveData<String> = _text
}