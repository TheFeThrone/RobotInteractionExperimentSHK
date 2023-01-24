package de.dollendorf.rie

import com.aldebaran.qi.Future
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject

class ExperimentHandler(private val experiment: ExperimentLoader, private val lookAt: LookAtTarget, private val speech: Speech, private val webInterface: Webinterface) : ExperimentObserver(), ExperimentControllerInterface, Runnable {

    private var steps: List<String>? = null
    private var lookAtFuture: Future<Void>? = null
    private var sayFuture: Future<Void>? = null
    private var currentStep = 0
    private var items: List<String>? = null
    private var runningState = 0
    private val thread = Thread(this)

    override fun run() {
        startExperiment()
    }

    private fun startExperiment() {
        runningState = 1
        updateRunningState(runningState)
        steps = experiment.getElement("sequence/order")?.split(",")
        for (counter in currentStep until steps!!.size) {
            executeStep(counter)
        }
    }

    private fun executeStep(index: Int) {
        currentStep = index
        val currentStepName = steps?.get(currentStep)
        val currentCommand = currentStepName?.substringBeforeLast("_")
        val stopping = experiment.getElement("sequence/$currentStepName/stopping").toBoolean()
        val requiresUserInteraction = experiment.getElement("sequence/$currentStepName/requires_user_interaction").toBoolean()

        updateExperimentState(ExperimentState(currentStep, stopping, requiresUserInteraction))

        when (currentCommand) {
            "look_at" -> {
                val x = experiment.getElement("sequence/$currentStepName/value/x")!!.toDouble()
                val y = experiment.getElement("sequence/$currentStepName/value/y")!!.toDouble()
                val z = experiment.getElement("sequence/$currentStepName/value/z")!!.toDouble()
                cancelMovements()
                lookAtFuture = lookAt.startLookAt(x, y, z)
                /*if (stopping) {
                    while (!lookAt.isCompleted()) {
                        Thread.sleep(100)
                        println("Waiting")
                    }
                    println("Ended waiting")
                }
                if (stopping) {
                    println("stopping")
                    while (!lookAtFuture!!.isSuccess) {
                        println("checking")
                        Thread.sleep(100)
                    }
                    println("continue")
                }*/
            }
            "time" -> {
                Thread.sleep(experiment.getElement("sequence/$currentStepName/value")!!.toLong())
            }
            /*"await" -> {
                val phraseSet = PhraseSetBuilder.with(qiContext).withTexts("Hello", "Hi").build() // Move to external class
            }*/
            "say" -> {
                sayFuture = speech.say(experiment.getElement("sequence/$currentStepName/value")!!)
                if (stopping) {
                    sayFuture?.sync()
                }
            }
            else -> println("Item not found")
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