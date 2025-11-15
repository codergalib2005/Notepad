package com.example.notepad.presentation.navigation

import kotlinx.serialization.Serializable


@Serializable
sealed class Screen {

    @Serializable
    data object OnBoardingScreen: Screen()

    @Serializable
    data object MainScreen : Screen()

    @Serializable
    data object EditNoteScreen: Screen()

    @Serializable
    data object FolderScreen: Screen()

    @Serializable
    data object ManageCategoriesScreen: Screen()

    @Serializable
    data object AddScreen: Screen()

    @Serializable
    data class EditScreen(
        val id: String,
    ): Screen()

    @Serializable
    data object SearchScreen: Screen()

    @Serializable
    data class ViewRecordScreen(
        val id: String
    ): Screen()

    @Serializable
    data object CurrencyScreen: Screen()

    @Serializable
    data object BudgetSettingScreen: Screen()

}