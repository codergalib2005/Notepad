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
    ): Screen()

    @Serializable
    data object AddNoteScreen: Screen()


    @Serializable
    data object AddChecklistScreen: Screen()
    @Serializable
    data class EditChecklistScreen(
        val checklistId: String
    ): Screen()



    @Serializable
    data object ArchivedNoteScreen: Screen()

    @Serializable
    data object LockedScreen: Screen()

    @Serializable
    data object RecycleNoteScreen: Screen()

    @Serializable
    data object SettingScreen: Screen()

    @Serializable
    data object AuthScreen: Screen()
    @Serializable
    data object Subscription: Screen()

    @Serializable
    data object AddDiaryScreen: Screen()

    @Serializable
    data class EditDiaryScreen(
        val diaryId: String,
        val encrypted: Boolean
    ): Screen()


    @Serializable
    data class ImageViewScreen(
        val imageUrl: String
    ): Screen()
}