package com.example.dicodingevent.ui.event_not_available

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import data.response.EventResponse
import data.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EventNotAvailableViewModel : ViewModel() {
    private val _eventResponse = MutableLiveData<EventResponse?>()
    val eventResponse: LiveData<EventResponse?> get() = _eventResponse

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun fetchNotAvailableEvents() {
        if (_eventResponse.value != null) return
        _isLoading.value = true
        ApiConfig.getApiService().getCompletedEvents().enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _eventResponse.value = response.body()
                } else {
                    when (response.code()) {
                        400 -> {
                            _errorMessage.value = "Permintaan tidak valid. Silakan periksa kembali input Anda."
                        }
                        401 -> {
                            _errorMessage.value = "Anda perlu login untuk mengakses sumber daya ini."
                        }
                        403 -> {
                            _errorMessage.value = "Akses ditolak. Anda tidak memiliki izin untuk mengakses halaman ini."
                        }
                        404 -> {
                            _errorMessage.value = "Halaman yang Anda cari tidak ditemukan."
                        }
                        500 -> {
                            _errorMessage.value = "Terjadi kesalahan pada server. Silakan coba lagi nanti."
                        }
                        else -> {
                            _errorMessage.value = "Kesalahan tidak diketahui. Kode status: ${response.code()}"
                        }
                    }
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                _isLoading.value = false
                _errorMessage.value = "Error: ${t.message}"
            }
        })
    }
}
