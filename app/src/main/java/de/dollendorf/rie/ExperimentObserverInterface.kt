package de.dollendorf.rie

interface ExperimentObserverInterface : ObserverInterface {
    fun updateExperimentState(experimentState: ExperimentState)
}