package com.simpleairalarm.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AlertDto(
    @SerialName("location_title") val locationTitle: String? = null,
    @SerialName("location_type") val locationType: String? = null,
    @SerialName("started_at") val startedAt: String? = null,
    @SerialName("finished_at") val finishedAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null,
    @SerialName("alert_type") val alertType: String? = null,
    @SerialName("location_uid") val locationUid: String? = null,
    @SerialName("location_oblast") val locationOblast: String? = null,
    @SerialName("location_raion") val locationRaion: String? = null
)
