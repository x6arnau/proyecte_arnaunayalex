package proven.gruparnaunayalex.cat
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.FlowType
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Discord
import io.github.jan.supabase.auth.providers.Github
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.compose.auth.ComposeAuth
import io.github.jan.supabase.compose.auth.composeAuth
import io.github.jan.supabase.compose.auth.composable.NativeSignInResult
import io.github.jan.supabase.compose.auth.composable.rememberSignInWithGoogle
import io.github.jan.supabase.compose.auth.googleNativeLogin
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import org.jetbrains.compose.ui.tooling.preview.Preview

object SupabaseProvider {
    val client = createSupabaseClient(
        supabaseUrl = "https://wqybldibsllassuxepxy.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6IndxeWJsZGlic2xsYXNzdXhlcHh5Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3Mzk5OTY3MzksImV4cCI6MjA1NTU3MjczOX0.9CbU2ykmNoUlgz3EVv3i7geVTl7s5N8QfTliD2QL4Jo"
    ) {
        install(Auth){
            flowType = FlowType.PKCE
        }
        install(ComposeAuth) {
            googleNativeLogin("751473724477-0ncgg8ohhufjjbatot4nb0onsj6elsgr.apps.googleusercontent.com")
        }
        install(Postgrest)
    }
}

sealed class AuthState {
    object Checking : AuthState()
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
}

@Composable
@Preview
fun App() {
    val supabase = SupabaseProvider.client
    var authState by remember { mutableStateOf<AuthState>(AuthState.Checking) }

    LaunchedEffect(Unit) {
        authState = if (supabase.auth.currentSessionOrNull() != null) {
            AuthState.Authenticated
        } else {
            AuthState.Unauthenticated
        }

        supabase.auth.sessionStatus.collect { status ->
            authState = when(status) {
                is SessionStatus.Authenticated -> AuthState.Authenticated
                is SessionStatus.NotAuthenticated -> AuthState.Unauthenticated
                SessionStatus.Initializing -> AuthState.Checking
                is SessionStatus.RefreshFailure -> AuthState.Unauthenticated
            }
        }
    }

    MaterialTheme {
        val navController = rememberNavController()
        Surface(modifier = Modifier.fillMaxSize()) {
            when (authState) {
                AuthState.Checking -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                AuthState.Unauthenticated -> {
                    LoginScreen(navController = navController,supabase)
                }
                AuthState.Authenticated -> {
                    NavHost(navController = navController, startDestination = ListSandwiches) {
                        composable<ListSandwiches> {
                            SandwichesScreen(navController, supabase)
                        }
                        composable<ListCesta> {
                            CestaScreen(navController, supabase)
                        }
                        composable<LogOut> {
                            LogOutScreen(navController, supabase)
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun LoginScreen(
    navController: NavController,
    supabase: SupabaseClient
) {
    val scope = rememberCoroutineScope()
    Column(

        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(16.dp))
        Text("Or sign up with email")
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var isloggedWithEmail by remember { mutableStateOf(false) }
        var isregisterWithEmail by remember { mutableStateOf(false) }
        var isloggedWithGithub by remember { mutableStateOf(false) }
        var isloggedWithDiscord by remember { mutableStateOf(false) }

        val authState = supabase.composeAuth.rememberSignInWithGoogle(

            onResult = { result ->
                when (result) {
                    is NativeSignInResult.Success -> {
                        println("Login successful: ${supabase.auth.currentSessionOrNull()}")
                    }
                    is NativeSignInResult.Error -> println("Error: ${result.message}")
                    is NativeSignInResult.ClosedByUser -> println("Login cancelled by user")
                    is NativeSignInResult.NetworkError -> println("Network error: ${result.message}")
                }
            }
        )

        LaunchedEffect(isloggedWithEmail) {
            if(isloggedWithEmail){
                try {
                    supabase.auth.signInWith(Email) {
                        this.email = email
                        this.password = password
                    }
                } catch (e: Exception) {
                    println("Login error: ${e.message}")
                }
            }else{
                println("No se ha logeado")
            }

        }
        LaunchedEffect(isregisterWithEmail) {
            if(isregisterWithEmail){
                try {
                    supabase.auth.signUpWith(Email) {
                        this.email = email
                        this.password = password
                    }
                } catch (e: Exception) {
                    println("Registration error: ${e.message}")
                }
            }else{
                println("No se ha logeado")
            }

        }
        LaunchedEffect(isloggedWithGithub) {
            if(isloggedWithGithub){
                try {
                    supabase.auth.signInWith(Github, redirectUrl = "https://wqybldibsllassuxepxy.supabase.co/auth/v1/callback") {
                    }
                } catch (e: Exception) {
                    println("Registration error: ${e.message}")
                }
            }else{
                println("No se ha logeado")
            }

        }
        LaunchedEffect(isloggedWithDiscord) {
            if(isloggedWithDiscord){
                try {
                    supabase.auth.signInWith(Discord, redirectUrl = "https://wqybldibsllassuxepxy.supabase.co/auth/v1/callback") {
                    }
                } catch (e: Exception) {
                    println("Registration error: ${e.message}")
                }
            }else{
                println("No se ha logeado")
            }

        }

        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.padding(8.dp)
        )

        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.padding(8.dp)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(8.dp)
        ) {
            Button(onClick = {
                isloggedWithEmail = true
            }) {
                Text("Sign In")
            }

            Button(onClick = {
                isregisterWithEmail = true
            }) {
                Text("Sign Up")
            }
            Button(onClick = {
                isloggedWithDiscord = true
            }) {
                Text("Sign in with Discord")
            }

            Button(onClick = {
                isloggedWithGithub = true
            }) {
                Text("Sign in with Github")
            }

            Button(onClick = { authState.startFlow() }) {
                Text("Sign in with Google")
            }


        }
    }
}


@Serializable
object ListSandwiches

@Serializable
object Login

//Crear la dataclass Sandwich
@Serializable
data class Sandwich(
    val id: Int,
    val name: String,
    val description: String,
    val price: Double
)

@Composable
fun SandwichesScreen(navController: NavController, supabase: SupabaseClient) {
    var sandwiches by remember { mutableStateOf<List<Sandwich>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    showMessageIntro()
    LaunchedEffect(Unit) {
        try {
            //TODO revisar owo
            val query = supabase.from("sandwiches").select(columns = Columns.list("id", "name", "description", "price"))
            sandwiches = query.decodeList<Sandwich>()
            isLoading = false
        } catch (e: Exception) {
            isLoading = false
        }
    }
    Column {
        Nav(navController)
        Spacer(modifier = Modifier.height(8.dp))
        when {
            isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            else -> LazyColumn {
                items(sandwiches) { sandwich ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        elevation = 4.dp
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(sandwich.name)
                            Text(sandwich.description)
                            Text("${sandwich.price}€")
                            //TODO añadir botón para añadir al carrito
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun showMessageIntro() {
    var openAlertDialog by remember { mutableStateOf(true) }

    if (openAlertDialog) {
        AlertDialog(
            onDismissRequest = { openAlertDialog = false },
            title = { Text("Welcome to the Sandwiches Store \uD83D\uDE0E") },
            text = { Text("Enjoy our selection of delicious sandwiches") },
            confirmButton = {
                TextButton(onClick = {
                    openAlertDialog = false
                    println("Confirmation registered")
                }) {
                    Text("Confirm")
                }
            }
        )
    }
}

@Serializable
object ListCesta

@Composable
fun CestaScreen(navController: NavController, supabase: SupabaseClient){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
   ){
       Nav(navController)
        Text("Cesta")
   }

}

@Serializable
object LogOut

@Composable
fun LogOutScreen(navController: NavController, supabase: SupabaseClient) {
    val scope = rememberCoroutineScope()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Nav(navController)
        Text("are you sure to go out?")
        Row(
            modifier = Modifier.padding(8.dp)
        ){
            Button(onClick = {
                scope.launch {
                    supabase.auth.signOut()
                    navController.navigate(ListSandwiches)
                }
            }){
                Text("Yes")
            }
            Button(onClick = {navController.navigate(ListSandwiches)}){
                Text("No")
            }
        }
    }
}


@Composable
fun Nav (controller:NavController){
    TopAppBar(
        title = { Text("Sandwiches Store") },
        actions = {
                    IconButton(onClick = {controller.navigate(ListCesta)}) {
                        Icon(imageVector = Icons.Filled.ShoppingCart, contentDescription = "ShoppingCart")
                    }
                    IconButton(onClick = {controller.navigate(ListSandwiches)}) {
                        Icon(imageVector = Icons.Filled.Home, contentDescription = "Home")
                    }
                    IconButton(onClick = {controller.navigate(LogOut)}){
                        Icon(imageVector = Icons.Filled.Close, contentDescription = "Close")
            }
        }
    )
}