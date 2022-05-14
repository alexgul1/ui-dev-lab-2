package com.example.ui_dev_lab4_var3

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlin.math.pow

@Serializable
internal enum class CalculationMode {
    Deposit,
    Credit
}

@Serializable
internal data class CalculationScreenState(
    val selectedMode: CalculationMode = CalculationMode.Deposit,
    val amount: String = "",
    val months: String = "",
    val result: String = "",
    val interest: String = "",
) : ScreenState()

internal class CalculationScreenViewModel(
    private val coroutineScope: CoroutineScope,
    private val fileHandler: FileHandler
) {

    private val _screenStateFlow: MutableStateFlow<CalculationScreenState> =
        MutableStateFlow(CalculationScreenState())
    internal val screenStateFlow: StateFlow<CalculationScreenState> = _screenStateFlow

    private val _resultButtonEnabled: MutableStateFlow<Boolean> = MutableStateFlow(false)
    internal val resultButtonEnabled: StateFlow<Boolean> = _resultButtonEnabled

    internal fun submitState(newState: CalculationScreenState) {
        val resultEnabled = if (newState.selectedMode == CalculationMode.Credit) {
            newState.amount.isNotEmpty()
                    && newState.months.isNotEmpty()
                    && newState.interest.isNotEmpty()
        } else {
            newState.amount.isNotEmpty()
                    && newState.months.isNotEmpty()
                    && newState.interest.isNotEmpty()
        }
        coroutineScope.launch {
            _screenStateFlow.emit(newState)
            _resultButtonEnabled.emit(resultEnabled)
        }
    }

    internal fun changeMode(newMode: CalculationMode) {
        val newState = _screenStateFlow.value.copy(selectedMode = newMode, result = "")
        submitState(newState)
    }

    internal fun changeMonths(newMonths: String) {
        val newState = _screenStateFlow.value.copy(months = newMonths)
        submitState(newState)
    }

    internal fun changeAmount(newAmount: String) {
        val newState = _screenStateFlow.value.copy(amount = newAmount)
        submitState(newState)
    }

    internal fun changeInterest(newPercent: String) {
        val newState = _screenStateFlow.value.copy(interest = newPercent)
        submitState(newState)
    }

    internal fun calculateResult() {
        val result = if (_screenStateFlow.value.selectedMode == CalculationMode.Deposit) {
            calculateDeposit()
        } else {
            calculateCredit()
        }.toString()

        val newState = _screenStateFlow.value.copy(result = result)
        submitState(newState)
    }

    private fun calculateDeposit(): Double {
        val p = _screenStateFlow.value.amount.toDoubleOrNull()
        val i = _screenStateFlow.value.interest.toDoubleOrNull()
        val t = _screenStateFlow.value.months.toDoubleOrNull()
        return if (p != null && i != null && t != null) {
            (p * i * t / 365.0) / 100.0
        } else {
            0.0
        }
    }

    private fun calculateCredit(): Double {
        val s = _screenStateFlow.value.amount.toDoubleOrNull()
        val p = _screenStateFlow.value.interest.toDoubleOrNull()
        val n = _screenStateFlow.value.months.toDoubleOrNull()
        return if (p != null && s != null && n != null) {
            val p1 = p / 1200.0
            s * ((p1 * (1.0 + p1).pow(n)) / ((1 + p1).pow(n) - 1.0))
        } else {
            0.0
        }
    }

    internal fun saveStateToFile() {
        fileHandler.saveModelToFile(_screenStateFlow.value)
    }
}
