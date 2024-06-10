package de.dollendorf.rie

import android.media.MediaPlayer
import android.os.Environment
import com.aldebaran.qi.Future
import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.`object`.conversation.Phrase
import com.aldebaran.qi.sdk.`object`.conversation.Say
import com.aldebaran.qi.sdk.`object`.locale.Locale
import com.aldebaran.qi.sdk.builder.SayBuilder
import com.aldebaran.qi.sdk.`object`.conversation.BodyLanguageOption
import java.io.FileDescriptor

class Speech(private val qiContext: QiContext, private val locale: Locale, private val mainActivity: MainActivity, private val speed: Int, private val pitch: Int, private val movement: Boolean) {

    private var sayFuture: Future<Void>? = null

    fun say(text: String, movement: Boolean): Future<Void>? {
        val say: Say
        if (!movement){
            say = SayBuilder.with(qiContext).withPhrase(Phrase("\\rspd=$speed\\ \\vct=$pitch\\ $text")).withBodyLanguageOption(
                BodyLanguageOption.DISABLED).withLocale(locale).build()
        } else{
            say = SayBuilder.with(qiContext).withPhrase(Phrase("\\rspd=$speed\\ \\vct=$pitch\\ $text")).withLocale(locale).build()
        }

        sayFuture = say.async().run()
        return sayFuture
    }

    fun play(audioPath: String) {
        var success = false
        var counter = 0
        while (!success && counter < 3) {
            try {
                val mediaPlayer = MediaPlayer()
                mediaPlayer.setDataSource("${Environment.getExternalStorageDirectory()}/RIE/AudioFiles/$audioPath")
                mediaPlayer.prepare()
                mediaPlayer.start()
                while (mediaPlayer.isPlaying) {
                    Thread.sleep(50)
                }
                mediaPlayer.release()
                success = true
            } catch (_: Exception) {
                counter++
            }
        }
    }
}