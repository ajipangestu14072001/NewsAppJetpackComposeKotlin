package newsapp.id.navigation

sealed class Screen(val route: String) {
    object Main : Screen(route = "main")
    object WebView : Screen(route = "webview")
}