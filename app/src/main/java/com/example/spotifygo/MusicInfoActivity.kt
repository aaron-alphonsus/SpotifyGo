package com.example.spotifygo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spotifygo.ui.theme.SpotifyGoTheme

class MusicInfoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SpotifyGoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // City input
                        var cityName by remember { mutableStateOf(TextFieldValue("")) }
                        var result by remember { mutableStateOf("") }

                        Text(
                            text = "Enter a City Name",
                            style = MaterialTheme.typography.titleLarge
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        TextField(
                            value = cityName.text,
                            onValueChange = { cityName = TextFieldValue(it) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            label = { Text(text = "City Name") },
                            textStyle = TextStyle(
                                color = MaterialTheme.colorScheme.onBackground,
                                fontSize = 18.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(onClick = {
                            // TODO: Handle OpenAI query with cityName.text
                        }) {
                            Text(text = "Get Music Info")
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        if (result.isNotEmpty()) {
                            Text(text = result)
                        }
                    }
                }
            }
        }
    }
}