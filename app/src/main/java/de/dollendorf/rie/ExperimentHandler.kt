package de.dollendorf.rie

import com.aldebaran.qi.Future
import java.lang.Double
import java.lang.Long

class ExperimentHandler(private val experiment: ExperimentLoader, private val lookAt: LookAtTarget, private val speech: Speech) : Thread() {

    private var steps: List<String>? = null
    private var lookAtFuture: Future<Void>? = null
    private var currentStep: String? = null

    override fun run() {
        startExperiment()
        for (step in steps!!) {
            doStep("sequence/$step")
        }
    }

    private fun startExperiment() {
        steps = experiment.getElement("sequence/order")?.split(",")
        println(steps)
    }

    private fun doStep(step: String) {
        val items = experiment.getElement("$step/order")?.split(",")
        println(items)
        for (item in items!!) {
            currentStep = item
            when (item.substringBeforeLast("_")) {
                "look_at" -> {
                    val x = Double.valueOf(experiment.getElement("$step/$item/x")!!)
                    val y = Double.valueOf(experiment.getElement("$step/$item/y")!!)
                    val z = Double.valueOf(experiment.getElement("$step/$item/z")!!)
                    cancelMovements()
                    lookAtFuture = lookAt.startLookAt(x, y, z)
                }
                "time" -> sleep(Long.valueOf(experiment.getElement("$step/$item")!!))
                /*"await" -> {
                    val phraseSet = PhraseSetBuilder.with(qiContext).withTexts("Hello", "Hi").build() // Move to external class
                }*/
                "say" -> speech.say(experiment.getElement("$step/$item")!!)
                else -> println("Item not found")
            }
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

}