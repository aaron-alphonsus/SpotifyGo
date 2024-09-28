package com.example.spotifygo

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.spotifygo.ui.theme.SpotifyGoTheme
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.Locale

class MusicInfoActivity : ComponentActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val requestLocationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                getLastLocation()  // If permission is granted, proceed to get location
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        setContent {
            SpotifyGoTheme {
                MusicInfoScreen(
                    onUseMyLocation = { requestLocationPermission() },
                    onSearch = { cityName -> performSearch(cityName) }
                )
            }
        }
    }

    private fun requestLocationPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                // Permission is granted, proceed to get the location
                getLastLocation()
            }

            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                // Show an explanation to the user why permission is needed
                Toast.makeText(
                    this,
                    "Location permission is needed to get your location",
                    Toast.LENGTH_SHORT
                ).show()
            }

            else -> {
                // Directly request the permission
                requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == 1001 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            // Permission granted, retry getting the location
//            getLastLocation()
//        } else {
//            // Permission denied, handle gracefully
//            Toast.makeText(this, "Permission denied. Unable to use location.", Toast.LENGTH_SHORT).show()
//        }
//    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
//        // Check if location permission is granted
//        if (ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            // Request the permission if not granted
//            ActivityCompat.requestPermissions(
//                this,
//                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
//                1001
//            )
//            return
//        }

        // Show temporary loading indicator while location is being retrieved
        Toast.makeText(this, "Getting your location...", Toast.LENGTH_SHORT).show()

        // Request the last known location
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                try {
                    // Reverse geocode the coordinates to get the city name
                    val geocoder = Geocoder(this, Locale.getDefault())
                    val addresses = geocoder.getFromLocation(it.latitude, it.longitude, 1)

                    // Extract city name from the address
                    val cityName = addresses?.get(0)?.locality ?: "Unknown Location"

                    // Trigger the search or use the city name as needed
                    performSearch(cityName)
                } catch (e: Exception) {
                    Toast.makeText(this, "Geocoder failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            } ?: run {
                // Handle the case where location is null
                Toast.makeText(this, "Failed to retrieve location", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun performSearch(cityName: String) {
        // Create an Intent to start MusicResultsActivity
        val intent = Intent(this, MusicResultsActivity::class.java).apply {
            putExtra("cityName", cityName)  // Pass the city name through the intent
        }
        // Start MusicResultsActivity
        startActivity(intent)
//        Toast.makeText(this, "Searching for music in $cityName", Toast.LENGTH_SHORT).show()
    }
}

@Composable
fun CitySearchField(
    cityName: String,
    onCityNameChange: (String) -> Unit,
    onSearch: () -> Unit
) {
    TextField(
        value = cityName,
        onValueChange = onCityNameChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(text = "City Name") },
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = { onSearch() }
        ),
        textStyle = TextStyle(
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 18.sp
        )
    )
}

@Composable
fun MusicInfoScreen(
    onUseMyLocation: () -> Unit,
    onSearch: (String) -> Unit
) {
    var cityName by remember { mutableStateOf("") }
    val cities = listOf("Seattle", "San Francisco", "New York", "Los Angeles", "Charlotte", "Rapid City")  // Example cities

    // Filter suggestions based on input
    val filteredCities = cities.filter {
        it.contains(cityName, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Enter a City Name",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        CityAutocompleteTextField(
            cityName = cityName,
            onCityNameChange = { cityName = it },
            suggestions = filteredCities,
            onSelectSuggestion = { selectedCity ->
                cityName = selectedCity
                onSearch(selectedCity)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { onSearch(cityName) }) {
            Text(text = "Get Music Info")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onUseMyLocation) {
            Text(text = "Use My Location")
        }
    }
}

@Composable
fun CityAutocompleteTextField(
    cityName: String,
    onCityNameChange: (String) -> Unit,
    suggestions: List<String>,
    onSelectSuggestion: (String) -> Unit
) {
    Column {
        TextField(
            value = cityName,
            onValueChange = onCityNameChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = "City Name") },
            singleLine = true,  // Make it a single line field
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { onSelectSuggestion(cityName) }  // Trigger search on 'Done'
            )
        )

        // Only show suggestions if the user has typed something
        if (cityName.isNotBlank() && suggestions.isNotEmpty()) {
            suggestions.forEach { suggestion ->
                Text(
                    text = suggestion,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable { onSelectSuggestion(suggestion) }
                )
            }
        }
    }
}