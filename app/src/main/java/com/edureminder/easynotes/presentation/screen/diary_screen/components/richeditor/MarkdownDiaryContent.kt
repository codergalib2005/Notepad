package com.edureminder.easynotes.presentation.screen.diary_screen.components.richeditor


import android.annotation.SuppressLint
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.FormatAlignLeft
import androidx.compose.material.icons.automirrored.outlined.FormatAlignRight
import androidx.compose.material.icons.automirrored.outlined.FormatListBulleted
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.ImageSearch
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.outlined.FormatAlignCenter
import androidx.compose.material.icons.outlined.FormatBold
import androidx.compose.material.icons.outlined.FormatItalic
import androidx.compose.material.icons.outlined.FormatStrikethrough
import androidx.compose.material.icons.outlined.FormatUnderlined
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.core.graphics.toColorInt
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.edureminder.easynotes.R
import com.edureminder.easynotes.data.Theme
import com.edureminder.easynotes.lib.richeditor.RichTextStyleButton
import com.edureminder.easynotes.presentation.navigation.Screen
import com.edureminder.easynotes.presentation.screen.edit_note.NoteEditorViewModel
import com.edureminder.easynotes.presentation.screen.edit_note.components.TextEditorLinkDialog
import com.edureminder.easynotes.ui.Primary
import com.edureminder.easynotes.utils.copyUriToInternalStorage
import com.edureminder.easynotes.viewmodels.MainViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.mohamedrejeb.richeditor.annotation.ExperimentalRichTextApi
import com.mohamedrejeb.richeditor.model.RichTextState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

val textColors = listOf(
    Color("#ffffff".toColorInt()),
    Color("#cccccc".toColorInt()),
    Color("#a5a5a5".toColorInt()),
    Color("#737373".toColorInt()),
    Color("#404040".toColorInt()),
    Color("#000000".toColorInt()),
    Color("#7e4ff5".toColorInt()),
    Color("#d05cf7".toColorInt()),
    Color("#ea4753".toColorInt()),
    Color("#df7a55".toColorInt()),
    Color("#edcd44".toColorInt()),
    Color("#81e48a".toColorInt()),
    Color("#78d8e3".toColorInt()),
    Color("#78d8e3".toColorInt()),
    Color("#406ef5".toColorInt()),
    Color("#1c3681".toColorInt()),
)
val backgroundColors = listOf(
    Color("#ffffff".toColorInt()),
    Color("#cccccc".toColorInt()),
    Color("#a5a5a5".toColorInt()),
    Color("#737373".toColorInt()),
    Color("#404040".toColorInt()),
    Color("#000000".toColorInt()),
    Color("#7e4ff5".toColorInt()),
    Color("#d05cf7".toColorInt()),
    Color("#ea4753".toColorInt()),
    Color("#df7a55".toColorInt()),
    Color("#edcd44".toColorInt()),
    Color("#81e48a".toColorInt()),
    Color("#78d8e3".toColorInt()),
    Color("#489fed".toColorInt()),
    Color("#406ef5".toColorInt())
)
val fontSizes = listOf(
    10.sp,
    12.sp,
    14.sp,
    16.sp,
    18.sp,
    24.sp,
)

@SuppressLint("ImplicitSamInstance")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class,
    ExperimentalRichTextApi::class,
)
@Composable
fun MarkdownDiaryContent(
    richTextState: RichTextState,
    focusRequester: FocusRequester,
    theme: Theme,
    editorViewModel: NoteEditorViewModel,
    backgroundColor: Color,
    navController: NavController,
    mainViewModel: MainViewModel,
) {
    LaunchedEffect(Unit) {
        richTextState.config.linkColor = Color(0xFF1d9bd1)
        richTextState.config.linkTextDecoration = TextDecoration.None
        richTextState.config.codeSpanColor = Color(0xFFd7882d)
        richTextState.config.codeSpanBackgroundColor = Color.Transparent
        richTextState.config.codeSpanStrokeColor = Color(0xFF494b4d)
        richTextState.config.unorderedListIndent = 40
        richTextState.config.orderedListIndent = 50
        richTextState.toggleSpanStyle(
            SpanStyle(
                fontSize = 17.sp
            )
        )
    }
    val isPro by mainViewModel.isPro.collectAsState()

    Box(
        modifier = Modifier
    ) {

        RichTextToMarkdown(
            richTextState = richTextState,
            modifier = Modifier
                .fillMaxWidth(),
            focusRequester,
            theme,
            editorViewModel,
            backgroundColor,
            navController,
            mainViewModel

        )
        // 1️⃣ Overlay FIRST — it goes UNDER
        if (!editorViewModel.isEditable) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .pointerInput(Unit) {
                        // DO NOTHING but detect clicks WITHOUT blocking
                        awaitPointerEventScope {
                            while (true) {
                                val event = awaitPointerEvent()
                                if (event.changes.any { it.pressed }) {
                                    editorViewModel.isEditable = true
                                }
                            }
                        }
                    }
            )
        }

    }
}
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun ThumbnailImage(
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    filePath: String,
    onClick: () -> Unit
) {
    val context = LocalContext.current

    val file = File(context.filesDir, "images/$filePath")

    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(context)
            .data(file)
            .size(200, 200)
            .build()
    )

    Box(
        modifier = Modifier
            .size(100.dp)
            .shadow(
                elevation = 4.dp,
                shape = MaterialTheme.shapes.small
            )
            .clip(MaterialTheme.shapes.small)
            .background(Color.LightGray, RoundedCornerShape(4.dp))
            .clickable {
                onClick()
            }
    ) {
        with(sharedTransitionScope) {
            Image(
                painter = painter,
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .sharedElement(
                        sharedTransitionScope.rememberSharedContentState(key = filePath),
                        animatedVisibilityScope = animatedContentScope
                    )
                    .matchParentSize()
            )
        }

        when (painter.state) {
            is AsyncImagePainter.State.Loading -> {
                Icon(
                    imageVector = Icons.Default.ImageSearch,
                    contentDescription = null,
                    modifier = Modifier
                        .size(60.dp)
                        .align(Alignment.Center),
                    tint = Color.Gray
                )
            }
            is AsyncImagePainter.State.Error -> {
                Icon(
                    imageVector = Icons.Default.BrokenImage,
                    contentDescription = null,
                    modifier = Modifier
                        .size(60.dp)
                        .align(Alignment.Center),
                    tint = Color.Gray
                )
            }
            else -> {}
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ImagePickerButton(
    onImageSelected: (Uri) -> Unit
) {
    val scope = rememberCoroutineScope()

    // 1️⃣ Permission for reading images
    val readPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        android.Manifest.permission.READ_MEDIA_IMAGES
    } else {
        android.Manifest.permission.READ_EXTERNAL_STORAGE
    }

    val permissionState = rememberPermissionState(permission = readPermission)

    // 2️⃣ Launcher to pick image
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { onImageSelected(it) }
    }

    Box(
        modifier = Modifier
            .size(40.dp)
            .zIndex(1f)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                scope.launch {
                    // Request permission if not granted
                    if (!permissionState.status.isGranted) {
                        permissionState.launchPermissionRequest()
                    } else {
                        launcher.launch("image/*") // Open gallery
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Image,
            contentDescription = null,
            tint = Color.Black,
            modifier = Modifier
                .size(23.dp)
        )
    }
}

@Composable
fun TextStyleSheet(
    richTextState: RichTextState,
    editorViewModel: NoteEditorViewModel,
    fontSizes: List<TextUnit>
){
    androidx.compose.animation.AnimatedVisibility( // explicitly call with full name if scope issues
        visible = editorViewModel.isTextFontSizePickerOpen,
        modifier = Modifier
            .zIndex(2f)
            .fillMaxWidth()
            .background(Color.White)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ){
                editorViewModel.isTextFontSizePickerOpen = false
            }
    ) {
        Column (
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row (
                modifier = Modifier
                    .height(50.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 5.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                fontSizes.forEach { fontSize ->
                    Box (
                        modifier = Modifier
                            .padding(vertical = 6.dp)
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(MaterialTheme.shapes.extraSmall)
                            .background(
                                if(richTextState.currentSpanStyle.fontSize == fontSize) Primary.copy(0.2f) else Color.Transparent
                            )
                            .clickable {
                                if (richTextState.currentSpanStyle.fontSize == fontSize) {
                                    richTextState.removeSpanStyle(
                                        SpanStyle(
                                            fontSize = richTextState.currentSpanStyle.fontSize
                                        )
                                    )
                                } else {
                                    // Otherwise, apply the selected font size
                                    richTextState.toggleSpanStyle(
                                        SpanStyle(fontSize = fontSize)
                                    )
                                    editorViewModel.isTextFontSizePickerOpen = false
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = fontSize.value.toInt().toString(),
                            fontSize = fontSize,
                            fontWeight = FontWeight.Bold,
                            color = if(richTextState.currentSpanStyle.fontSize == fontSize) Primary else Color.Black.copy(0.6f)

                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            editorViewModel.isTextFontSizePickerOpen = false
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        modifier = Modifier
                            .size(20.dp)
                    )
                }
            }
            Row (
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                RichTextStyleButton(
                    onClick = {
                        richTextState.toggleSpanStyle(
                            SpanStyle(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    },
                    isSelected = richTextState.currentSpanStyle.fontWeight == FontWeight.Bold,
                    icon = Icons.Outlined.FormatBold
                )
                RichTextStyleButton(
                    onClick = {
                        richTextState.toggleSpanStyle(
                            SpanStyle(
                                fontStyle = FontStyle.Italic
                            )
                        )
                    },
                    isSelected = richTextState.currentSpanStyle.fontStyle == FontStyle.Italic,
                    icon = Icons.Outlined.FormatItalic
                )
                RichTextStyleButton(
                    onClick = {
                        richTextState.toggleSpanStyle(
                            SpanStyle(
                                textDecoration = TextDecoration.Underline
                            )
                        )
                    },
                    isSelected = richTextState.currentSpanStyle.textDecoration?.contains(
                        TextDecoration.Underline
                    ) == true,
                    icon = Icons.Outlined.FormatUnderlined
                )
                RichTextStyleButton(
                    onClick = {
                        richTextState.toggleSpanStyle(
                            SpanStyle(
                                textDecoration = TextDecoration.LineThrough
                            )
                        )
                    },
                    isSelected = richTextState.currentSpanStyle.textDecoration?.contains(
                        TextDecoration.LineThrough
                    ) == true,
                    icon = Icons.Outlined.FormatStrikethrough
                )


                RichTextStyleButton(
                    onClick = {
                        richTextState.addParagraphStyle(
                            ParagraphStyle(
                                textAlign = TextAlign.Left,
                            )
                        )
                    },
                    isSelected = richTextState.currentParagraphStyle.textAlign == TextAlign.Left,
                    icon = Icons.AutoMirrored.Outlined.FormatAlignLeft
                )
                RichTextStyleButton(
                    onClick = {
                        richTextState.addParagraphStyle(
                            ParagraphStyle(
                                textAlign = TextAlign.Center
                            )
                        )
                    },
                    isSelected = richTextState.currentParagraphStyle.textAlign == TextAlign.Center,
                    icon = Icons.Outlined.FormatAlignCenter
                )
                RichTextStyleButton(
                    onClick = {
                        richTextState.addParagraphStyle(
                            ParagraphStyle(
                                textAlign = TextAlign.Right
                            )
                        )
                    },
                    isSelected = richTextState.currentParagraphStyle.textAlign == TextAlign.Right,
                    icon = Icons.AutoMirrored.Outlined.FormatAlignRight
                )
            }

            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                verticalAlignment = Alignment.CenterVertically
            ) {
                textColors.forEach{ colorItem ->
                    Box (
                        modifier = Modifier
                            .size(32.dp)
                            .border(
                                width = 2.dp,
                                color = if(richTextState.currentSpanStyle.color == colorItem) Primary else Color.LightGray,
                                shape = CircleShape,
                            )
                            .background(Color.White)
                            .padding(6.dp)
                            .clip(CircleShape)
                            .background(colorItem)
                            .clickable(){
                                richTextState.toggleSpanStyle(
                                    SpanStyle(
                                        color = colorItem
                                    )
                                )
                                editorViewModel.isTextColorPickerOpen = false
                            },
                        contentAlignment = Alignment.Center
                    ) {

                    }
                }
            }
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box (
                    modifier = Modifier
                        .padding(end = 2.dp)
                        .size(32.dp)
                        .background(Color.White)
                        .drawBehind {
                            val strokeWidthPx = 2f

                            // X position for the right-side line
                            val x = size.width - strokeWidthPx / 2

                            drawLine(
                                color = Primary,
                                start = Offset(x, 0f),              // Top of the right edge
                                end = Offset(x, size.height),       // Bottom of the right edge
                                strokeWidth = strokeWidthPx,
                            )
                        }
                        .clickable(){
                            richTextState.removeSpanStyle(SpanStyle(background = richTextState.currentSpanStyle.background))
                            editorViewModel.isTextBackgroundColorPickerOpen = false

                        }
                        .padding(end = 2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Spacer(
                        Modifier
                            .rotate(-50f)
                            .height(2.dp)
                            .fillMaxWidth()
                            .background(Color.LightGray)
                            .scale(scaleY = 0f, scaleX = 1.2f)
                    )
                    Spacer(
                        Modifier
                            .rotate(50f)
                            .height(2.dp)
                            .fillMaxWidth()
                            .background(Color.LightGray)
                            .scale(scaleY = 0f, scaleX = 1.2f)
                    )
                    if(richTextState.currentSpanStyle.background == Color.Unspecified){
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                        )
                    }
                }
                backgroundColors.forEach { colorItem ->
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(colorItem)
                            .clickable() {
                                richTextState.toggleSpanStyle(
                                    SpanStyle(
                                        background = colorItem
                                    )
                                )
                                editorViewModel.isTextBackgroundColorPickerOpen =
                                    false
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if(richTextState.currentSpanStyle.background == colorItem){
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
const val Dot = "•"
const val RightArrow = "➤"
const val Check = "✓"
const val Diamond = "🔹"
const val Pin = "📍"
const val Fire = "🔥"
const val Watermelon = "🍉"
const val Star = "⭐"
const val Square = "⬛"
const val RightFinger = "👉"
const val Apple = "🍎"
const val Love = "❤️"
const val Lychees = "🍒"
const val Lyche = "🍓"

 */