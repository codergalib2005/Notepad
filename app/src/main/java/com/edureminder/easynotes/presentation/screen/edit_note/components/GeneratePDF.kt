package com.edureminder.easynotes.presentation.screen.edit_note.components


import android.content.Context
import android.print.PrintAttributes
import android.print.PrintManager
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.MutableState
import com.edureminder.easynotes.presentation.screen.edit_note.ChecklistItem
import java.io.File

fun generatePDF(context: Context, directory: File, toHtml: String, titleText: String) {
    val webView = WebView(context)
    webView.webViewClient = object : WebViewClient() {
        override fun onPageFinished(view: WebView?, url: String?) {
            createWebPrintJob(view, context, directory, titleText)
        }
    }

    val htmlContent = """
            <html>
            <head>
                <style>
                    body { font-family: sans-serif; margin: 20px; text-align: center; }
                    .header { font-size: 26px; font-weight: bold; margin-bottom: 5px; color: #333; }
                    .subheader { font-size: 18px; font-weight: normal; margin-bottom: 10px; color: #666; }
                    .logo { 
                        width: 80px;
                        height: 80px;
                        margin-bottom: 10px;
                        border-radius: 12px;
                    }
                    .border { border-bottom: 3px solid #000; margin: 10px auto; width: 80%; }
                    .content { font-size: 14px; color: #333; text-align: left; padding: 10px; }
                    a { color: #007BFF; text-decoration: none; }
                     .footer {
                        display: flex;
                        justify-content: space-between;
                        align-items: center;
                        margin-top: 30px;
                        padding: 20px;
                        background: #f8f8f8;
                        border-top: 2px solid #ddd;
                        border-radius: 10px;
                        flex-wrap: wrap; /* Makes it responsive if screen is small */
                    }
                    
                    .footer-content {
                        text-align: left;
                        max-width: 70%;
                    }
                    
                    .footer-text {
                        font-size: 16px;
                        font-weight: bold;
                        margin-bottom: 5px;
                    }
                    
                    .footer-subtext {
                        font-size: 14px;
                        color: #666;
                        margin-bottom: 10px;
                    }
                    
                    .download-button {
                        display: inline-block;
                        text-decoration: none;
                        background-color: #4CAF50;
                        color: white;
                        padding: 8px 16px;
                        border-radius: 6px;
                        font-size: 14px;
                        transition: background-color 0.3s;
                    }
                    
                    .download-button:hover {
                        background-color: #45a049;
                    }
                    
                    .footer-qr {
                        text-align: right;
                    }
                    
                    .qr-code-image {
                        width: 100px;
                        height: 100px;
                        margin-bottom: 10px;
                    }
                    .main-content {
                        padding: 10px 10px; /* Adds top and bottom padding */
                        text-align: left;
                    }

                    .content-title {
                        font-size: 24px;
                        font-weight: bold;
                        margin-bottom: 15px; /* Space below the title */
                        color: #333;
                    }

                    .content-body {
                        font-size: 15px;
                        color: #555;
                        line-height: 1.4;
                    }
                </style>
            </head>
            <body>
                <a href="https://play.google.com/store/apps/details?id=com.edureminder.notepad">
                   <img src="file:///android_res/drawable/logo.png" class="logo" alt="Edu Reminder Logo"/>
                </a>
                <div class="header">Notepad - Easy Notes, Reminder</div>
                <div class="subheader">Powered by Edu Reminder</div>
                <div class="border"></div>
                <div class="main-content">
                    <h2 class="content-title">${titleText}</h2>
                    <div class="content-body">
                          $toHtml
                    </div>
                </div>
                <div class="footer">
                    <div class="footer-content">
                        <p class="footer-text">Thank you for using <strong>Notepad - Easy Notes, Reminder</strong>!</p>
                        <p class="footer-subtext">Stay organized, stay productive.</p>
                        <a class="download-button" href="https://play.google.com/store/apps/details?id=com.edureminder.notepad">📥 Download the App</a>
                    </div>
                    <div class="footer-qr">
                        <a href="https://play.google.com/store/apps/details?id=com.edureminder.notepad">
                            <img src="file:///android_res/drawable/qr_code.jpeg" class="qr-code-image" alt="Edu Reminder QR Code"/>
                        </a>
                    </div>
                </div>

            </body>
            </html>
        """.trimIndent()
    webView.loadDataWithBaseURL(null, htmlContent, "text/HTML", "UTF-8", null)
}

private fun createWebPrintJob(webView: WebView?, context: Context, directory: File, title: String) {
    val printManager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager
    val shortTitle = if (title.length > 20) title.substring(0, 20) else title


    val printAdapter = webView?.createPrintDocumentAdapter("Notepad - $shortTitle")
    // Get at most 20 characters from title (or the entire title if it's shorter)
    val jobName = "Notepad - $shortTitle.pdf"
    if (printAdapter != null) {
        printManager.print(jobName, printAdapter, PrintAttributes.Builder().build())
    }
}


fun generateChecklistPDF(context: Context, directory: File, checklistItems: List<ChecklistItem>, titleText: String) {
    val webView = WebView(context)
    webView.webViewClient = object : WebViewClient() {
        override fun onPageFinished(view: WebView?, url: String?) {
            createWebPrintJob(view, context, directory, titleText)
        }
    }

    val itemsHtml = buildString {
        append("<ol style=\"padding-left: 20px;\">") // Add some left padding for nicer look
        checklistItems.forEach { item ->
            // Generate a random pastel color with transparency
            val red = (100..255).random()
            val green = (100..255).random()
            val blue = (100..255).random()
            val backgroundColor = "rgba($red,$green,$blue,0.15)" // very transparent

            val statusIcon = if (item.isCompleted) "✅" else "❌"
            append("""
            <li style="
                background-color: $backgroundColor;
                padding: 12px;
                margin-bottom: 10px;
                border-radius: 8px;
                list-style-position: inside;
            ">
                <span style="font-size: 14px; font-weight: bold;">$statusIcon</span>
                <span style="margin-left: 8px; font-size: 16px;">${item.title}</span>
            </li>
        """.trimIndent())
        }
        append("</ol>")
    }


    val htmlContent = """
            <html>
            <head>
                <style>
                    body { font-family: sans-serif; margin: 20px; text-align: center; }
                    .header { font-size: 26px; font-weight: bold; margin-bottom: 5px; color: #333; }
                    .subheader { font-size: 18px; font-weight: normal; margin-bottom: 10px; color: #666; }
                    .logo { 
                        width: 80px;
                        height: 80px;
                        margin-bottom: 10px;
                        border-radius: 12px;
                    }
                    .border { border-bottom: 3px solid #000; margin: 10px auto; width: 80%; }
                    .content { font-size: 14px; color: #333; text-align: left; padding: 10px; }
                    a { color: #007BFF; text-decoration: none; }
                    .footer {
                        display: flex;
                        justify-content: space-between;
                        align-items: center;
                        margin-top: 30px;
                        padding: 20px;
                        background: #f8f8f8;
                        border-top: 2px solid #ddd;
                        border-radius: 10px;
                        flex-wrap: wrap; /* Makes it responsive if screen is small */
                    }
                    
                    .footer-content {
                        text-align: left;
                        max-width: 70%;
                    }
                    
                    .footer-text {
                        font-size: 16px;
                        font-weight: bold;
                        margin-bottom: 5px;
                    }
                    
                    .footer-subtext {
                        font-size: 14px;
                        color: #666;
                        margin-bottom: 10px;
                    }
                    
                    .download-button {
                        display: inline-block;
                        text-decoration: none;
                        background-color: #4CAF50;
                        color: white;
                        padding: 8px 16px;
                        border-radius: 6px;
                        font-size: 14px;
                        transition: background-color 0.3s;
                    }
                    
                    .download-button:hover {
                        background-color: #45a049;
                    }
                    
                    .footer-qr {
                        text-align: right;
                    }
                    .qr-code-image {
                        width: 100px;
                        height: 100px;
                        margin-bottom: 10px;
                    }
                    .main-content {
                        padding: 10px 10px; /* Adds top and bottom padding */
                        text-align: left;
                    }

                    .content-title {
                        font-size: 24px;
                        font-weight: bold;
                        margin-bottom: 15px; /* Space below the title */
                        color: #333;
                    }

                    .content-body {
                        font-size: 15px;
                        color: #555;
                        line-height: 1.4;
                    }
                </style>
            </head>
            <body>
                <a href="https://play.google.com/store/apps/details?id=com.edureminder.notepad">
                   <img src="file:///android_res/drawable/logo.png" class="logo" alt="Edu Reminder Logo"/>
                </a>
                <div class="header">Notepad - Easy Notes, Reminder</div>
                <div class="subheader">Powered by Edu Reminder</div>
                <div class="border"></div>
                <div class="main-content">
                    <h2 class="content-title">${titleText}</h2>
                    <div class="content-body">
                          $itemsHtml
                    </div>
                </div>
                <div class="footer">
                    <div class="footer-content">
                        <p class="footer-text">Thank you for using <strong>Notepad - Easy Notes, Reminder</strong>!</p>
                        <p class="footer-subtext">Stay organized, stay productive.</p>
                        <a class="download-button" href="https://play.google.com/store/apps/details?id=com.edureminder.notepad">📥 Download the App</a>
                    </div>
                    <div class="footer-qr">
                        <a href="https://play.google.com/store/apps/details?id=com.edureminder.notepad">
                            <img src="file:///android_res/drawable/qr_code.jpeg" class="qr-code-image" alt="Edu Reminder QR Code"/>
                        </a>
                    </div>
                </div>

            </body>
            </html>
        """.trimIndent()

    webView.loadDataWithBaseURL(null, htmlContent, "text/HTML", "UTF-8", null)
}