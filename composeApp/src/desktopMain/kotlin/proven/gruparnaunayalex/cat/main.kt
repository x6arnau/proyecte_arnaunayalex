package proven.gruparnaunayalex.cat

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "proyecte_arnaunayalex",
    ) {
        App()
    }
}