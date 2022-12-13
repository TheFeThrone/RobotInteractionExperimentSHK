package de.dollendorf.rie

import java.lang.Double
import java.lang.Long
import kotlin.math.exp

class ExperimentHandler(private val experiment: ExperimentLoader, private val lookAt: LookAtTarget, private val speech: Speech) : Thread() {

    private var steps: List<String>? = null

    override fun run() {
        startExperiment()
        for (step in steps!!) {
            doStep("sequence/$step")
        }
    }

    private fun startExperiment() {
        steps = experiment.getElement("sequence/order")?.split(",")
    }

    private fun doStep(step: String) {
        val items = experiment.getElement("$step/order")?.split(",")
        for (item in items!!) {
            when (item.substringBeforeLast("_")) {
                "look_at" -> {
                    val x = Double.valueOf(experiment.getElement("$step/$item/x")!!)
                    val y = Double.valueOf(experiment.getElement("$step/$item/y")!!)
                    val z = Double.valueOf(experiment.getElement("$step/$item/z")!!)
                    lookAt.startLookAt(x, y, z)
                }
                "time" -> sleep(Long.valueOf(experiment.getElement("$step/$item")!!))
                "say" -> speech.say(experiment.getElement("$step/$item")!!)
                else -> println("Item not found")
            }
        }
    }

    fun pauseExperiment() {

    }

    fun resumeExperiment() {

    }

}