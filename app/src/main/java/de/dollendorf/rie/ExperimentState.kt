package de.dollendorf.rie

import kotlinx.serialization.Serializable

@Serializable
data class ExperimentState(val index: Int, val stopped: Boolean, val userInteractionNeeded: Boolean)
