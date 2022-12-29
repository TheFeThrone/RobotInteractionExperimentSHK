package de.dollendorf.rie

import android.content.res.AssetManager
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File

class Webinterface(private val port: Int) {
    fun startServer(assets: AssetManager, experimentHandler: ExperimentHandler) {
        val vue = assets.open("webinterface/js/myBlog.js").bufferedReader().use { it.readText() }
        val html = assets.open("webinterface/index.html").bufferedReader().use { it.readText() }
        embeddedServer(Netty, 8080) {
            routing {
                get("/") {
                    call.respondText(html, ContentType.Text.Html)
                }
                get("/myBlog.js") {
                    call.respondText(vue, ContentType.Text.JavaScript)
                }
                get("/data") {
                    call.respondText("{\"data\": \"" + experimentHandler.getCurrentStep()!!+ "\"}")
                }
            }
        }.start(wait = false)
    }
}