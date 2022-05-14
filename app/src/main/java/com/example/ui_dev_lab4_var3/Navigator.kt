package com.example.ui_dev_lab4_var3

import android.annotation.SuppressLint
import android.view.WindowInsetsController
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*

@SuppressLint("ComposableNaming")
@Composable
internal fun withNavigation(initialDestination: NavigationEvent) {
    val navigator = remember {
        Navigator()
    }
    val currentScreen by navigator.navigationEventFlow.collectAsState()
    val scope = rememberCoroutineScope()
    var enableBackHandler by remember {
        mutableStateOf(true)
    }

    val colors = if(BuildConfig.FLAVOR == "demas") {
        demasColors
    } else {
        sanyaColors
    }

    MaterialTheme(colors) {
        Box(
            Modifier
                .fillMaxSize()
                .background(colors.background)) {
            currentScreen(navigator)
            BackHandler(enableBackHandler) {
                scope.launch {
                    if (!navigator.isLastScreen()) {
                        navigator.popStack()
                    } else {
                        enableBackHandler = false
                    }
                }
            }
        }
    }

    LaunchedEffect(navigator) {
        navigator.addToStack(initialDestination)
    }
}

internal typealias ContentScreen = @Composable (Navigator) -> Unit

@SuppressLint("ComposableNaming")
internal class Navigator {

    private val stack: ArrayDeque<NavigationEvent> = ArrayDeque()

    private val _navigationEventFlow: MutableStateFlow<ContentScreen> = MutableStateFlow {}
    internal val navigationEventFlow: StateFlow<ContentScreen> = _navigationEventFlow

    private fun navigateTo(navigationEvent: NavigationEvent): ContentScreen {
        return when (navigationEvent) {
            is NavigateToMenuScreen -> ({
                MenuScreen(it)
            })
            is NavigateToCalculationScreen -> ({
                CalculationScreen(initialState = navigationEvent.screenState, navigator = it)
            })
            else -> ({

            })
        }
    }

    internal suspend fun addToStack(navigationEvent: NavigationEvent, stateToSave:NavigationEvent? = null) {
        stateToSave?.let {
            stack.pop()
            stack.offerFirst(it)
        }
        if (stack.offerFirst(navigationEvent)) {
            _navigationEventFlow.emit(navigateTo(navigationEvent))
        }
    }
    internal suspend fun popStack() {
        stack.pop()
        stack.peekFirst()?.let {
            _navigationEventFlow.emit(navigateTo(it))
        }
    }

    internal fun isLastScreen(): Boolean {
        return stack.size <= 1
    }
}

internal sealed class NavigationEvent {
    object Entry: NavigationEvent()
}

internal sealed class ScreenState {
    object NoState: ScreenState()
}
