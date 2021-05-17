import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.html.*
import kotlinx.html.dom.create
import kotlinx.html.js.onClickFunction
import org.w3c.dom.HTMLInputElement
import org.w3c.xhr.XMLHttpRequest

fun main() {
    window.onload = {
        val btn = document.getElementById("shorten-button")!!
        btn.addEventListener("click", {
            val input = (document.getElementById("shorten-input") as HTMLInputElement)

            val http = XMLHttpRequest()
            http.open("GET", "/shorten?value=${encodeURIComponent(input.value)}")

            http.onload = {
                if (http.status in 200..399) {
                    input.apply {
                        value = http.responseText
                        setAttribute("readonly", "true")
                    }

                    btn.replaceWith(document.create.button {
                        id = "shorten-button"
                        classes = setOf("copy-button")
                        type = ButtonType.submit

                        onClickFunction = {


                            (document.getElementById("shorten-input") as HTMLInputElement).apply {
                                select()
                                setSelectionRange(0, 99999)
                            }
                            document.execCommand("copy")
                        }

                        span {
                            id = "shorten-button-text"
                            +"Copy"
                        }
                    })
                }
            }

            http.send()
        })
    }
}

external fun encodeURIComponent(s: String): String