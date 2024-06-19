package de.dollendorf.rie.experiment

import de.dollendorf.rie.ObserverInterface

interface ExperimentControllerInterface : ObserverInterface {
    fun triggerCommand(command: String)
}