package proven.gruparnaunayalex.cat
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    //version libreria avanmzada de sqlite para no tener que hacer el driver
    if (DatabaseConfig.development) {
        Path(DatabaseConfig.name).deleteIfExists()
    }
    val driver = JdbcSqliteDriver("jdbc:sqlite:${DatabaseConfig.name}")

    Window(
        onCloseRequest = ::exitApplication,
        title = "proyecte_arnaunayalex",
    ) {
        App(driver)
    }
}