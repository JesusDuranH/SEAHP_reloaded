package com.aem.sheap_reloaded.ui.project.assess.select_xy

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FragmentSelectXYViewModel: ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Choice yours options"
    }
    val text: LiveData<String> = _text

}