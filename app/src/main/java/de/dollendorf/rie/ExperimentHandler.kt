package de.dollendorf.rie

import com.aldebaran.qi.Future

class ExperimentHandler(private val experiment: ExperimentLoader, private val lookAt: LookAtTarget, private val speech: Speech, private val webInterface: Webinterface, private val experimentObserver: ExperimentObserver) : Thread() {

    private var steps: List<String>? = null
    private var lookAtFuture: Future<Void>? = null
    private var currentStep: String? = null
    private var items: List<String>? = null

    override fun run() {
        startExperiment()
    }

    private fun startExperiment() {
        steps = experiment.getElement("sequence/order")?.split(",")

        for ((counter, step) in steps!!.withIndex()) {
            currentStep = step
            var stopped = false
            when (step.substringBeforeLast("_")) {
                "look_at" -> {
                    val x = experiment.getElement("sequence/$step/x")!!.toDouble()
                    val y = experiment.getElement("sequence/$step/y")!!.toDouble()
                    val z = experiment.getElement("sequence/$step/z")!!.toDouble()
                    cancelMovements()
                    lookAtFuture = lookAt.startLookAt(x, y, z)
                }
                "time" -> {
                    stopped = true
                    sleep(experiment.getElement("sequence/$step")!!.toLong())
                }
                /*"await" -> {
                    val phraseSet = PhraseSetBuilder.with(qiContext).withTexts("Hello", "Hi").build() // Move to external class
                }*/
                "say" -> speech.say(experiment.getElement("sequence/$step")!!)
                else -> println("Item not found")
            }
            experimentObserver.updateExperimentState(ExperimentState(counter, stopped, true))
        }
    }

    fun pauseExperiment() {

    }

    fun resumeExperiment() {

    }

    fun cancelMovements() {
        lookAtFuture?.requestCancellation()
    }

    fun getCurrentStep(): String? {
        return currentStep
    }
    fun getItems(): List<String>? {
        return items
    }

}