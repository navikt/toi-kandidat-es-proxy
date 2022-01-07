package no.nav.toi.kandidatesproxy

import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.get
import io.javalin.apibuilder.ApiBuilder.post
import no.nav.security.token.support.core.configuration.IssuerProperties
import org.slf4j.Logger
import org.slf4j.LoggerFactory

val Any.log: Logger
    get() = LoggerFactory.getLogger(this::class.java)

fun log(name: String): Logger = LoggerFactory.getLogger(name)

fun startApp(issuerProperties: List<IssuerProperties>) {
    val javalin = Javalin.create {
        it.defaultContentType = "application/json"
        it.accessManager(styrTilgang(issuerProperties))
    }

    javalin.routes {
        get("/internal/isAlive", { it.status(200) }, Rolle.ALLE)
        get("/internal/isReady", { it.status(200) }, Rolle.ALLE)
        post("/{indeks}/_search", { it.status(200) }, Rolle.VEILEDER)
    }.start(8300)

    javalin.exception(Exception::class.java) { e, ctx ->
        log("Main").error("Feil i kandidat-es-proxy", e)
    }
}

fun main() {
    val envs = System.getenv()
    startApp(hentIssuerProperties(envs))
}
