package com.unmsm.nutrihealth.ui.util


import androidx.compose.animation.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

object ComposeUtils {
    const val slideDownInDuration = 400

    @Composable
    fun SlideUpAnimatedVisibility(
        modifier: Modifier = Modifier,
        visible: Boolean,
        content: @Composable () -> Unit
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = modifier
        ) {
            content()
        }
    }
}
