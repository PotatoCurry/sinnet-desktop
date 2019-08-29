package view

import app.*
import io.ktor.util.KtorExperimentalAPI
import javafx.beans.property.SimpleStringProperty
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import tornadofx.*

class LoginView : View() {
    private val hostInput = SimpleStringProperty()

    @KtorExperimentalAPI
    override val root = form {
        fieldset {
            field("Host") {
                textfield(hostInput)
            }
        }

        button("Connect") {
            isDefaultButton = true

            action {
                server = hostInput.value
                channels.forEach { currentMessages[it.name] = observableListOf() }
                messageHistory.forEach { addMessage(it) }
                GlobalScope.launch {
                    launchWebsocket()
                }
                replaceWith<ActiveView>(ViewTransition.Slide(0.3.seconds, ViewTransition.Direction.LEFT), true)
            }
        }
    }
}
