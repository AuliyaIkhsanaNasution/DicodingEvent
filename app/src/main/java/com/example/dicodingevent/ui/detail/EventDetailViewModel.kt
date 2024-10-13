package com.example.dicodingevent.ui.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import data.response.DetailResponse
import data.response.DetailEvent
import data.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EventDetailViewModel : ViewModel() {

    private val _eventData = MutableLiveData<DetailEvent?>()
    val eventData: LiveData<DetailEvent?> get() = _eventData

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun loadDetailEvent(eventId: Int) {
        _isLoading.value = true

        val apiService = ApiConfig.getApiService() // Menggunakan ApiConfig untuk mendapatkan ApiService
        apiService.getEventDetail(eventId).enqueue(object : Callback<DetailResponse> {
            override fun onResponse(call: Call<DetailResponse>, response: Response<DetailResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val eventDetail = response.body()?.event // Ambil event dari DetailResponse
                    Log.d("EventDetailViewModel", "Response body: $eventDetail")
                    if (eventDetail != null) {
                        _eventData.value = eventDetail // Set eventDetail ke LiveData
                    } else {
                        _errorMessage.value = "Event detail is empty or null"
                    }
                } else {
                    _errorMessage.value = "Failed to load Detail Event"
                }
            }

            override fun onFailure(call: Call<DetailResponse>, t: Throwable) {
                _isLoading.value = false
                _errorMessage.value = "Error: ${t.message}"
            }
        })
    }
}
