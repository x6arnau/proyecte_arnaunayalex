package proven.gruparnaunayalex.cat
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import app.cash.sqldelight.driver.android.AndroidSqliteDriver

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (DatabaseConfig.development) {
            this.applicationContext.deleteDatabase(DatabaseConfig.name)
        }
        val driver = AndroidSqliteDriver(Database.Schema, this.applicationContext, DatabaseConfig.name)
        setContent {
            App(driver)
        }
    }
}

//@Preview
//@Composable
//fun AppAndroidPreview() {
//   App()
//}