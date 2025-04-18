package com.example.minikeep.ui

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.minikeep.R
import com.example.minikeep.ui.theme.onPrimaryLight
import com.example.minikeep.ui.theme.primaryLight
import com.example.minikeep.ui.theme.secondaryDark
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, drawerState: DrawerState) {
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            MiniKeepTopBar(
                "Home",
                drawerState = drawerState,
                coroutineScope = coroutineScope,
                modifier = Modifier
            )
        },
        modifier = Modifier.systemBarsPadding() // 支持 Edge-to-Edge
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // 欢迎区域
//            WelcomeSection(userName = "User")
            CheckBoxList("Today Workout Plan")
            CheckBoxList("Today Diet Plan")

            FormResultCard()

        }
    }
}

@Composable
fun CheckBoxList(title: String) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp).background(color = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        var checked1 by remember { mutableStateOf(false) }
        var checked2 by remember { mutableStateOf(false) }
        Text(text = title, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(16.dp))
        CheckoutBox(false, onCheckedChange = { checked1 = it },"Toast Text1", "Title1", "Description1")
        CheckoutBox(false, onCheckedChange = { checked2 = it },"Toast Text2", "Title2", "Description2")
    }
}


@Composable
fun CheckoutBox(checked: Boolean, onCheckedChange: (Boolean) -> Unit,toastText: String, title: String, description: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        val context = LocalContext.current
        Checkbox(
            checked = checked,
            onCheckedChange = { isChecked ->
                onCheckedChange(isChecked)
                if (isChecked) {
                    Toast.makeText(
                        context,
                        toastText,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )
        Column(
            modifier = Modifier.padding(start = 8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Black
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MiniKeepTopBar(
    title: String,
    drawerState: DrawerState,
    coroutineScope: CoroutineScope,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { Text(title) },
        navigationIcon = {
            IconButton(onClick = {
                coroutineScope.launch { drawerState.open() }
            }) {
                Icon(Icons.Default.Menu, contentDescription = "Open Drawer")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        modifier = modifier
    )
}


@Preview
@Composable
fun HomeScreenPreview() {
    val navController = androidx.navigation.compose.rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    HomeScreen(navController, drawerState)
}