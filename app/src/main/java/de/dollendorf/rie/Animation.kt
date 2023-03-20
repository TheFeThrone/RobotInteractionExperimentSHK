package de.dollendorf.rie

import android.os.Environment
import com.aldebaran.qi.Future
import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.`object`.actuation.*
import com.aldebaran.qi.sdk.`object`.geometry.Quaternion
import com.aldebaran.qi.sdk.`object`.geometry.Vector3
import com.aldebaran.qi.sdk.builder.AnimateBuilder
import com.aldebaran.qi.sdk.builder.AnimationBuilder
import com.aldebaran.qi.sdk.builder.LookAtBuilder
import com.aldebaran.qi.sdk.builder.TransformBuilder
import java.io.File
import java.nio.charset.Charset

class Animation(private val qiContext: QiContext) {

    private var animationFuture: Future<Void>? = null

    fun startAnimation(animation: String): Future<Void>? {
        animationFuture?.requestCancellation()
        val animationString = File("${Environment.getExternalStorageDirectory()}/RIE/Animations/${animation}.qianim").readText(Charset.defaultCharset())
        val animationBuild = AnimationBuilder.with(qiContext).withTexts(animationString).build()
        val animate = AnimateBuilder.with(qiContext).withAnimation(animationBuild).build()
        animationFuture = animate.async().run()
        return animationFuture
    }
}