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

class Webinterface(private val port: Int) : ExperimentController(), ExperimentObserverInterface {

    private val sessions = ArrayList<WebSocketServerSession>()
    private lateinit var experimentHandler: ExperimentHandler

    fun startServer(assets: AssetManager, experimentLoader: ExperimentLoader) {

        val server = embeddedServer(Netty, port) {
            install(WebSockets)
            routing {
                get("/") {
                    call.respondText(assets.open("webinterface/index.html").bufferedReader().use { it.readText() }, ContentType.Text.Html)
                }
                get("index.html") {
                    call.respondText(assets.open("webinterface/index.html").bufferedReader().use { it.readText() }, ContentType.Text.Html)
                }
                get("experiment.html") {
                    call.respondText(assets.open("webinterface/experiment.html").bufferedReader().use { it.readText() }, ContentType.Text.Html)
                }
                get("config.html") {
                    call.respondText(assets.open("webinterface/config.html").bufferedReader().use { it.readText() }, ContentType.Text.Html)
                }
                get("/js/vue.js") {
                    call.respondText(assets.open("webinterface/js/vue.js").bufferedReader().use { it.readText() }, ContentType.Text.JavaScript)
                }
                get("/css/style.css") {
                    call.respondText(assets.open("webinterface/css/style.css").bufferedReader().use { it.readText() }, ContentType.Text.CSS)
                }
                get("/data") {
                    call.respondText(experimentLoader.getFullData())
                }
                get("/currentstate") {
                    call.respondText(experimentHandler.getRunningState().toString())
                }
                webSocket("/websocket") {
                    try {
                        sessions.add(this)
                        for (frame in incoming) {
                            val command = (frame as Frame.Text).readText()
                            triggerCommand(command)
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

    private suspend fun send(text: String) {
        for (session in sessions) {
            session.outgoing.send(Frame.Text(text))
        }
    }

    override fun updateExperimentState(experimentState: ExperimentState) {
        sendText(Json.encodeToString(experimentState))
    }

    override fun updateRunningState(state: Int) {
        sendText("new-state $state")
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun sendText(text: String) {
        GlobalScope.async { send(text) }
    }

    fun setExperimentHandler(experimentHandler: ExperimentHandler) {
        this.experimentHandler = experimentHandler
    }
}