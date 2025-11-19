package com.edureminder.easynotes.presentation.screen.edit_note

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.*
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import com.edureminder.easynotes.data.ThemeManager
import com.edureminder.easynotes.presentation.screen.edit_note.components.reminderTypes
import com.edureminder.easynotes.room.folder.Folder
import com.edureminder.easynotes.room.note.Type
import kotlinx.serialization.Serializable
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Calendar
import java.util.UUID
import com.edureminder.easynotes.R


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
data class CanvasObject(
    val id: String = UUID.randomUUID().toString(),
    val res: Int,
    val offset: Offset = Offset(100f, 100f),
    val rotation: Float = 0f,
    val scale: Float = 1f,
    val isSelected: Boolean = false   // 🔥 NEW
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
    var isListSelectorSheetOpen by mutableStateOf(false)
    var isStickersSelectorSheetOpen by mutableStateOf(true)
    var selectedEmoji by mutableStateOf("")

    var selectedCategory by mutableStateOf("Color")

//    val isMoreOptionsOpen = remember { mutableStateOf(false) }

    /**
     * Canvas
     */
    var canvasItems by mutableStateOf(listOf<CanvasObject>(


    ))
        private set

    fun addImage(res: Int) {
        val newItem = CanvasObject(res = res).copy(isSelected = true)
        canvasItems = canvasItems.map { it.copy(isSelected = false) } + newItem
    }


    fun updateItem(updated: CanvasObject) {
        canvasItems = canvasItems.map { if (it.id == updated.id) updated else it }
    }

    fun deleteItem(item: CanvasObject) {
        canvasItems = canvasItems.filter { it.id != item.id }
    }
    fun selectItem(item: CanvasObject) {
        canvasItems = canvasItems.map {
            if (it.id == item.id) it.copy(isSelected = true)
            else it.copy(isSelected = false)
        }
    }
    fun deselectAll() {
        canvasItems = canvasItems.map { it.copy(isSelected = false) }
    }
}
