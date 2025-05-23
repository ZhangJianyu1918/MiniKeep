package com.example.minikeep

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
//import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
//import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
//import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.minikeep.ui.CalendarScreen
import com.example.minikeep.ui.FormScreen
import com.example.minikeep.ui.HomeScreen
import com.example.minikeep.ui.LoginScreen
import com.example.minikeep.ui.MapScreen
import com.example.minikeep.ui.ProfileScreen
import com.example.minikeep.ui.RegisterScreen
import com.example.minikeep.ui.theme.MiniKeepTheme
import com.example.minikeep.viewmodel.CalendarEventViewModel
import com.example.minikeep.viewmodel.UserViewModel
import com.example.minikeep.viewmodel.UserDetailViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.minikeep.viewmodel.DietPlanViewModel
import com.example.minikeep.viewmodel.WorkoutPlanViewModel

import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val userViewModel: UserViewModel by viewModels()

    private val calendarEventViewModel: CalendarEventViewModel by viewModels()

    private val dietPlanViewModel: DietPlanViewModel by viewModels()

    private val workoutPlanViewModel: WorkoutPlanViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MiniKeepTheme(dynamicColor = false) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    val userDetailViewModel: UserDetailViewModel = viewModel(
                        factory = object : ViewModelProvider.Factory {
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                return UserDetailViewModel(application) as T
                            }
                        }
                    )

                    MiniKeepNavigation(
                        userViewModel = userViewModel,
                        userDetailViewModel = userDetailViewModel,
                        calendarEventViewModel,
                        dietPlanViewModel,
                        workoutPlanViewModel
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MiniKeepTheme {
        Greeting("Android")
    }
}

@SuppressLint("UnrememberedMutableState")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MiniKeepNavigation(
    userViewModel: UserViewModel,
    userDetailViewModel: UserDetailViewModel,
    calendarEventViewModel: CalendarEventViewModel,
    dietPlanViewModel: DietPlanViewModel,
    workoutPlanViewModel: WorkoutPlanViewModel
) {
    val navigationController = rememberNavController()
    val coroutineScope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val events by mutableStateOf<List<com.google.api.services.calendar.model.Event>>(emptyList())
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                DrawerContent(menus) { route ->coroutineScope.launch{
                    drawerState.close()
                }
                    navigationController.navigate(route)
                }
            }
        }
    ) {
        NavHost(navController = navigationController, startDestination = "login") {
            composable("home") { HomeScreen(navigationController, drawerState, userViewModel, userDetailViewModel, dietPlanViewModel, workoutPlanViewModel) }
            composable("login") { LoginScreen(navigationController, drawerState, userViewModel) }
            composable("register") { RegisterScreen(navigationController, drawerState, userViewModel) }
            composable("form") { FormScreen(navigationController, drawerState, userDetailViewModel, userViewModel) }
            composable("map") { MapScreen(navigationController, drawerState, userViewModel) }
            composable("profile") { ProfileScreen(navigationController, drawerState, userViewModel, dietPlanViewModel, workoutPlanViewModel) }
            composable("calendar") { CalendarScreen(navigationController, drawerState, calendarEventViewModel, userViewModel) }
        }
    }
}

data class MenuItem(
    val title: String,
    val route: String,
    val icon: ImageVector? = null
)

val menus = listOf(
    MenuItem("Home", "home", Icons.Default.Home),
    MenuItem("Login", "login", Icons.Default.Person),
    MenuItem("Register", "register", Icons.Default.AccountBox),
    MenuItem("Form", "form", Icons.Default.Edit),
    MenuItem("Map", "map", Icons.Default.Place),
    MenuItem("Profile", "profile", Icons.Default.AccountCircle),
    MenuItem("Calendar", "calendar", Icons.Default.DateRange)
)

@Composable
fun DrawerContent(
    menus: List<MenuItem>,
    onMenuClick: (String) -> Unit
) {
    Spacer(modifier = Modifier.height(16.dp))
    menus.forEach { menu ->
        NavigationDrawerItem(
            icon = { menu.icon?.let { Icon(it, contentDescription = null) } },
            label = { Text(menu.title) },
            selected = false,
            onClick = { onMenuClick(menu.route) },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
            colors = NavigationDrawerItemDefaults.colors(
                selectedContainerColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.onPrimary,
                selectedIconColor = MaterialTheme.colorScheme.onPrimary
            )
        )
    }
}
