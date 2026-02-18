package ca.cheuksblog.scrabblechecker.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val CorrectColorScheme = darkColorScheme(
)

private val IncorrectColorScheme = darkColorScheme(
)

@Composable
fun ScrabbleCheckerTheme(
    areAllValid: Boolean? = null,
    content: @Composable () -> Unit
) {
    val colorScheme = when (areAllValid) {
        true -> CorrectColorScheme
        false -> IncorrectColorScheme
        else -> DarkColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}