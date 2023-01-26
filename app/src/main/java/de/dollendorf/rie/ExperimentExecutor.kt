package de.dollendorf.rie

class ExperimentExecutor(private val currentStep: Int, private val steps: List<String>, private val experiment: ExperimentLoader, private val lookAt: LookAtTarget, private val speech: Speech, private val experimentHandler: ExperimentHandler) : Runnable {

    override fun run() {
        executeStep()
    }

    private fun executeStep() {
        val currentStepName = steps[currentStep]
        var currentCommand = currentStepName.substringBeforeLast("_")
        var stopping = experiment.getElement("sequence/$currentStepName/stopping").toBoolean()
        val requiresUserInteraction = experiment.getElement("sequence/$currentStepName/requires_user_interaction").toBoolean()

        var value = experiment.getElement("sequence/$currentStepName/value")

        experimentHandler.updateExperimentState(ExperimentState(currentStep, stopping, requiresUserInteraction))

        doStep(currentCommand, value!!, stopping)

        if (requiresUserInteraction) {
            experimentHandler.updateRunningState(2)
            while (experimentHandler.getDecision() == null) {
                Thread.sleep(100)
            }
            val decision = experimentHandler.getDecision()
            val possibilities = experiment.getElement("sequence/$currentStepName/possibilities/order")!!.split(",")
            if (decision!! < possibilities.size) {
                val currentDecision = possibilities[decision]
                currentCommand = currentDecision.substringBeforeLast("_")
                value = experiment.getElement("sequence/$currentStepName/possibilities/$currentDecision/value")
                stopping = experiment.getElement("sequence/$currentStepName/possibilities/$currentDecision/stopping").toBoolean()

                experimentHandler.updateExperimentState(ExperimentState(decision, stopping, false))

                doStep(currentCommand, value!!, stopping)
            }
            experimentHandler.updateRunningState(1)
        }
    }

    private fun doStep(currentCommand: String, value: String, stopping: Boolean) {
        when (currentCommand) {
            "look_at" -> {
                //cancelMovements()
                val lookAtFuture = lookAt.startLookAt(value[0].code.toDouble(), value[1].code.toDouble(), value[2].code.toDouble())
            }
            "time" -> {
                try {
                    Thread.sleep(value.toLong())
                }
                catch (_: InterruptedException) {}
            }
            /*"await" -> {
                val phraseSet = PhraseSetBuilder.with(qiContext).withTexts("Hello", "Hi").build() // Move to external class
            }*/
            "say" -> {
                val sayFuture = speech.say(value)
                if (stopping) {
                    sayFuture?.sync()
                }
            }
            else -> println("Item not found")
        }
    }

}