package de.dollendorf.rie

import android.os.Environment
import java.io.File
import java.nio.charset.Charset
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

class Config(private val path: String) {

    private val configFile = File(Environment.getExternalStorageDirectory(), path)
    private lateinit var config: JsonElement

    fun loadConfig(): JsonElement {
        if (configFile.isFile) {
            config = Json.parseToJsonElement(configFile.readText(Charset.defaultCharset()))
        } else {
            loadDefaults()
        }
        return config
    }

    private fun loadDefaults() {
        config = Json.parseToJsonElement("""{}""")
        if (!configFile.exists()) {
            File(configFile.path.dropLast(12)).mkdirs()
            configFile.createNewFile()
        }
        configFile.writeText(config.toString())
    }
}