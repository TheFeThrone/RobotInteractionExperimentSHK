package de.dollendorf.rie.experiment

import de.dollendorf.rie.Observer

abstract class ExperimentController : Observer() {
    fun triggerCommand(command: String) {
        for (observer in observers as ArrayList<ExperimentControllerInterface>) {
            observer.triggerCommand(command)
        }
    }
}