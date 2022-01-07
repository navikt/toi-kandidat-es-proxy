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

fun startApp(env: Map<String, String>, issuerProperties: List<IssuerProperties>) {
    val javalin = Javalin.create {
        it.defaultContentType = "application/json"
        it.accessManager(styrTilgang(issuerProperties))
    }

    val username = env.hentMiljøvariabel("OPEN_SEARCH_USERNAME")
    val password = env.hentMiljøvariabel("OPEN_SEARCH_PASSWORD")
    val url = env.hentMiljøvariabel("OPEN_SEARCH_URI")
    val søkeController = SøkeController(username, password, url)

    javalin.routes {
        get("/internal/isAlive", { it.status(200) }, Rolle.ALLE)
        get("/internal/isReady", { it.status(200) }, Rolle.ALLE)
        post("/{indeks}/_search", søkeController.søkPåIndeks, Rolle.VEILEDER)
    }.start(8300)

    javalin.exception(Exception::class.java) { e, ctx ->
        log("Main").error("Feil i kandidat-es-proxy", e)
    }
}

private fun Map<String, String>.hentMiljøvariabel(variabel: String) =
    this[variabel] ?: throw RuntimeException("Fant ikke miljøvariabel $variabel")

fun main() {
    val envs = System.getenv()
    startApp(envs, hentIssuerProperties(envs))
}
