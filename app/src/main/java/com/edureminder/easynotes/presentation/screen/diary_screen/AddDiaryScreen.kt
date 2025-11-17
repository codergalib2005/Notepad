package com.edureminder.easynotes.presentation.screen.diary_screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.dialog.DialogDefaults
import com.edureminder.easynotes.R
import java.io.File

data class Mode(
    val id: Int,
    val icon: Int,
)

@Composable
fun AddDiaryScreen(navController: NavHostController, onPDFGenerate: File) {
    val moods = listOf(
        Mode(1, R.drawable.mood_1),
        Mode(2, R.drawable.mood_2),
        Mode(3, R.drawable.mood_3),
        Mode(4, R.drawable.mood_4),
        Mode(5, R.drawable.mood_5),
    )
    var showMoodDialog by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }


    Scaffold { innerPadding ->
        Column (
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Column (
                modifier = Modifier
                    .fillMaxWidth()
                    .drawWithContent {
                        drawContent()
                        clipRect { // Not needed if you do not care about painting half stroke outside
                            val strokeWidth = Stroke.DefaultMiter
                            val y = size.height // - strokeWidth
                            // if the whole line should be inside component
                            drawLine(
                                brush = SolidColor(Color.LightGray),
                                strokeWidth = strokeWidth,
                                cap = StrokeCap.Square,
                                start = Offset.Zero.copy(y = y),
                                end = Offset(x = size.width, y = y)
                            )
                        }
                    }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
                    IconButton(
                        onClick = {}
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.back_icon),
                            contentDescription = null,
                            modifier = Modifier
                                .size(24.dp)
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ){
                        IconButton(
                            onClick = {}
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreHoriz,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(24.dp)
                            )
                        }
                        IconButton(
                            onClick = {}
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.edit_icon_dark),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(24.dp)
                            )
                        }
                    }
                }
            }
            Column {
                Column {
                    IconButton(
                        onClick = {

                        },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color.LightGray.copy(0.5f)
                        ),
                        shape = MaterialTheme.shapes.small,
                        modifier = Modifier
                            .padding(10.dp)
                            .size(45.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.logging_icon),
                            contentDescription = null,
                            modifier = Modifier
                                .size(30.dp),
                            tint = Color.Gray
                        )
                    }
                }
                BasicTextField(
                    value = title,
                    onValueChange = {
                        if(it.length <= 65) {
                            title = it
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp),
                    decorationBox = { innerTextField ->
                        if(title.isEmpty()) {
                            Text(
                                text = "Title",
                                color = Color.Gray.copy(0.8f)
                            )
                        } else {
                            innerTextField()
                        }
                    },
                    textStyle = TextStyle(
                        color = Color.Black.copy(0.8f),
                        fontSize = 25.sp
                    )
                )
                var activeFormats by rememberSaveable { mutableStateOf(setOf<FormattingAction>()) }
                var formattingSpans by remember { mutableStateOf(listOf<FormattingSpan>()) }
                var annotatedText by remember { mutableStateOf(AnnotatedString("")) }

                EditorToolbar(
                    activeFormats = activeFormats,
                    onFormatToggle = { format ->
                        activeFormats = if (activeFormats.contains(format)) {
                            activeFormats - format
                        } else {
                            // Only one heading type should be active
                            if (format == FormattingAction.Heading || format == FormattingAction.SubHeading) {
                                activeFormats - FormattingAction.Heading - FormattingAction.SubHeading + format
                            } else {
                                activeFormats + format
                            }
                        }
                    }
                )

                ComposeTextEditor(
                    annotatedString = annotatedText,         // ⬅ PASS IT HERE
                    activeFormats = activeFormats,
                    onAnnotatedStringChange = { updated ->
                        annotatedText = updated              // ⬅ UPDATE MAIN TEXT
                    },
                    onFormattingSpansChange = { updatedSpans ->
                        formattingSpans = updatedSpans       // ⬅ UPDATE SPANS
                    },
                    modifier = Modifier.padding(10.dp)
                )

            }
        }


        if(showMoodDialog){
            Dialog(
                onDismissRequest = {

                },

                ) {
                Column (
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.medium)
                        .background(Color.White)
                        .padding(15.dp),
                    verticalArrangement = Arrangement.spacedBy(15.dp)
                ) {
                    Text(
                        text = "How are you?",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.Black
                    )
                    Row (
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        moods.forEach { mood ->
                            Image(
                                painter = painterResource(id = mood.icon),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(50.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
