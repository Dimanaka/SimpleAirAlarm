package com.simpleairalarm.domain.model

data class Location(
    val id: String,
    val name: String,
    val alertLevel: AlertLevel = AlertLevel.NONE,
    val alertType: AlertType = AlertType.UNKNOWN,
    val lastUpdated: Long = System.currentTimeMillis()
)
