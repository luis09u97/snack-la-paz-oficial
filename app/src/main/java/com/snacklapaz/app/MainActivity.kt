package com.snacklapaz.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.snacklapaz.app.ui.navigation.SnackNavGraph
import com.snacklapaz.app.ui.splash.SplashScreen
import com.snacklapaz.app.ui.theme.SnackLaPazTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // IMPORTANTE: installSplashScreen() precisa ser chamado ANTES do super.onCreate()
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SnackLaPazTheme {
                SnackLaPazApp()
            }
        }
    }
}

@Composable
fun SnackLaPazApp() {
    // Controla se ainda estamos na nossa splash em Compose ou já passamos para o app
    var showSplash by remember { mutableStateOf(true) }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        if (showSplash) {
            SplashScreen(onFinished = { showSplash = false })
        } else {
            SnackNavGraph()
        }
    }
}