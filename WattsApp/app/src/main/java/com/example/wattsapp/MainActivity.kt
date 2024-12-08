package com.example.wattsapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
import android.net.Uri
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.res.stringResource
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.getValue
import androidx.navigation.compose.currentBackStackEntryAsState

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
        topBar = {
            TopBar(navController)
        },
        bottomBar = {
            BottomNavigationBar(navController = navController)
        },
        content = { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "page1",
                modifier = Modifier.padding(innerPadding) // Apply the innerPadding here
            ) {
                composable("page1") {// Home
                    Page1(navController)
                }
                composable("page2") {// Calculator
                    Page2(navController)
                }
                composable("page3") {// Data
                    Page3(navController)
                }
            }
        }
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val title = when (currentRoute) {
        "page1" -> stringResource(R.string.home_title)
        "page2" -> stringResource(R.string.calculator_page_title)
        "page3" -> stringResource(R.string.title_data_page)
        else -> stringResource(R.string.app_name)
    }

    TopAppBar(
        title = {
            Text(text = title,
                textAlign = TextAlign.Center,
                fontSize = 30.sp,
                modifier = Modifier.padding(16.dp)
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )

}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
            label = { Text(stringResource(R.string.home_nav_button)) },
            selected = currentRoute == "page1",
            onClick = {
                navController.navigate("page1") {
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(painter = painterResource(id = R.drawable.baseline_calculate_24), contentDescription = "Calculate") },
            label = { Text(stringResource(R.string.counter_nav_button)) },
            selected = currentRoute == "page2",
            onClick = {
                navController.navigate("page2") {
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(painter = painterResource(id = R.drawable.baseline_data_object_24), contentDescription = "Data") },
            label = { Text(stringResource(R.string.data_nav_button)) },
            selected = currentRoute == "page3",
            onClick = {
                navController.navigate("page3") {
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                unselectedIconColor = MaterialTheme.colorScheme.onSurface
            )
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
    }
}


@Composable
fun Page3(navController: NavHostController) {
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.padding(30.dp))
        Text(
            text = stringResource(R.string.wattsapp_uses_data_from_porssisahko_api_wich_provides_electricity_prices_in_finland),
            textAlign = TextAlign.Center,
            fontSize = 20.sp,
            modifier =  Modifier.padding(16.dp)
        )
        Button(onClick = {
            val intentBrowserMain = Intent(Intent.ACTION_VIEW,
                Uri.parse("https://www.porssisahko.net/api"))
            context.startActivity(intentBrowserMain)
            }
            ) {
            Text(text = stringResource(R.string.api_main_page_button))
        }
        Spacer(modifier = Modifier.padding(25.dp))
        Text(
            text = stringResource(R.string.the_latest_prices_are_available_in_json_format_tomorrows_prices_are_available_after_14_15),
            textAlign = TextAlign.Center,
            fontSize = 20.sp,
            modifier =  Modifier.padding(16.dp)
        )
        Button(onClick = {
            val intentBrowserDataJSON = Intent(Intent.ACTION_VIEW,
                Uri.parse("https://api.porssisahko.net/v1/latest-prices.json"))
            context.startActivity(intentBrowserDataJSON)
        }
        ) {
            Text(text = stringResource(R.string.data_in_json_button))
        }

    }
}