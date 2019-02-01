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
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import io.ktor.network.util.ioCoroutineDispatcher
import io.ktor.request.receiveMultipart
import io.ktor.response.respond
import io.ktor.response.respondFile
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import java.io.File
import java.io.InputStream
import java.io.OutputStream

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

    if (!versionFile.exists())
        versionFile.createNewFile()

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
            val version = call.parameters["version"]
            val multipart = call.receiveMultipart()
            multipart.forEachPart { part ->
                when (part) {
                    is PartData.FileItem -> {
                        val file = File(appFiles, "crobot-app-$version.jar")
                        part.streamProvider().use { input -> file.outputStream().buffered().use { output -> input.copyToSuspend(output) } }
                    }
                }

                part.dispose()
            }
            versionFile.appendText(System.lineSeparator() + version)
        }
    }
}

suspend fun InputStream.copyToSuspend(
        out: OutputStream,
        bufferSize: Int = DEFAULT_BUFFER_SIZE,
        yieldSize: Int = 4 * 1024 * 1024,
        dispatcher: CoroutineDispatcher = ioCoroutineDispatcher
): Long {
    return withContext(dispatcher) {
        val buffer = ByteArray(bufferSize)
        var bytesCopied = 0L
        var bytesAfterYield = 0L
        while (true) {
            val bytes = read(buffer).takeIf { it >= 0 } ?: break
            out.write(buffer, 0, bytes)
            if (bytesAfterYield >= yieldSize) {
                yield()
                bytesAfterYield %= yieldSize
            }
            bytesCopied += bytes
            bytesAfterYield += bytes
        }
        return@withContext bytesCopied
    }
}
