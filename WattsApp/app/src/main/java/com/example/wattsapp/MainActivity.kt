package com.example.wattsapp

import android.annotation.SuppressLint
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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.res.stringResource
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.getValue
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import com.example.wattsapp.ui.theme.errorContainerLight
import com.example.wattsapp.ui.theme.primaryContainerLight
import com.example.wattsapp.ui.theme.secondaryLight
import android.content.SharedPreferences
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults

const val BASE_URL = "https://api.porssisahko.net/"
const val LATEST_PRICES_ENDPOINT = "v1/latest-prices.json"
const val API_MAIN_PAGE_URL = "https://www.porssisahko.net/api"

class MainActivity : ComponentActivity() {
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)

        enableEdgeToEdge()
        setContent {
            WattsAppTheme {
                var userName by remember { mutableStateOf(sharedPreferences.getString("user_name", "") ?: "") } // Read the user name from the shared preferences

                WattsApp(
                    sharedPreferences = sharedPreferences,
                    userName = userName,
                    onUserNameChange = { newUserName -> // Update the user name
                        userName = newUserName
                        sharedPreferences.edit().putString("user_name", newUserName).apply()
                    }
                )
            }
        }
    }
}


@Composable
fun WattsApp(sharedPreferences: SharedPreferences, userName: String, onUserNameChange: (String) -> Unit) {
    val navController = rememberNavController()

    Scaffold(
        topBar = {
            TopBar(navController, sharedPreferences)
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
                composable("page4") {// User
                    Page4(navController, sharedPreferences, userName, onUserNameChange)
                }
            }
        }
    )
}

// Top app bar
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(navController: NavHostController, sharedPreferences: SharedPreferences) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val title = when (currentRoute) {
        "page1" -> stringResource(R.string.home_title)
        "page2" -> stringResource(R.string.calculator_page_title)
        "page3" -> stringResource(R.string.title_data_page)
        else -> stringResource(R.string.app_name)
    }
    val userName = sharedPreferences.getString("user_name", "") ?: ""

    TopAppBar(
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    textAlign = TextAlign.Center,
                    fontSize = 28.sp,
                    modifier = Modifier.padding(16.dp)
                )
                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.padding(16.dp, 0.dp, 16.dp, 0.dp)
                ) {
                    if (userName.isNotEmpty()) {
                        Icon(
                            Icons.Filled.Person,
                            contentDescription = "User",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = userName,
                            fontSize = 20.sp,

                        )
                    }
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}

// Bottom navigation bar
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
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Person, contentDescription = "User") },
            label = { Text(stringResource(R.string.user_nav_button)) },
            selected = currentRoute == "page4",
            onClick = {
                navController.navigate("page4") {
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )
    }
}

// First page for home screen
@SuppressLint("DefaultLocale")
@Composable
fun Page1(navController: NavHostController) {

    var prices: List<Price> by remember { mutableStateOf(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                val response = RetrofitInstance.api.getPrices()
                prices = response.prices
                loading = false
            } catch (e: Exception) {
                error = e.message
                loading = false
            }
        }
    }

    if (loading) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Loading price data...")
        }
    } else if (error != null) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Error: $error")
        }
    } else {

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            BarChart(prices = prices)

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
                            .clip(RoundedCornerShape(2.dp))

                        //.border(1.dp, MaterialTheme.colorScheme.onBackground)
                        ,
                        horizontalArrangement = Arrangement.spacedBy(1.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(MaterialTheme.colorScheme.tertiaryContainer)
                        ) {
                            Text(
                                text = "Date",
                                color = MaterialTheme.colorScheme.onTertiaryContainer,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .padding(4.dp)
                                    .fillMaxWidth(),
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(MaterialTheme.colorScheme.tertiaryContainer)
                        ) {
                            Text(
                                text = "Time",
                                color = MaterialTheme.colorScheme.onTertiaryContainer,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .padding(4.dp)
                                    .fillMaxWidth(),
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(MaterialTheme.colorScheme.tertiaryContainer)
                        ) {
                            Text(
                                text = "Cent/kWh",
                                color = MaterialTheme.colorScheme.onTertiaryContainer,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .padding(4.dp)
                                    .fillMaxWidth(),
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }
                items(prices) { price ->
                    val zonedDateTime = ZonedDateTime.parse(price.startDate)
                    val date = zonedDateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                    val time = zonedDateTime.format(DateTimeFormatter.ofPattern("HH.mm"))
                    val priceInCents = String.format("%.2f", price.price)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 4.dp, top = 2.dp, end = 4.dp, bottom = 2.dp)
                            .clip(RoundedCornerShape(2.dp))
                        //.background(MaterialTheme.colorScheme.primaryContainer)
                        //.border(1.dp, MaterialTheme.colorScheme.onBackground)
                        ,
                        horizontalArrangement = Arrangement.spacedBy(1.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            Text(
                                text = date,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .padding(4.dp)
                                    .fillMaxWidth(),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            Text(
                                text = time,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .padding(4.dp)
                                    .fillMaxWidth(),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            Text(
                                text = priceInCents,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .padding(4.dp)
                                    .fillMaxWidth(),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }
        }
    }
}

data class Price(
    val price: Double,
    val startDate: String,
    val endDate: String
)

data class PriceResponse(
    val prices: List<Price>
)

interface ApiService {
    @GET(LATEST_PRICES_ENDPOINT)
    suspend fun getPrices(): PriceResponse
}

object RetrofitInstance {
    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun BarChart(prices: List<Price>) {
    val currentTime = ZonedDateTime.now()
    val startTime = currentTime.minusHours(4)
    val endTime = startTime.plusHours(12)
    val filteredPrices = prices.filter {
        val priceTime = ZonedDateTime.parse(it.startDate)
        priceTime.isAfter(startTime) && priceTime.isBefore(endTime)
    }.sortedBy { ZonedDateTime.parse(it.startDate) }
    val maxPrice = filteredPrices.maxOfOrNull { it.price } ?: 0.0
    var selectedPrice by remember { mutableStateOf<Triple<Double, String, String>?>(null) }

    Canvas(modifier = Modifier
        .padding(start = 40.dp, top = 16.dp, end = 16.dp, bottom = 16.dp)
        .fillMaxWidth()
        .height(200.dp)
        .pointerInput(Unit) {
            detectTapGestures { offset ->
                val gap = 4.dp.toPx()
                val barWidth = (size.width - gap * (filteredPrices.size - 1)) / filteredPrices.size
                val index = (offset.x / (barWidth + gap)).toInt()
                if (index in filteredPrices.indices) {
                    val price = filteredPrices[index]
                    val dateTime = ZonedDateTime.parse(price.startDate)
                    val time = dateTime.format(DateTimeFormatter.ofPattern("HH:mm"))
                    val date = dateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                    selectedPrice = Triple(price.price, time, date)
                }
            }
        }
    ) {
        val gap = 8.dp.toPx()
        val barWidth = (size.width - gap * (filteredPrices.size - 1)) / filteredPrices.size
        val yAxisInterval = maxPrice / 5

        // Draw the bars first
        filteredPrices.forEachIndexed { index, price ->
            val barHeight = (price.price / maxPrice * size.height).toFloat()
            val xOffset = index * (barWidth + gap)
            var barColor = when {
                price.price < 7 -> errorContainerLight
                price.price < 14 -> primaryContainerLight
                else -> secondaryLight
            }

            // Change the color opacity if the bar corresponds to the current time
            val priceTime = ZonedDateTime.parse(price.startDate)
            if (priceTime.hour == currentTime.hour) {
                barColor = barColor.copy(alpha = 0.6f)
            }

            // Draw the filled bar
            drawRect(
                color = barColor,
                topLeft = androidx.compose.ui.geometry.Offset(xOffset, size.height - barHeight),
                size = androidx.compose.ui.geometry.Size(barWidth, barHeight)
            )

            // Draw a thin border if the bar corresponds to the current time
            if (priceTime.hour == currentTime.hour) {
                drawRect(
                    color = Color.Black,
                    topLeft = androidx.compose.ui.geometry.Offset(xOffset, size.height - barHeight),
                    size = androidx.compose.ui.geometry.Size(barWidth, barHeight),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.dp.toPx())
                )
            }

            // Draw x-axis labels
            drawContext.canvas.nativeCanvas.drawText(
                ZonedDateTime.parse(price.startDate).format(DateTimeFormatter.ofPattern("HH")),
                xOffset,
                size.height + 12.sp.toPx(),
                android.graphics.Paint().apply {
                    color = android.graphics.Color.BLACK
                    textSize = 12.sp.toPx()
                }
            )
        }

        // Draw y-axis help lines and labels
        for (i in 0..5) {
            val y = size.height - (i * yAxisInterval / maxPrice * size.height).toFloat()
            drawLine(
                color = Color.Gray,
                start = androidx.compose.ui.geometry.Offset(0f, y),
                end = androidx.compose.ui.geometry.Offset(size.width, y),
                strokeWidth = 1.dp.toPx()
            )
            val label = if (maxPrice >= 10) {
                String.format("%.0f", i * yAxisInterval)
            } else {
                String.format("%.1f", i * yAxisInterval)
            }
            drawContext.canvas.nativeCanvas.drawText(
                label,
                -70f,
                y,
                android.graphics.Paint().apply {
                    color = android.graphics.Color.BLACK
                    textSize = 12.sp.toPx()
                }
            )
        }
    }

//    Text(
//        text = "CURRENT: $currentTime",
//        modifier = Modifier
//            .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 16.dp)
//            .fillMaxWidth(),
//        style = MaterialTheme.typography.bodyLarge,
//        textAlign = TextAlign.Center
//    )

    // Display selected price, time, and date
    selectedPrice?.let { (price, time, date) ->
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Selected Price: $price\nTime $time on $date",
                modifier = Modifier
                    .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 16.dp)
                    .fillMaxWidth(),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )

        }
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
                Uri.parse(API_MAIN_PAGE_URL))
            context.startActivity(intentBrowserMain)
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
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
        Button(
            onClick = {
                val intentBrowserDataJSON = Intent(Intent.ACTION_VIEW, Uri.parse(BASE_URL + LATEST_PRICES_ENDPOINT))
                context.startActivity(intentBrowserDataJSON)
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        ) {
            Text(text = stringResource(R.string.data_in_json_button))
        }

    }
}

// Fourth page for adding user name
@Composable
fun Page4(navController: NavHostController, sharedPreferences: SharedPreferences, userName: String, onUserNameChange: (String) -> Unit) {
    var localUserName by remember { mutableStateOf(userName) }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (userName.isNotEmpty()) {
            Spacer(modifier = Modifier.padding(30.dp))
            Text(text= "Not $userName?",
                fontSize = 24.sp,
                modifier =  Modifier.padding(16.dp)
            )
            Button(onClick = {
                onUserNameChange("") // Delete the user name
            }) {
                Text("Delete user name")
            }
            Spacer(modifier = Modifier.padding(20.dp))
            Text(text = "Or change the user name",
                fontSize = 24.sp,
                modifier =  Modifier.padding(16.dp)
            )
            TextField(
                value = localUserName,
                onValueChange = { newValue ->
                    localUserName = newValue
                },
                label = { Text("Name") }
            )
        } else {
        Spacer(modifier = Modifier.padding(30.dp))
        Text(text = "Who's using this app?",
            fontSize = 24.sp,
            modifier =  Modifier.padding(16.dp)
        )
        TextField(
            value = localUserName,
            onValueChange = { newValue ->
                localUserName = newValue
            },
            label = { Text("Name") }
        )}
        Spacer(modifier = Modifier.padding(16.dp))
        Button(onClick = {
            onUserNameChange(localUserName) // Save the user name
        }) {
            Text("Save")
        }


    }
}