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
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Webinterface(private val port: Int) : ExperimentObserverInterface {

    private val sessions = ArrayList<WebSocketServerSession>()

    fun startServer(assets: AssetManager, experimentLoader: ExperimentLoader) {

        val vue = assets.open("webinterface/js/vue.js").bufferedReader().use { it.readText() }
        val html = assets.open("webinterface/index.html").bufferedReader().use { it.readText() }
        val css = assets.open("webinterface/css/style.css").bufferedReader().use { it.readText() }

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
                get("/data") {
                    call.respondText(experimentLoader.getFullData())
                }
                webSocket("/websocket") {
                    try {
                        sessions.add(this)
                        for (frame in incoming) {
                            println((frame as Frame.Text).readText())
                        }
                    } catch (e: ClosedReceiveChannelException) {
                        println("Client disconnected.")
                    } catch (e: Throwable) {
                        println("Error in websocket connection.")
                    }
                    finally {
                        sessions.remove(this)
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

    @OptIn(DelicateCoroutinesApi::class)
    override fun updateExperimentState(experimentState: ExperimentState) {
        val text = Json.encodeToString(experimentState)
        GlobalScope.async { send(text) }
    }
}