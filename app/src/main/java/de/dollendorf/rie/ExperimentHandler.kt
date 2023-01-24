package de.dollendorf.rie

import com.aldebaran.qi.Future
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject

class ExperimentHandler(private val experiment: ExperimentLoader, private val lookAt: LookAtTarget, private val speech: Speech) : ExperimentObserver(), ExperimentControllerInterface, Runnable {

    private var steps: List<String>? = null
    private var lookAtFuture: Future<Void>? = null
    private var sayFuture: Future<Void>? = null
    private var currentStep = 0
    private var items: List<String>? = null
    private var runningState = 0
    private val thread = Thread(this)
    private var executorThread = Thread()

    override fun run() {
        startExperiment()
    }

    private fun startExperiment() {
        runningState = 1
        updateRunningState(runningState)

        steps = experiment.getElement("sequence/order")?.split(",")

        for (counter in currentStep until steps!!.size) {
            currentStep = counter
            executorThread = Thread(ExperimentExecutor(currentStep, steps!!, experiment, lookAt, speech, this))
            println(executorThread)
            executorThread.start()
            executorThread.join()
        }
    }

    fun pauseExperiment() {

    }

    fun resumeExperiment() {

    }

    private fun cancelMovements() {
        lookAtFuture?.requestCancellation()
    }

    fun getCurrentStep(): Int {
        return currentStep
    }
    fun getItems(): List<String>? {
        return items
    }
    fun getRunningState(): Int {
        return runningState
    }

    override fun triggerCommand(command: String) {
        val json = Json.parseToJsonElement(command).jsonObject
        when (json["type"].toString()) {
            "start" -> {
                currentStep = 0
                thread.start()
            }
            "jumpto" -> {
                currentStep = json["value"].toString().toInt()
                thread.start()
            }
            else -> println("Command not found.")
        }
    }
}