package de.dollendorf.rie

import android.os.Environment
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import java.io.File
import java.io.FileNotFoundException
import java.nio.charset.Charset

open class JsonLoader(path: String) {

    private val file = File("${Environment.getExternalStorageDirectory()}/RIE/$path")
    private lateinit var items: JsonElement

    protected fun loadData() {
        try {
            items = Json.parseToJsonElement(file.readText(Charset.defaultCharset()))
        }
        catch (e: Exception) {
            throw FileNotFoundException()
        }
    }

    fun getElement(elementPath: String): String? {
        return try {
            val keys = elementPath.split("/")
            var item: JsonElement = items
            for (key in keys) {
                item = item.jsonObject[key]!!
            }
            item.toString().replace("\"", "")
        } catch (e: Exception) {
            null
        }
    }

    fun writeData(data: String) {
        items = Json.parseToJsonElement(data)
        if (!file.exists()) {
            File(file.path.substringBeforeLast("/")).mkdirs()
            file.createNewFile()
        }
        file.writeText(data)
    }
}