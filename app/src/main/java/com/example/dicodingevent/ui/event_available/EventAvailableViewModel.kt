package com.example.dicodingevent.ui.event_available

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class EventAvailableViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Event Available Fragment"
    }
    val text: LiveData<String> = _text
}