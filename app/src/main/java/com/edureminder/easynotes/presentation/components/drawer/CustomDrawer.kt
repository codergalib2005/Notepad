package com.edureminder.easynotes.presentation.components.drawer

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.FolderCopy
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonOff
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SpaceDashboard
import androidx.compose.material.icons.filled.StarRate
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import androidx.core.net.toUri
import androidx.navigation.NavController
import com.edureminder.easynotes.presentation.navigation.Screen
import com.edureminder.easynotes.ui.Primary


data class DrawerButton(
    val id: Int,
    val name: String,
    val icon: ImageVector,
    val col: Int,
    val color: Color,
    val onClick: () -> Unit
)
data class ButtonGroup(
    val name: String,
    val buttons: List<DrawerButton>
)

@Composable
fun CustomDrawer(
    onCloseClick: () -> Unit,
    navController: NavController,
    modeViewModel: Int
) {
    val context = LocalContext.current

    val buttons = listOf(
        ButtonGroup(
            "Main Features",
            listOf(
                DrawerButton(
                    id = 1,
                    name = "Homepage",
                    col = 1,
                    color = Color(0xFF66246D),
                    icon = Icons.Default.Home,
                    onClick = {
                        //navController.navigate(Screen.MyApp)
                    }
                ),
                DrawerButton(
                    id = 2,
                    name = "Folder",
                    col = 1,
                    icon = Icons.Default.FolderCopy,
                    color = Color(0xFF126567),
                    onClick = {
                        navController.navigate(Screen.FolderScreen)
                    }
                ),
                DrawerButton(
                    id = 2,
                    name = "Restore/Sync",
                    col = 1,
                    icon = Icons.Default.Backup,
                    color = Color(0xFF126567),
                    onClick = {
                       // navController.navigate(Screen.SyncAccountScreen)
                    }
                ),
//                DrawerButton(
//                    id = 3,
//                    name = "Widgets",
//                    col = 1,
//                    icon = Icons.Default.SpaceDashboard,
//                    color = Color(0xFF7D5260),
//                    onClick = {
//                        navController.navigate(Screen.WidgetsScreen)
//                    }
//                ),
            )
        ),
        ButtonGroup(
            "Support & Feedback",
            listOf(
                DrawerButton(
                    id = 8,
                    name = "Help Center",
                    icon = Icons.AutoMirrored.Filled.HelpOutline,
                    col = 2,
                    color = Color(0xFF126567),
                    onClick = {
                        //navController.navigate(Screen.HelpCenterScreen)
                    }
                ),
                DrawerButton(
                    id = 9,
                    name = "Feedback",
                    icon = Icons.Default.StarRate,
                    color = Color(0xFFEFB8C8),
                    col = 2,
                    onClick = {
                        val packageName = context.packageName
                        val uri = "market://details?id=$packageName".toUri()
                        val goToMarketIntent = Intent(Intent.ACTION_VIEW, uri).apply {
                            // Make sure it opens Play Store
                            setPackage("com.android.vending")
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }

                        try {
                            context.startActivity(goToMarketIntent)
                        } catch (e: Exception) {
                            // If Play Store not available, open in browser
                            val webIntent = Intent(
                                Intent.ACTION_VIEW,
                                "https://play.google.com/store/apps/details?id=$packageName".toUri()
                            )
                            context.startActivity(webIntent)
                        }
                    }
                ),
                DrawerButton(
                    id = 10,
                    name = "Share app",
                    icon = Icons.Default.Share,
                    col = 2,
                    color = Color(0xFFFFA500),
                    onClick = {
                        val shareUrl = "https://play.google.com/store/apps/details?id=${context.packageName}" // Replace with actual link if needed
                        // 1. Copy to clipboard
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("App Link", shareUrl)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Link copied to clipboard", Toast.LENGTH_SHORT).show()

                        // 2. Launch share intent
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, "Check out this app: $shareUrl")
                        }
                        val chooser = Intent.createChooser(intent, "Share App")
                        startActivity(context, chooser, null)
                    }
                )
            )
        ),
        ButtonGroup(
            "App Info",
            listOf(
                DrawerButton(
                    id = 12,
                    name = "Privacy Policy",
                    icon = Icons.Default.PrivacyTip,
                    col = 3,
                    color = Color(0xFF66246D),
                    onClick = {
                        val privacyPolicyUrl = "https://www.edureminder.com/notepad/privacy-policy" // Replace with your actual URL
                        val intent = Intent(Intent.ACTION_VIEW, privacyPolicyUrl.toUri())
                        context.startActivity(intent)
                    }
                ),
                DrawerButton(
                    id = 13,
                    name = "About Us",
                    icon = Icons.Default.Person,
                    col = 3,
                    color = Color(0xFF87CEEB),
                    onClick = {
                        val privacyPolicyUrl = "https://www.edureminder.com/notepad" // Replace with your actual URL
                        val intent = Intent(Intent.ACTION_VIEW, privacyPolicyUrl.toUri())
                        context.startActivity(intent)
                    }
                ),
                DrawerButton(
                    id = 13,
                    name = "Setting",
                    icon = Icons.Default.Settings,
                    col = 3,
                    color = Color(0xFF87CEEB),
                    onClick = {
                        navController.navigate(Screen.SettingScreen)
                    }
                )
            )
        )
    )



    Column (
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .background(Color.White)
            .background(Primary.copy(0.1f))
    ) {
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .background(Primary)
                .padding(horizontal = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            IconButton(
                onClick = {
                    onCloseClick()
                },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = Color.White.copy(0.2f)
                )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = null,
                    tint = Color.White
                )
            }
            Text(
                text = "Noteezy",
                color = Color.White.copy(0.9f),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        }
        Column (
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Spacer(Modifier.height(10.dp))
            buttons.forEach { group ->
                Column (
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.medium)
                        .background(Color.White)
                        .padding(vertical = 10.dp)
                ) {
                    Text(
                        text = group.name,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black.copy(0.6f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 15.dp)
                    )
                    group.buttons.forEach { item ->
                        NavigationButton(
                            item,
                            onCloseClick
                        )
                    }
                }
            }
            Spacer(Modifier.height(10.dp))
        }
    }
}
@Composable
fun NavigationButton(
    item: DrawerButton,
    onCloseClick: () -> Unit
) {
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .clickable{
                onCloseClick()
                item.onClick()
            }
            .padding(vertical = 8.dp, horizontal = 15.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row (
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box (
                modifier = Modifier
                    .size(35.dp)
                    .clip(MaterialTheme.shapes.small)
                    .background(item.color),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = null,
                    tint = Color.White
                )
            }
            Text(
                text = item.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black.copy(0.7f)
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowRight,
            contentDescription = null,
            tint = Color.Black.copy(0.6f)
        )
    }
}