package com.example.ui_dev_lab4_var3

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope

internal class NavigateToCalculationScreen(val screenState: CalculationScreenState = CalculationScreenState()) :
    NavigationEvent()

@Composable
internal fun CalculationScreen(initialState: CalculationScreenState, navigator: Navigator) {
    val context = LocalContext.current

    withViewModel(factory = { viewModel(it, context) }) { viewModel ->
        val focusManager = LocalFocusManager.current
        val state by viewModel.screenStateFlow.collectAsState()
        val resultState by viewModel.resultButtonEnabled.collectAsState()

        LaunchedEffect(viewModel) {
            viewModel.submitState(initialState)
        }

        Column(Modifier.padding(horizontal = 8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                ModeSelector(
                    name = "Deposit",
                    isSelected = state.selectedMode == CalculationMode.Deposit,
                    onClick = {
                        viewModel.changeMode(CalculationMode.Deposit)
                    }
                )
                ModeSelector(
                    name = "Credit",
                    isSelected = state.selectedMode == CalculationMode.Credit,
                    onClick = {
                        viewModel.changeMode(CalculationMode.Credit)
                    }
                )
            }

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.months,
                onValueChange = { viewModel.changeMonths(it) },
                label = { Text(text = "Duration") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.amount,
                onValueChange = { viewModel.changeAmount(it) },
                label = { Text(text = "Money") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                )
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.interest,
                onValueChange = { viewModel.changeInterest(it) },
                label = { Text(text = "Interest") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.clearFocus() }
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        focusManager.clearFocus()
                        viewModel.calculateResult()
                    },
                    enabled = resultState
                ) {
                    Text(text = "Calculate")
                }

                if (state.result.isNotEmpty() && resultState) {
                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            viewModel.saveStateToFile()
                        }
                    ) {
                        Text(text = "Save")
                    }
                }
            }

            if (state.result.isNotEmpty()) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = TextFieldValue(state.result),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(text = "Result") },
                    singleLine = true,
                    enabled = false
                )
            }
        }
    }
}


@Composable
private fun ModeSelector(name: String, isSelected: Boolean, onClick: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        RadioButton(selected = isSelected, onClick = onClick)
        Text(text = name)
    }
}

private fun viewModel(
    coroutineScope: CoroutineScope,
    context: Context
): CalculationScreenViewModel {
    return CalculationScreenViewModel(coroutineScope, FileHandler(context, coroutineScope))
}


