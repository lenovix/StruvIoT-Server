package example.com

import io.ktor.http.*
import io.ktor.server.testing.*
import io.ktor.server.application.*
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class RoutingTest {
    @Test
    fun testReaktorEndpoint() {
        withTestApplication(Application::module) {
            handleRequest(HttpMethod.Post, "/reaktor") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody("""{ "id-alat": "2343189498", "status": "on" }""")
            }.apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals("Data sent to topic: struv/2343189498/reaktor/control", response.content)
            }
        }
    }

    @Test
    fun testPenyaringEndpoint() {
        withTestApplication(Application::module) {
            handleRequest(HttpMethod.Post, "/penyaring") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody("""{ "id-alat": "2343189498", "status": "on" }""")
            }.apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals("Data sent to topic: struv/2343189498/penyaring/control", response.content)
            }
        }
    }

    @Test
    fun testPengeringEndpoint() {
        withTestApplication(Application::module) {
            handleRequest(HttpMethod.Post, "/pengering") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody("""{ "id-alat": "2343189498", "status": "on" }""")
            }.apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals("Data sent to topic: struv/2343189498/pengering/control", response.content)
            }
        }
    }
}