package no.nav.toi.kandidatesproxy

import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.get
import org.slf4j.Logger
import org.slf4j.LoggerFactory

val Any.log: Logger
    get() = LoggerFactory.getLogger(this::class.java)

fun log(name: String): Logger = LoggerFactory.getLogger(name)

val port = 8300
val aliveUrl = "/internal/isAlive"
val readyUrl = "/internal/isReady"

fun startApp() {
    val javalin = Javalin.create {
        it.defaultContentType = "application/json"
    }

    javalin.routes {
        get(aliveUrl) { it.status(200) }
        get(readyUrl) { it.status(200) }
    }.start(port)

    javalin.exception(Exception::class.java) { e, ctx ->
        log("Main").error("Feil i kandidat-es-proxy", e)
    }
}

fun main() {
    startApp()
}
