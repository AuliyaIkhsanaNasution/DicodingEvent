package com.example.dicodingevent.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.denzcoskun.imageslider.models.SlideModel
import data.response.EventResponse
import data.response.ListEventsItem
import data.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel : ViewModel() {

    private val _eventSlider = MutableLiveData<List<SlideModel>>()
    val eventSlider: LiveData<List<SlideModel>> get() = _eventSlider


    private val _completedEventList = MutableLiveData<List<ListEventsItem>>()
    val completedEventList: LiveData<List<ListEventsItem>> get() = _completedEventList

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: MutableLiveData<String?> get() = _errorMessage

    // Function to load active events
    fun loadEventData() {
        _loading.value = true
        _errorMessage.value = null

        ApiConfig.getApiService().getActiveEvents().enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                _loading.value = false

                if (response.isSuccessful) {
                    response.body()?.let { eventResponse ->
                        val topEvents = eventResponse.listEvents.take(5)

                        val slides = topEvents.map { event ->
                            SlideModel(event.mediaCover, event.name)
                        }
                        _eventSlider.value = slides
                    }
                } else {
                    _errorMessage.value = "Failed to load data"
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                _loading.value = false
                _errorMessage.value = "Error: ${t.message}"
            }
        })
    }

    // Function to load completed events
    fun loadCompletedEvents() {
        _loading.value = true
        _errorMessage.value = null

        ApiConfig.getApiService().getCompletedEvents().enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                _loading.value = false

                if (response.isSuccessful) {
                    response.body()?.let { eventResponse ->
                        _completedEventList.value = eventResponse.listEvents.take(5)
                    }
                } else {
                    _errorMessage.value = "Failed to load completed events"
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                _loading.value = false
                _errorMessage.value = "Error: ${t.message}"
            }
        })
    }
}
