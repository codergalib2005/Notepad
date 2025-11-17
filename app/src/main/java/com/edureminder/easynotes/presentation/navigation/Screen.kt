package com.edureminder.easynotes.presentation.navigation

import kotlinx.serialization.Serializable



enum class ActionType {
    PASSCODE,
    FINGERPRINT,
    PATTERN
}


@Serializable
sealed class Screen {
    @Serializable
    data object MainScreen : Screen()

    @Serializable
    data object FolderScreen: Screen()

    @Serializable
    data class EditNoteScreen(
        val noteId: String,
        val encrypted: Boolean
    ): Screen()

    @Serializable
    data object AddNoteScreen: Screen()

    @Serializable
    data object Subscription: Screen()
}