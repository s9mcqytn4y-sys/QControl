package id.primaraya.qcontrol.data.remote.http

import id.primaraya.qcontrol.konfigurasi.KonfigurasiAplikasi
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

fun buatKlienHttpAplikasi(): HttpClient {
    return HttpClient(CIO) {
        install(HttpRequestRetry) {
            retryOnServerErrors(maxRetries = 3)
            retryOnException(maxRetries = 3, retryOnTimeout = true)
            exponentialDelay(baseDelayMs = 500, maxDelayMs = 5_000)
            retryIf(maxRetries = 3) { _, response ->
                response.status.value == 429 || response.status.value in 500..599
            }
        }

        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
            })
        }
        
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.INFO
        }
        
        install(HttpTimeout) {
            requestTimeoutMillis = 15000
            connectTimeoutMillis = 10000
            socketTimeoutMillis = 15000
        }
        
        defaultRequest {
            url(KonfigurasiAplikasi.URL_SERVER_DEFAULT)
        }
    }
}
