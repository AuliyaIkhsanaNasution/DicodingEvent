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
import java.io.IOException

class HomeViewModel : ViewModel() {

    private val _eventSlider = MutableLiveData<List<SlideModel>>()
    val eventSlider: LiveData<List<SlideModel>> get() = _eventSlider

    private val _completedEventList = MutableLiveData<List<ListEventsItem>>()
    val completedEventList: LiveData<List<ListEventsItem>> get() = _completedEventList

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: MutableLiveData<String?> get() = _errorMessage

    private fun handleApiError(code: Int) {
        _errorMessage.value = when (code) {
            400 -> "Permintaan tidak valid. Silakan periksa kembali input Anda."
            401 -> "Anda perlu login untuk mengakses sumber daya ini."
            403 -> "Akses ditolak. Anda tidak memiliki izin untuk mengakses halaman ini."
            404 -> "Halaman yang Anda cari tidak ditemukan."
            500 -> "Terjadi kesalahan pada server. Silakan coba lagi nanti."
            else -> "Kesalahan tidak diketahui. Kode status: $code"
        }
    }

    // Function to load active events
    fun loadEventData() {

        if (_eventSlider.value != null && _eventSlider.value!!.isNotEmpty()) {
            return
        }

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
                    handleApiError(response.code())
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                _loading.value = false
                if (t is IOException) {
                    _errorMessage.value = "Gagal dimuat, coba cek koneksi internet"
                } else {
                    _errorMessage.value = "Error: ${t.message}"
                }
            }
        })
    }


    fun loadCompletedEvents() {

        if (_completedEventList.value != null && _completedEventList.value!!.isNotEmpty()) {
            return
        }

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
                    handleApiError(response.code())
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                _loading.value = false
                if (t is IOException) {
                    _errorMessage.value = "Gagal dimuat, coba cek koneksi internet"
                } else {
                    _errorMessage.value = "Error: ${t.message}"
                }
            }
        })
    }
}

