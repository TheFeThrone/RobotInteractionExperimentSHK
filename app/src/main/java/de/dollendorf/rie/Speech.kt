package de.dollendorf.rie

import com.aldebaran.qi.Future
import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.`object`.conversation.Phrase
import com.aldebaran.qi.sdk.`object`.conversation.Say
import com.aldebaran.qi.sdk.`object`.locale.Locale
import com.aldebaran.qi.sdk.builder.SayBuilder

class Speech(private val qiContext: QiContext, private val locale: Locale) {

    private var sayFuture: Future<Void>? = null

    fun say(text: String): Future<Void>? {
        val say: Say = SayBuilder.with(qiContext).withPhrase(Phrase(text)).withLocale(locale).build()
        sayFuture = say.async().run()
        return sayFuture
    }
}