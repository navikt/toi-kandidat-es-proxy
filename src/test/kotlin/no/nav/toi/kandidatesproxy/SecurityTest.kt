package no.nav.toi.kandidatesproxy

import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.extensions.authentication
import com.github.kittinunf.fuel.jackson.responseObject
import com.nimbusds.jose.util.Base64
import no.nav.security.mock.oauth2.MockOAuth2Server
import no.nav.security.mock.oauth2.token.DefaultOAuth2TokenCallback
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.net.InetAddress

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SecurityTest {
    private val mockOAuth2Server = MockOAuth2Server()

    private val isAliveUrl = "http://localhost:8300/internal/isAlive"
    private val isReadyUrl = "http://localhost:8300/internal/isReady"
    private val søkUrlSomKreverVeileder = "http://localhost:8300/${EsMock.indeks}/_search"
    private val proxyUrlSomKreverSystembruker = "http://localhost:8300/es"

    @BeforeAll
    fun init() {
        LokalApplikasjon.startAppForTest()
        mockOAuth2Server.start(InetAddress.getByName("localhost"), 18300)
    }

    @AfterAll
    fun teardown() {
        mockOAuth2Server.shutdown()
    }

    @Test
    fun `Kall med autentisert bruker mot beskyttet endepunkt skal returnere 200`() {
        val token = hentToken(mockOAuth2Server)
        val fuelHttpClient = FuelManager()

        val (_, response) = fuelHttpClient.post(søkUrlSomKreverVeileder)
            .authentication().bearer(token.serialize())
            .responseObject<String>()

        assertThat(response.statusCode).isEqualTo(200)
    }

    @Test
    fun `Kall uten autentisert bruker mot beskyttet endepunkt skal returnere 403`() {
        val fuelHttpClient = FuelManager()
        val (_, response) = fuelHttpClient.post(søkUrlSomKreverVeileder)
            .responseObject<String>()

        assertThat(response.statusCode).isEqualTo(403)
    }

    @Test
    fun `Kall med ugyldig token mot beskyttet endepunkt skal returnere 403`() {
        val ugyldigToken = hentUgyldigToken(mockOAuth2Server)
        val fuelHttpClient = FuelManager()
        val (_, response) = fuelHttpClient.post(søkUrlSomKreverVeileder).authentication()
            .bearer(ugyldigToken.serialize())
            .responseObject<String>()

        assertThat(response.statusCode).isEqualTo(403)
    }

    @Test
    fun `Kall med token uten claim for NAV-ident mot beskyttet endepunkt skal returnere 403`() {
        val tokenUtenNavIdentClaim = hentTokenUtenNavIdentClaim(mockOAuth2Server)
        val fuelHttpClient = FuelManager()
        val (_, response) = fuelHttpClient.post(søkUrlSomKreverVeileder).authentication()
            .bearer(tokenUtenNavIdentClaim.serialize())
            .responseObject<String>()

         assertThat(response.statusCode).isEqualTo(403)
    }

    @Test
    fun `Skal kunne kalle endepunkt for isAlive uten å være autentisert`() {
        val fuelHttpClient = FuelManager()
        val (_, response) = fuelHttpClient.get(isAliveUrl)
            .responseObject<String>()

        assertThat(response.statusCode).isEqualTo(200)
    }

    @Test
    fun `Skal kunne kalle endepunkt for isReady uten å være autentisert`() {
        val fuelHttpClient = FuelManager()
        val (_, response) = fuelHttpClient.get(isReadyUrl)
            .responseObject<String>()

        assertThat(response.statusCode).isEqualTo(200)
    }

    @Test
    fun `Avvise none uten signatur`() {
        val token = hentToken(mockOAuth2Server)
        val tokenParts = token.serialize().split('.')
        val tokenStr = listOf(Base64.encode("{ \"alg\":\"none\" }"),tokenParts[1]).joinToString(".")
        val fuelHttpClient = FuelManager()
        val (_, response) = fuelHttpClient.post(søkUrlSomKreverVeileder).authentication()
            .bearer(tokenStr)
            .responseObject<String>()

        assertThat(response.statusCode).isEqualTo(403)
    }

    @Test
    fun `Avvise none med signatur`() {
        val token = hentToken(mockOAuth2Server)
        val tokenParts = token.serialize().split('.')
        val tokenStr = listOf(Base64.encode("{ \"alg\":\"none\" }"),tokenParts[1],tokenParts[1]).joinToString(".")
        val fuelHttpClient = FuelManager()
        val (_, response) = fuelHttpClient.post(søkUrlSomKreverVeileder).authentication()
            .bearer(tokenStr)
            .responseObject<String>()

        assertThat(response.statusCode).isEqualTo(403)
    }

    private fun hentToken(mockOAuth2Server: MockOAuth2Server) = mockOAuth2Server.issueToken("isso-idtoken", "someclientid",
        DefaultOAuth2TokenCallback(
            issuerId = "isso-idtoken",
            claims = mapOf(
                Pair("name", "navn"),
                Pair("NAVident", "NAVident"),
                Pair("unique_name", "unique_name"),
                ),
            audience = listOf("audience")
        )
    )

    private fun hentUgyldigToken(mockOAuth2Server: MockOAuth2Server) = mockOAuth2Server.issueToken("feilissuer", "someclientid",
        DefaultOAuth2TokenCallback(
            issuerId = "feilissuer",
            claims = mapOf(
                Pair("name", "navn"),
                Pair("NAVident", "NAVident"),
                Pair("unique_name", "unique_name"),
                ),
            audience = listOf("audience")
        )
    )

    private fun hentTokenUtenNavIdentClaim(mockOAuth2Server: MockOAuth2Server) = mockOAuth2Server.issueToken("isso-idtoken", "someclientid",
        DefaultOAuth2TokenCallback(
            issuerId = "isso-idtoken",
            claims = mapOf(
                Pair("name", "navn"),
                Pair("unique_name", "unique_name"),
                ),
            audience = listOf("audience")
        )
    )
}
