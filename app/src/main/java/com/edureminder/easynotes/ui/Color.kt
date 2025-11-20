package com.edureminder.easynotes.ui

import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColorInt

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

val Primary = Color("#FB84A6".toColorInt())
var ColorBlack = Color(0xFF000000)
var ColorWhite = Color(0xFFFFFFFF)


val Container2 = Color("#F2E3D7".toColorInt())
val Container = Color("#F5EBE7".toColorInt())
val PALETTE_COLORS = listOf(
    Color(0xFFFBD9D4), // Original
    Color(0xFFFCE38A),
    Color(0xFFFBFFB1),
    Color(0xFFF08A5D),
    Color(0xFFEA5455),
    Color(0xFFFF2E63),
    Color(0xFF609966),
    Color(0xFF557153),
    Color(0xFF00ADB5),
    Color(0xFF3F72AF),
    Color(0xFF950101),
    Color(0xFF222831),
)

val linkColor = Color(0xFF90D5FF)




//added
//var ColorPrimary = Color("#FB5477".toColorInt())

//val Purple40 = Color(0xFF6650a4)
//val PurpleGrey40 = Color(0xFF625b71)
//val Pink40 = Color(0xFF7D5260)
//var ColorSky = Color(0xFF87CEEB)
//var ColorOrange = Color(0xFFFFA500)
//val ColorDeepGreen = Color(0xFF126567)
//val ColorSecondary = Color(0xFF66246D)
//val FalseColor = Color("#FC5C6E".toColorInt())
//var ColorPurple = Color("#944E63".toColorInt())
//var ColorBlack = Color(0xFF000000)
//var ColorWhite = Color(0xFFFFFFFF)
//var colorStopsGradient = arrayOf(
//    0.0f to ColorPrimary.copy(0.3f),
//    0.2f to ColorOrange.copy(0.5f),
//    1f to ColorPrimary.copy(0.3f)
//)
//var ColorGray = Color(0xFF888888)
//var ColorLightGray = Color(0xFFCCCCCC)
//var ContainerColor = colorConverter("#F2EFFF")


val BlueSky= Color(0xFF4478a9)
val NightSky =  Color(0xFF333333)
val BorderColor = Color(0x40000000)

data class Theme(
    val id: Int,
    val lightColor: Color,
    val darkColor: Color
)


//val themes = listOf(
//    // Themes
//    Theme(
//        id = 1,
//        lightColor = colorConverter("#1A4870"),
//        darkColor = colorConverter("#1A4870")
//
//    ),
//    Theme(
//        id = 2,
//        lightColor = colorConverter("#FB5477"),
//        darkColor = colorConverter("#FB5477")
//    ),
//    Theme(
//        id = 3,
//        lightColor = colorConverter("#617A55"),
//        darkColor = colorConverter("#617A55")
//    ),
//    Theme(
//        id = 4,
//        lightColor = colorConverter("#3EB8D4"),
//        darkColor = colorConverter("#176B87")
//    ),
//    Theme(
//        id = 5,
//        lightColor = colorConverter("#795757"),
//        darkColor = colorConverter("#795757")
//
//    ),
//    Theme(
//        id = 6,
//        lightColor = colorConverter("#004D40"),
//        darkColor = colorConverter("#004D40")
//    ),
//    Theme(
//        id = 7,
//        lightColor = colorConverter("#0091EA"),
//        darkColor = colorConverter("#0091EA")
//    )
//)

// Light color schema
fun lightColorScheme (isTheme: Int) {
//    ColorPrimary = themes.find { it.id == isTheme }!!.lightColor
//
//    ContainerColor = colorConverter("#F2EFFF")
//
//    ColorGray = Color(0xFF888888)
//    ColorLightGray = Color(0xFFCCCCCC)
//
//    ColorBlack = Color(0xFF000000)
//    ColorWhite = Color(0xFFFFFFFF)
//
//    colorStopsGradient = arrayOf(
//        0.0f to ColorPrimary.copy(0.2f),
//        0.2f to ContainerColor.copy(0.7f),
//        1f to ColorPrimary.copy(0.2f)
//    )
}
// 3A4D39 - 176B87
// Dark color schema
fun darkColorScheme (isTheme: Int) {
//    ColorPrimary = themes.find { it.id == isTheme }!!.darkColor
//
//    ContainerColor = colorConverter("#35374B")
//
//    ColorGray = Color(0xFFCCCCCC)
//    ColorLightGray = Color(0xFF888888)
//
//    ColorWhite = colorConverter("#2D3250")
//    ColorBlack = Color(0xFFFFFFFF)
//
//    colorStopsGradient =  arrayOf(
//        0.0f to ColorPrimary.copy(0.3f),
//        0.2f to ColorWhite.copy(0.5f),
//        1f to ColorPrimary.copy(0.3f)
//    )

}