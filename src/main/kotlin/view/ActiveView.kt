package view

import app.channels
import app.currentMessages
import app.sendMessage
import io.ktor.util.KtorExperimentalAPI
import javafx.geometry.Side
import javafx.scene.control.ListView
import javafx.scene.control.SelectionMode
import javafx.scene.control.TabPane
import kotlinx.coroutines.runBlocking
import tornadofx.*

class ActiveView : View() {
    override val root = borderpane {
        //        find<MessageView>(mapOf(MessageView::channel to channels.first()))
        right<MessageView>()
//        right<MessageView>()
    }
}

//class ChannelView : View() {
//    override val root = vbox {
//        listview<String> {
//            items = channels.map(Channel::name).asObservable()
//
//            onUserSelect { selection ->
//                val channel = channels.single { it.name == selection }
//                find<MessageView>(mapOf(MessageView::channel to channel)).openModal()
//            }
//        }
//    }
//}
//
//class MessageView : View() {
//    val channel by param<Channel>()
//
//    override val root = vbox {
//        tabpane {
//            side = Side.LEFT
//            tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE
//
//            channels.forEach { channel ->
//                tab(channel.name)
//
//            }
//        }
//
////        scrollpane {
////            listview(messageHistory.filter { it.channel == channel.name }.map(Message::text).asObservable())
////            text("User") {
////                fill = Color.PURPLE
////                font = Font(18.0)
////            }
////            text("Message") {
////                fill = Color.BLACK
////                font = Font(12.0)
////            }
////        }
////        hbox {
////            textarea("Type message here")
////            button("Send")
////        }
//    }
//}

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

//        scrollpane {
//            listview(messageHistory.filter { it.channel == channel.name }.map(Message::text).asObservable())
//            text("User") {
//                fill = Color.PURPLE
//                font = Font(18.0)
//            }
//            text("Message") {
//                fill = Color.BLACK
//                font = Font(12.0)
//            }
//        }
//        hbox {
//            textarea("Type message here")
//            button("Send")
//        }
    }
}
