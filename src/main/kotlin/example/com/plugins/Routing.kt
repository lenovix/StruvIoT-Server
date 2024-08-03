package example.com.plugins

//import io.ktor.application.*
//import io.ktor.response.*
//import io.ktor.request.*
//import io.ktor.routing.*
import kotlinx.serialization.Serializable
//import org.eclipse.paho.client.mqttv3.*
import example.com.AwsIotClient
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.SerialName

//@Serializable
//data class DeviceStatus(val id_alat: String, val status: String)

@Serializable
data class DeviceStatus(
    @SerialName("id-alat") val idAlat: String,
    val status: String
)

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        post("/reaktor") {
            val deviceStatus = call.receive<DeviceStatus>()
            val topic = "struv/${deviceStatus.idAlat}/reaktor"
            AwsIotClient.publishToTopic(topic, deviceStatus.status)
            call.respondText("Data sent to topic: $topic")
        }
        post("/pengering") {
            val deviceStatus = call.receive<DeviceStatus>()
            val topic = "struv/${deviceStatus.idAlat}/pengering"
            AwsIotClient.publishToTopic(topic, deviceStatus.status)
            call.respondText("Data sent to topic: $topic")
        }
        post("/penyaring") {
            val deviceStatus = call.receive<DeviceStatus>()
            val topic = "struv/${deviceStatus.idAlat}/penyaring"
            AwsIotClient.publishToTopic(topic, deviceStatus.status)
            call.respondText("Data sent to topic: $topic")
        }
    }
}
