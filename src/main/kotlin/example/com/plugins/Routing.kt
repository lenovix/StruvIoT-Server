//Routing.kt
package example.com.plugins

import kotlinx.serialization.Serializable
import example.com.AwsIotClient
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.SerialName

@Serializable
data class DeviceStatus(
    @SerialName("id-alat") val idAlat: String,
    val status: String
)

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("StruvIoT Server")
        }
        //struv/id/pengering/control
        post("/reaktor") {
            val deviceStatus = call.receive<DeviceStatus>()
            val topic = "struv/${deviceStatus.idAlat}/reaktor/control"
            AwsIotClient.publishToTopic(topic, deviceStatus.status)
            call.respondText("Data sent to topic: $topic")
        }
        post("/pengering") {
            val deviceStatus = call.receive<DeviceStatus>()
            val topic = "struv/${deviceStatus.idAlat}/pengering/control"
            AwsIotClient.publishToTopic(topic, deviceStatus.status)
            call.respondText("Data sent to topic: $topic")
        }
        post("/penyaring") {
            val deviceStatus = call.receive<DeviceStatus>()
            val topic = "struv/${deviceStatus.idAlat}/penyaring/control"
            AwsIotClient.publishToTopic(topic, deviceStatus.status)
            call.respondText("Data sent to topic: $topic")
        }
    }
}
