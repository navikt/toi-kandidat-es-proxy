package no.nav.toi.kandidatesproxy

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.authentication
import com.github.kittinunf.result.Result
import no.nav.security.mock.oauth2.MockOAuth2Server
import no.nav.security.mock.oauth2.token.DefaultOAuth2TokenCallback
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.net.InetAddress

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class KandidatsøkTest {

    private val mockOAuth2Server = MockOAuth2Server()
    private val feilIndeksSearchUrl = "http://localhost:8300/feilIndeks/_search"
    private val eksisterendeIndeksSearchUrl = "http://localhost:8300/indeks/_search"

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
    fun `Søkekall mot feil indeks skal viderebringe svar fra OpenSearch-instans`() {
        val token = hentToken(mockOAuth2Server)

        val (_, response, result) = Fuel.post(feilIndeksSearchUrl).authentication()
            .bearer(token.serialize())
            .responseString()

        assertThat(response.statusCode).isEqualTo(EsMock.feilResultatStatusKode)
        assertThat((result as Result.Failure).error.response.body()).isEqualTo(EsMock.jsonFeilResultat)
    }

    @Test
    fun `Søkekall mot riktig indeks skal viderebringe svar fra OpenSearch-instans`() {
        val token = hentToken(mockOAuth2Server)

        val (_, response, result) = Fuel.post(eksisterendeIndeksSearchUrl).authentication()
            .bearer(token.serialize())
            .responseString()

        assertThat(response.statusCode).isEqualTo(EsMock.korrektResultatStatusKode)
        assertThat(result.get()).isEqualTo(EsMock.jsonResultat)
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
}
