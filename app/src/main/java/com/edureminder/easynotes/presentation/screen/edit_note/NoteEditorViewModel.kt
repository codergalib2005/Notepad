package com.edureminder.easynotes.presentation.screen.edit_note

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.*
import androidx.compose.runtime.setValue
import com.edureminder.easynotes.data.ThemeManager
import com.edureminder.easynotes.presentation.screen.edit_note.components.reminderTypes
import com.edureminder.easynotes.room.folder.Folder
import com.edureminder.easynotes.room.note.Type
import kotlinx.serialization.Serializable
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Calendar


val folderColors = listOf(
    "#641e16",
    "#78281f",
    "#512e5f",
    "#4a235a",
    "#154360",
    "#1b4f72",
    "#0e6251",
    "#0b5345",
    "#145a32",
    "#186a3b",
    "#7d6608",
    "#7e5109",
    "#784212",
    "#6e2c00",
    "#7b7d7d",
    "#626567",
    "#424949"
)

@Serializable
data class ChecklistItem(
    val id: Int,
    val title: String,
    val isCompleted: Boolean,
)
data class SelectedDay(
    val day: Int,
    val isSelected: Boolean = true
)

class NoteEditorViewModel : ViewModel() {
    // UI State
    var title by mutableStateOf("")
    var body by mutableStateOf("")
    var checklist by mutableStateOf(emptyList<ChecklistItem>())
    var type by mutableStateOf(Type.NOTE)
    var isLocked by mutableStateOf(false)
    var theme by mutableStateOf(ThemeManager.themes.first())
    fun changeTheme(themeId: Int) {
        ThemeManager.getThemeById(themeId)?.let { theme ->
            this.theme = theme
        }
    }
    fun resetNote() {
        title = ""
        body = ""
        isLocked = false
        theme = ThemeManager.themes.first()
        type = Type.NOTE
        checklist = emptyList()
        selectedFolder = null
        selectedFolderId = "0"
        isOpenFolderList = false
        isNewFolderPopupOpen = false
        isMoreOptionsOpen = false
        showThemeSheet = false
        selectedThemeTabIndex = 0
        isEditable = true
        isPinned = false
        isDeletePopupOpen = false
        isAddOrRenameFolderPopupOpen = false
        folderToDelete = null
        folderBeingEdited = null
        folderName = ""
        selectedColor = folderColors.first()
        isMarkdownToRichText = true
        isTextColorPickerOpen = false
        isTextBackgroundColorPickerOpen = false
        isTextFontSizePickerOpen = false
    }
    var selectedFolder by mutableStateOf<Folder?>(null)
    var selectedFolderId by mutableStateOf("0")
    var isOpenFolderList by mutableStateOf(false)
    var isNewFolderPopupOpen by mutableStateOf(false)
    var isMoreOptionsOpen by mutableStateOf(false)
    var showThemeSheet by mutableStateOf(false)
    var showReminderDialog by  mutableStateOf(false)
    var selectedThemeTabIndex by mutableIntStateOf(0)
    fun getCurrentDate(): String {
        val zoneId = ZoneId.systemDefault()
        val zoneDateTime = ZonedDateTime.now(zoneId)
        return zoneDateTime.toLocalDate().toString()
    }
    var isEditable by mutableStateOf(true)
    var isPinned by  mutableStateOf(false)
    var isDeletePopupOpen by mutableStateOf(false)
    var isAddOrRenameFolderPopupOpen by mutableStateOf(false)
    var folderToDelete by mutableStateOf<Folder?>(null)
    var folderBeingEdited by mutableStateOf<Folder?>(null)
    var folderName by mutableStateOf("")
    var selectedColor by mutableStateOf(folderColors.first())
    var timeText by mutableStateOf("18:00")
    var dateText by mutableStateOf(getCurrentDate())
    var selectReminderType by mutableStateOf(reminderTypes.first())
    var repeatableDays by mutableStateOf(
        listOf(
            SelectedDay(
                Calendar.SUNDAY
            ),
            SelectedDay(
                Calendar.MONDAY
            ),
            SelectedDay(
                Calendar.TUESDAY
            ),
            SelectedDay(
                Calendar.WEDNESDAY
            ),
            SelectedDay(
                Calendar.THURSDAY
            ),
            SelectedDay(
                Calendar.FRIDAY
            ),
            SelectedDay(
                Calendar.SATURDAY
            )
        )
    )


    var isMarkdownToRichText by  mutableStateOf(true)
    var isTextColorPickerOpen by mutableStateOf(false)
    var isTextBackgroundColorPickerOpen by mutableStateOf(false)
    var isTextFontSizePickerOpen by mutableStateOf(false)

    var selectedCategory by mutableStateOf("Color")

//    val isMoreOptionsOpen = remember { mutableStateOf(false) }
}