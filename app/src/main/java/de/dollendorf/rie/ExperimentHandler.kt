package de.dollendorf.rie

import com.aldebaran.qi.Future
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

class ExperimentHandler(private val experiment: ExperimentLoader, private val lookAt: LookAtTarget, private val speech: Speech, private val webInterface: Webinterface) : Thread() {

    private var steps: List<String>? = null
    private var lookAtFuture: Future<Void>? = null
    private var currentStep: String? = null
    private var items: List<String>? = null

    override fun run() {
        startExperiment()
        for (step in steps!!) {
            doStep("sequence/$step")
        }
    }

    private fun startExperiment() {
        steps = experiment.getElement("sequence/order")?.split(",")
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun doStep(step: String) {
        items = experiment.getElement("$step/order")?.split(",")
        for ((counter, item) in items!!.withIndex()) {
            currentStep = item
            var stopped = false
            when (item.substringBeforeLast("_")) {
                "look_at" -> {
                    val x = experiment.getElement("$step/$item/x")!!.toDouble()
                    val y = experiment.getElement("$step/$item/y")!!.toDouble()
                    val z = experiment.getElement("$step/$item/z")!!.toDouble()
                    cancelMovements()
                    lookAtFuture = lookAt.startLookAt(x, y, z)
                }
                "time" -> {
                    stopped = true
                    sleep(experiment.getElement("$step/$item")!!.toLong())
                }
                /*"await" -> {
                    val phraseSet = PhraseSetBuilder.with(qiContext).withTexts("Hello", "Hi").build() // Move to external class
                }*/
                "say" -> speech.say(experiment.getElement("$step/$item")!!)
                else -> println("Item not found")
            }
            val result = GlobalScope.async { webInterface.send("{\"index\": $counter, \"stopped\": $stopped}") }
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