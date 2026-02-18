package ca.cheuksblog.scrabblechecker

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toUpperCase
import ca.cheuksblog.scrabblechecker.ui.theme.Correct
import ca.cheuksblog.scrabblechecker.ui.theme.Incorrect
import ca.cheuksblog.scrabblechecker.ui.theme.ScrabbleCheckerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val wordViewModel = WordViewModel(context = this)

        setContent {
            ScrabbleCheckerTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    Dictionary(
                        modifier = Modifier.padding(innerPadding),
                        wordViewModel,
                    )
                }
            }
        }
    }
}

@Composable
fun Dictionary(modifier: Modifier, wordViewModel: WordViewModel) {
    val context = LocalContext.current
    var text by remember { mutableStateOf("") }
    var areAllValid by remember { mutableStateOf<Boolean?>(null) }
    var shouldShowIndividualWrongs by remember { mutableStateOf(false) }
    val state by wordViewModel.uiState.collectAsState()
    val dictionaryCorrectnessTransformation = remember(state) { DictionaryCorrectnessTransformation(state) }
    val transform = remember(shouldShowIndividualWrongs, dictionaryCorrectnessTransformation) {
        if (shouldShowIndividualWrongs) {
            dictionaryCorrectnessTransformation
        } else {
            VisualTransformation.None
        }
    }

    Column {
        Row {
            Checkbox(
                modifier = modifier.fillMaxWidth(0.1f),
                checked = shouldShowIndividualWrongs,
                onCheckedChange = {
                    shouldShowIndividualWrongs = it
                }
            )
            Button(
                modifier = modifier.fillMaxWidth(0.9f),
                enabled = state is WordState.Success,
                onClick = {
                    when (val current = state) {
                        is WordState.Success -> {
                            val trie = current.dictionary
                            areAllValid =
                                text.toUpperCase(Locale.current).lines().all { trie.isValid(it.toByteArray()) }
                            Toast.makeText(
                                context, if (areAllValid == true) {
                                    R.string.correct
                                } else {
                                    R.string.incorrect
                                }, Toast.LENGTH_SHORT
                            ).show()
                        }
                        else -> {
                            Toast.makeText(
                                context, R.string.dict_not_loaded, Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                },
            ) {
                Text("Check")
            }
        }
        TextField(
            modifier = Modifier.fillMaxSize(),
            visualTransformation = transform,
            value = text,
            onValueChange = { s ->
                text = s
                areAllValid = null
            },
            colors = when {
                shouldShowIndividualWrongs -> TextFieldDefaults.colors()
                areAllValid == true -> TextFieldDefaults.colors(
                    focusedContainerColor = Correct,
                    focusedTextColor = Color.Black,
                )
                areAllValid == false -> TextFieldDefaults.colors(
                    focusedContainerColor = Incorrect
                )
                else -> TextFieldDefaults.colors()
            }
        )
    }
}