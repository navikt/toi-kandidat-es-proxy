package no.nav.toi.kandidatesproxy

import io.javalin.core.security.RouteRole
import io.javalin.http.Context
import io.javalin.http.ForbiddenResponse
import io.javalin.http.Handler
import no.nav.security.token.support.core.configuration.IssuerProperties
import no.nav.security.token.support.core.configuration.MultiIssuerConfiguration
import no.nav.security.token.support.core.http.HttpRequest
import no.nav.security.token.support.core.jwt.JwtTokenClaims
import no.nav.security.token.support.core.validation.JwtTokenValidationHandler

enum class Rolle : RouteRole {
    SYSTEMBRUKER,
    VEILEDER,
    ALLE
}

fun styrTilgang(handler: Handler, ctx: Context, roller: MutableSet<RouteRole>, issuerProperties: List<IssuerProperties>) {

    val autentisertingsmetode: Autentiseringsmetode = when {
        roller.contains(Rolle.ALLE) -> Autentiseringsmetode { true }
        roller.contains(Rolle.SYSTEMBRUKER) -> autentisertSystembruker
        roller.contains(Rolle.VEILEDER) -> autentisertVeileder
        else -> Autentiseringsmetode { false }
    }

    val tokenClaims = hentTokenClaims(ctx, issuerProperties)

    if (autentisertingsmetode.erAutentisert(tokenClaims)) {
        handler.handle(ctx)
    } else {
        throw ForbiddenResponse()
    }
}

fun interface Autentiseringsmetode {
    fun erAutentisert(claims: JwtTokenClaims?): Boolean
}

val autentisertSystembruker = Autentiseringsmetode { it?.get("sub") == it?.get("oid") }
val autentisertVeileder = Autentiseringsmetode { it?.get("NAVident")?.toString()?.isNotEmpty() ?: false }


private fun hentTokenClaims(ctx: Context, issuerProperties: List<IssuerProperties>) =
    lagTokenValidationHandler(issuerProperties)
        .getValidatedTokens(ctx.httpRequest)
        .anyValidClaims.orElseGet { null }

private fun lagTokenValidationHandler(issuerProperties: List<IssuerProperties>) =
    JwtTokenValidationHandler(
        MultiIssuerConfiguration(issuerProperties.associateBy { it.cookieName })
    )

private val Context.httpRequest: HttpRequest
    get() = object : HttpRequest {
        override fun getHeader(headerName: String?) = headerMap()[headerName]
        override fun getCookies() = cookieMap().map { (name, value) ->
            object : HttpRequest.NameValue {
                override fun getName() = name
                override fun getValue() = value
            }
        }.toTypedArray()
    }



