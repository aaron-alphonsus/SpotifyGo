package com.example.spotifygo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.spotifygo.ui.theme.SpotifyGoTheme

class MusicResultsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get the city name passed from the previous activity
        val cityName = intent.getStringExtra("cityName") ?: "Unknown City"

        // Mock data for city info and songs
        val cityInfo = "$cityName is known for its rich music culture, with famous artists and bands emerging from the local scene."
        val songs = listOf(
            Song("Song 1 from $cityName", "Artist 1", "spotify://track/abc123"),
            Song("Song 2 from $cityName", "Artist 2", "spotify://track/def456"),
            Song("Song 3 from $cityName", "Artist 3", "spotify://track/ghi789")
        )

        setContent {
            SpotifyGoTheme {
                MusicInfoResultScreen(cityName = cityName, cityInfo = cityInfo, songs = songs)
            }
        }
    }
}

@Composable
fun MusicInfoResultScreen(cityName: String, cityInfo: String, songs: List<Song>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        // City Information
        Text(
            text = "Discover Music in $cityName",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = cityInfo,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Songs List Header
        Text(
            text = "Culturally Important Songs from $cityName",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // List of Songs
        songs.forEach { song ->
            SongItem(song = song)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun SongItem(song: Song) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Navigate to Spotify song */ }
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = song.title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "By ${song.artist}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
        }
        Button(onClick = { /* Open in Spotify */ }) {
            Text(text = "Play on Spotify")
        }
    }
}

data class Song(val title: String, val artist: String, val spotifyUrl: String)