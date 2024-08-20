package com.aem.sheap_reloaded.ui.project.create.participant

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ParticipantViewModel: ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is Project Create User Fragment"
    }
    val text: LiveData<String> = _text
}