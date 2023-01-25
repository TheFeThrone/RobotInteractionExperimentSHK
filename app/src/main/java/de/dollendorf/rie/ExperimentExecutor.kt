package de.dollendorf.rie

class ExperimentExecutor(private val currentStep: Int, private val steps: List<String>, private val experiment: ExperimentLoader, private val lookAt: LookAtTarget, private val speech: Speech, private val experimentHandler: ExperimentHandler) : Runnable {

    override fun run() {
        executeStep()
    }

    private fun executeStep() {
        val currentStepName = steps[currentStep]
        val currentCommand = currentStepName.substringBeforeLast("_")
        val stopping = experiment.getElement("sequence/$currentStepName/stopping").toBoolean()
        val requiresUserInteraction = experiment.getElement("sequence/$currentStepName/requires_user_interaction").toBoolean()

        experimentHandler.updateExperimentState(ExperimentState(currentStep, stopping, requiresUserInteraction))

        when (currentCommand) {
            "look_at" -> {
                val x = experiment.getElement("sequence/$currentStepName/value/x")!!.toDouble()
                val y = experiment.getElement("sequence/$currentStepName/value/y")!!.toDouble()
                val z = experiment.getElement("sequence/$currentStepName/value/z")!!.toDouble()
                //cancelMovements()
                val lookAtFuture = lookAt.startLookAt(x, y, z)
            }
            "time" -> {
                try {
                    Thread.sleep(experiment.getElement("sequence/$currentStepName/value")!!.toLong())
                }
                catch (_: InterruptedException) {}
            }
            /*"await" -> {
                val phraseSet = PhraseSetBuilder.with(qiContext).withTexts("Hello", "Hi").build() // Move to external class
            }*/
            "say" -> {
                val sayFuture = speech.say(experiment.getElement("sequence/$currentStepName/value")!!)
                if (stopping) {
                    sayFuture?.sync()
                }
            }
            else -> println("Item not found")
        }

        if (requiresUserInteraction) {
            experimentHandler.updateRunningState(2)
        }
    }

}