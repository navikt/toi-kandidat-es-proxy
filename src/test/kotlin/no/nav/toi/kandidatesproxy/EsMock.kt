package no.nav.toi.kandidatesproxy

import org.mockserver.integration.ClientAndServer
import org.mockserver.model.HttpRequest.request
import org.mockserver.model.HttpResponse.response

object EsMock {
    var serverHasStarted = false

    fun startEsMock() {
        if (serverHasStarted) return

        val client = ClientAndServer.startClientAndServer(esMockPort)

        client.`when`(
            request()
                .withMethod("POST")
                .withPath("/feilIndeks/_search"),
        )
            .respond(
                response()
                    .withStatusCode(feilResultatStatusKode)
                    .withBody(jsonFeilResultat)
            )

        client.`when`(
            request()
                .withMethod("POST")
                .withPath("/indeks/_search"),
        )
            .respond(
                response()
                    .withStatusCode(
                        korrektResultatStatusKode
                    )
                    .withBody(jsonResultat)
            )

        serverHasStarted = true
    }

    const val esMockPort = 9000

    const val feilResultatStatusKode = 403
    const val korrektResultatStatusKode = 200

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

    val jsonResultat = """
        {
            "masseData": 1
        }
    """.trimIndent()
}