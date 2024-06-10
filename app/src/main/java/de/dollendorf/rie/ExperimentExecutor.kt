package de.dollendorf.rie

import java.lang.Thread.currentThread

class ExperimentExecutor(private val currentStep: Int, private val steps: List<String>, private val experiment: ExperimentLoader, private val lookAt: LookAtTarget, private val moveTo: MoveToTarget, private val animation: Animation,  val speech: Speech, private val display: Display, private val movement: Boolean, private val experimentHandler: ExperimentHandler, private val executeAgain: Boolean, private val documentation: Documentation) : Runnable {

    private var interrupted = false

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

        if (executeAgain) {
            doStep(currentCommand, value!!, stopping, movement)
        }

        if (requiresUserInteraction) {
            experimentHandler.updateRunningState(2)
            while (experimentHandler.getDecision() == null) {
                try {
                    Thread.sleep(50)
                }
                catch (_: InterruptedException) {
                    interrupted = true
                }
            }
            if (!currentThread().isInterrupted && !interrupted) {
                val decision = experimentHandler.getDecision()
                val possibilities = experiment.getElement("sequence/$currentStepName/possibilities/order")!!.split(",")
                if (decision!! < possibilities.size) {
                    val currentDecision = possibilities[decision]
                    currentCommand = currentDecision.substringBeforeLast("_")
                    value = experiment.getElement("sequence/$currentStepName/possibilities/$currentDecision/value")
                    stopping = experiment.getElement("sequence/$currentStepName/possibilities/$currentDecision/stopping").toBoolean()

                    experimentHandler.updateExperimentState(ExperimentState(decision, stopping, false))

                    doStep(currentCommand, value!!, stopping, movement)
                }
            } else {
                println("Interrupted")
            }
            experimentHandler.updateRunningState(1)
        }
    }

    private fun doStep(currentCommand: String, value: String, stopping: Boolean, movement: Boolean) {
        documentation.addEvent("Executing $currentCommand with value \"$value\"")
        when (currentCommand) {
            "look_at" -> {
                experimentHandler.cancelMovements()
                val coordinates = value.replace(Regex("\\{|\\}|x|y|z|:"), "").split(",")
                experimentHandler.setLookAtFuture(lookAt.startLookAt(coordinates[0].toDouble(), coordinates[1].toDouble(), coordinates[2].toDouble(),movement)!!)
                /*if (stopping) {
                    lookAtFuture?.sync()
                }*/
            }
            "move_to" -> {
                experimentHandler.cancelMovements()
                val coordinates = value.replace(Regex("\\{|\\}|x|y|z|:"), "").split(",")
                val coordinateX = coordinates[0].takeIf { it.isNotBlank() && it != "null" }?.toDoubleOrNull() ?: 0.0
                val coordinateY = coordinates[1].takeIf { it.isNotBlank() && it != "null" }?.toDoubleOrNull() ?: 0.0
                val coordinateZ = coordinates[2].takeIf { it.isNotBlank() && it != "null" }?.toDoubleOrNull() ?: 0.0

                val moveToFuture = moveTo.startMoveTo(coordinateX, coordinateY, coordinateZ)
                experimentHandler.setMoveToFuture(moveToFuture!!)
                if (stopping) {
                    while (!moveToFuture.isDone) {
                        Thread.sleep(50)
                    }
                }
            }
            "animation" -> {
                experimentHandler.cancelMovements()
                val animationFuture = animation.startAnimation(value)
                experimentHandler.setAnimationFuture(animationFuture!!)
                if (stopping) {
                    animationFuture.sync()
                }
            }
            "time" -> {
                try {
                    Thread.sleep(value.toLong())
                }
                catch (_: InterruptedException) {
                    interrupted = true
                }
            }
            "say" -> {
                experimentHandler.cancelSounds()
                val sayFuture = speech.say(value, movement)
                experimentHandler.setSayFuture(sayFuture!!)
                if (stopping) {
                    sayFuture.sync()
                }
            }
            "sound" -> {
                speech.play(value)
            }
            "reset_look" -> {
                experimentHandler.cancelMovements()
            }
            "display" -> {
                display.show(value)
            }
            "reset_display" -> {
                display.blank()
            }
            "point_at" -> {
                experimentHandler.cancelMovements()
                val animationFuture = animation.startAnimation(value)
                experimentHandler.setAnimationFuture(animationFuture!!)
                if (stopping) {
                    animationFuture.sync()
                }
            }
            "empty" -> {

            }
            else -> println("Item not found")
        }
    }

}