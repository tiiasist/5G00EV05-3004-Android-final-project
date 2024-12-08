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
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
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
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.wattsapp.ui.theme.errorContainerLight
import com.example.wattsapp.ui.theme.primaryContainerDark
import com.example.wattsapp.ui.theme.primaryContainerLight
import com.example.wattsapp.ui.theme.primaryLight
import com.example.wattsapp.ui.theme.secondaryLight
import com.example.wattsapp.ui.theme.tertiaryContainerLight
import kotlin.math.roundToInt

const val BASE_URL = "https://api.porssisahko.net/"
const val LATEST_PRICES_ENDPOINT = "v1/latest-prices.json"
const val API_MAIN_PAGE_URL = "https://www.porssisahko.net/api"


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
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = " Data behind the app",
            textAlign = TextAlign.Center,
            fontSize = 30.sp,
            modifier =  Modifier.padding(16.dp)
        )
        Spacer(modifier = Modifier.padding(30.dp))
        Text(
            text = "WattsApp uses data from Porssisahko API, wich provides electricity prices in Finland",
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
            Text(text = "API main page")
        }
        Spacer(modifier = Modifier.padding(25.dp))
        Text(
            text = "The latest prices are available in JSON format. Tomorrows prices are available after 14:15",
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
            Text(text = "Data in JSON")
        }

    }
}