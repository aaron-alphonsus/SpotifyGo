package com.example.spotifygo

import android.annotation.SuppressLint
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import com.example.spotifygo.ui.theme.SpotifyGoTheme
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.Locale

class MusicInfoActivity : ComponentActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        setContent {
            SpotifyGoTheme {
                MusicInfoScreen(
                    onUseMyLocation = { getLastLocation() },
                    onSearch = { cityName -> performSearch(cityName) }
                )
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                val geocoder = Geocoder(this, Locale.getDefault())
                val addresses = geocoder.getFromLocation(it.latitude, it.longitude, 1)
                val cityName = addresses?.get(0)?.locality ?: "Unknown Location"
                performSearch(cityName)
            }
        }
    }

    private fun performSearch(cityName: String) {
        Toast.makeText(this, "Searching for music in $cityName", Toast.LENGTH_SHORT).show()
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
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Search  // Shows an enter/search button on the keyboard
        ),
        keyboardActions = KeyboardActions(
            onSearch = { onSearch() }  // Trigger the search action when enter is pressed
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

    val cities = listOf("Seattle", "San Francisco", "New York", "Los Angeles")

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
            suggestions = cities.filter { it.contains(cityName, ignoreCase = true) },
            onSelectSuggestion = { selectedCity ->
                cityName = selectedCity
                // Automatically search with the selected suggestion
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
            label = { Text(text = "City Name") }
        )

        // Display suggestions as a dropdown below the text field
        if (suggestions.isNotEmpty()) {
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