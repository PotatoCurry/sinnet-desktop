package app

import com.beust.klaxon.FieldRenamer
import com.beust.klaxon.Klaxon
import io.ktor.client.HttpClient
import io.ktor.client.features.defaultRequest
import io.ktor.client.features.websocket.DefaultClientWebSocketSession
import io.ktor.client.features.websocket.WebSockets
import io.ktor.client.features.websocket.webSocket
import io.ktor.client.request.get
import io.ktor.client.request.host
import io.ktor.client.request.port
import io.ktor.http.cio.websocket.CloseReason
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.close
import io.ktor.http.cio.websocket.readText
import io.ktor.util.KtorExperimentalAPI
import javafx.application.Platform
import javafx.collections.ObservableList
import javafx.stage.Stage
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import tornadofx.App
import tornadofx.observableListOf
import tornadofx.observableMapOf
import view.ActiveView

val klaxon = Klaxon().fieldRenamer(
    object: FieldRenamer {
        override fun toJson(fieldName: String) = FieldRenamer.camelToUnderscores(fieldName)
        override fun fromJson(fieldName: String) = FieldRenamer.underscoreToCamel(fieldName)
    }
)

lateinit var websocketSession: DefaultClientWebSocketSession

@KtorExperimentalAPI
val client = HttpClient {
    defaultRequest {
        host = "127.0.0.1"
        port = 8080
    }

    install(WebSockets)
}

@KtorExperimentalAPI
val channels: List<Channel>
    get() {
        val channelsJson = runBlocking { client.get<String>(path = "/channels") }
        return klaxon.parseArray(channelsJson)!!
    }

@KtorExperimentalAPI
val messageHistory: List<Message>
    get() {
        val messagesJson = runBlocking { client.get<String>(path = "/channels/channel1") }
        return klaxon.parseArray(messagesJson)!!
    }

@KtorExperimentalAPI
val currentMessages = observableMapOf<String, ObservableList<String>>()

suspend fun sendMessage(channel: String, text: String) {
    with (websocketSession) {
        val message = Message(
            channel,
            text
        )
        outgoing.send(Frame.Text(klaxon.toJsonString(message)))
        println("sent $message")
    }
}

@KtorExperimentalAPI
class Main : App(ActiveView::class) {
    override fun start(stage: Stage) {
        stage.title = "Sinnet"
        stage.setOnCloseRequest {
            runBlocking { closeSession() }
        }

        channels.forEach { currentMessages[it.name] = observableListOf() }
        messageHistory.forEach { addMessage(it) }
        GlobalScope.launch {
            launchWebsocket()
        }

        super.start(stage)
    }

    suspend fun launchWebsocket() {
        client.webSocket(path = "/messages") {
            websocketSession = this

            for (frame in incoming) {
                when (frame) {
                    is Frame.Text -> {
                        val text = frame.readText()
                        val message = klaxon.parse<Message>(text)!!
                        println("received $message")
                        addMessage(message)
                    }
                    else -> {
                        println("Received ${frame.data}")
                    }
                }
            }
        }
    }

    fun addMessage(message: Message) {
        Platform.runLater {
            currentMessages[message.channel]!!.add(message.text)
        }
    }

    suspend fun closeSession() {
        websocketSession.close(CloseReason(CloseReason.Codes.NORMAL, "Client disconnected"))
    }
}
