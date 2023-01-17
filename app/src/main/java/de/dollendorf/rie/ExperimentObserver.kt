package de.dollendorf.rie

open class ExperimentObserver : Observer() {
    fun updateExperimentState(experimentState: ExperimentState) {
        for (observer in observers as ArrayList<ExperimentObserverInterface>) {
            observer.updateExperimentState(experimentState)
        }
    }
}