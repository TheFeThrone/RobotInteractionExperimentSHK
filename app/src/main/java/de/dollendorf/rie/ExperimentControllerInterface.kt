package de.dollendorf.rie

interface ExperimentControllerInterface : ObserverInterface {
    fun jumpToExperimentState(experimentState: ExperimentState)
}