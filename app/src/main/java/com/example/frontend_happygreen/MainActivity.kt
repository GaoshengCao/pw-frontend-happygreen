package com.example.frontend_happygreen

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.icu.text.CaseMap.Title
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Email
import android.view.WindowInsets
import android.view.animation.OvershootInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import android.Manifest
import android.content.pm.PackageManager
import android.provider.MediaStore.Audio.Genres.Members
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.platform.LocalContext
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.traceEventEnd
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import coil.compose.rememberAsyncImagePainter
import com.example.frontend_happygreen.ui.theme.AzzurroChiaro2
import com.example.frontend_happygreen.ui.theme.FrontendhappygreenTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.graphics.Color as AndroidColor
import androidx.compose.ui.graphics.Color as ComposeColor
import com.example.frontend_happygreen.ui.theme.VerdeNatura
import com.example.frontend_happygreen.ui.theme.GialloSolare
import com.example.frontend_happygreen.ui.theme.BiancoSporco
import androidx.compose.material3.TextFieldDefaults

fun String.toComposeColor(): ComposeColor = ComposeColor(AndroidColor.parseColor(this))

object SecureStorage {

    private fun getSharedPrefs(context: Context): SharedPreferences {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        return EncryptedSharedPreferences.create(
            context,
            "secure_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun saveToken(context: Context, token: String?) {
        getSharedPrefs(context)
            .edit()
            .putString("auth_token", token)
            .apply()
    }

    fun saveUser(context: Context, id: Int) {
        getSharedPrefs(context)
            .edit()
            .putInt("User_ID", id)
            .apply()
    }

    fun savePassword(context: Context, password: String?) {
        getSharedPrefs(context)
            .edit()
            .putString("password", password)
            .apply()
    }

    fun getToken(context: Context): String? {
        return getSharedPrefs(context).getString("auth_token", null)
    }

    fun getUser(context: Context): Int {
        return getSharedPrefs(context).getInt("User_ID", 0)
    }
    fun getPassword(context: Context): String? {
        return getSharedPrefs(context).getString("password", null)
    }

    fun clearToken(context: Context) {
        getSharedPrefs(context).edit().remove("auth_token").apply()
    }

    fun clearUser(context: Context) {
        getSharedPrefs(context).edit().remove("User_ID").apply()
    }
    fun clearPassword(context: Context) {
        getSharedPrefs(context).edit().remove("password").apply()
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, true)
        setContent {
            FrontendhappygreenTheme {
                val navController = rememberNavController()
                val context = LocalContext.current.applicationContext
                var page = "splash_screen"



                Surface(
                    modifier = Modifier.fillMaxSize(),
                            color = Color(0xFF4CAF50)
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = page,
                        modifier = Modifier.navigationBarsPadding()
                    ) {
                        composable("splash_screen") { SplashScreen(navController = navController) }
                        composable("first") { FirstPage((navController)) } // 1
                        composable("login") { LoginPage((navController)) } // 2
                        composable("register") { RegisterPage((navController)) } // 3
                        composable("home") { HomePage(navController) } // 4
                        composable("createGroup") { CreateGroupPage((navController)) } // 5
                        composable("enterGroup") { JoinGroupPage((navController)) } // 6
                        composable("mapPage/{name}") { backStackEntry ->
                            val name = backStackEntry.arguments?.getString("name") ?: "???"
                            GroupMapPage(navController, name)
                        } // 8
                        composable("group/{name}") { backStackEntry ->
                            val name = backStackEntry.arguments?.getString("name") ?: "???"
                            GroupPage(navController, name)
                        } // 7
                        composable("addPost/{name}") { backStackEntry ->
                            val name = backStackEntry.arguments?.getString("name") ?: "???"
                            AddPostPage(navController, name)
                        } // 9
                        composable(
                            "comment/{id}",
                            arguments = listOf(navArgument("id") {
                                type = NavType.IntType
                            })
                        ) { backStackEntry ->
                            val id = backStackEntry.arguments?.getInt("id") ?: 0
                            CommentPage(navController, id)
                        } // 10
                        composable("game") { GamePage(navController) }
                        composable("quizpage") {
                            val quizState = produceState<List<QuizQuestion>>(initialValue = emptyList()) {
                                val api = RetrofitInstance.api
                                value = getFiveQuizQuestion(api)
                            }

                            if (quizState.value.isNotEmpty()) {
                                QuizPage(navController, quizState.value)
                            } else {
                                // You can show a loading indicator while the data is fetched
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator()
                                }
                            }
                        }

                        composable("resultpage/{result}") { backStackEntry ->
                            val result =
                                backStackEntry.arguments?.getString("result")?.toIntOrNull() ?: -1
                            QuizResultPage(result, navController)
                        }
                        composable("camera") { CameraPage(navController) }
                        composable("updatePicture") { UpdatePicture(navController) }


                        composable("user") { UserPage(navController) }
                        composable("options") { OptionsPage(navController) }
                    }
                }
            }
        }
    }
}

@Composable
fun SplashScreen(navController: NavController) {
    val scale = remember {
        androidx.compose.animation.core.Animatable(0f)
    }
    val context = LocalContext.current.applicationContext

    // AnimationEffect
    LaunchedEffect(key1 = true) {
        scale.animateTo(
            targetValue = 0.7f,
            animationSpec = tween(
                durationMillis = 800,
                easing = {
                    OvershootInterpolator(4f).getInterpolation(it)
                })
        )
        delay(3000L)
        if (SecureStorage.getToken(context) == null) {
            navController.navigate("first")
        } else {
            navController.navigate("home")
        }

    }

    // Image
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background("#4CAF50".toComposeColor())
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo",
            modifier = Modifier.scale(scale.value)
        )
    }
}

//Barra Con Titolo
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeaderBar(navController: NavHostController, title: String) {
    TopAppBar(
        title = {
            Text(
                text = title,
                fontSize = 30.sp,
                fontWeight = FontWeight.Black
            )
        },
        modifier = Modifier.fillMaxWidth(),
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = VerdeNatura, // Colore principale (verde)
            titleContentColor = Color.White // Testo leggibile sul verde
        )
    )
}


//Barra Di Navgazione
@Composable
fun BottomNavBar(navController: NavHostController) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(VerdeNatura),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val navItems = listOf(
            Triple("game", R.drawable.controller, "Game"),
            Triple("camera", R.drawable.camera, "Camera"),
            Triple("home", R.drawable.home, "Home"),
            Triple("user", R.drawable.user, "User"),
            Triple("options", R.drawable.menu, "Options")
        )

        navItems.forEach { (destination, icon, label) ->
            val selected = currentRoute?.startsWith(destination) == true
            NavBarButton(destination, icon, label, navController, selected)
        }
    }
}

@Composable
fun NavBarButton(
    destination: String,
    iconResId: Int,
    label: String,
    navController: NavHostController,
    selected: Boolean = false
) {
    val bgColor = if (selected) GialloSolare else AzzurroChiaro2
    val contentColor = if (selected) Color.Black else BiancoSporco.copy(alpha = 0.8f)

    Button(
        onClick = {
            if (destination != navController.currentBackStackEntry?.destination?.route) {
                navController.navigate(destination) {
                    launchSingleTop = true
                    restoreState = true
                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                }
            }
        },
        shape = RoundedCornerShape(5.dp),
        modifier = Modifier.padding(2.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = bgColor,
            contentColor = contentColor
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
    ) {
        Image(
            painter = painterResource(iconResId),
            contentDescription = label,
            modifier = Modifier
                .size(24.dp)
                .padding(2.dp)
        )
    }
}

//
// 1(FirstPage)
//
//Pagina quando apri l'app per la prima volta
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FirstPage(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(VerdeNatura)  // sfondo principale
    ) {
        TopAppBar(
            title = {},
            navigationIcon = {
                IconButton(onClick = {}, enabled = false) {
                    Icon(
                        Icons.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.Transparent
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent // mantiene trasparente
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(VerdeNatura),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(100.dp))
            Text(
                text = "Happy Green",
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(200.dp))

            NavigationButton(
                text = "Login",
                destination = "login",
                navController = navController
            )
            NavigationButton(
                text = "Register",
                destination = "register",
                navController = navController
            )
        }
    }
}

//
// 2 (LoginPage)
//
//Pagina Per effettuare il Login
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginPage(navController: NavHostController) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current.applicationContext

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(VerdeNatura)  // Palette dominante
    ) {
        TopAppBar(
            title = {},
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 0.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(100.dp))
            Text(
                text = "Happy Green",
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(50.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(0.8f),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = BiancoSporco.copy(alpha = 0.5f),
                    unfocusedBorderColor = BiancoSporco.copy(alpha = 0.5f),
                    focusedLabelColor = BiancoSporco.copy(alpha = 0.5f),
                    cursorColor = BiancoSporco.copy(alpha = 0.5f),
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(0.8f),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = BiancoSporco.copy(alpha = 0.5f),
                    unfocusedBorderColor = BiancoSporco.copy(alpha = 0.5f),
                    focusedLabelColor = BiancoSporco.copy(alpha = 0.5f),
                    cursorColor = BiancoSporco.copy(alpha = 0.5f),
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    coroutineScope.launch {
                        val api = RetrofitInstance.api
                        val apiToken = loginUser(api, username, password)
                        val id = getId(api, username)
                        SecureStorage.saveToken(context, apiToken)
                        SecureStorage.saveUser(context, id)
                        SecureStorage.savePassword(context, password)

                        navController.navigate("home")
                    }
                },
                shape = RoundedCornerShape(5.dp),
                modifier = Modifier.fillMaxWidth(0.4f),
                enabled = username.isNotBlank() && password.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GialloSolare,
                    contentColor = Color.Black
                )
            ) {
                Text("Login")
            }
        }
    }
}

//
// 3(RegisterPage)
//
//Pagina Per effettuare la registrazione
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterPage(navController: NavHostController) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current.applicationContext
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var risultato by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(VerdeNatura)  // colore dominante
    ) {
        TopAppBar(
            title = {},
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
            modifier = Modifier.fillMaxWidth()
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(100.dp))

            Text(
                text = "Happy Green",
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(50.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(0.8f),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = BiancoSporco.copy(alpha = 0.5f),
                    unfocusedBorderColor = BiancoSporco.copy(alpha = 0.5f),
                    focusedLabelColor = BiancoSporco.copy(alpha = 0.5f),
                    cursorColor = BiancoSporco.copy(alpha = 0.5f),
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(0.8f),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = GialloSolare,
                    unfocusedBorderColor = BiancoSporco,
                    focusedLabelColor = GialloSolare,
                    cursorColor = GialloSolare,
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    coroutineScope.launch {
                        val apiService = RetrofitInstance.api
                        val result = registerUser(apiService, username, password)

                        val firstWord = result?.split(" ")?.firstOrNull()

                        if (firstWord == "Registered") {
                            val id = getId(apiService, username)
                            SecureStorage.saveUser(context, id)
                            SecureStorage.savePassword(context, password)
                            SecureStorage.saveToken(
                                context,
                                loginUser(apiService, username, password)
                            )
                            navController.navigate("home")
                        } else {
                            risultato = "Registrazione Fallita, Nome Utente Già Esistente"
                        }
                    }
                },
                shape = RoundedCornerShape(5.dp),
                modifier = Modifier.fillMaxWidth(0.4f),
                enabled = username.isNotBlank() && password.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GialloSolare,
                    contentColor = Color.Black
                )
            ) {
                Text("Register")
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (risultato.isNotEmpty()) {
                Text(
                    text = risultato,
                    color = Color.Red,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

//
// 4 (HOME)
//
@Composable
fun HomePage(navController: NavHostController) {
    val coroutineScope = rememberCoroutineScope()
    var groups by remember { mutableStateOf<List<Group>?>(emptyList()) }
    val context = LocalContext.current.applicationContext

    val userId = SecureStorage.getUser(context)

    LaunchedEffect(userId) {
        val api = RetrofitInstance.api
        groups = getGroupsByUserID(api, userId)
    }

    Scaffold(
        topBar = { HeaderBar(navController, "Happy Green") },
        bottomBar = { BottomNavBar(navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            groups?.forEachIndexed { index, group ->
                ElementGroup(navController, group)
                if (index != groups?.lastIndex) {
                    Divider(
                        color = Color.LightGray,
                        thickness = 1.dp,
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            NavigationButton("Crea Gruppo", "createGroup", navController)
            Spacer(modifier = Modifier.height(12.dp))
            NavigationButton("Unisciti Gruppo", "enterGroup", navController)
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ElementGroup(navController: NavHostController, group: Group) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { navController.navigate("group/${group.name}") }
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = group.name,
                style = TextStyle(fontSize = 30.sp, fontWeight = FontWeight.SemiBold),
            )
        }
    }
}
//
// 5 (Create Group)
//
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateGroupPage(navController: NavHostController) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current.applicationContext
    var username by remember { mutableStateOf("") }
    var risultato by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(79, 149, 157)) // VerdeNatura-like
    ) {
        TopAppBar(
            title = {},
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = BiancoSporco)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
            modifier = Modifier.fillMaxWidth()
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(100.dp))
            Text(
                text = "Happy Green",
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = BiancoSporco
            )

            Spacer(modifier = Modifier.height(50.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Nome Gruppo", color = BiancoSporco) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(0.8f),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = BiancoSporco.copy(alpha = 0.5f),
                    unfocusedBorderColor = BiancoSporco.copy(alpha = 0.5f),
                    focusedLabelColor = BiancoSporco.copy(alpha = 0.5f),
                    unfocusedLabelColor = BiancoSporco.copy(alpha = 0.7f),
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    coroutineScope.launch {
                        val api = RetrofitInstance.api
                        val id = SecureStorage.getUser(context)
                        val responce = createGroup(api, id, username)
                        val firstWord = responce.split(" ")?.firstOrNull()

                        if (firstWord == "Created") {
                            navController.navigate("home")
                        } else {
                            risultato = responce
                        }
                    }
                },
                shape = RoundedCornerShape(5.dp),
                modifier = Modifier.fillMaxWidth(0.4f),
                enabled = username.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GialloSolare,
                    contentColor = Color.Black
                )
            ) {
                Text("Crea Gruppo")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(risultato, color = Color.Red)
        }
    }
}

//
// 7(Join Group )
//
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JoinGroupPage(navController: NavHostController) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current.applicationContext

    var username by remember { mutableStateOf("") }
    var risultato by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(79, 149, 157)) // VerdeNatura
    ) {
        TopAppBar(
            title = {},
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = BiancoSporco)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
            modifier = Modifier.fillMaxWidth()
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(100.dp))
            Text(
                text = "Happy Green",
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = BiancoSporco
            )

            Spacer(modifier = Modifier.height(50.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("ID Gruppo", color = BiancoSporco) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(0.8f),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = BiancoSporco.copy(alpha = 0.5f),
                    unfocusedBorderColor = BiancoSporco.copy(alpha = 0.5f),
                    focusedLabelColor = BiancoSporco.copy(alpha = 0.5f),
                    unfocusedLabelColor = BiancoSporco.copy(alpha = 0.7f),
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    coroutineScope.launch {
                        val api = RetrofitInstance.api
                        val id = SecureStorage.getUser(context)
                        val responce = joinGroup(api, id, username.toInt())

                        val firstWord = responce.split(" ")?.firstOrNull()

                        if (firstWord == "Joined") {
                            navController.navigate("home")
                        } else {
                            risultato = responce
                        }
                    }
                },
                shape = RoundedCornerShape(5.dp),
                modifier = Modifier.fillMaxWidth(0.4f),
                enabled = username.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GialloSolare,
                    contentColor = Color.Black
                )
            ) {
                Text("Join")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(risultato, color = Color.Red)
        }
    }
}

//
// 7 (Group Page)
//
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun GroupPage(navController: NavHostController, nome: String) {
    val coroutineScope = rememberCoroutineScope()
    var posts by remember { mutableStateOf<List<Post>>(emptyList()) }
    val context = LocalContext.current.applicationContext
    val userId = SecureStorage.getUser(context)

    LaunchedEffect(userId) {
        val api = RetrofitInstance.api
        posts = getPostByGroupName(api, nome) ?: emptyList()
    }

    Scaffold(
        topBar = { GroupHeaderBar(navController, nome) },
        bottomBar = { BottomNavBar(navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            posts.forEachIndexed { index, post ->
                ElementPost(navController, post)
                if (index != posts.lastIndex) {
                    Divider(
                        color = BiancoSporco.copy(alpha = 0.5f),
                        thickness = 1.dp,
                        modifier = Modifier
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun ElementPost(navController: NavHostController, post: Post) {
    var author by remember { mutableStateOf("") }

    val text = post.text
    val imageLink = post.image?.replaceFirst("http://", "https://") ?: ""

    val lat = post.location_lat ?: 0.0
    val lng = post.location_lng ?: 0.0

    LaunchedEffect(post.author) {
        val api = RetrofitInstance.api
        author = getUsernameById(api, post.author) ?: "Unknown"
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(VerdeNatura.copy(alpha = 0.15f), RoundedCornerShape(10.dp))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = author,
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            AsyncImage(
                model = imageLink,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = text,
                style = TextStyle(
                    fontSize = 24.sp,
                    color = Color.Black
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (lat != 0.0 || lng != 0.0) {
                Text(
                    text = "📍 $lat, $lng",
                    style = TextStyle(fontSize = 16.sp, color = Color.Black)
                )
            }

            Button(
                onClick = { navController.navigate("comment/${post.id}") },
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(top = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GialloSolare,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(5.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Go to comments"
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Commenta")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupHeaderBar(navController: NavHostController, title: String) {
    TopAppBar(
        title = {
            Text(
                text = title,
                fontSize = 30.sp,
                fontWeight = FontWeight.Black,
                color = BiancoSporco
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .background(VerdeNatura)
            .clickable { navController.navigate("mapPage/${title}") },
        actions = {
            IconButton(onClick = { navController.navigate("addPost/${title}") }) {
                Icon(
                    imageVector = Icons.Filled.AddCircle,
                    contentDescription = "Add",
                    tint = BiancoSporco,
                    modifier = Modifier.size(35.dp)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = VerdeNatura,
            titleContentColor = BiancoSporco
        )
    )
}

//
// 8(MapPage)
//
@OptIn(ExperimentalMaterial3Api::class, MapsComposeExperimentalApi::class)
@Composable
fun GroupMapPage(navController: NavHostController, nomeGruppo: String) {
    val coroutineScope = rememberCoroutineScope()
    var posts by remember { mutableStateOf<List<Post>>(emptyList()) }
    val context = LocalContext.current.applicationContext
    val userId = SecureStorage.getUser(context)

    var members by remember { mutableStateOf<List<User>>(emptyList()) }
    var groupID by remember { mutableStateOf(0) }
    var userLocation by remember { mutableStateOf(LatLng(41.8967, 12.4822)) }

    LaunchedEffect(userId) {
        val api = RetrofitInstance.api
        groupID = getIDGroup(api, nomeGruppo)
        posts = getPostByGroupName(api, nomeGruppo) ?: emptyList()
        members = getMembersByGroupName(api, nomeGruppo)

        userLocation = LatLng(41.8967, 12.4822)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(VerdeNatura) // Your custom background color
    ) {
        TopAppBar(
            title = {},
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
            modifier = Modifier.fillMaxWidth()
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 0.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            val cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(
                    posts.firstOrNull()?.let {
                        LatLng(it.location_lat ?: 0.0, it.location_lng ?: 0.0)
                    } ?: LatLng(0.0, 0.0), 10f
                )
            }

            GoogleMap(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                cameraPositionState = cameraPositionState
            ) {
                posts.forEach { post ->
                    val lat = post.location_lat
                    val lng = post.location_lng
                    if (lat != null && lng != null) {
                        Marker(
                            state = MarkerState(position = LatLng(lat, lng)),
                            title = post.author.toString(),
                            snippet = post.text
                        )
                    }
                }
                Marker(
                    state = MarkerState(position = userLocation),
                    title = "Tu sei qui",
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "ID Gruppo : $groupID",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Membri del Gruppo",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black
            )

            Column {
                members.forEach { user ->
                    Text(
                        text = user.username,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        color = Color.Black
                    )
                    Divider(
                        color = Color.LightGray,
                        thickness = 1.dp,
                        modifier = Modifier
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .fillMaxWidth()
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                coroutineScope.launch {
                    val api = RetrofitInstance.api
                    quitGroup(api, userId, nomeGruppo)
                    navController.navigate("home")
                }
            },
            shape = RoundedCornerShape(5.dp),
            modifier = Modifier
                .fillMaxWidth(0.4f)
                .align(Alignment.CenterHorizontally),
            colors = ButtonDefaults.buttonColors(
                containerColor = AzzurroChiaro2,
                contentColor = Color.Black
            )
        ) {
            Text("Esci Gruppo", color = Color.Black)
        }
    }
}

//
// 9(Create Post)
//
@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AddPostPage(navController: NavHostController, groupName: String) {
    val context = LocalContext.current.applicationContext
    val coroutineScope = rememberCoroutineScope()

    var description by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var location by remember { mutableStateOf<LatLng?>(null) }
    var resultText by remember { mutableStateOf("") }

    val permissionState = rememberPermissionState(android.Manifest.permission.ACCESS_FINE_LOCATION)

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        imageUri = uri
    }

    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    LaunchedEffect(permissionState.status.isGranted) {
        if (permissionState.status.isGranted) {
            val hasPermission = ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

            if (hasPermission) {
                fusedLocationClient.lastLocation.addOnSuccessListener { locationResult ->
                    locationResult?.let {
                        location = LatLng(it.latitude, it.longitude)
                    }
                }
            }
        } else {
            permissionState.launchPermissionRequest()
        }
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(location ?: LatLng(41.9028, 12.4964), 6f)
    }

    LaunchedEffect(location) {
        location?.let {
            cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(it, 15f))
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(VerdeNatura)
            .verticalScroll(rememberScrollState())
    ) {
        TopAppBar(
            title = {},
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Aggiungi Post a $groupName",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Descrizione", color = Color.Black) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            singleLine = false,
            maxLines = 3,
            textStyle = LocalTextStyle.current.copy(color = Color.Black)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = { launcher.launch("image/*") },
            modifier = Modifier.align(Alignment.CenterHorizontally),
            colors = ButtonDefaults.buttonColors(containerColor = AzzurroChiaro2)
        ) {
            Text("Seleziona Immagine", color = Color.Black)
        }

        imageUri?.let {
            Image(
                painter = rememberAsyncImagePainter(it),
                contentDescription = null,
                modifier = Modifier
                    .padding(16.dp)
                    .height(200.dp)
                    .fillMaxWidth(),
                contentScale = ContentScale.Fit
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        location?.let {
            Text(
                text = "Posizione selezionata: ${it.latitude}, ${it.longitude}",
                color = Color.Black,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        Text(
            text = "Tocca la mappa per selezionare un'altra posizione:",
            color = Color.Black,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        GoogleMap(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .padding(horizontal = 16.dp),
            cameraPositionState = cameraPositionState,
            onMapClick = { latLng -> location = latLng }
        ) {
            location?.let {
                Marker(
                    state = MarkerState(position = it),
                    title = "Posizione selezionata"
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                coroutineScope.launch {
                    val api = RetrofitInstance.api
                    if (imageUri != null && location != null && description.isNotBlank()) {
                        val postData = PostData(
                            groupId = getIDGroup(api, groupName),
                            authorId = SecureStorage.getUser(context),
                            text = description,
                            locationLat = location?.latitude,
                            locationLng = location?.longitude,
                            imageUri = imageUri!!
                        )
                        resultText = createPost(api, context, postData)
                        navController.navigate("group/$groupName")
                    } else {
                        resultText = "Seleziona immagine, posizione e inserisci descrizione."
                    }
                }
            },
            shape = RoundedCornerShape(5.dp),
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .align(Alignment.CenterHorizontally),
            enabled = description.isNotBlank() && location != null,
            colors = ButtonDefaults.buttonColors(containerColor = AzzurroChiaro2)
        ) {
            Text("Crea Post", color = Color.Black)
        }
    }
}

//
// 10 (Comment Page)
//
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentPage(navController: NavHostController, postId: Int) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current.applicationContext

    var post by remember { mutableStateOf<Post?>(null) }
    var author by remember { mutableStateOf("") }
    var comments by remember { mutableStateOf<List<Comment>>(emptyList()) }
    var newComment by remember { mutableStateOf("") }

    LaunchedEffect(postId) {
        val api = RetrofitInstance.api
        val fetchedPost = getPostById(api, postId)
        post = fetchedPost
        author = post?.let { getUsernameById(api, it.author) } ?: "Unknown"
        comments = getCommentsByPostId(api, postId)
    }

    post?.let { currentPost ->
        val text = currentPost.text
        val imageLink = currentPost.image?.replaceFirst("http://", "https://") ?: ""
        val lat = currentPost.location_lat ?: 0.0
        val lng = currentPost.location_lng ?: 0.0

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(VerdeNatura)
                .verticalScroll(rememberScrollState())
        ) {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )

            Text("Autore: $author", color = Color.Black, modifier = Modifier.padding(8.dp))
            Text("Testo: $text", color = Color.Black, modifier = Modifier.padding(8.dp))

            AsyncImage(
                model = imageLink,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Fit
            )

            Text("Posizione: ($lat, $lng)", color = Color.Black, modifier = Modifier.padding(8.dp))

            Divider(color = Color.Black, thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))

            Text(
                "Commenti:",
                color = Color.Black,
                fontSize = 20.sp,
                modifier = Modifier.padding(start = 8.dp)
            )

            comments.forEach { comment ->
                CommentElement(navController, comment.text.toString(), comment.author)
            }

            OutlinedTextField(
                value = newComment,
                onValueChange = { newComment = it },
                label = { Text("Scrivi un commento", color = Color.Black) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                textStyle = LocalTextStyle.current.copy(color = Color.Black)
            )

            Button(
                onClick = {
                    coroutineScope.launch {
                        val api = RetrofitInstance.api
                        val userId = SecureStorage.getUser(context)
                        val result = addCommentToPost(api, postId, userId, newComment)

                        if (result != "") {
                            comments = getCommentsByPostId(api, postId)
                            newComment = ""
                        }
                    }
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 16.dp),
                enabled = newComment.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = AzzurroChiaro2)
            ) {
                Text("Invia Commento", color = Color.Black)
            }
        }
    } ?: run {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Caricamento post...", color = Color.Black)
        }
    }
}

@Composable
fun CommentElement(navController: NavHostController, text: String, author: Int) {
    var authorUsername by remember { mutableStateOf("") }

    LaunchedEffect(author) {
        val api = RetrofitInstance.api
        authorUsername = getUsernameById(api, author).toString()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = authorUsername,
            style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = Color.Black),
            modifier = Modifier.padding(bottom = 2.dp)
        )
        Text(
            text = text,
            style = TextStyle(fontSize = 16.sp, color = Color.Black),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Divider(color = Color.Gray, thickness = 1.dp)
    }
}

//
// (GamePage)
//
@Composable
fun GamePage(navController: NavHostController) {
    Scaffold(
        topBar = { HeaderBar(navController, "Happy Green") },
        bottomBar = { BottomNavBar(navController) }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(VerdeNatura),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = { navController.navigate("quizpage") },
                colors = ButtonDefaults.buttonColors(containerColor = AzzurroChiaro2)
            ) {
                Text("Inizia Quiz", color = Color.Black)
            }
        }
    }
}

@Composable
fun QuizPage(navController: NavHostController, questions: List<QuizQuestion>) {
    var currentIndex by remember { mutableStateOf(0) }
    var selectedAnswer by remember { mutableStateOf<Domanda?>(null) }
    var score by remember { mutableStateOf(0) }
    var domande = remember(currentIndex) { prendiDomande(questions, currentIndex) }
    var questionText = questions[currentIndex].question_text
    var enableNext by remember { mutableStateOf(false) }
    var quizComopletato by remember { mutableStateOf(false) }

    var risposta by remember { mutableStateOf(true) }
    var backgroundColor by remember { mutableStateOf(Color.Gray) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(VerdeNatura)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .widthIn(max = 600.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Domanda ${currentIndex + 1}/${questions.size}",
                style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(
                text = questionText,
                style = TextStyle(fontSize = 22.sp, fontWeight = FontWeight.SemiBold, color = Color.Black),
                modifier = Modifier.padding(bottom = 24.dp)
            )

            domande.forEach { answer ->
                Button(
                    onClick = {
                        if (answer.correct == true) {
                            score++
                            backgroundColor = Color.Green
                        } else {
                            backgroundColor = Color.Red
                        }
                        enableNext = true
                        risposta = false
                    },
                    enabled = risposta,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = ButtonColors(Color.Gray, Color.Black, backgroundColor, Color.Black)
                ) {
                    Text(answer.text, color = Color.Black)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (currentIndex < questions.lastIndex) {
                        currentIndex++
                        domande = prendiDomande(questions, currentIndex)
                        questionText = questions[currentIndex].question_text
                        enableNext = false
                        backgroundColor = Color.Gray
                        risposta = true
                    } else {
                        quizComopletato = true
                    }
                },
                enabled = enableNext,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AzzurroChiaro2)
            ) {
                Text("Next Question", color = Color.Black)
            }

            Button(
                onClick = {
                    navController.navigate("resultpage/$score")
                },
                enabled = quizComopletato,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AzzurroChiaro2)
            ) {
                Text("Finish", color = Color.Black)
            }
        }
    }
}

@Composable
fun QuizResultPage(score: Int, navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(VerdeNatura)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Hai totalizzato $score su 5!",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = {
                navController.navigate("game")
            },
            colors = ButtonDefaults.buttonColors(containerColor = AzzurroChiaro2)
        ) {
            Text("Torna Schemata Giochi", color = Color.Black)
        }
    }
}

@Composable
fun CameraPage(navController: NavHostController) {
    Scaffold(
        topBar = { HeaderBar(navController, "Happy Green") },
        bottomBar = { BottomNavBar(navController) }
    ) { paddingValues ->
        ScannerScreen(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 5.dp)
                .padding(paddingValues)
        )
    }
}

@Composable
fun UserPage(navController: NavHostController) {
    val context = LocalContext.current.applicationContext
    val coroutineScope = rememberCoroutineScope()
    val id = SecureStorage.getUser(context)
    var utente by remember { mutableStateOf<User?>(null) }

    LaunchedEffect(id) {
        val api = RetrofitInstance.api
        utente = api.getUser(id)
    }

    utente?.let { currentUser ->
        val usrn = currentUser.username
        val pic = currentUser.profile_pic?.replaceFirst("http://", "https://")
            ?: "https://stock.adobe.com/search/images?k=default+user"
        val pts = currentUser.points ?: 0
        val lvl = currentUser.level ?: 0
        val dte = currentUser.date_joined
        val dateString = (dte as? String)?.take(10) ?: "N/A"

        Scaffold(
            topBar = { HeaderBar(navController, "Happy Green") },
            bottomBar = { BottomNavBar(navController) }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .background(VerdeNatura)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(pic),
                        contentDescription = "Profile picture",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .border(2.dp, Color.Gray, CircleShape)
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text("Username: $usrn.", fontWeight = FontWeight.Bold, color = Color.White)
                        Text("Punti: $pts", color = Color.White)
                        Text("Livello: $lvl", color = Color.White)
                        Text("Registrato il: $dateString", color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Add this spacer with weight 1 to push buttons to the bottom
                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = {
                        SecureStorage.clearToken(context)
                        SecureStorage.clearUser(context)
                        SecureStorage.clearPassword(context)
                        navController.navigate("first")
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Logout")
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { navController.navigate("updatePicture") },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("Cambia immagine Profilo")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdatePicture(navController: NavHostController) {
    val context = LocalContext.current.applicationContext
    val coroutineScope = rememberCoroutineScope()
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        imageUri = uri
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(VerdeNatura)
            .verticalScroll(rememberScrollState())
    ) {
        TopAppBar(
            title = {},
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { launcher.launch("image/*") },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Seleziona Immagine")
        }

        imageUri?.let {
            Image(
                painter = rememberAsyncImagePainter(it),
                contentDescription = null,
                modifier = Modifier
                    .padding(16.dp)
                    .height(200.dp)
                    .fillMaxWidth(),
                contentScale = ContentScale.Fit
            )
        }

        Button(
            onClick = {
                coroutineScope.launch {
                    val userID = SecureStorage.getUser(context)
                    val password = SecureStorage.getPassword(context).toString()
                    val api = RetrofitInstance.api
                    val user = api.getUser(userID)

                    val userData = UserData(
                        username = user.username,
                        imageUri = imageUri!!,
                        password = user.password,
                        points = user.points,
                        level = user.level,
                        date_joined = user.date_joined
                    )

                    caricaImmagineProfilo(api, userID, password, context, userData)
                    navController.navigate("user")
                }
            },
            enabled = imageUri != null,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (imageUri != null) MaterialTheme.colorScheme.primary else Color.Gray
            )
        ) {
            Text("Conferma")
        }
    }
}

@Composable
fun OptionsPage(navController: NavHostController) {
    Scaffold(
        topBar = { HeaderBar(navController, "Happy Green") },
        bottomBar = { BottomNavBar(navController) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.maxwell), // your drawable
                contentDescription = "Centered Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.78f), // adjust ratio to your image aspect (e.g., 16:9 ~ 1.78)
                contentScale = ContentScale.FillWidth
            )
        }
    }
}

// Simple navigation buttons
@Composable
fun NavigationButton(text: String, destination: String, navController: NavHostController) {
    Button(
        onClick = { navController.navigate(destination) },
        shape = RoundedCornerShape(5.dp),
        modifier = Modifier.fillMaxWidth(0.4f) // 40% width of screen
    ) {
        Text(text = text)
    }
}


data class Question(
    var correct : Boolean,
    var text : String
)

fun prendiDomande(questions: List<QuizQuestion>, indice : Int): List<Question>{
    var lista = mutableListOf<Question>()

    lista.add(Question(true,questions[indice].correct_answer))
    lista.add(Question(false,questions[indice].wrong_answers[0]))
    lista.add(Question(false,questions[indice].wrong_answers[1]))
    lista.add(Question(false,questions[indice].wrong_answers[2]))

    lista.shuffle()
    return lista
}

data class Domanda(
    val text: String,
    val correct: Boolean
)
