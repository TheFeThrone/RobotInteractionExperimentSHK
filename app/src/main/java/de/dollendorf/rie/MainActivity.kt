package de.dollendorf.rie

import android.graphics.Bitmap
import android.os.Bundle
import android.widget.ImageView
import androidx.core.view.isVisible
import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.QiSDK
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks
import com.aldebaran.qi.sdk.`object`.actuation.FreeFrame
import com.aldebaran.qi.sdk.design.activity.RobotActivity


class MainActivity : RobotActivity(), RobotLifecycleCallbacks {
    private lateinit var initClass: Init
    private lateinit var baseFrame: FreeFrame
    private lateinit var lookAt: LookAtTarget
    private lateinit var moveTo: MoveToTarget
    private lateinit var animation: Animation
    private lateinit var speech: Speech
    private lateinit var display: Display
    private lateinit var config: Config
    private lateinit var experiment: ExperimentLoader
    private lateinit var experimentHandler: ExperimentHandler
    private lateinit var documentation: Documentation
    private var init = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Register the RobotLifecycleCallbacks to this Activity.
        QiSDK.register(this, this)
    }

    override fun onDestroy() {
        // Unregister the RobotLifecycleCallbacks for this Activity.
        QiSDK.unregister(this, this)
        super.onDestroy()
    }

    override fun onRobotFocusGained(qiContext: QiContext) {
        // The robot focus is gained.
        if (!init) {
            runOnUiThread {
                setContentView(R.layout.activity_main)
            }

            initClass = Init(qiContext)
            initClass.fullInit(this)
            init = true

            val webInterface = Webinterface(config)

            experimentHandler = ExperimentHandler(experiment, lookAt, moveTo, animation, speech, display, documentation)

            webInterface.setExperimentHandler(experimentHandler)

            experimentHandler.registerObserver(webInterface)
            webInterface.registerObserver(experimentHandler)
            webInterface.startServer(assets, experiment)
        } else {
            resume()
        }
    }

    override fun onRobotFocusLost() {
        // The robot focus is lost.
        storeState()
    }

    override fun onRobotFocusRefused(reason: String) {
        // The robot focus is refused.
    }

    private fun resume() {
        experimentHandler.resumeExperiment()
    }

    private fun storeState() {
        experimentHandler.pauseExperiment()
    }

    fun setBaseFrame(baseFrame: FreeFrame) {
        this.baseFrame = baseFrame
    }

    fun setSpeech(speech: Speech) {
        this.speech = speech
    }

    fun setLookAt(lookAt: LookAtTarget) {
        this.lookAt = lookAt
    }

    fun setConfig(config: Config) {
        this.config = config
    }

    fun setExperiment(experiment: ExperimentLoader) {
        this.experiment = experiment
    }

    fun setDisplay(display: Display) {
        this.display = display
    }

    fun setMoveTo(moveTo: MoveToTarget) {
        this.moveTo = moveTo
    }

    fun setAnimation(animation: Animation) {
        this.animation = animation
    }

    fun setDocumentation(documentation: Documentation) {
        this.documentation = documentation
    }
}