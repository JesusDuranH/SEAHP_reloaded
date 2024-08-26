package com.aem.sheap_reloaded.ui.project.assess.p2p

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class P2PViewModel: ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is P2P Assess Fragment"
    }
    val text: LiveData<String> = _text
}