package de.dollendorf.rie.robot.activities

import com.aldebaran.qi.Future
import com.aldebaran.qi.QiException
import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.`object`.actuation.*
import com.aldebaran.qi.sdk.`object`.geometry.Vector3
import com.aldebaran.qi.sdk.builder.GoToBuilder
import com.aldebaran.qi.sdk.builder.TransformBuilder

class MoveToTarget(private val qiContext: QiContext, private val baseFrame: FreeFrame) {

    private var moveToFuture: Future<Void>? = null
    private lateinit var targetFrame: FreeFrame

    fun startMoveTo(targetX: Double, targetY: Double, targetZ: Double): Future<Void>? {
        moveToFuture?.requestCancellation()
        val transform = TransformBuilder.create().fromTranslation(Vector3(targetX, targetY, targetZ))
        targetFrame = qiContext.mapping.makeFreeFrame()
        targetFrame.update(baseFrame.frame(), transform, 0L)
        val moveTo: GoTo = GoToBuilder.with(qiContext).withFrame(targetFrame.frame()).withPathPlanningPolicy(PathPlanningPolicy.GET_AROUND_OBSTACLES).withFinalOrientationPolicy(OrientationPolicy.FREE_ORIENTATION).withMaxSpeed(0.35f).build()
        try {
            moveToFuture = moveTo.async().run()
        } catch (e: QiException) {
            println("Invalid coordinates")
        }
        return moveToFuture
    }
}