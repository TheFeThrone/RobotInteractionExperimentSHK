package de.dollendorf.rie

import java.io.FileNotFoundException

class ExperimentLoader(path: String) : JsonHandler(path) {

    fun loadExperiment() {
        try {
            super.loadData()
        }
        catch (e: FileNotFoundException) {
            println("Failed to load experiment file.")
        }
    }

}