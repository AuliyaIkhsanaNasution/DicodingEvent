package com.example.dicodingevent.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import data.response.DetailResponse
import data.response.DetailEvent
import data.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class EventDetailViewModel : ViewModel() {

    private val _eventData = MutableLiveData<DetailEvent?>()
    val eventData: LiveData<DetailEvent?> get() = _eventData

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun loadDetailEvent(eventId: Int) {
        if (_eventData.value != null) {
            return
        }

        _isLoading.value = true

        val apiService = ApiConfig.getApiService()
        apiService.getEventDetail(eventId).enqueue(object : Callback<DetailResponse> {
            override fun onResponse(call: Call<DetailResponse>, response: Response<DetailResponse>) {
                _isLoading.value = false
                when (response.code()) {
                    200 -> {
                        _eventData.value = response.body()?.event
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

            override fun onFailure(call: Call<DetailResponse>, t: Throwable) {
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
