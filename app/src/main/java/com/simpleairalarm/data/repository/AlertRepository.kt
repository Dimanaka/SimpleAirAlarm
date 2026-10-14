package com.simpleairalarm.data.repository

import com.simpleairalarm.data.api.AlertsApiService
import com.simpleairalarm.data.dto.AlertDto
import com.simpleairalarm.domain.model.AlertLevel
import com.simpleairalarm.domain.model.AlertType
import com.simpleairalarm.domain.model.Location
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

class AlertRepository(private val apiService: AlertsApiService? = null) {

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    private val client: AlertsApiService by lazy {
        apiService ?: createDefaultApiService()
    }

    private fun createDefaultApiService(): AlertsApiService {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        val contentType = "application/json".toMediaType()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://devs.alerts.in.ua/")
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()

        return retrofit.create(AlertsApiService::class.java)
    }

    suspend fun fetchActiveAlerts(apiToken: String): Result<List<AlertDto>> {
        return try {
            val bearerToken = if (apiToken.startsWith("Bearer ", ignoreCase = true)) apiToken else "Bearer $apiToken"
            val alerts = client.getActiveAlerts(bearerToken)
            Result.success(alerts)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    fun getUkrainianRegions(): List<Location> {
        return listOf(
            Location(id = "11", name = "Київська область"),
            Location(id = "31", name = "м. Київ"),
            Location(id = "12", name = "Вінницька область"),
            Location(id = "13", name = "Волинська область"),
            Location(id = "14", name = "Дніпропетровська область"),
            Location(id = "15", name = "Донецька область"),
            Location(id = "16", name = "Житомирська область"),
            Location(id = "17", name = "Закарпатська область"),
            Location(id = "18", name = "Запорізька область"),
            Location(id = "19", name = "Івано-Франківська область"),
            Location(id = "20", name = "Кіровоградська область"),
            Location(id = "21", name = "Луганська область"),
            Location(id = "22", name = "Львівська область"),
            Location(id = "23", name = "Миколаївська область"),
            Location(id = "24", name = "Одеська область"),
            Location(id = "25", name = "Полтавська область"),
            Location(id = "26", name = "Рівненська область"),
            Location(id = "27", name = "Сумська область"),
            Location(id = "28", name = "Тернопільська область"),
            Location(id = "29", name = "Харківська область"),
            Location(id = "30", name = "Херсонська область"),
            Location(id = "3", name = "Хмельницька область"),
            Location(id = "4", name = "Черкаська область"),
            Location(id = "5", name = "Чернівецька область"),
            Location(id = "6", name = "Чернігівська область")
        )
    }
}
