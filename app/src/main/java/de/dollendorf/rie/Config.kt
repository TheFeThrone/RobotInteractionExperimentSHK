package de.dollendorf.rie

import kotlinx.serialization.json.Json
import java.io.FileNotFoundException

class Config(path: String) : JsonHandler(path) {

    fun loadConfig(){
        try {
            super.loadData()
        }
        catch (e: FileNotFoundException) {
            println("Loading default values.")
            loadDefaults()
        }
    }

    private fun loadDefaults() {
        val config = Json.parseToJsonElement("""{"experiment": "experiment.json"}""")
        super.writeData(config.toString())
    }
}