package de.dollendorf.rie

abstract class ExperimentController : Observer() {
    fun triggerCommand(command: String) {
        for (observer in observers as ArrayList<ExperimentControllerInterface>) {
            observer.triggerCommand(command)
        }
    }
}