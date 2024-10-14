package data.retrofit

import data.response.DetailResponse
import retrofit2.Call
import data.response.EventResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("events")
    fun getActiveEvents(@Query("active") active: Int = 1): Call<EventResponse>


    @GET("events")
    fun getCompletedEvents(@Query("active") active: Int = 0): Call<EventResponse>


    @GET("events")
    fun searchEvents(@Query("active") active: Int = -1, @Query("q") keyword: String): Call<EventResponse>


    @GET("events/{id}")
    fun getEventDetail(@Path("id") eventId: Int): Call<DetailResponse>
}
