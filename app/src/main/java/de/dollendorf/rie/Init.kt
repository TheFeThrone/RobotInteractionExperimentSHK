package de.dollendorf.rie

import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.`object`.geometry.Vector3
import com.aldebaran.qi.sdk.`object`.locale.Language
import com.aldebaran.qi.sdk.`object`.locale.Locale
import com.aldebaran.qi.sdk.`object`.locale.Region
import com.aldebaran.qi.sdk.builder.TransformBuilder

class Init(private val qiContext: QiContext) {
    fun fullInit(mainActivity: MainActivity) {
        val baseFrame = qiContext.mapping.makeFreeFrame()
        val robotFrame = qiContext.actuation.robotFrame()
        val transform = TransformBuilder.create().fromTranslation(Vector3(0.0,0.0,0.0))
        baseFrame.update(robotFrame, transform, 0L)
        if (ContextCompat.checkSelfPermission(mainActivity, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mainActivity, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE), 100)
        }
        mainActivity.setBaseFrame(baseFrame)
        mainActivity.setSpeech(Speech(qiContext, Locale(Language.GERMAN, Region.GERMANY)))
        mainActivity.setLookAt(LookAtTarget(qiContext, baseFrame))
    }
}