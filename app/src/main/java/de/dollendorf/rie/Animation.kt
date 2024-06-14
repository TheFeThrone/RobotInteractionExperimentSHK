package de.dollendorf.rie

import android.content.res.AssetManager
import android.os.Environment

import android.util.Log
import com.aldebaran.qi.Future
import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.`object`.actuation.*
import com.aldebaran.qi.sdk.builder.AnimateBuilder
import com.aldebaran.qi.sdk.builder.AnimationBuilder
import java.io.File
import java.io.FileNotFoundException
import java.nio.charset.Charset

class Animation(private val qiContext: QiContext, assets: AssetManager) {

    private var animationFuture: Future<Void>? = null
    private val assetBrowser = assets

    fun startAnimation(animation: String): Future<Void> {
        animationFuture?.requestCancellation()

        return if (!animation.endsWith(".qianim")) {
            Log.w("pointAtAnimation","starting $animation")
            startPointAtAnimation(animation)
        } else {
            Log.w("customAnimation","starting $animation")
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
        val animationAssetString= assetBrowser.open("pointanims/$animationName").bufferedReader().use { it.readText() }
        return buildAndRunAnimation(animationAssetString)
    }

    private fun startCustomAnimation(animation: String): Future<Void> {
        animationFuture?.requestCancellation()

        val animationFile = readFile(animation)
        val animationString = animationFile.readText(Charset.defaultCharset())

        return buildAndRunAnimation(animationString)
    }

    private fun buildAndRunAnimation(animationData: String): Future<Void> {
        val animationBuild = AnimationBuilder.with(qiContext).withTexts(animationData).build()
        val animate = AnimateBuilder.with(qiContext).withAnimation(animationBuild).build()
        return animate.async().run().also { animationFuture = it }
    }

    private fun readFile(animationName : String): File {
        val fullPath = "${Environment.getExternalStorageDirectory()}/RIE/Animations/$animationName"

        val animationFile = File(fullPath)
        if (!animationFile.exists()) {
            throw FileNotFoundException("Animation file $fullPath not found.")
        }
        return animationFile
    }
}
