package com.davithayrapetyan.textreaderapp.android

import android.os.Bundle
import android.os.StrictMode
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

class MainActivity : ComponentActivity() {
    private lateinit var queue: RequestQueue

    override fun onCreate(savedInstanceState: Bundle?) {
        // Disable StrictMode (not recommended for production)
        System.setProperty("dns.server", "8.8.8.8")
        System.setProperty("java.net.preferIPv4Stack", "true")
        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder().permitAll().build())

        super.onCreate(savedInstanceState)

        // Initialize Volley RequestQueue
        queue = Volley.newRequestQueue(this)

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    GreetingScreen(queue)
                }
            }
        }
    }
}

@Composable
fun GreetingScreen(queue: RequestQueue) {
    // State to store the response text
    var greetingText by remember { mutableStateOf("Loading...") }

    // Launch a coroutine to fetch the SpaceX launch data
    LaunchedEffect(Unit) {
        fetchNextLaunch(queue) { result ->
            greetingText = result ?: "Failed to load data"
        }
    }

    // Display the fetched result in the UI
    GreetingView(greetingText)
}

// Function to fetch the next SpaceX launch data using Volley
fun fetchNextLaunch(queue: RequestQueue, callback: (String?) -> Unit) {
    val url = "https://api.spacexdata.com/v3/launches/next"

    // Create a request to fetch data from the SpaceX API
    val stringRequest = StringRequest(
        Request.Method.GET, url,
        Response.Listener<String> { response ->
            callback(response) // Send response back to UI
        },
        Response.ErrorListener { error ->
            callback("Error: ${error.message}")
        })

    // Add the request to the RequestQueue
    queue.add(stringRequest)
}

@Composable
fun GreetingView(text: String) {
    Text(text = text)
}

@Preview
@Composable
fun DefaultPreview() {
    MyApplicationTheme {
        GreetingView("Hello, Android!")
    }
}
