package no.nav.toi.kandidatesproxy

import no.nav.security.token.support.core.configuration.IssuerProperties
import no.nav.toi.kandidatesproxy.LokalApplikasjon.startAppForTest
import java.net.URL

fun main() {
    startAppForTest()
}

object LokalApplikasjon {
    private var javalinServerStartet = false

    fun startAppForTest() {
        EsMock.startEsMock()

        val env = mapOf(
            "OPEN_SEARCH_USERNAME" to "dummy_username",
            "OPEN_SEARCH_PASSWORD" to "dummy_password",
            "OPEN_SEARCH_URI" to "http://localhost:9000"
        )

        if (!javalinServerStartet) {
            startApp(env, listOf(issuerProperties))
            javalinServerStartet = true
        }
    }


    private val issuerProperties = IssuerProperties(
        URL("http://localhost:18300/gyldig-issuer/.well-known/openid-configuration"),
        listOf("audience"),
        "gyldig-issuer"
    )
}
