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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.res.stringResource
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.getValue
import androidx.navigation.compose.currentBackStackEntryAsState
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.wattsapp.ui.theme.errorContainerLight
import com.example.wattsapp.ui.theme.primaryContainerDark
import com.example.wattsapp.ui.theme.primaryContainerLight
import com.example.wattsapp.ui.theme.primaryLight
import com.example.wattsapp.ui.theme.secondaryLight
import com.example.wattsapp.ui.theme.tertiaryContainerLight
import kotlin.math.roundToInt
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import androidx.lifecycle.viewmodel.compose.viewModel

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
            containerColor = MaterialTheme.colorScheme.inverseSurface,
            titleContentColor = MaterialTheme.colorScheme.surface
        )
    )

}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.inverseSurface,
        contentColor = MaterialTheme.colorScheme.onSurface,
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
            label = { Text(stringResource(R.string.home_nav_button),
                color = MaterialTheme.colorScheme.surface) },
            selected = currentRoute == "page1",
            onClick = {
                navController.navigate("page1") {
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                unselectedIconColor = MaterialTheme.colorScheme.surface
            )
        )
        NavigationBarItem(
            icon = { Icon(painter = painterResource(id = R.drawable.baseline_calculate_24), contentDescription = "Calculate") },
            label = { Text(stringResource(R.string.counter_nav_button),
                color = MaterialTheme.colorScheme.surface) },
            selected = currentRoute == "page2",
            onClick = {
                navController.navigate("page2") {
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                unselectedIconColor = MaterialTheme.colorScheme.surface
            )
        )
        NavigationBarItem(
            icon = { Icon(painter = painterResource(id = R.drawable.baseline_data_object_24), contentDescription = "Data") },
            label = { Text(stringResource(R.string.data_nav_button),
                color = MaterialTheme.colorScheme.surface) },
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
                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                unselectedIconColor = MaterialTheme.colorScheme.surface
            )
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

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item{
                Text(
                    text = "Cents/kWh Prices",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp)
                )

                BarChart(prices = prices)

                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                        .clip(RoundedCornerShape(6.dp)),
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
                            text = "Cents/kWh",
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
                        .clip(RoundedCornerShape(6.dp)),
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
    val currentTimeFormatted = currentTime.format(DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy"))
    val currentPrice = filteredPrices.find { ZonedDateTime.parse(it.startDate).hour == currentTime.hour }?.price ?: 0.0

    Canvas(modifier = Modifier
        .padding(start = 40.dp, top = 16.dp, end = 16.dp, bottom = 32.dp)
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
        val cornerRadius = 3.dp.toPx()

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

            // Draw the filled bar with rounded corners
            drawRoundRect(
                color = barColor,
                topLeft = androidx.compose.ui.geometry.Offset(xOffset, size.height - barHeight),
                size = androidx.compose.ui.geometry.Size(barWidth, barHeight),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadius, cornerRadius)
            )

            // Draw a thin border with rounded corners if the bar corresponds to the current time
            if (priceTime.hour == currentTime.hour) {
                drawRoundRect(
                    color = Color.Black,
                    topLeft = androidx.compose.ui.geometry.Offset(xOffset, size.height - barHeight),
                    size = androidx.compose.ui.geometry.Size(barWidth, barHeight),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadius, cornerRadius),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.dp.toPx())
                )
            }

            // Draw x-axis labels centered to the bars
            drawContext.canvas.nativeCanvas.drawText(
                ZonedDateTime.parse(price.startDate).format(DateTimeFormatter.ofPattern("HH")),
                xOffset + barWidth / 2,
                size.height + 12.sp.toPx(),
                android.graphics.Paint().apply {
                    color = android.graphics.Color.BLACK
                    textAlign = android.graphics.Paint.Align.CENTER
                    textSize = 12.sp.toPx()
                }
            )
        }

        // Draw y-axis help lines and labels centered to the lines
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
                -90f,
                y + 6.sp.toPx(), // Adjusted to center the label to the line
                android.graphics.Paint().apply {
                    color = android.graphics.Color.BLACK
                    textAlign = android.graphics.Paint.Align.CENTER
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

    // Display selected price, time, and date or current time and price if no spot is selected
    Box(
        modifier = Modifier
            .border(2.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
        ,
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = selectedPrice?.let { (price, time, date) ->
                "Selected Spot:\n$time $date\n$price cents/kWh"
            } ?: "Current Spot:\n$currentTimeFormatted\n$currentPrice cents/kWh",
            modifier = Modifier
                .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 16.dp),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
    }
}


// Second page for calculation of electricity bill
class Page2ViewModel : ViewModel() {
    var consumption by mutableStateOf("")
    var fixedPrice by mutableStateOf("")
    var yearlyCost by mutableStateOf<Double?>(null)
    var monthlyCost by mutableStateOf<Double?>(null)
    var roundedYearlyCost by mutableStateOf("")
    var roundedMonthlyCost by mutableStateOf("")
    var averageYearlyCost by mutableStateOf<Double?>(null)
    var averageMonthlyCost by mutableStateOf<Double?>(null)
    var roundedAverageYearlyCost by mutableStateOf("")
    var roundedAverageMonthlyCost by mutableStateOf("")
    var prices: List<Price> by mutableStateOf(emptyList())
    var loading by mutableStateOf(true)
    var error by mutableStateOf<String?>(null)

    init {
        fetchPrices()
    }

    private fun fetchPrices() {
        viewModelScope.launch {
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
}

@Composable
fun Page2(navController: NavHostController, viewModel: Page2ViewModel = viewModel()) {
    val keyboardController = LocalSoftwareKeyboardController.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            // Input fields
            OutlinedTextField(
                value = viewModel.consumption,
                onValueChange = { viewModel.consumption = it },
                label = { Text("Energy Consumption (kWh / year)") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { keyboardController?.hide() }
                ),
                modifier = Modifier
                    .fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = viewModel.fixedPrice,
                onValueChange = { viewModel.fixedPrice = it },
                label = { Text("Fixed Price (cents/kWh)") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { keyboardController?.hide() }
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(64.dp))
        }

        item {
            // Calculate costs
            val consumptionValue = viewModel.consumption.toDoubleOrNull()
            val fixedPriceValue = viewModel.fixedPrice.toDoubleOrNull()

            if (consumptionValue != null && fixedPriceValue != null) {
                viewModel.yearlyCost = consumptionValue * fixedPriceValue / 100
                viewModel.monthlyCost = viewModel.yearlyCost!! / 12

                viewModel.roundedYearlyCost = String.format("%.2f", viewModel.yearlyCost)
                viewModel.roundedMonthlyCost = String.format("%.2f", viewModel.monthlyCost)
            } else {
                viewModel.yearlyCost = null
                viewModel.monthlyCost = null
            }

            // Display calculated costs
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                    .padding(16.dp)
            ) {
                Text(
                    text = "Fixed price",
                    style = MaterialTheme.typography.titleLarge.copy(textDecoration = TextDecoration.Underline),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = buildAnnotatedString {
                        if (viewModel.consumption.isEmpty() || viewModel.fixedPrice.isEmpty()) {
                            append("Input both values to calculate")
                        } else if (viewModel.yearlyCost == null || viewModel.monthlyCost == null) {
                            append("Invalid input values")
                        } else {
                            append("Yearly Cost: ")
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("${viewModel.roundedYearlyCost} €")
                            }
                            append("\nMonthly Cost: ")
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("${viewModel.roundedMonthlyCost} €")
                            }
                        }
                    },
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            // Display fetched data status and average price calculation
            if (viewModel.loading) {
                Text(text = "Loading price data...", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            } else if (viewModel.error != null) {
                Text(text = "Error: ${viewModel.error}", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            } else {
                val averagePrice = viewModel.prices.map { it.price }.average()
                val roundedAveragePrice = String.format("%.2f", averagePrice)

                if (viewModel.consumption.isNotEmpty()) {
                    viewModel.averageYearlyCost = viewModel.consumption.toDoubleOrNull()?.let { it * averagePrice / 100 }
                    viewModel.averageMonthlyCost = viewModel.averageYearlyCost?.div(12)

                    viewModel.roundedAverageYearlyCost = viewModel.averageYearlyCost?.let { String.format("%.2f", it) } ?: ""
                    viewModel.roundedAverageMonthlyCost = viewModel.averageMonthlyCost?.let { String.format("%.2f", it) } ?: ""
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.errorContainer)
                        .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Recent spot price",
                        style = MaterialTheme.typography.titleLarge.copy(textDecoration = TextDecoration.Underline),
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = buildAnnotatedString {
                            append("Average price from past few days:\n")
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("$roundedAveragePrice €")
                            }
                            append(" cents/kWh\n\n")
                            if (viewModel.consumption.isNotEmpty()) {
                                append("Average Yearly Cost: ")
                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                    append("${viewModel.roundedAverageYearlyCost} €")
                                }
                                append("\nAverage Monthly Cost: ")
                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                    append("${viewModel.roundedAverageMonthlyCost} €")
                                }
                            } else {
                                append("Input your consumption to calculate average costs.")
                            }
                        },
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }
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