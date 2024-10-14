package com.example.dicodingevent.ui.event_available

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import data.response.EventResponse
import data.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class EventAvailableViewModel : ViewModel() {
    private val _eventResponse = MutableLiveData<EventResponse?>()
    val eventResponse: LiveData<EventResponse?> get() = _eventResponse

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: MutableLiveData<String?> get() = _errorMessage

    fun fetchActiveEvents() {
        _errorMessage.value = null
        if (_eventResponse.value != null) {
            return
        }

        _isLoading.value = true
        ApiConfig.getApiService().getActiveEvents().enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                _isLoading.value = false
                when (response.code()) {
                    200 -> {
                        _eventResponse.value = response.body()
                    }
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
}
