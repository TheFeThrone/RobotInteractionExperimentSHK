package de.dollendorf.rie

import android.os.Environment
import com.aldebaran.qi.Future
import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.`object`.actuation.*
import com.aldebaran.qi.sdk.builder.AnimateBuilder
import com.aldebaran.qi.sdk.builder.AnimationBuilder
import java.io.File
import java.nio.charset.Charset

class Animation(private val qiContext: QiContext) {

    private var animationFuture: Future<Void>? = null

    fun startAnimation(animation: String): Future<Void>? {
        animationFuture?.requestCancellation()

        return if (!animation.endsWith(".anim")) {
            startPointAtAnimation(animation)
        } else {
            startCustomAnimation(animation)
        }
    }

    private fun startPointAtAnimation(animation: String): Future<Void> {
        val pointAtEnum = try {
            PointAtAnimation.valueOf(animation.uppercase())
        } catch (e: IllegalArgumentException) {
            println("Error: $animation is not a valid PointAtAnimation enum name.")
            return Future.of(null) // Return a failed future or a specific error future
        }
        val animationName = pointAtEnum.selectPointAtAnimation()
        val animationString ="pointanims/$animationName"
        return buildAndRunAnimation(animationString)
    }

    private fun startCustomAnimation(animation: String): Future<Void> {
        val animationString = File("${Environment.getExternalStorageDirectory()}/RIE/Animations/$animation").readText(Charset.defaultCharset())
        return buildAndRunAnimation(animationString)
    }

    private fun buildAndRunAnimation(animationData: String): Future<Void> {
        val animationBuild = AnimationBuilder.with(qiContext).withTexts(animationData).build()
        val animate = AnimateBuilder.with(qiContext).withAnimation(animationBuild).build()
        return animate.async().run().also { animationFuture = it }
    }
}
