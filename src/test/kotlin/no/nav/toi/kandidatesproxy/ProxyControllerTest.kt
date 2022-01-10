package no.nav.toi.kandidatesproxy

import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.extensions.authentication
import com.github.kittinunf.fuel.jackson.responseObject
import no.nav.security.mock.oauth2.MockOAuth2Server
import no.nav.security.mock.oauth2.token.DefaultOAuth2TokenCallback
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.net.InetAddress

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProxyControllerTest {
    private val mockOAuth2Server = MockOAuth2Server()

    private val urlSomKreverSystembruker = "http://localhost:8300/es/proxytest"

    @BeforeAll
    fun init() {
        LokalApplikasjon.startAppForTest()
        EsMock.startEsMock()
        mockOAuth2Server.start(InetAddress.getByName("localhost"), 18300)
    }

    @AfterAll
    fun teardown() {
        mockOAuth2Server.shutdown()
    }

    @Test
    fun `Proxy GET-kall skal viderebringe hele requesten og responsen`() {
        val token = hentToken(mockOAuth2Server)
        val fuelHttpClient = FuelManager()

        val (_, response) = fuelHttpClient.get(urlSomKreverSystembruker)
            .authentication().bearer(token.serialize())
            .responseObject<String>()

        assertThat(response.statusCode).isEqualTo(200)
    }

    @Test
    fun `Proxy PUT-kall skal viderebringe hele requesten og responsen`() {
        val token = hentToken(mockOAuth2Server)
        val fuelHttpClient = FuelManager()

        val (_, response) = fuelHttpClient.put(urlSomKreverSystembruker)
            .authentication().bearer(token.serialize())
            .responseObject<String>()

        assertThat(response.statusCode).isEqualTo(200)
    }

    @Test
    fun `Proxy POST-kall skal viderebringe hele requesten og responsen`() {
        val token = hentToken(mockOAuth2Server)
        val fuelHttpClient = FuelManager()

        val (_, response) = fuelHttpClient.post(urlSomKreverSystembruker)
            .authentication().bearer(token.serialize())
            .responseObject<String>()

        assertThat(response.statusCode).isEqualTo(200)
    }

    @Test
    fun `Proxy PATCH-kall skal viderebringe hele requesten og responsen`() {
        val token = hentToken(mockOAuth2Server)
        val fuelHttpClient = FuelManager()

        val (_, response) = fuelHttpClient.patch(urlSomKreverSystembruker)
            .authentication().bearer(token.serialize())
            .responseObject<String>()

        assertThat(response.statusCode).isEqualTo(200)
    }

    @Test
    fun `Proxy deletekall skal viderebringe hele requesten og responsen`() {
        val token = hentToken(mockOAuth2Server)
        val fuelHttpClient = FuelManager()

        val (_, response) = fuelHttpClient.delete(urlSomKreverSystembruker)
            .authentication().bearer(token.serialize())
            .responseObject<String>()

        assertThat(response.statusCode).isEqualTo(200)
    }

    private fun hentToken(mockOAuth2Server: MockOAuth2Server) = mockOAuth2Server.issueToken(
        "gyldig-issuer", "someclientid",
        DefaultOAuth2TokenCallback(
            issuerId = "aad",
            claims = mapOf(
                Pair("sub", "jalla"),
                Pair("oid", "jalla")
            ),
            audience = listOf("audience")
        )
    )
}