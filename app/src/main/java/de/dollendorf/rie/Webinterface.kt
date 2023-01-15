package de.dollendorf.rie

import android.content.res.AssetManager
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException

class Webinterface(private val port: Int) {

    private val sessions = ArrayList<WebSocketServerSession>()

    fun startServer(assets: AssetManager, experimentHandler: ExperimentHandler) {

        val vue = assets.open("webinterface/js/vue.js").bufferedReader().use { it.readText() }
        val html = assets.open("webinterface/index.html").bufferedReader().use { it.readText() }
        val css = assets.open("webinterface/css/style.css").bufferedReader().use { it. readText() }

        val server = embeddedServer(Netty, port) {
            install(WebSockets)
            routing {
                get("/") {
                    call.respondText(html, ContentType.Text.Html)
                }
                get("/js/vue.js") {
                    call.respondText(vue, ContentType.Text.JavaScript)
                }
                get("/css/style.css") {
                    call.respondText(css, ContentType.Text.CSS)
                }
                get("/favicon.png") {
                    call.respond(200)
                }
                get("/data") {
                    val items = experimentHandler.getItems()
                    var retVal = "{\"steps\": ["
                    for ((counter, item) in items!!.withIndex()) {
                        retVal = "$retVal{\"index\": $counter, \"item\": \"$item\"}, "
                    }
                    retVal = retVal.substringBeforeLast(",")
                    retVal = "$retVal]}"
                    call.respondText(retVal)
                }
                webSocket("/websocket") {
                    try {
                        sessions.add(this)
                        for (frame in incoming) {
                            println((frame as Frame.Text).readText())
                        }
                    }
                    catch (e: ClosedReceiveChannelException) {
                        println("Client disconnected.")
                        sessions.remove(this)
                    }
                    catch (e: Throwable) {
                        println("Error")
                    }
                }
            }
        }
        server.start(true)
    }

    suspend fun send(text: String) {
        for (session in sessions) {
            session.outgoing.send(Frame.Text(text))
        }
    }
}