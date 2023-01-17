package de.dollendorf.rie

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.QiSDK
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks
import com.aldebaran.qi.sdk.`object`.actuation.FreeFrame
import com.aldebaran.qi.sdk.design.activity.RobotActivity


class MainActivity : RobotActivity(), RobotLifecycleCallbacks {
    private lateinit var initClass: Init
    private lateinit var baseFrame: FreeFrame
    private lateinit var lookAt: LookAtTarget
    private lateinit var speech: Speech
    private lateinit var config: Config
    private lateinit var experiment: ExperimentLoader
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
            initClass = Init(qiContext)
            initClass.fullInit(this)
            runOnUiThread {
                setContentView(R.layout.activity_main)
            }
            init = true
            val experimentObserver = ExperimentObserver()
            val webInterface = Webinterface(8080)
            experimentObserver.registerObserver(webInterface)
            val experimentHandler = ExperimentHandler(experiment, lookAt, speech, webInterface, experimentObserver)
            experimentHandler.start()
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
        println("Resumed")
    }

    private fun storeState() {
        println("Storing state")
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
}