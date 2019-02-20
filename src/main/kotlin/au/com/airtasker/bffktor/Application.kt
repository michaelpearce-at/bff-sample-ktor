package au.com.airtasker.bffktor

import au.com.airtasker.bffktor.bff.BffAdapter
import au.com.airtasker.bffktor.deserializers.LocalDateDeserializer
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.module.SimpleModule
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.features.BrowserUserAgent
import io.ktor.client.features.json.JacksonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.logging.Logging
import io.ktor.features.ContentNegotiation
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.jackson
import io.ktor.request.header
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.routing
import java.time.LocalDate

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }

    val client = HttpClient(Apache) {
        install(JsonFeature) {
            serializer = JacksonSerializer {
                propertyNamingStrategy = PropertyNamingStrategy.SNAKE_CASE
                val module = SimpleModule().apply { addDeserializer(LocalDate::class.java, LocalDateDeserializer()) }
                registerModule(module)
            }
        }
        install(Logging) {
            level = LogLevel.HEADERS
        }
        BrowserUserAgent()
    }

    val adapter = BffAdapter(client)

    routing {
        get("/bff/claimable") {
            val authToken = call.request.header("X-Auth-Token")
            if (authToken.isNullOrEmpty()) {
                call.respond(HttpStatusCode.Unauthorized)
            } else {
                call.respond(adapter.claimable(authToken))
            }
        }
    }
}
