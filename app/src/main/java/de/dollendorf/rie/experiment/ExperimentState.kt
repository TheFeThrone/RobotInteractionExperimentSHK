package de.dollendorf.rie.experiment

import kotlinx.serialization.Serializable

@Serializable
data class ExperimentState(val index: Int, val stopped: Boolean, val requiresUserInteraction: Boolean)
