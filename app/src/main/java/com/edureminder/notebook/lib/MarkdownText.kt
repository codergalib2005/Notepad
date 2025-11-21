package com.edureminder.notebook.lib

import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.Markwon
import io.noties.markwon.MarkwonSpansFactory
import org.commonmark.node.Link
import kotlin.math.roundToInt


@Composable
fun MarkdownText(
    markdown: String,
    textColor: Color = Color.Black,
    linkColor: Color = Color.Red, // Add link color parameter
    fontSize: Float = 16f, // Font size in SP
    lineSpacing: Dp = 4.dp // Extra spacing in DP
) {
    val context = LocalContext.current

    // Convert Dp to pixels within a @Composable context
    val lineSpacingPx = with(LocalDensity.current) { lineSpacing.toPx() }

    AndroidView(
        factory = { TextView(context) },
        update = { textView ->
            // Apply text appearance to the TextView
            textView.setTextColor(textColor.toArgb())
            textView.textSize = fontSize

            // Set line spacing
            textView.setLineSpacing(lineSpacingPx, 1.0f)

            // Create Markwon instance with custom link plugin
            val markwon = Markwon.builder(context)
                .usePlugin(LinkColorPlugin(linkColor)) // Custom plugin for link color
                .build()

            markwon.setMarkdown(textView, markdown)
        }
    )
}

// Custom Markwon Plugin to change link color
class LinkColorPlugin(private val linkColor: Color) : AbstractMarkwonPlugin() {

    override fun configureSpansFactory(builder: MarkwonSpansFactory.Builder) {
        builder.setFactory(Link::class.java) { _, _ ->
            object : ClickableSpan() {
                override fun onClick(widget: View) {
                    // Handle link click (optional)
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.color = linkColor.toArgb() // Apply custom link color
                    ds.isUnderlineText = true // Keep the underline effect for links
                }
            }
        }
    }
}



fun Color.toArgb(): Int {
    return android.graphics.Color.argb(
        (alpha * 255).roundToInt(), // Use roundToInt for better precision
        (red * 255).roundToInt(),
        (green * 255).roundToInt(),
        (blue * 255).roundToInt()
    )
}



@Composable
fun Dp.toPx(): Float {
    val density = LocalDensity.current
    return with(density) { this@toPx.toPx() }
}