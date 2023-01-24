package de.dollendorf.rie

abstract class ExperimentObserver : Observer() {
    fun updateExperimentState(experimentState: ExperimentState) {
        for (observer in observers as ArrayList<ExperimentObserverInterface>) {
            observer.updateExperimentState(experimentState)
        }
    }
    fun updateRunningState(state: Int) {
        for (observer in observers as ArrayList<ExperimentObserverInterface>) {
            observer.updateRunningState(state)
        }
    }
}