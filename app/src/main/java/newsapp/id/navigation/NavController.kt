package newsapp.id.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import newsapp.id.view.detail.DetailScreen
import newsapp.id.view.main.MainScreen

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun NavController(
    navController: NavHostController,
    startDestination: String
) {
    AnimatedNavHost(
        navController = navController,
        startDestination = startDestination
    ){
        composable(route = Screen.Main.route){
            MainScreen(navController = navController)
        }

        composable(
            route = "${Screen.WebView.route}/{urlNews}",
            arguments = listOf(navArgument("urlNews") { type = NavType.StringType })
        ) { backStackEntry ->
            val urlNews = backStackEntry.arguments?.getString("urlNews")
            DetailScreen(urlNews = urlNews!!)
        }
    }
}