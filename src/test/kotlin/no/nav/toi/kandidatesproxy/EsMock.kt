package no.nav.toi.kandidatesproxy

import org.mockserver.integration.ClientAndServer
import org.mockserver.model.HttpRequest.request
import org.mockserver.model.HttpResponse.response

object EsMock {

    var serverHasStarted = false

    fun startEsMock() {
        if (serverHasStarted) return
        ClientAndServer.startClientAndServer(esMockPort)
            .`when`(
                request()
                    .withMethod("POST")
                    .withPath("*"),
            )
            .respond(
                response()
                    .withStatusCode(feilResultatStatusKode)
                    .withBody(jsonFeilResultat)
            )
        serverHasStarted = true
    }

    val esMockPort = 9000

    val feilResultatStatusKode = 403

    val jsonFeilResultat = """
        {
            "error": {
                "root_cause": [
                    {
                        "type": "index_not_found_exception",
                        "reason": "no such index [feilIndeks]",
                        "index": "feilIndeks",
                        "resource.id": "feilIndeks",
                        "resource.type": "index_or_alias",
                        "index_uuid": "_na_"
                    }
                ],
                "type": "index_not_found_exception",
                "reason": "no such index [feilIndeks]",
                "index": "feilIndeks",
                "resource.id": "feilIndeks",
                "resource.type": "index_or_alias",
                "index_uuid": "_na_"
            },
            "status": 404
        }
    """.trimIndent()
}