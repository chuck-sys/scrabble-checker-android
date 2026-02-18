package ca.cheuksblog.scrabblechecker

import android.util.Log
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.text.intl.Locale
import ca.cheuksblog.scrabblechecker.ui.theme.Correct
import ca.cheuksblog.scrabblechecker.ui.theme.Incorrect

val wordRegex = Regex("\\w+")

class DictionaryCorrectnessTransformation(private val state: WordState): VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        return when (val current = state) {
            is WordState.Success -> {
                val builder = AnnotatedString.Builder(text)
                val correctStyle = SpanStyle(color = Correct)
                val incorrectStyle = SpanStyle(color = Incorrect)

                for (result in wordRegex.findAll(text.text)) {
                    val word = result.value
                    builder.addStyle(
                        style = if (current.dictionary.isValid(word.toUpperCase(Locale.current))) {
                            correctStyle
                        } else {
                            incorrectStyle
                        },
                        start = result.range.first,
                        end = result.range.last + 1,
                    )
                }

                TransformedText(builder.toAnnotatedString(), OffsetMapping.Identity)
            }
            else -> {
                TransformedText(text, OffsetMapping.Identity)
            }
        }
    }
}