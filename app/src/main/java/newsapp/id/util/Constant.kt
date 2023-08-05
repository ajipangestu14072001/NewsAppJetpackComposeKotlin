package newsapp.id.util

import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material.ripple.RippleTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.squareup.moshi.Moshi
import newsapp.id.model.response.ErrorResponse
import java.net.MalformedURLException
import java.net.URL

object Constant {
    const val BASE_URL = "https://newsapi.org/v2/"
    const val API_KEY = "cb77c71114764ad0b76aa30376e9cb0a"

    fun isValidUrl(url: String): Boolean {
        return try {
            URL(url)
            true
        } catch (e: MalformedURLException) {
            false
        }
    }

    fun getErrorMessage(errorBody: String?): String {
        return try {
            val moshi = Moshi.Builder().build()
            val jsonAdapter = moshi.adapter(ErrorResponse::class.java)
            val errorResponse = errorBody?.let { jsonAdapter.fromJson(it) }
            errorResponse?.message ?: "Unknown error occurred"
        } catch (e: Exception) {
            "Error reading response body: ${e.message}"
        }
    }

    object NoRippleTheme : RippleTheme {
        @Composable
        override fun defaultColor() = Color.Unspecified

        @Composable
        override fun rippleAlpha(): RippleAlpha = RippleAlpha(0.0f, 0.0f, 0.0f, 0.0f)
    }

}