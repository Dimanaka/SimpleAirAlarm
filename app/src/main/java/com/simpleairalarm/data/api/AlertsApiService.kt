package com.simpleairalarm.data.api

import com.simpleairalarm.data.dto.AlertDto
import retrofit2.http.GET
import retrofit2.http.Header

interface AlertsApiService {
    @GET("api/v1/alerts/active.json")
    suspend fun getActiveAlerts(
        @Header("Authorization") token: String
    ): List<AlertDto>
}
