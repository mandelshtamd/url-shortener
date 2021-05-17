import io.ktor.application.*
import io.ktor.html.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.html.*
import java.io.File
import java.util.*

fun Application.main() {
    routing {
        get("/") {
            call.respondHtml {
                head {
                    title {
                        +"Simple shortener"
                    }

                    link {
                        href = "/favicon.apng"
                        rel = "icon"
                    }
                    link {
                        href = "https://use.fontawesome.com/releases/v5.8.1/css/all.css"
                        rel = "stylesheet"
                    }
                    link {
                        href = "https://fonts.googleapis.com/css?family=Fira+Sans:300,400,600&display=swap"
                        rel = "stylesheet"
                    }
                    link {
                        href = "/css/shortener.css"
                        rel = "stylesheet"
                    }

                    script {
                        src = "/client.js"
                    }
                }

                body {
                    div("main-block") {
                        input {
                            id = "shorten-input"
                            classes = setOf("url-input")
                            type = InputType.url
                            placeholder = "Shorten your link"
                        }
                        button {
                            id = "shorten-button"
                            classes = setOf("url-button")
                            type = ButtonType.submit

                            span {
                                id = "shorten-button-text"
                                +"Go!"
                            }
                        }
                    }
                    div("footer-block") {
                        span("footer-text") {
                            +"Simple shortener"
                        }
                        span("footer-muted-text") {
                            +"Powered by Ktor"
                        }
                    }
                }
            }
        }

        static("/") {
            resources("/")
        }

        val random = Random()
        // TODO: use a database
        val urlToKey = mutableMapOf<String, String>()
        val keyToUrl = mutableMapOf<String, String>()

        get("/r") {
            val k = call.parameters["k"]!!

            val url = keyToUrl[k]

            println("got $k -> $url")

            when {
                url == null -> call.respond(HttpStatusCode.NotFound, "Unknown short URL")
                url.toString().startsWith("http") -> call.respondRedirect(url)
                else -> call.respondRedirect("https://$url")
            }
        }

        get("/shorten") {
            val value = call.parameters["value"]!!
            println("URL for shortening $value")

            val response = urlToKey.getOrPut(value) { random.nextLong().toULong().toString(16) }
            keyToUrl[response] = value

            println("saved $value -> $response")

            call.respondText { "http://localhost:8080/r?k=$response" }
        }
    }
}