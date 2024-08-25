package com.aem.sheap_reloaded.ui.project.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ProjectListViewModel: ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Project List Fragment"
    }
    val text: LiveData<String> = _text

}