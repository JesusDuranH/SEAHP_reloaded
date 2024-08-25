package com.aem.sheap_reloaded.ui.project.matrix

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MatrixViewModel: ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is Matrix Fragment"
    }
    val text: LiveData<String> = _text
}