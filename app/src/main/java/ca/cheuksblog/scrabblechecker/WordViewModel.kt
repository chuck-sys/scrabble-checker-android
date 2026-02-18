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
import java.io.FileInputStream
import java.nio.channels.FileChannel

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
                val fd = context.resources.openRawResourceFd(R.raw.csw24)
                val stream = FileInputStream(fd.fileDescriptor)
                val buffer = stream.channel.map(FileChannel.MapMode.READ_ONLY, fd.startOffset + 5, fd.declaredLength - 5)
                _uiState.value = WordState.Success(TrieSearcher(buffer))
            } catch (e: Exception) {
                Log.e("Dictionary::loadWords", e.stackTraceToString())
                _uiState.value = WordState.Error(e.message.toString())
            }
        }
    }
}

sealed class WordState {
    object Loading : WordState()

    data class Success(val dictionary: TrieSearcher): WordState()
    data class Error(val msg: String): WordState()
}