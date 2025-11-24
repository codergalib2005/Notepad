package com.edureminder.notebook.presentation.screen.main_screen.setting_views

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.edureminder.notebook.R

data class Setting(
    val icon: Int,
    val name: String,
    val group: Int,
    val onClick: () -> Unit
)

@Composable
fun SettingView(
    navController: NavController,
    onSnackbarUpdate: (String) -> Unit,
    onToggleSidebar: () -> Unit
) {
    val settings = listOf(
        Setting(
            icon = R.drawable.theme,
            name = "Theme",
            group = 1,
            onClick = {
            }
        ),
        Setting(
            icon = R.drawable.lock,
            name = "Lock",
            group = 2,
            onClick = {
            }
        ),
        Setting(
            icon = R.drawable.reminder,
            name = "Reminders",
            group = 2,
            onClick = {
            }
        ),
        Setting(
            icon = R.drawable.folder,
            name = "Folder Manager",
            group = 2,
            onClick = {
            }
        ),
        Setting(
            icon = R.drawable.cloud_upload,
            name = "Cloud Backup",
            group = 3,
            onClick = {
            }
        ),
        Setting(
            icon = R.drawable.export_data,
            name = "Export Data",
            group = 3,
            onClick = {
            }
        ),
        Setting(
            icon = R.drawable.delete_icon_dark,
            name = "Delete app data",
            group = 3,
            onClick = {
            }
        ),
        Setting(
            icon = R.drawable.import_data,
            name = "Import Data",
            group = 3,
            onClick = {
            }
        ),
        Setting(
            icon = R.drawable.share,
            name = "Share with friends",
            group = 4,
            onClick = {
            }
        ),
        Setting(
            icon = R.drawable.feedback,
            name = "Give Us Feedback",
            group = 4,
            onClick = {
            }
        ),
        Setting(
            icon = R.drawable.privacy,
            name = "Privacy Policy",
            group = 4,
            onClick = {
            }
        ),
        Setting(
            icon = R.drawable.term_and_condition,
            name = "Terms & Condition",
            group = 4,
            onClick = {
            }
        ),
    )


    Column (
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 10.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        settings.groupBy { it.group }.forEach { (group, items) ->
            Column (
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.medium)
                    .background(Color.White)
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                items.forEach { items ->
                    SettingItemViwe(
                        icon = items.icon,
                        name = items.name,
                        onClick = items.onClick
                    )
                }
            }
        }
        Spacer(
            modifier = Modifier
                .height(90.dp)
        )
    }
}

@Composable
fun SettingItemViwe(icon: Int, name: String, onClick: () -> Unit) {
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row (
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                tint = Color.Black.copy(0.8f),
                modifier = Modifier
                    .size(25.dp)
            )
            Text(
                text = name,
                color = Color.Black.copy(0.8f),
            )
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            modifier = Modifier
                .size(20.dp),
            tint = Color.Black.copy(0.6f)
        )
    }
}