package com.example.dicodingevent.ui.event_not_available

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import data.response.EventResponse
import data.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class EventNotAvailableViewModel : ViewModel() {

    private val _eventResponse = MutableLiveData<EventResponse?>()
    val eventResponse: LiveData<EventResponse?> get() = _eventResponse

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading


    fun fetchNotAvailableEvents() {
        if (_eventResponse.value != null) {
            return
        }


        _isLoading.value = true
        ApiConfig.getApiService().getCompletedEvents().enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _eventResponse.value = response.body()
                } else {
                    handleApiError(response.code())
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                _isLoading.value = false
                if (t is IOException) {
                    _errorMessage.value = "Gagal dimuat, coba cek koneksi internet"
                } else {
                    _errorMessage.value = "Error: ${t.message}"
                }
            }
        })
    }

    fun searchEvents(query: String) {
        _isLoading.value = true
        _eventResponse.value = null

        ApiConfig.getApiService().searchEvents(keyword = query).enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    if (response.body()?.listEvents.isNullOrEmpty()) {
                        _errorMessage.value = "Tidak ada hasil yang cocok untuk pencarian \"$query\""
                        _eventResponse.value = null
                    } else {
                        _eventResponse.value = response.body()
                        _errorMessage.value = null
                    }
                } else {
                    handleApiError(response.code())
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                _isLoading.value = false
                _errorMessage.value = "Error: ${t.message}"
            }
        })
    }


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

    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}

