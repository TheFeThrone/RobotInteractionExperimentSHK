package de.dollendorf.rie

import android.content.res.AssetManager
import android.os.Environment
import android.provider.ContactsContract.Directory
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.request.*
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
import java.io.File
import java.io.FileNotFoundException
import java.nio.charset.Charset

class Webinterface(private val config: Config) : ExperimentController(), ExperimentObserverInterface {

    private val sessions = ArrayList<WebSocketServerSession>()
    private lateinit var experimentHandler: ExperimentHandler

    fun startServer(assets: AssetManager, experimentLoader: ExperimentLoader, documentation: Documentation) {

        val server = embeddedServer(Netty, config.getElement("port")!!.toInt()) {
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
                get("/experiment") {
                    try {
                        val response = File("${Environment.getExternalStorageDirectory()}/RIE/${call.request.queryParameters["name"]}").readText(Charset.defaultCharset())
                        call.respondText(response)
                    } catch (e: FileNotFoundException) {
                        call.respond(404)
                    }
                }
                put("/experiment") {
                    try {
                        val file = File("${Environment.getExternalStorageDirectory()}/RIE/${call.request.queryParameters["name"]}");
                        if (file.exists()) {
                            file.writeText(call.receiveText())
                        } else {
                            file.createNewFile()
                            file.writeText("""{"experiment": {"name": "${call.request.queryParameters["name"]}"},"sequence": {"order": "empty_0", "empty_0": {"name": "empty_0", "friendly_name": "Empty", "value": null, "stopping": true, "requires_user_interaction": false, "possibilities": {"order": ""}}}}""")
                        }
                        call.respondText(getExperiments())
                    } catch (e: FileNotFoundException) {
                        call.respond(404)
                    }
                }
                put("/file") {
                    try {
                        val fileName = call.request.queryParameters["name"]
                        var type = ""
                        when(call.request.queryParameters["type"]) {
                            "animation" -> type = "Animations"
                            "sound" -> type = "AudioFiles"
                            "picture" -> type = "Pictures"
                        }
                        val multiPart = call.receiveMultipart()
                        multiPart.forEachPart { part ->
                            if (part is PartData.FileItem) {
                                val file = File("${Environment.getExternalStorageDirectory()}/RIE/$type/$fileName")
                                part.streamProvider().use { its ->
                                    file.outputStream().buffered().use {
                                        its.copyTo(it)
                                    }
                                }
                            }
                            part.dispose()
                        }
                        call.respondText("Success")
                    } catch (e: FileNotFoundException) {
                        call.respond(404)
                    }
                }
                get("/experiments") {
                    call.respondText(getExperiments())
                }
                get("/currentstate") {
                    call.respondText(experimentHandler.getRunningState().toString())
                }
                get("/config") {
                    call.respondText(config.getFullData())
                }
                get("/log") {
                    call.respondFile(File("${Environment.getExternalStorageDirectory()}/RIE/Logs/${documentation.getCurrentFileName()}"))
                }
                get("/logname") {
                    call.respondText(documentation.getCurrentFileName())
                }
                get("/restart") {
                    call.respondText("Ok")
                    updateRunningState(0)
                }
                put("/config") {
                    val newValue = call.receiveText()
                    if (config.getFullData().replace(Regex(" |\n"), "") != newValue) {
                        config.writeData(newValue)
                    }
                    call.respondText(config.getFullData())
                }
                get("/js/config.js") {
                    call.respondText(assets.open("webinterface/js/config.js").bufferedReader().use { it.readText() }, ContentType.Text.JavaScript)
                }
                get("/js/experiment.js") {
                    call.respondText(assets.open("webinterface/js/experiment.js").bufferedReader().use { it.readText() }, ContentType.Text.JavaScript)
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

    fun getExperiments(): String {
        val experiments = File("${Environment.getExternalStorageDirectory()}/RIE/").listFiles()
        var list = ""
        for (experiment in experiments!!) {
            if (!experiment.isDirectory && experiment.name != "config.json") {
                list = if (list == "") {
                    experiment.name
                } else {
                    "$list,${experiment.name}"
                }
            }
        }
        return list
    }
}