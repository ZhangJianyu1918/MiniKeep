package com.example.minikeep

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
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
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MiniKeepTheme {
//                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//                    Greeting(
//                        name = "Android",
//                        modifier = Modifier.padding(innerPadding)
//                    )
//                }
                Surface(modifier = Modifier.fillMaxSize()) {
                    MiniKeepNavigation()
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

@Composable
fun MiniKeepNavigation() {
    val navigationController = rememberNavController()
    val coroutineScope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

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
        NavHost(navController = navigationController, startDestination = "home") {
            composable("home") { HomeScreen(navigationController, drawerState) }
            composable("login") { LoginScreen(navigationController, drawerState) }
            composable("register") { RegisterScreen(navigationController, drawerState) }
            composable("form") { FormScreen(navigationController, drawerState) }
            composable("map") { MapScreen(navigationController, drawerState) }
            composable("profile") { ProfileScreen(navigationController, drawerState) }
            composable("calendar") { CalendarScreen(navigationController, drawerState) }
        }
    }
}

data class MenuItem(
    val title: String,
    val route: String,
    val icon: ImageVector? = null // 可选图标，使用 Icons.Default
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
    ModalDrawerSheet {
        Spacer(modifier = Modifier.height(16.dp))
        menus.forEach { menu ->
            NavigationDrawerItem(
                icon = { menu.icon?.let { Icon(it, contentDescription = null) } },
                label = { Text(menu.title) },
                selected = false, // 可以动态设置选中状态
                onClick = { onMenuClick(menu.route) },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )
        }
    }
}