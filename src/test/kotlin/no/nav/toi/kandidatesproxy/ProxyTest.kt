package no.nav.toi.kandidatesproxy

import no.nav.security.mock.oauth2.MockOAuth2Server
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.net.InetAddress

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProxyTest {
    private val mockOAuth2Server = MockOAuth2Server()

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
    fun `Proxy-kall skal viderebringe hele requesten og responsen`() {

    }
}