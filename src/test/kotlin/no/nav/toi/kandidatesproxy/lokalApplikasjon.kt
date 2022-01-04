package no.nav.toi.kandidatesproxy

import no.nav.security.token.support.core.configuration.IssuerProperties
import java.net.URL

fun main() {
    EsMock.startEsMock()
    startApp(listOf(LokalApplikasjon.issuerProperties))
}

object LokalApplikasjon {

    private var javalinServerStartet = false

    fun startAppForTest() {
        EsMock.startEsMock()

        if (!javalinServerStartet) {
            startApp(listOf(issuerProperties))
            javalinServerStartet = true
        }
    }


    val issuerProperties = IssuerProperties(
        URL("http://localhost:18300/isso-idtoken/.well-known/openid-configuration"),
        listOf("audience"),
        "isso-idtoken"
    )
}
