package ca.cheuksblog.scrabblechecker

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.FileNotFoundException

class WordViewModel(
    private val context: Context,
) : ViewModel() {

    private val _uiState = MutableStateFlow<WordState>(WordState.Loading)
    val uiState: StateFlow<WordState> = _uiState.asStateFlow()

    init {
        loadWords()
    }

    private fun loadWords() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _uiState.value = WordState.Success(Trie(context.resources.openRawResource(R.raw.csw24)))
            } catch (e: FileNotFoundException) {
                Log.e("Dictionary::loadWords", "File not found: ${e.message}")
                _uiState.value = WordState.Success(Trie())
            } catch (e: Exception) {
                Log.e("Dictionary::loadWords", "Exception: ${e.message}")
                _uiState.value = WordState.Success(Trie())
            }
        }
    }
}

sealed class WordState {
    object Loading : WordState()

    data class Success(val dictionary: Trie): WordState()
    data class Error(val msg: String): WordState()
}