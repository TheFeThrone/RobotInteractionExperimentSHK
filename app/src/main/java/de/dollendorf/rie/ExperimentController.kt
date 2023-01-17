package de.dollendorf.rie

class ExperimentController : Observer() {
    fun jumpToExperimentState(experimentState: ExperimentState) {
        for (observer in observers as ArrayList<ExperimentControllerInterface>) {
            observer.jumpToExperimentState(experimentState)
        }
    }
}