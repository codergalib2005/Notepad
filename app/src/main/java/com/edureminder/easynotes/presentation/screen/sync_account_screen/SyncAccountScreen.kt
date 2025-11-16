package com.edureminder.easynotes.presentation.screen.sync_account_screen

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.outlined.CloudDownload
import androidx.compose.material.icons.outlined.CloudUpload
import androidx.compose.material.icons.outlined.QuestionMark
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.edureminder.easynotes.R
import com.edureminder.easynotes.drive.AuthViewModel
import com.edureminder.easynotes.drive.SyncViewModel
import com.edureminder.easynotes.room.note.NoteViewModel
import com.edureminder.easynotes.ui.Primary
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext


@Composable
fun SyncAccountScreen (
    navController: NavController,
    authViewModel: AuthViewModel,
    syncViewModel: SyncViewModel = hiltViewModel()
) {
    val notesViewModel: NoteViewModel = hiltViewModel()
    val pendingCount by notesViewModel.getPendingSyncNoteCount().collectAsState(initial = 0)

    val context = LocalContext.current
    val authState by authViewModel.authState.collectAsState()
    var statusMessage by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    val signInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        authViewModel.handleSignInResult(result)
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val message by syncViewModel.operationMessage.collectAsState()
    LaunchedEffect(message) {
        message?.let {
            snackbarHostState.showSnackbar(it)
            delay(3000) // Wait for 3 seconds (or customize the duration)
            syncViewModel.clearMessage()
        }
    }

    Scaffold (
        containerColor = Primary,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
                    .padding(horizontal = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row (
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    IconButton(
                        onClick = {
                            navController.navigateUp()
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
                        text = "Synchronized",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                IconButton(
                    onClick = {
                        
                    },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Color.White.copy(0.2f)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Outlined.QuestionMark,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }
            Column (
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color.White),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when (authState) {
                    is AuthViewModel.AuthState.Loading -> {
                        Column (
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            verticalArrangement = Arrangement.spacedBy(15.dp, Alignment.CenterVertically),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(
                                strokeCap = StrokeCap.Round,
                                color = Primary,
                                strokeWidth = 10.dp
                            )
                            Text(
                                "Loading...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Primary
                            )
                        }
                    }
                    is AuthViewModel.AuthState.SignedOut -> {
                        IsNotSignedContent(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            text = "Sign in with Google",
                            onClick = {
                                authViewModel.signIn(signInLauncher)
                            },
                        )
                    }
                    is AuthViewModel.AuthState.SignedIn -> {
                        val account = (authState as AuthViewModel.AuthState.SignedIn).account
//                            Text("Welcome, ${account.displayName ?: "User"}!", style = MaterialTheme.typography.headlineSmall)
//                            Spacer(modifier = Modifier.height(8.dp))
//                            Text("Email: ${account.email ?: "N/A"}", style = MaterialTheme.typography.bodyLarge)
//                            Spacer(modifier = Modifier.height(24.dp))

                        IsSignedContent(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            pendingCount,
                            account,
                            onClick = {
                                authViewModel.signOut()
                            },
                            syncViewModel
                        )
                    }
                    is AuthViewModel.AuthState.Error -> {
                        val error = (authState as AuthViewModel.AuthState.Error).message
                        Spacer(modifier = Modifier.height(24.dp))
                        IsNotSignedContent(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            text = "Try Signing In Again",
                            onClick = {
                                authViewModel.signIn(signInLauncher)
                            },
                            error,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun IsNotSignedContent(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
    error: String? = null,
) {
    val context = LocalContext.current

    Column (
        modifier = modifier
            .padding(vertical = 20.dp, horizontal = 10.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column {
            if(error != null) Text("Error: $error", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier)
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.cloud_sync),
                contentDescription = null,
                modifier = Modifier
                    .size(140.dp)
            )
            Text(
                text = "Sync your data",
                color = Primary,
                fontSize = 26.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "Sign in to your Google Drive account to sync your notes\nWhen synced successfully, notes and checklists will be stored in your Google Drive account.",
                textAlign = TextAlign.Center,
                fontSize = 15.sp,
                lineHeight = 20.sp,
                color = Color.Black.copy(0.7f)
            )
            Row (
                modifier = Modifier
                    .width(330.dp)
                    .shadow(
                        elevation = 3.dp,
                        clip = true,
                        shape = CircleShape,
                        ambientColor = Primary,
                        spotColor = Primary
                    )
                    .clip(CircleShape)
                    .background(Primary)
                    .clickable {
                        onClick()
                    }
                    .padding(5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box (
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color.White),
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.cloud_sync),
                        contentDescription = null,
                        modifier = Modifier
                            .size(40.dp)
                            .padding(3.dp)
                    )
                }
                Text(
                    text = text,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.size(40.dp))
            }
        }
        val annotatedText = buildAnnotatedString {
            append("We commit not to collect or share any data about the content of your notes. See our ")

//            pushStringAnnotation(tag = "TERMS", annotation = "https://yourdomain.com/terms")
//            withStyle(
//                    style = SpanStyle(color = Primary,
//                    textDecoration = TextDecoration.Underline,
//                    fontWeight = FontWeight.Medium
//                )
//            ) {
//                append("Terms & Conditions")
//            }
//            pop()
//
//            append(" and ")

            pushStringAnnotation(tag = "PRIVACY", annotation = "https://www.edureminder.com/notepad/privacy-policy")
            withStyle(
                style = SpanStyle(color = Primary,
                    textDecoration = TextDecoration.Underline,
                    fontWeight = FontWeight.Medium
                )
            ) {
                append("Privacy Policy")
            }
            pop()

            append(".")
        }

        ClickableText(
            text = annotatedText,
            style = TextStyle(
                fontSize = 13.sp,
                color = Color.Black.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            ),
            onClick = { offset ->
                annotatedText.getStringAnnotations(tag = "TERMS", start = offset, end = offset)
                    .firstOrNull()?.let {
                        val intent = Intent(Intent.ACTION_VIEW, it.item.toUri())
                        context.startActivity(intent)
                    }
                annotatedText.getStringAnnotations(tag = "PRIVACY", start = offset, end = offset)
                    .firstOrNull()?.let {
                        val intent = Intent(Intent.ACTION_VIEW, it.item.toUri())
                        context.startActivity(intent)
                    }
            }
        )

    }
}

@Composable
fun IsSignedContent(
    modifier: Modifier = Modifier,
    pendingCount: Int,
    account: GoogleSignInAccount,
    onClick: () -> Unit,
    syncViewModel: SyncViewModel,
){
    val isBackingUp by syncViewModel.isBackingUp.collectAsState()
    val isRestoring by syncViewModel.isRestoring.collectAsState()
    val isEnable by syncViewModel.isAnyLoading.collectAsState()
    val lastBackupTime by syncViewModel.lastBackupTime.collectAsState()

    Column (
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Column (
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box (
                modifier = Modifier
                    .size(120.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Box (
                    modifier = Modifier
                        .fillMaxSize()
                        .border(
                            width = 4.dp,
                            color = if(0 < pendingCount) Color.LightGray else Primary ,
                            shape = CircleShape
                        )
                        .padding(5.dp)
                        .clip(CircleShape)
                        .padding(2.dp)
                        .clip(CircleShape)
                ) {
                    if(account.photoUrl != null){
                        ProfileImage(
                            image = "${account.photoUrl}"
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.cloud_sync),
                            contentDescription = null,
                            modifier = modifier
                                .scale(1.4f)
                                .fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
                Row (
                    modifier = Modifier
                        .offset(y = 4.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .padding(3.dp)
                        .clip(CircleShape)
                        .background(if(pendingCount > 0)  Color.LightGray.copy(0.5f) else Primary)
                        .padding(horizontal = 10.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterHorizontally)
                ) {
                    Text(
                        text = if(pendingCount > 0) "Not Sync" else "Synced",
                        fontSize = 14.sp,
                        color = if(pendingCount > 0) Color.Gray else Color.White
                    )
                    Spacer(
                        Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if(pendingCount > 0) Primary else Color.White)
                    )
                }
            }
            Spacer(Modifier.height(10.dp))
            Text(
                text = "${account.displayName}",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${account.email}",
                fontSize = 14.sp,
                color = Color.Black.copy(0.5f)
            )
        }
        Spacer(Modifier.height(100.dp))
        Column (
            modifier = Modifier
                .width(300.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if(lastBackupTime != null){
                Text(
                    text = "Last sync: $lastBackupTime",
                    fontSize = 12.sp,
                    color = Color.Black.copy(0.5f),
                    fontWeight = FontWeight.Medium
                )
            }
            RestoreButton(
                isEnable,
                syncViewModel,
                account,
                isRestoring
            )
            SyncButton(
                isEnable,
                isBackingUp,
                pendingCount,
                syncViewModel,
                account
            )

            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
                    .border(
                        width = 2.dp,
                        color = Primary,
                        shape = MaterialTheme.shapes.small
                    )
                    .clip(MaterialTheme.shapes.small)
                    .clickable {
                        onClick()
                    }
                    .padding(horizontal = 15.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = null,
                    tint = Primary
                )
                Text(
                    text = "Logout",
                    color = Primary,
                    fontWeight = FontWeight.Medium,
                    fontSize = 17.sp
                )
            }
        }
    }
}

@Composable
fun RestoreButton(
    isEnable: Boolean,
    syncViewModel: SyncViewModel,
    account: GoogleSignInAccount,
    isRestoring: Boolean
) {

    Box (
        modifier = Modifier
            .fillMaxWidth()
            .height(55.dp)
            .clip(MaterialTheme.shapes.small)
            .background(Primary)
            .clickable (
                enabled = !isEnable
            ) {
                syncViewModel.restoreFromDrive(account)
            }
    ) {
        Row (
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 15.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.CloudDownload,
                contentDescription = null,
                modifier = Modifier
                    .size(30.dp),
                tint = Color.White
            )
            Text(
                text = "Restore",
                color = Color.White,
                fontWeight = FontWeight.Medium,
                fontSize = 17.sp
            )
            Spacer(modifier = Modifier)
        }
        if(isRestoring){
            Box (
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White.copy(0.6f))
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(30.dp)
                        .align(Alignment.Center),
                    color = Primary,
                    strokeWidth = 7.dp
                )
            }
        }
    }
}

@Composable
fun SyncButton(
    isEnable: Boolean,
    isBackingUp: Boolean,
    pendingCount: Int,
    syncViewModel: SyncViewModel,
    account: GoogleSignInAccount
) {
    Box (
        modifier = Modifier
            .fillMaxWidth()
            .height(55.dp)
            .clip(MaterialTheme.shapes.small)
            .background(Primary)
            .clickable (
                enabled = !isEnable
            ) {
                syncViewModel.backupToDrive(account)
            }
    ) {
        Row (
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 15.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.CloudUpload,
                contentDescription = null,
                modifier = Modifier
                    .size(30.dp),
                tint = Color.White
            )
            Text(
                text = "Sync Now",
                color = Color.White,
                fontWeight = FontWeight.Medium,
                fontSize = 17.sp
            )
            if (pendingCount > 0) {
                Box(
                    modifier = Modifier
                        .height(25.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .padding(horizontal = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = pendingCount.toString(),
                        color = Primary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            } else {
                Spacer(Modifier.size(10.dp))
            }
        }
        if(isBackingUp){
            Box (
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White.copy(0.6f))
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(30.dp)
                        .align(Alignment.Center),
                    color = Primary,
                    strokeWidth = 7.dp
                )
            }
        }
    }
}

@Composable
fun ProfileImage(
    image: String,
    modifier: Modifier = Modifier
){
    val gradient = listOf(
        Color.LightGray.copy(alpha = 0.5f),
        Color.LightGray.copy(alpha = 0.2f),
        Color.LightGray.copy(alpha = 0.5f),
        Color.LightGray.copy(alpha = 0.5f),
        Color.LightGray.copy(alpha = 0.7f),
        Color.LightGray.copy(alpha = 0.5f),
        Color.LightGray.copy(alpha = 0.2f),
        Color.LightGray.copy(alpha = 0.5f),
        Color.LightGray.copy(alpha = 0.5f),
        Color.LightGray.copy(alpha = 0.7f),
    )

    val transition = rememberInfiniteTransition(label = "")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(durationMillis = 3200, easing = LinearEasing)),
        label = ""
    )

    var positionInRootTopBar by remember { mutableStateOf(Offset.Zero) }
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp
    val screenWidth = configuration.screenWidthDp

    val diff = screenWidth * translateAnim * 4

    val initialX = diff - (positionInRootTopBar.x) - (screenWidth * 2)
    val endX = (screenWidth * 2) + initialX

    val initialY = -positionInRootTopBar.y + diff
    val endY = screenHeight - positionInRootTopBar.y + diff
    val brush = Brush.linearGradient(
        colors = gradient,
        start = Offset(initialX, initialY),
        end = Offset(endX, endY),
    )


    SubcomposeAsyncImage(
        model = image,
        contentDescription = "banner image $image",
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White.copy(0.5f)),
    ) {
        val state = painter.state
        when (state) {
            is AsyncImagePainter.State.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(8.dp))
                        .background(brush)
                        .onGloballyPositioned {
                            positionInRootTopBar = it.positionInRoot()
                        }
                )
            }
            is AsyncImagePainter.State.Error -> {
                Box (
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.LightGray.copy(0.6f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column (
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Image(
                            imageVector = Icons.Default.BrokenImage,
                            contentDescription = "Not Found Image",
                            modifier = Modifier
                                .size(100.dp)
                        )
                        Text(
                            text = "Not Found Image",
                            color = Color.Black.copy(0.5f),
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            else -> {
                SubcomposeAsyncImageContent(
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier
                        .fillMaxSize(),
                    alignment = Alignment.TopStart
                )
            }
        }
    }
}

/**
 * Helper function to create Google Drive service.
 * This service object is what you'll use to make API calls (list, upload, download, etc.).
 */
suspend fun getDriveService(context: Context, account: GoogleSignInAccount): Drive? {
    return withContext(Dispatchers.IO) {
        try {
            // Get credentials from the signed-in Google account
            val credential = GoogleAccountCredential.usingOAuth2(
                context,
                listOf(
                    DriveScopes.DRIVE_FILE, // For app-specific files
                    // DriveScopes.DRIVE // For wider access
                )
            ).apply {
                selectedAccount = account.account
            }

            // Build the Drive service
            Drive.Builder(
                NetHttpTransport(), // HTTP transport
                GsonFactory(), // JSON factory
                credential // Our credentials
            )
                .setApplicationName("EduReminder Notepad") // Your app name
                .build()
        } catch (e: Exception) {
            Log.e("Log1", "Error creating Drive service, $e")
            null
        }
    }
}

/**
 * Helper function to upload a file to Google Drive.
 */
//suspend fun uploadFileToDrive(
//    driveService: Drive,
//    fileName: String,
//    fileContent: String,
//    mimeType: String
//): String? {
//    return withContext(Dispatchers.IO) {
//        try {
//            val fileMetadata = com.google.api.services.drive.model.File().apply {
//                name = fileName
//                parents = listOf("appDataFolder") // Store in the app-specific hidden folder
//                // For regular visible folder, you'd provide a folder ID or omit `parents` for root.
//                mimeType = mimeType
//            }
//
//            val mediaContent = com.google.api.client.http.ByteArrayContent.fromString(mimeType, fileContent)
//
//            val uploadedFile = driveService.files().create(fileMetadata, mediaContent)
//                .setFields("id")
//                .execute()
//
//            uploadedFile.id
//        } catch (e: Exception) {
//            Log.e("Log1", "Error uploading file to Drive")
//            null
//        }
//    }
//}
