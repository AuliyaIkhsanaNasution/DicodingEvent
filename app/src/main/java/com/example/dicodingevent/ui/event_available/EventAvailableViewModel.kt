package com.example.dicodingevent.ui.event_available

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import data.response.EventResponse
import data.response.ListEventsItem
import data.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EventAvailableViewModel : ViewModel() {
    private val _eventResponse = MutableLiveData<EventResponse?>()
    val eventResponse: LiveData<EventResponse?> get() = _eventResponse

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun fetchActiveEvents() {
        _isLoading.value = true
        ApiConfig.getApiService().getActiveEvents().enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _eventResponse.value = response.body()
                } else {
                    _eventResponse.value = null
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                _isLoading.value = false
                _eventResponse.value = null
            }
        })
    }
}
