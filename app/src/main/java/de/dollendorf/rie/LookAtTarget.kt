package de.dollendorf.rie

import com.aldebaran.qi.Future
import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.`object`.actuation.*
import com.aldebaran.qi.sdk.`object`.geometry.Quaternion
import com.aldebaran.qi.sdk.`object`.geometry.Vector3
import com.aldebaran.qi.sdk.builder.LookAtBuilder
import com.aldebaran.qi.sdk.builder.TransformBuilder

class LookAtTarget(private val qiContext: QiContext, private val baseFrame: FreeFrame) {

    private var lookAtFuture: Future<Void>? = null

    fun startLookAt(targetX: Double, targetY: Double, targetZ: Double): Future<Void>? {
        val transform = TransformBuilder.create().fromTranslation(Vector3(targetX, targetY, targetZ))
        val target = qiContext.mapping.makeFreeFrame()
        target.update(baseFrame.frame(), transform, 0L)
        val lookAt: LookAt = LookAtBuilder.with(qiContext).withFrame(target.frame()).build()
        lookAt.policy = LookAtMovementPolicy.HEAD_AND_BASE
        lookAtFuture = lookAt.async().run()
        return lookAtFuture
    }
}