package com.example.ui_dev_lab4_var3

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.time.*

@SuppressLint("ComposableNaming")
@Composable
internal fun <T>withViewModel(factory: suspend (CoroutineScope) -> T, content: @Composable (T) -> Unit) {
    val coroutineScope = rememberCoroutineScope()

    val viewModel = remember {
        val state = mutableStateOf<T?>(null)
        coroutineScope.launch {
            state.value = factory(coroutineScope)
        }
        state
    }

    viewModel.value?.let {
        content(it)
    }
}

internal class FileHandler(private val context: Context, private val scope: CoroutineScope) {

    private val json = Json {
        prettyPrint = true
        encodeDefaults = true
    }

    internal inline fun <reified T> saveModelToFile(model: T) {
        val serializedModel = json.encodeToString(model)
        val timeStamp =
            ZonedDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()).toLocalDateTime()
        val fileName = "result-$timeStamp.txt"

        context.openFileOutput(fileName, Context.MODE_PRIVATE).use {
            it.write(serializedModel.toByteArray())
        }
    }

    internal fun listFiles(): List<String> {
        return context.fileList().toList()
    }

    internal inline fun <reified T> openFile(fileName: String): T {
        val reader = context.openFileInput(fileName).bufferedReader()
        val serializedModel = reader.readText()
        reader.close()

        return json.decodeFromString(serializedModel)
    }
}
