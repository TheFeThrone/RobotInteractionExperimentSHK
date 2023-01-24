package de.dollendorf.rie

interface ExperimentControllerInterface : ObserverInterface {
    fun triggerCommand(command: String)
}