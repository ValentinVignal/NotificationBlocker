package com.example.notificationblocker

import android.app.ActivityManager
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.notificationblocker.ui.theme.NotificationBlockerTheme
import android.provider.Settings
import androidx.core.app.NotificationManagerCompat


class MainActivity : ComponentActivity() {

    private lateinit var serviceIntent: Intent


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        serviceIntent = Intent(this, NotificationBlockerListenerService::class.java)
        Log.d("NB", "MainActivity.onCreate")

        setContent {
            NotificationBlockerTheme {
                var isServiceRunning by remember { mutableStateOf(NotificationBlockerListenerService.active) }

                Scaffold(
                    floatingActionButton = {
                        FloatingActionButton(
                            onClick = {
                                val notificationManager =
                                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                                if (isServiceRunning) {
                                    notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL)
                                } else {
                                    if (!NotificationManagerCompat.getEnabledListenerPackages(this)
                                            .contains(
                                                packageName
                                            )
                                    ) {
                                        startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
                                    }

                                    notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_NONE)
                                }
                                isServiceRunning = !isServiceRunning
                                NotificationBlockerListenerService.active = isServiceRunning
                            }
                        ) {
                            Icon(
                                imageVector = if (isServiceRunning) Icons.Default.Close else Icons.Default.PlayArrow,
                                contentDescription = if (isServiceRunning) "Pause" else "Play"
                            )
                        }
                    }
                ) { innerPadding ->
                    Surface(
                        modifier = Modifier.padding(innerPadding),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        // Your UI content
                    }
                }
            }
        }
    }
}
