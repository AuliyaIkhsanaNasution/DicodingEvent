package com.example.dicodingevent.ui.event_not_available

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class EventNotAvailableViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Event Not available Fragment"
    }
    val text: LiveData<String> = _text
}