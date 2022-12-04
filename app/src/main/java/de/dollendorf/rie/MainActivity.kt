package de.dollendorf.rie

import android.os.Bundle
import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.QiSDK
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks
import com.aldebaran.qi.sdk.`object`.actuation.FreeFrame
import com.aldebaran.qi.sdk.`object`.geometry.Vector3
import com.aldebaran.qi.sdk.builder.TransformBuilder
import com.aldebaran.qi.sdk.design.activity.RobotActivity

class MainActivity : RobotActivity(), RobotLifecycleCallbacks {
    lateinit var baseFrame: FreeFrame
    lateinit var lookAt: LookAtTarget
    var init = false

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
            baseFrame = qiContext.mapping.makeFreeFrame()
            val robotFrame = qiContext.actuation.robotFrame()
            val transform = TransformBuilder.create().fromTranslation(Vector3(0.0,0.0,0.0))
            baseFrame.update(robotFrame, transform,0L)

            lookAt = LookAtTarget(qiContext, baseFrame)
            init = true
        }
        lookAt.startLookAt(5.0, 5.0, 5.0)
    }

    override fun onRobotFocusLost() {
        // The robot focus is lost.
    }

    override fun onRobotFocusRefused(reason: String) {
        // The robot focus is refused.
    }
}