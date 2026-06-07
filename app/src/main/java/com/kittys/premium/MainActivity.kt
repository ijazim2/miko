package com.kittys.premium

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.rememberNavController
import com.kittys.premium.core.navigation.MikoNavGraph
import com.kittys.premium.ui.theme.MikoTheme
import dagger.hilt.android.AndroidEntryPoint
import com.kittys.premium.core.ui.theme.MikoColors
import com.kittys.premium.core.ui.theme.MikoTypography
// ════════════════════════════════════════════════════════════════
//   MIKO — MainActivity
//   Single-activity Compose host with edge-to-edge layout
// ════════════════════════════════════════════════════════════════

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                scrim = MikoColors.Background.toArgb(),
                darkScrim = MikoColors.Background.toArgb()
            ),
            navigationBarStyle = SystemBarStyle.light(
                scrim = MikoColors.Surface.toArgb(),
                darkScrim = MikoColors.Surface.toArgb()
            )
        )

        super.onCreate(savedInstanceState)

        setContent {
            MikoTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MikoColors.Background),
                    color = MikoColors.Background
                ) {
                    val navController = rememberNavController()
                    MikoNavGraph(navController = navController)
                }
            }
        }
    }

    private fun Color.toArgb(): Int = android.graphics.Color.argb(
        (alpha * 255).toInt(),
        (red * 255).toInt(),
        (green * 255).toInt(),
        (blue * 255).toInt()
    )
}
