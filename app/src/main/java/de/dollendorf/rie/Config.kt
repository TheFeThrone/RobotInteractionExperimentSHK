package de.dollendorf.rie

import kotlinx.serialization.json.Json
import java.io.FileNotFoundException

class Config(path: String) : JsonLoader(path) {

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
        val config = Json.parseToJsonElement("""{"experiment": "experiment.json", "language": "de_DE"}""")
        super.writeData(config.toString())
    }
}