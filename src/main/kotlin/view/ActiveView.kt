package view

import app.channels
import app.currentMessages
import app.sendMessage
import io.ktor.util.KtorExperimentalAPI
import javafx.geometry.Side
import javafx.scene.control.ListView
import javafx.scene.control.TabPane
import kotlinx.coroutines.runBlocking
import tornadofx.*

class ActiveView : View() {
    override val root = borderpane {
        // Add server selector to left?
        right<MessageView>()
    }
}

lateinit var messageView: ListView<String>

class MessageView : View() {
    @KtorExperimentalAPI
    override val root = vbox {
        tabpane {
            side = Side.LEFT
            tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE

            channels.forEach { channel ->
                tab(channel.name) {
                    vbox {
                        messageView = listview(currentMessages[channel.name])
                        messageView.scrollTo(currentMessages.size)

                        hbox {
                            val messageField = textarea()
                            button("Send").action {
                                runAsync {
                                    runBlocking {
                                        sendMessage(channel.name, messageField.text)
                                    }
                                    messageField.clear()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
