package com.edureminder.easynotes.lib.richeditor.markdowneditor


import android.annotation.SuppressLint
import android.util.Log
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
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.FormatAlignLeft
import androidx.compose.material.icons.automirrored.outlined.FormatAlignRight
import androidx.compose.material.icons.automirrored.outlined.FormatListBulleted
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.FormatAlignCenter
import androidx.compose.material.icons.outlined.FormatBold
import androidx.compose.material.icons.outlined.FormatItalic
import androidx.compose.material.icons.outlined.FormatListNumbered
import androidx.compose.material.icons.outlined.FormatStrikethrough
import androidx.compose.material.icons.outlined.FormatUnderlined
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.Spellcheck
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.core.graphics.toColorInt
import androidx.navigation.NavController
import com.edureminder.easynotes.R
import com.edureminder.easynotes.data.Theme
import com.edureminder.easynotes.lib.richeditor.RichTextStyleButton
import com.edureminder.easynotes.lib.richeditor.SpellCheck
import com.edureminder.easynotes.presentation.navigation.Screen
import com.edureminder.easynotes.presentation.screen.edit_note.NoteEditorViewModel
import com.edureminder.easynotes.presentation.screen.edit_note.components.TextEditorLinkDialog
import com.edureminder.easynotes.ui.Primary
import com.edureminder.easynotes.viewmodels.MainViewModel
import com.mohamedrejeb.richeditor.annotation.ExperimentalRichTextApi
import com.mohamedrejeb.richeditor.model.RichTextState
import okio.utf8Size

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
    ExperimentalRichTextApi::class
)
@Composable
fun MarkdownEditorContent(
    richTextState: RichTextState,
    focusRequester: FocusRequester,
    theme: Theme,
    editorViewModel: NoteEditorViewModel,
    backgroundColor: Color,
    navController: NavController,
    mainViewModel: MainViewModel
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


    val openLinkDialog = remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    var isAtEnd by remember { mutableStateOf(false) }
    // Watch scroll position
    LaunchedEffect(scrollState.value, scrollState.maxValue) {
        isAtEnd = scrollState.value >= scrollState.maxValue
    }

    val isPro by mainViewModel.isPro.collectAsState()
    val plainText = richTextState.toText().toString()

    val baseLimit = /**if (isPro) 10 else 15 */ if (isPro) 50000 else 5000
    val maxAllowedLength = baseLimit + 500  // Let them finish naturally

    val isMaxLengthReached by remember(plainText, isPro) {
        derivedStateOf { plainText.length >= baseLimit }
    }

    LaunchedEffect(plainText, isPro) {
        if (plainText.length > maxAllowedLength) {
            // Just cut off extra characters
            val truncated = plainText.take(maxAllowedLength)
            richTextState.setText(truncated)
        }
    }


    Column(
        modifier = Modifier
            .imePadding()
            .navigationBarsPadding()
    ) {
        Box (
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
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
            if(!editorViewModel.isEditable){
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight().combinedClickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onDoubleClick = {
                                editorViewModel.isEditable = true
                            },
                            onClick = {

                            }
                        )
                )
            }
        }
        Column (
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Box (
                contentAlignment = Alignment.BottomCenter,
            ) {
                // Top layer: Color picker
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
                }
                // Top layer: Color picker
                androidx.compose.animation.AnimatedVisibility( // explicitly call with full name if scope issues
                    visible = editorViewModel.isTextColorPickerOpen,
                    modifier = Modifier
                        .zIndex(2f)
                        .fillMaxWidth()
                        .background(Color.White)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ){
                            editorViewModel.isTextColorPickerOpen = false
                        }
                ) {
                    Column (
                        modifier = Modifier
                            .height(140.dp)
                    ) {
                        Row (
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Spacer(Modifier.size(25.dp))
                            Text(
                                text = "Text Color",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black.copy(0.7f)
                            )
                            Box (
                                modifier = Modifier
                                    .size(40.dp)
                                    .clickable(
                                        indication = null,
                                        interactionSource = remember { MutableInteractionSource() }
                                    ){
                                        editorViewModel.isTextColorPickerOpen = false
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
                        FlowRow (
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(7.dp, Alignment.CenterHorizontally),
                            verticalArrangement = Arrangement.spacedBy(7.dp)
                        ) {
                            textColors.forEach { colorItem ->
                                Box {
                                    Box (
                                        modifier = Modifier
                                            .size(32.dp)
                                            .border(
                                                width = 2.dp,
                                                color = if(richTextState.currentSpanStyle.color == colorItem) Primary else Primary.copy(0.2f),
                                                shape = CircleShape,
                                            )
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
                        }
                    }
                }
                // Top layer: Text Background picker
                androidx.compose.animation.AnimatedVisibility( // explicitly call with full name if scope issues
                    visible = editorViewModel.isTextBackgroundColorPickerOpen,
                    modifier = Modifier
                        .zIndex(2f)
                        .fillMaxWidth()
                        .background(Color.White)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ){
                            editorViewModel.isTextBackgroundColorPickerOpen = false
                        }
                ) {
                    Column (
                        modifier = Modifier
                            .height(140.dp)
                    ) {
                        Row (
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Spacer(Modifier.size(25.dp))
                            Text(
                                text = "Background Text Color",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black.copy(0.7f)
                            )
                            Box (
                                modifier = Modifier
                                    .size(40.dp)
                                    .clickable(
                                        indication = null,
                                        interactionSource = remember { MutableInteractionSource() }
                                    ){
                                        editorViewModel.isTextBackgroundColorPickerOpen = false
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

                        FlowRow (
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(7.dp, Alignment.CenterHorizontally),
                            verticalArrangement = Arrangement.spacedBy(7.dp)
                        ) {
                            Box {
                                Box (
                                    modifier = Modifier
                                        .border(
                                            width = 2.dp,
                                            color = if(richTextState.currentSpanStyle.background == Color.Unspecified) Primary else Primary.copy(0.2f),
                                            shape = CircleShape,
                                        )
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(Color.White)
                                        .clickable(){
                                            richTextState.removeSpanStyle(SpanStyle(background = richTextState.currentSpanStyle.background))
                                            editorViewModel.isTextBackgroundColorPickerOpen = false

                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Spacer(
                                        Modifier
                                            .rotate(-30f)
                                            .height(2.dp)
                                            .fillMaxWidth()
                                            .background(Color.LightGray)
                                            .scale(scaleY = 0f, scaleX = 1.2f)
                                    )

                                }
                            }
                            backgroundColors.forEach { colorItem ->
                                Box {
                                    Box (
                                        modifier = Modifier
                                            .size(32.dp)
                                            .border(
                                                width = 2.dp,
                                                color = if(richTextState.currentSpanStyle.background == colorItem) Primary else Primary.copy(0.2f),
                                                shape = CircleShape,
                                            )
                                            .padding(2.dp)
                                            .clip(CircleShape)
                                            .background(colorItem)
                                            .clickable(){
                                                richTextState.toggleSpanStyle(
                                                    SpanStyle(
                                                        background = colorItem
                                                    )
                                                )
                                                editorViewModel.isTextBackgroundColorPickerOpen = false
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                    }
                                }
                            }
                        }
                    }
                }
                androidx.compose.animation.AnimatedVisibility(
                    visible = openLinkDialog.value,
                    modifier = Modifier
                        .zIndex(2f)
                        .fillMaxWidth()
                        .background(Color.White)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ){
                            openLinkDialog.value = false
                        }
                ) {
                    TextEditorLinkDialog(
                        state = richTextState,
                        openLinkDialog = openLinkDialog
                    )
                }

                Column {
                    androidx.compose.animation.AnimatedVisibility(
                        visible = isMaxLengthReached,
                        modifier = Modifier
                            .zIndex(2f)
                            .fillMaxWidth()
                            .background(Color.White)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(40.dp)
                                    .padding(horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                if (!isPro) {
                                    Text(
                                        text = "Upgrade Pro for more space",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.Black
                                    )
                                    Box(
                                        modifier = Modifier
                                            .clip(CircleShape)
                                            .background(Color.Red)
                                            .clickable(
                                                interactionSource = remember { MutableInteractionSource() },
                                                indication = null
                                            ) {
                                                navController.navigate(Screen.Subscription){
                                                    popUpTo(Screen.MainScreen) {
                                                        inclusive = false
                                                    }
                                                }
                                            }
                                            .padding(horizontal = 18.dp, vertical = 7.dp),
                                    ) {
                                        Text(
                                            text = "Pro",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                        )
                                    }
                                } else {
                                    Text(
                                        text = "Max characters reached",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.Black
                                    )
                                }
                            }
                            HorizontalDivider(
                                color = Primary.copy(0.1f)
                            )
                        }
                    }

                    Row(
                        modifier = Modifier
                            .zIndex(1f)
                            .fillMaxWidth()
                            .background(Color.White)
                            .padding(start = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(35.dp)
                                .clip(MaterialTheme.shapes.extraSmall)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    editorViewModel.showThemeSheet = true
                                },
                            contentAlignment = Alignment.Center,
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.paint_palette),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(20.dp)
                            )
                        }
                        Box(
                            Modifier
                                .height(24.dp)
                                .width(1.dp)
                                .background(Color(0x48393B3D))
                        )
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .horizontalScroll(scrollState),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
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
                            Box(
                                Modifier
                                    .height(24.dp)
                                    .width(1.dp)
                                    .background(Color(0x48393B3D))
                            )
                            RichTextStyleButton(
                                onClick = {
                                    openLinkDialog.value = true
                                },
                                isSelected = richTextState.isLink,
                                icon = Icons.Outlined.Link
                            )
                            Box(
                                Modifier
                                    .height(24.dp)
                                    .width(1.dp)
                                    .background(Color(0x48393B3D))
                            )
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(MaterialTheme.shapes.extraSmall)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        editorViewModel.isTextFontSizePickerOpen =
                                            !editorViewModel.isTextFontSizePickerOpen
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.TextFields,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier
                                        .size(23.dp)
                                        .clip(MaterialTheme.shapes.extraSmall)
                                        .background(Color.White)
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(MaterialTheme.shapes.extraSmall)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        editorViewModel.isTextBackgroundColorPickerOpen =
                                            !editorViewModel.isTextBackgroundColorPickerOpen
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Circle,
                                    contentDescription = null,
                                    tint = Color.Red,
                                    modifier = Modifier
                                        .size(27.dp)
                                        .clip(MaterialTheme.shapes.extraSmall)
                                        .background(Color.White)
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(MaterialTheme.shapes.extraSmall)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        editorViewModel.isTextColorPickerOpen =
                                            !editorViewModel.isTextColorPickerOpen
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Circle,
                                    contentDescription = null,
                                    tint = Color.Red,
                                    modifier = Modifier
                                        .size(23.dp)
                                        .clip(MaterialTheme.shapes.extraSmall)
                                        .background(Color.White)
                                )
                            }
                            Box(
                                Modifier
                                    .height(24.dp)
                                    .width(1.dp)
                                    .background(Color(0x48393B3D))
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
                            Box(
                                Modifier
                                    .height(24.dp)
                                    .width(1.dp)
                                    .background(Color(0x48393B3D))
                            )
                            RichTextStyleButton(
                                onClick = {
                                    richTextState.toggleUnorderedList()
                                },
                                isSelected = richTextState.isUnorderedList,
                                icon = Icons.AutoMirrored.Outlined.FormatListBulleted,
                            )
                            RichTextStyleButton(
                                onClick = {
                                    richTextState.toggleOrderedList()
                                },
                                isSelected = richTextState.isOrderedList,
                                icon = Icons.Outlined.FormatListNumbered,
                            )
                            Box(
                                Modifier
                                    .height(24.dp)
                                    .width(1.dp)
                                    .background(Color(0x48393B3D))
                            )
                            RichTextStyleButton(
                                onClick = {
                                    richTextState.addRichSpan(SpellCheck)
                                },
                                isSelected = false,
                                icon = Icons.Outlined.Spellcheck,
                            )
                            RichTextStyleButton(
                                onClick = {
                                    richTextState.toggleCodeSpan()
                                },
                                isSelected = richTextState.isCodeSpan,
                                icon = Icons.Outlined.Code,
                            )
                        }
                    }
                }
            }
        }
    }
}