package com.example.wattsapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.wattsapp.ui.theme.WattsAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WattsAppTheme {
                WattsApp()
            }
        }
    }
}

@Composable
@Preview
fun WattsApp() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController)
        },
        content = { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "page1",
                modifier = Modifier.padding(innerPadding) // Apply the innerPadding here
            ) {
                composable("page1") {
                    Page1(navController)
                }
                composable("page2") {
                    Page2(navController)
                }
                composable("page3") {
                    Page3(navController)
                }
            }
        }
    )

}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    NavigationBar {
        NavigationBarItem( // Home
            icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = navController.currentDestination?.route == "page1",
            onClick = {
                navController.navigate("page1")
            }
        )
        NavigationBarItem( // Calculate
            icon = {Icon(painter = painterResource(id = R.drawable.baseline_calculate_24), contentDescription = "Calculate")},
            label = {Text("Counter")},
            selected = navController.currentDestination?.route == "page2",
            onClick = {
                navController.navigate("page2")
            }
        )
        NavigationBarItem(
            icon = {Icon(painter = painterResource(id = R.drawable.baseline_data_object_24), contentDescription = "Data")},
            label = {Text("Data")},
            selected = navController.currentDestination?.route == "page3",
            onClick = {
                navController.navigate("page3")
            }
        )
    }
}

// First page for home screen
@Composable
fun Page1(navController: NavHostController) {

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = " Electricity price today",
            textAlign = TextAlign.Center,
            fontSize = 30.sp,
            modifier = Modifier.fillMaxSize()
                .padding(16.dp)

        )
    }
}

// Second page for calculation of electricity bill
@Composable
fun Page2(navController: NavHostController) {

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = " Count your electricity bill",
            textAlign = TextAlign.Center,
            fontSize = 30.sp,
            modifier = Modifier.fillMaxSize()
                .padding(16.dp)

        )
    }
}

@Composable
fun Page3(navController: NavHostController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = " About DATA",
            textAlign = TextAlign.Center,
            fontSize = 30.sp,
            modifier = Modifier.fillMaxSize()
                .padding(16.dp)

        )
    }
}