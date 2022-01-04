package no.nav.toi.kandidatesproxy

import org.mockserver.integration.ClientAndServer
import org.mockserver.model.HttpRequest.request
import org.mockserver.model.HttpResponse.response

object EsMock {

    var serverHasStarted = false

    fun startEsMock() {
        if (serverHasStarted) return
        ClientAndServer.startClientAndServer(9000)
            .`when`(
                request()
                    .withMethod("GET")
                    .withPath(".*")
            )
            .respond(
                response()
                    .withStatusCode(200)
                    .withBody(jsonResultat)
            )
        serverHasStarted = true
    }

    val jsonResultat = ""
}