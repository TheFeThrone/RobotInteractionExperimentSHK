package de.dollendorf.rie

import android.content.pm.PackageManager
import android.os.Environment
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.`object`.geometry.Vector3
import com.aldebaran.qi.sdk.`object`.holder.Holder
import com.aldebaran.qi.sdk.`object`.locale.Language
import com.aldebaran.qi.sdk.`object`.locale.Locale
import com.aldebaran.qi.sdk.`object`.locale.Region
import com.aldebaran.qi.sdk.builder.TransformBuilder
import java.io.File


class Init(private val qiContext: QiContext) {

    private lateinit var holder: Holder

    fun fullInit(mainActivity: MainActivity) {
        if (!File("${Environment.getExternalStorageDirectory()}/RIE/Logs").isDirectory || !File("${Environment.getExternalStorageDirectory()}/RIE/AudioFiles").isDirectory || !File("${Environment.getExternalStorageDirectory()}/RIE/Pictures").isDirectory || !File("${Environment.getExternalStorageDirectory()}/RIE/Animations").isDirectory) {
            try {
                File("${Environment.getExternalStorageDirectory()}/RIE/Animations").mkdirs()
                File("${Environment.getExternalStorageDirectory()}/RIE/AudioFiles").mkdirs()
                File("${Environment.getExternalStorageDirectory()}/RIE/Pictures").mkdirs()
                File("${Environment.getExternalStorageDirectory()}/RIE/Logs").mkdirs()
            } catch (_: Exception) {

            }
        }
        val baseFrame = qiContext.mapping.makeFreeFrame()
        val robotFrame = qiContext.actuation.robotFrame()
        val transform = TransformBuilder.create().fromTranslation(Vector3(0.0,0.0,0.0))
        while (mainActivity.findViewById<ImageView>(R.id.experimentPicture) == null) {
            Thread.sleep(10)
        }
        val display = Display(mainActivity.findViewById(R.id.experimentPicture), mainActivity)
        baseFrame.update(robotFrame, transform, 0L)
        if (ContextCompat.checkSelfPermission(mainActivity, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mainActivity, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE), 100)
        }
        val config = Config("config.json")
        config.loadConfig()
        mainActivity.setConfig(config)
        val experimentFile = config.getElement("experiment")
        if (experimentFile != null) {
            val experiment = ExperimentLoader(experimentFile)
            experiment.loadExperiment()
            mainActivity.setExperiment(experiment)
        }
        mainActivity.setBaseFrame(baseFrame)

        val locale = if (config.getElement("language") == "de_DE") {
            Locale(Language.GERMAN, Region.GERMANY)
        } else {
            Locale(Language.ENGLISH, Region.UNITED_STATES)
        }

        val autonomousAbilitiesBoolean = config.getElement("autonomous_activity").toBoolean()
        AutonomousAbilitiesToggle().toggleAutonomousAbilities(autonomousAbilitiesBoolean, qiContext, holder)

        mainActivity.setSpeech(Speech(qiContext, locale, mainActivity, config.getElement("speech_speed")!!.toInt(), config.getElement("speech_pitch")!!.toInt()))
        mainActivity.setLookAt(LookAtTarget(qiContext, baseFrame))
        mainActivity.setMoveTo(MoveToTarget(qiContext, baseFrame))
        mainActivity.setAnimation(Animation(qiContext))
        mainActivity.setDisplay(display)
        mainActivity.setDocumentation(Documentation(experimentFile!!))
    }
}