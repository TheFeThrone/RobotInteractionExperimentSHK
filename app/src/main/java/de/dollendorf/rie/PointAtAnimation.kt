package de.dollendorf.rie

enum class PointAtAnimation {
    CLOSE_FRONT_LEFT,
    CLOSE_FRONT_RIGHT,
    CLOSE_MEDIUM_LEFT,
    CLOSE_MEDIUM_RIGHT,
    CLOSE_HALF_LEFT,
    CLOSE_HALF_RIGHT,
    FRONT_LEFT,
    FRONT_RIGHT,
    MEDIUM_LEFT,
    MEDIUM_RIGHT,
    HALF_LEFT,
    HALF_RIGHT;

    fun selectPointAtAnimation(): String {
        return when (this) {
            CLOSE_FRONT_LEFT -> "PointShortFrontL.qianim"
            CLOSE_FRONT_RIGHT -> "PointShortFrontR.qianim"
            CLOSE_MEDIUM_LEFT -> "PointShortMediumL.qianim"
            CLOSE_MEDIUM_RIGHT -> "PointShortMediumR.qianim"
            CLOSE_HALF_LEFT -> "PointShortHalfL.qianim"
            CLOSE_HALF_RIGHT -> "PointShortHalfR.qianim"
            FRONT_LEFT -> "PointFrontL.qianim"
            FRONT_RIGHT -> "PointFrontR.qianim"
            MEDIUM_LEFT -> "PointMediumL.qianim"
            MEDIUM_RIGHT -> "PointMediumR.qianim"
            HALF_LEFT -> "PointHalfL.qianim"
            HALF_RIGHT -> "PointHalfR.qianim"
        }
    }
}