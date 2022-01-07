package no.nav.toi.kandidatesproxy

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.authentication
import com.github.kittinunf.result.Result
import io.javalin.http.Context

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
            .responseString()

        when (result) {
            is Result.Success -> {
                ctx.json(result.get())
                ctx.status(response.statusCode)
            }

            is Result.Failure -> {
                ctx.json(result.error.response.body())
                ctx.status(response.statusCode)
            }
        }
    }
}
