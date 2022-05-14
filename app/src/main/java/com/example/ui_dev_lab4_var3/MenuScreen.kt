package com.example.ui_dev_lab4_var3

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

internal object NavigateToMenuScreen : NavigationEvent()

@Composable
internal fun MenuScreen(navigator: Navigator) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    withViewModel(factory = { FileHandler(context, it) }) { fileHandler ->
        Column {
            MenuButton(text = "New calculation", onClick = {
                coroutineScope.launch { navigator.addToStack(NavigateToCalculationScreen()) }
            })
            MenuButton(text = "Load last saved calculation", onClick = {
                fileHandler.listFiles().lastOrNull()?.let {
                    val state = fileHandler.openFile<CalculationScreenState>(it)
                    coroutineScope.launch { navigator.addToStack(NavigateToCalculationScreen(state)) }
                }
            })
        }
    }
}

@Composable
private fun MenuButton(text: String, onClick: () -> Unit) {
    Box(modifier = Modifier
        .clickable { onClick() }
        .padding(12.dp)
        .fillMaxWidth()) {
        Text(text = text, fontSize = 16.sp)
    }
    Spacer(
        modifier = Modifier
            .height(1.dp)
            .fillMaxWidth()
            .background(Color.DarkGray)
    )
}
