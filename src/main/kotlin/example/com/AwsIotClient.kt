//AwsIotClient.kt
@file:Suppress("SameParameterValue")

package example.com

import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.openssl.PEMKeyPair
import org.bouncycastle.openssl.PEMParser
import org.eclipse.paho.client.mqttv3.*
import java.nio.file.Files
import java.nio.file.Paths
import java.security.KeyFactory
import java.security.KeyStore
import java.security.PrivateKey
import java.security.Security
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.security.spec.PKCS8EncodedKeySpec
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import java.io.StringReader

object AwsIotClient {
    private const val brokerUrl = "ssl://a2myxdlmwu3fx-ats.iot.ap-southeast-1.amazonaws.com:8883"
    private const val clientId = "StruvIoT-Client"
    private const val certificateFile = "certs/5ad413cbd19e293fab7e5ab5e057be06bfde0634e414171a821e985915a7164a-certificate.pem.crt"
    private const val privateKeyFile = "certs/5ad413cbd19e293fab7e5ab5e057be06bfde0634e414171a821e985915a7164a-private.pem.key"
    private const val rootCAFile = "certs/AmazonRootCA1.pem"

    var client: MqttClient

    init {
        Security.addProvider(BouncyCastleProvider())

        val sslSocketFactory = getSocketFactory(certificateFile, privateKeyFile, rootCAFile)

        val options = MqttConnectOptions().apply {
            socketFactory = sslSocketFactory
        }

        client = MqttClient(brokerUrl, clientId).apply {
            setCallback(object : MqttCallback {
                override fun connectionLost(cause: Throwable?) {
                    println("Connection lost: ${cause?.message}")
                }

                override fun messageArrived(topic: String?, message: MqttMessage?) {
                    println("Received message: ${message.toString()} from topic: $topic")
                }

                override fun deliveryComplete(token: IMqttDeliveryToken?) {
                    println("Delivery complete")
                }
            })
            connect(options)
            println("Connected to AWS IoT Core")
        }
    }

    fun publishToTopic(topic: String, status: String) {
        val payload = """{ "id-alat": "${topic.split("/")[1]}", "status": "$status" }"""
        val message = MqttMessage(payload.toByteArray())
        client.publish(topic, message)
        println("Published message: $payload to topic: $topic")
    }

    private fun getSocketFactory(certificateFile: String, privateKeyFile: String, rootCAFile: String): SSLSocketFactory {
        val certificateFactory = CertificateFactory.getInstance("X.509")
        val rootCA = certificateFactory.generateCertificate(Files.newInputStream(Paths.get(rootCAFile))) as X509Certificate
        val clientCert = certificateFactory.generateCertificate(Files.newInputStream(Paths.get(certificateFile))) as X509Certificate

        val privateKey = loadPrivateKey(privateKeyFile)

        val keyStore = KeyStore.getInstance(KeyStore.getDefaultType()).apply {
            load(null, null)
            setCertificateEntry("ca-cert", rootCA)
            setCertificateEntry("client-cert", clientCert)
            setKeyEntry("private-key", privateKey, "".toCharArray(), arrayOf(clientCert))
        }

        val keyManagerFactory = javax.net.ssl.KeyManagerFactory.getInstance(javax.net.ssl.KeyManagerFactory.getDefaultAlgorithm()).apply {
            init(keyStore, "".toCharArray())
        }

        val sslContext = SSLContext.getInstance("TLSv1.2").apply {
            init(keyManagerFactory.keyManagers, null, null)
        }

        return sslContext.socketFactory
    }

    private fun loadPrivateKey(privateKeyFile: String): PrivateKey {
        val keyBytes = Files.readAllBytes(Paths.get(privateKeyFile))
        val pemParser = PEMParser(StringReader(String(keyBytes)))
        val keyPair = pemParser.readObject() as PEMKeyPair
        val privateKeyInfo = keyPair.privateKeyInfo
        val keySpec = PKCS8EncodedKeySpec(privateKeyInfo.encoded)
        val keyFactory = KeyFactory.getInstance("RSA")
        return keyFactory.generatePrivate(keySpec)
    }
}
