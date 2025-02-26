package proven.gruparnaunayalex.cat
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.compose.auth.ComposeAuth
import io.github.jan.supabase.compose.auth.composeAuth
import io.github.jan.supabase.compose.auth.composable.NativeSignInResult
import io.github.jan.supabase.compose.auth.composable.rememberSignInWithGoogle
import io.github.jan.supabase.compose.auth.googleNativeLogin
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme {
        val navController = rememberNavController()
        Surface(modifier = Modifier.fillMaxSize()) {
            NavHost(navController = navController, startDestination = "login") {
                composable("login") {
                    LoginScreen(onLoginSuccess = { navController.navigate("todoList") })
                }
                composable("todoList") {
                    TodoList()
                }
            }
        }
    }
}

val supabase = createSupabaseClient(
    supabaseUrl = "https://wqybldibsllassuxepxy.supabase.co",
    supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6IndxeWJsZGlic2xsYXNzdXhlcHh5Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3Mzk5OTY3MzksImV4cCI6MjA1NTU3MjczOX0.9CbU2ykmNoUlgz3EVv3i7geVTl7s5N8QfTliD2QL4Jo"
) {
    install(Auth)
    install(ComposeAuth) {
        googleNativeLogin("751473724477-enm8q34ru1gg91tfbja380tes6pptp74.apps.googleusercontent.com"
             ) // Replace with your Web Client ID

    }
}

@Serializable
data class TodoItem(val id: Int, val name: String)

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val authState = supabase.composeAuth.rememberSignInWithGoogle(
        onResult = { result ->
            when (result) {
                NativeSignInResult.ClosedByUser -> errorMessage = "Google Sign-In cancelled"
                is NativeSignInResult.Error -> errorMessage = "Google Sign-In error: ${result.message}"
                is NativeSignInResult.NetworkError -> errorMessage = "Network error: ${result.message}"
                NativeSignInResult.Success -> onLoginSuccess()
            }
        }
    )

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    withContext(Dispatchers.IO) {
                        supabase.auth.signInWith(Email) {
                            this.email = email
                            this.password = password
                        }
                    }
                    onLoginSuccess()
                } catch (e: Exception) {
                    errorMessage = "Login failed: ${e.message}"
                }
            }
        }) {
            Text("Login")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { authState.startFlow() }) {
            Text("Login with Google")
        }
        errorMessage?.let {
            Text(it, color = MaterialTheme.colors.error)
        }
    }
}

@Composable
fun TodoList() {
    var items by remember { mutableStateOf<List<TodoItem>>(emptyList()) }
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            items = supabase.from("ingredients")
                .select()
                .decodeList<TodoItem>()
        }
    }
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        items(items = items, key = { it.id }) { item ->
            Text(item.name, modifier = Modifier.padding(8.dp))
        }
    }
}