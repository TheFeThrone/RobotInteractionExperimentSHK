package de.dollendorf.rie

import com.aldebaran.qi.Future
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject

class ExperimentHandler(private val experiment: ExperimentLoader, private val lookAt: LookAtTarget, private val moveTo: MoveToTarget, private val animation: Animation, private val speech: Speech, private val display: Display, private val documentation: Documentation) : ExperimentObserver(), ExperimentControllerInterface, Runnable {

    private var steps: List<String>? = null
    private var lookAtFuture: Future<Void>? = null
    private var moveToFuture: Future<Void>? = null
    private var animationFuture: Future<Void>? = null
    private var sayFuture: Future<Void>? = null
    private var currentStep = 0
    private var items: List<String>? = null
    private var runningState = 0
    private var thread = Thread(this)
    private var executorThread = Thread()
    private var interrupt = false
    private var decision: Int? = null
    private var executeAgain = true
    private var afterJump = false

    override fun run() {
        interrupt = false
        startExperiment()
    }

    private fun startExperiment() {

        runningState = 1
        updateRunningState(1)

        steps = experiment.getElement("sequence/order")?.split(",")

        for (counter in currentStep until steps!!.size) {
            if (interrupt) {
                break;
            }
            decision = null
            if (!afterJump) {
                executeAgain = true
            }
            afterJump = false
            currentStep = counter
            executorThread = Thread(ExperimentExecutor(currentStep, steps!!, experiment, lookAt, moveTo, animation, speech, display, this, executeAgain, documentation))
            executorThread.start()
            executorThread.join()
        }
        if (!interrupt) {
            documentation.createFile()
            updateRunningState(3)
            display.blank()
            moveToFuture = moveTo.startMoveTo(0.0, 0.0, 0.0)
            lookAtFuture = lookAt.startLookAt(10.0, 0.0, 1.2)
            moveToFuture?.sync()
            Thread.sleep(500)
            cancelMovements()
        }
    }

    fun pauseExperiment() {
        interrupt = true
    }

    fun resumeExperiment() {
        currentStep++
        thread = Thread(this)
        thread.start()
    }

    fun cancelMovements() {
        lookAtFuture?.requestCancellation()
        moveToFuture?.requestCancellation()
        animationFuture?.requestCancellation()
    }

    fun cancelSounds() {
        sayFuture?.requestCancellation()
        sayFuture?.sync()
    }

    fun setSayFuture(sayFuture: Future<Void>) {
        this.sayFuture = sayFuture
    }

    fun setLookAtFuture(lookAtFuture: Future<Void>) {
        this.lookAtFuture = lookAtFuture
    }

    fun setMoveToFuture(moveToFuture: Future<Void>) {
        this.moveToFuture = moveToFuture
    }

    fun setAnimationFuture(animationFuture: Future<Void>) {
        this.animationFuture = animationFuture
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
                runningState = 0
                thread = Thread(this)
                thread.start()
            }
            "jumpto" -> {
                try {
                    interrupt = true
                    cancelSounds()
                    cancelMovements()
                    executorThread.interrupt()
                    executorThread.join()
                } finally {
                    currentStep = json["value"].toString().toInt()
                    thread = Thread(this)
                    thread.start()
                }
            }
            "decision" -> {
                decision = json["value"].toString().toInt()
                val possibilities = experiment.getElement("sequence/${steps?.get(currentStep)}/possibilities/order")!!.split(",")
                val jump = experiment.getElement("sequence/${steps?.get(currentStep)}/possibilities/${possibilities[decision!!]}/jump")
                if (jump != null && jump != "") {
                    interrupt = true
                    executorThread.join()
                    currentStep = steps!!.indexOf(jump.substringBeforeLast(","))
                    executeAgain = jump.substringAfter(",").toInt() != 0
                    afterJump = true
                    thread = Thread(this)
                    thread.start()
                }
            }
            else -> println("Command not found.")
        }
    }

    fun getDecision(): Int? {
        return decision
    }
}