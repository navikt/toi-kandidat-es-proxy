package no.nav.toi.kandidatesproxy

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.Method
import com.github.kittinunf.fuel.core.extensions.authentication
import com.github.kittinunf.result.Result
import io.javalin.http.Context

class ProxyController(
    username: String,
    password: String,
    url: String,
) {
    val proxyKall: (Context) -> Unit = { ctx ->
        val (_, response, result) = Fuel
            .request(
                method = Method.valueOf(ctx.method()),
                path = url + ctx.path()
            )
            .authentication().basic(username, password)
            .body(ctx.body())
            .responseString()

        when (result) {
            is Result.Success -> {
                ctx.json(result.get())
                ctx.status(response.statusCode)
            }

            is Result.Failure -> {
                ctx.json(String(result.error.response.data))
                ctx.status(response.statusCode)
            }
        }
    }
}
