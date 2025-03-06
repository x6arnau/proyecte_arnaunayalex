package proven.gruparnaunayalex.cat
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import io.github.jan.supabase.auth.auth

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleIntent(intent)
        if (DatabaseConfig.development) {
            this.applicationContext.deleteDatabase(DatabaseConfig.name)
        }
        val driver = AndroidSqliteDriver(Database.Schema, this.applicationContext, DatabaseConfig.name)
        setContent {
            App(driver)
        }
    }


    private fun handleIntent(intent: Intent?) {
        intent?.data?.let { uri ->
            if (uri.toString().startsWith("bocadillos://login-callback")) {
                // Supabase procesará automáticamente el token
                println("Redirección recibida: $uri")
            }
        }
    }

}

//@Preview
//@Composable
//fun AppAndroidPreview() {
//   App()
//}