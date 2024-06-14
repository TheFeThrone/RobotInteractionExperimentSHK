package de.dollendorf.rie.experiment

import de.dollendorf.rie.ObserverInterface

interface ExperimentObserverInterface : ObserverInterface {
    fun updateExperimentState(experimentState: ExperimentState)
    fun updateRunningState(state: Int)
}