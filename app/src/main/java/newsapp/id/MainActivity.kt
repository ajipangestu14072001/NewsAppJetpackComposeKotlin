package newsapp.id

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.animation.ExperimentalAnimationApi
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import dagger.hilt.android.AndroidEntryPoint
import newsapp.id.navigation.NavController
import newsapp.id.navigation.Screen
import newsapp.id.ui.theme.NewsAppJetpackComposeKotlinTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NewsAppJetpackComposeKotlinTheme {
                val navController = rememberAnimatedNavController()
                NavController(
                    navController = navController,
                    startDestination = Screen.Main.route
                )
            }
        }
    }
}