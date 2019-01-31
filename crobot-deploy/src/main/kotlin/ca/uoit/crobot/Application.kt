package ca.uoit.crobot

import com.google.gson.GsonBuilder
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.AutoHeadResponse
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.gson.GsonConverter
import io.ktor.gson.gson
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.response.respondFile
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import java.io.File

fun Application.main() {
    install(DefaultHeaders)
    install(AutoHeadResponse)
    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
            register(ContentType.Application.Json, GsonConverter(GsonBuilder().apply {
            }.create()))
        }
    }

    val appFiles = File("files")
    val versionFile = File("files/version.txt")

    if (!appFiles.exists())
        appFiles.mkdirs()

    println(appFiles.absolutePath)

    routing {
        get("/download") {
            val ver = call.parameters["version"]
            val file = File(appFiles, "crobot-app-$ver.jar")

            if (file.exists()) {
                call.respondFile(file)
            } else {
                call.respond(HttpStatusCode.NotFound, "Version $ver does not exist")
            }
        }

        get("/version") {
            if (versionFile.exists()) {
                val lines = versionFile.readLines()
                call.respond(lines.lastOrNull() ?: "null")
            }
        }

        post("/deploy") {

        }
    }
}
