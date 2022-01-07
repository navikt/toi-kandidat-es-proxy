package no.nav.toi.kandidatesproxy

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.authentication
import com.github.kittinunf.result.Result
import io.javalin.http.Context
import io.javalin.http.HttpCode

class SøkeController(
    username: String,
    password: String,
    url: String,
) {
    val søkPåIndeks: (Context) -> Unit = { ctx ->
        val indeks = ctx.pathParam("indeks")
        val searchUrl = "$url/$indeks/_search"

        val (_, response, result) = Fuel
            .post(searchUrl)
            .authentication().basic(username, password)
            .body(ctx.body())
            .response()

        when (result) {
            is Result.Success -> {
                ctx.json(response.body())
                ctx.status(response.statusCode)
            }

            is Result.Failure -> {
                log.error("Klarte ikke å videresende kall til url $searchUrl", result.error.message)

                ctx.status(HttpCode.SERVICE_UNAVAILABLE)
            }
        }
    }
}
