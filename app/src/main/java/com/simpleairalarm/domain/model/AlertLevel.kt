package com.simpleairalarm.domain.model

enum class AlertLevel {
    NONE,   // Безпечно (відбій / немає тривоги)
    YELLOW, // Підвищена небезпека / часткова тривога
    RED;    // Повітряна тривога!

    companion object {
        fun fromString(value: String?): AlertLevel {
            return when (value?.uppercase()) {
                "RED", "AIR_RAID", "ACTIVE" -> RED
                "YELLOW", "PARTIAL", "WARNING" -> YELLOW
                else -> NONE
            }
        }
    }
}
