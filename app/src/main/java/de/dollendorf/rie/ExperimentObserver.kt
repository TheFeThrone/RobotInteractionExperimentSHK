package de.dollendorf.rie

class ExperimentObserver {
    private var observers = ArrayList<ExperimentObserverInterface>()

    fun registerObserver(observer: ExperimentObserverInterface) {
        observers.add(observer)
    }

    fun unregisterObserver(observer: ExperimentObserverInterface) {
        observers.remove(observer)
    }

    fun updateExperimentState(experimentState: ExperimentState) {
        for (observer in observers) {
            observer.updateExperimentState(experimentState)
        }
    }
}