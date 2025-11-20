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

@Serializable
data class OffsetC(val x: Float, val y: Float)

@Serializable
data class CanvasObject(
    val id: String = UUID.randomUUID().toString(),
    val res: Int,
    val offset: OffsetC = OffsetC(100f, 100f),
    val rotation: Float = 0f,
    val scale: Float = 1f,
    val isSelected: Boolean = false
)

data class Background(
    val id: Int,
    val res: Int
)
val backgrounds = listOf(
    Background(1, R.drawable.foreground_empty),
    Background(2, R.drawable.foreground_bluewatercolor),
    Background(3, R.drawable.foreground_clouds),
    Background(4, R.drawable.foreground_collage),
    Background(5, R.drawable.foreground_birds),
    Background(6, R.drawable.foreground_floral),
    Background(7, R.drawable.foreground_flowers),
    Background(8, R.drawable.foreground_heartflowers),
    Background(9, R.drawable.foreground_hearts),
    Background(10, R.drawable.foreground_kawaii),
    Background(11, R.drawable.foreground_martianscenery),
    Background(12, R.drawable.foreground_orange),
    Background(13, R.drawable.foreground_pinkwatercolor),
    Background(14, R.drawable.foreground_rabbit),
    Background(15, R.drawable.foreground_stripes),
    Background(16, R.drawable.foreground_winterscenery),
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
    var selectedBackground by mutableStateOf(backgrounds.first())
    var selectedImages by mutableStateOf(emptyList<String>())


    var isMarkdownToRichText by  mutableStateOf(true)
    var isTextColorPickerOpen by mutableStateOf(false)
    var isTextBackgroundColorPickerOpen by mutableStateOf(false)
    var isTextFontSizePickerOpen by mutableStateOf(false)
    var isListSelectorSheetOpen by mutableStateOf(false)
    var isStickersSelectorSheetOpen by mutableStateOf(false)
    var selectedEmoji by mutableStateOf("")

    var selectedCategory by mutableStateOf("Color")

//    val isMoreOptionsOpen = remember { mutableStateOf(false) }


    fun changeBackground(backgroundId: Int) {
        backgrounds.find { it.id == backgroundId }?.let {
            selectedBackground = it
        } ?: run {
            selectedBackground = backgrounds.first()
        }
    }
    /**
     * Canvas
     */
    var canvasItems by mutableStateOf(listOf<CanvasObject>())

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
