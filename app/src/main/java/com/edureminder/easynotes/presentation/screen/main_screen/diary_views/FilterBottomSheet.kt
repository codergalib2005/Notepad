package com.edureminder.easynotes.presentation.screen.main_screen.diary_views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.edureminder.easynotes.ui.ColorBlack
import com.edureminder.easynotes.ui.ColorWhite
import com.edureminder.easynotes.ui.Primary


enum class SortOrder(val value: String) {
    TITLE_ASCENDING("titleAsc"),
    TITLE_DESCENDING("titleDesc"),
    DATE_NEWEST_FIRST("createdDesc"),
    DATE_OLDEST_FIRST("createdAsc"),
    UPDATED_AT_NEWEST_FIRST("updatedDesc"),
    UPDATED_AT_OLDEST_FIRST("updatedAsc")
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    sheetState: SheetState,
    isFilterSheetOpen: Boolean,
    diaryFilter: DiaryFilterPreference,
    onDismiss: () -> Unit,
) {
    if (isFilterSheetOpen) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            dragHandle = null,
            containerColor = ColorWhite,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 15.dp)
                    .padding(top = 15.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // ---------------- Sort Options ----------------
                Box(
                    contentAlignment = Alignment.CenterStart
                ) {
                    HorizontalDivider(color = Color.LightGray)
                    Text(
                        text = "Sort By",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(vertical = 4.dp)
                            .background(Color.White)
                            .padding(horizontal = 10.dp)
                    )
                }

                // ---------------- Sort Options ----------------
                Box(contentAlignment = Alignment.CenterStart) {
                    HorizontalDivider(color = Color.LightGray)
                    Text(
                        text = "Sort By",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(vertical = 4.dp)
                            .background(Color.White).padding(horizontal = 10.dp)
                    )
                }
                // ---------------- Sort Options ----------------
                val sortOrders = listOf(
                    SortOrder.TITLE_ASCENDING,
                    SortOrder.TITLE_DESCENDING,
                    SortOrder.DATE_NEWEST_FIRST,
                    SortOrder.DATE_OLDEST_FIRST,
                    SortOrder.UPDATED_AT_NEWEST_FIRST,
                    SortOrder.UPDATED_AT_OLDEST_FIRST
                )

                sortOrders.forEach { sort ->
                    SettingItem(
                        icon = when(sort) {
                            SortOrder.TITLE_ASCENDING, SortOrder.TITLE_DESCENDING -> Icons.Default.Title
                            SortOrder.DATE_NEWEST_FIRST, SortOrder.DATE_OLDEST_FIRST -> Icons.Default.DateRange
                            SortOrder.UPDATED_AT_NEWEST_FIRST, SortOrder.UPDATED_AT_OLDEST_FIRST -> Icons.Default.History
                        },
                        label = sort.name,
                        selected = diaryFilter.sortOption == sort.value,
                        enabled = diaryFilter.sortOption != sort.value
                    ) {
                        diaryFilter.sortOption = sort.value
                    }
                }


                // ---------------- Mood Options ----------------
//                Box(contentAlignment = Alignment.CenterStart) {
//                    HorizontalDivider(color = Color.LightGray)
//                    Text(
//                        text = "Mood",
//                        style = MaterialTheme.typography.titleMedium,
//                        modifier = Modifier.padding(vertical = 4.dp)
//                            .background(Color.White).padding(horizontal = 10.dp)
//                    )
//                }
//
//                val moods = listOf(
//                    null to "All",
//                    0 to "Neutral",
//                    1 to "Happy",
//                    2 to "Sad",
//                    3 to "Angry"
//                )
//
//                moods.forEach { (value, label) ->
//                    SettingItem(
//                        icon = Icons.Default.Face,
//                        label = label,
//                        selected = diaryFilter.mood == value,
//                        enabled = diaryFilter.mood != value
//                    ) {
//                        diaryFilter.mood = value
//                    }
//                }

                // ---------------- Created/Updated Filters ----------------
                // You can add custom UI for picking date ranges
                // Example: Pickers or Buttons to update createdAfter/Before & updatedAfter/Before
            }
        }
    }
}

@Composable
fun SettingItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CircleShape)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(vertical = 4.dp, horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(28.dp),
            tint = if (selected) Primary else ColorBlack.copy(0.8f)
        )
        Text(
            text = label,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = if (selected) Primary else ColorBlack.copy(0.8f)
        )
    }
}
