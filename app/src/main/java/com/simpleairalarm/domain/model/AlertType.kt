package com.simpleairalarm.domain.model

enum class AlertType(val titleUa: String) {
    AIR("Повітряна тривога"),
    ARTILLERY("Артилерійський обстріл"),
    DRONE("Загроза БПЛА"),
    NUCLEAR("Ядерна небезпека"),
    CHEMICAL("Хімічна небезпека"),
    UNKNOWN("Загроза невідомого типу");

    companion object {
        fun fromString(value: String?): AlertType {
            return when (value?.uppercase()) {
                "AIR", "AIR_RAID" -> AIR
                "ARTILLERY" -> ARTILLERY
                "DRONE", "UAV", "SHAHED" -> DRONE
                "NUCLEAR" -> NUCLEAR
                "CHEMICAL" -> CHEMICAL
                else -> UNKNOWN
            }
        }
    }
}
