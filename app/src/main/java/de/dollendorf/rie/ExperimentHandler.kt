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
    private var thread = Thread(this)
    private var executorThread = Thread()
    private var interrupt = false

    override fun run() {
        interrupt = false
        startExperiment()
    }

    private fun startExperiment() {
        runningState = 1
        updateRunningState(runningState)

        steps = experiment.getElement("sequence/order")?.split(",")

        for (counter in currentStep until steps!!.size) {
            if (interrupt) {
                break;
            }
            currentStep = counter
            executorThread = Thread(ExperimentExecutor(currentStep, steps!!, experiment, lookAt, speech, this))
            executorThread.start()
            executorThread.join()
        }
    }

    fun pauseExperiment() {
        interrupt = true
    }

    fun resumeExperiment() {
        thread = Thread(this)
        thread.start()
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
                try {
                    interrupt = true
                    executorThread.interrupt()
                } finally {
                    currentStep = json["value"].toString().toInt()
                    thread = Thread(this)
                    thread.start()
                }
            }
            "decision" -> {

            }
            else -> println("Command not found.")
        }
    }
}