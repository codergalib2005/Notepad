package com.edureminder.notebook.data


import com.edureminder.notebook.R


data class Theme(
    val id: Int,
    val type: String,
    val value: Int,
    val cat: String,
    val headerColor: String,
    val showTopLine: Boolean,
    val backgroundColor: String,
    val lineColor: String,
    val textColor: String
)

object ThemeManager {
    val themes: List<Theme> = listOf(
        Theme(
            id = 1,
            type = "none",
            value = 1,
            cat = "solid",
            headerColor = "#1A4870",
            showTopLine = true,
            backgroundColor = "#1A4870B2",
            lineColor = "#1A1A1A",
            textColor = "#1A1A1A"
        ),
        Theme(
            id = 2,
            type = "none",
            value = R.drawable.back_col02,
            cat = "color",
            headerColor = "#fbac47",
            showTopLine = true,
            backgroundColor = "#00000000",
            lineColor = "#FFD369",
            textColor = "#000000"
        ),
        Theme(
            id = 3,
            type = "none",
            value = R.drawable.back_col03,
            cat = "color",
            headerColor = "#2ecc71",
            showTopLine = true,
            backgroundColor = "#00000000",
            lineColor = "#A7FFBB",
            textColor = "#000000"
        ),
        Theme(
            id = 4,
            type = "none",
            value = R.drawable.back_col04,
            cat = "color",
            headerColor = "#2597f5",
            showTopLine = true,
            backgroundColor = "#00000000",
            lineColor = "#ADDAFF",
            textColor = "#000000"
        ),
        Theme(
            id = 5,
            type = "none",
            value = R.drawable.back_col05,
            cat = "color",
            headerColor = "#e74c3c",
            showTopLine = true,
            backgroundColor = "#00000000",
            lineColor = "#FFADA7",
            textColor = "#000000"
        ),
        Theme(
            id = 6,
            type = "none",
            value = R.drawable.back_col06,
            cat = "color",
            headerColor = "#9b59b6",
            showTopLine = true,
            backgroundColor = "#00000000",
            lineColor = "#E5A2FF",
            textColor = "#000000"
        ),
        Theme(
            id = 7,
            type = "none",
            value = R.drawable.back_col07,
            cat = "color",
            headerColor = "#00d6d0",
            showTopLine = true,
            backgroundColor = "#00000000",
            lineColor = "#96E0FF",
            textColor = "#000000"
        ),
        Theme(
            id = 8,
            type = "none",
            value = R.drawable.back_col08,
            cat = "color",
            headerColor = "#6D313C",
            showTopLine = true,
            backgroundColor = "#00000000",
            lineColor = "#80FFFFFF",
            textColor = "#FFFFFF"
        ),
        Theme(
            id = 9,
            type = "none",
            value = R.drawable.back_col09,
            cat = "color",
            headerColor = "#74A0D4",
            showTopLine = true,
            backgroundColor = "#00000000",
            lineColor = "#ABBAC5",
            textColor = "#000000"
        ),
        Theme(
            id = 10,
            type = "none",
            value = R.drawable.back_col10,
            cat = "color",
            headerColor = "#60A888",
            showTopLine = true,
            backgroundColor = "#00000000",
            lineColor = "#EAD5BD",
            textColor = "#000000"
        ),
        Theme(
            id = 11,
            type = "none",
            value = R.drawable.back_col11,
            cat = "color",
            headerColor = "#1C839E",
            showTopLine = true,
            backgroundColor = "#00000000",
            lineColor = "#CFCEC3",
            textColor = "#000000"
        ),
        Theme(
            id = 12,
            type = "none",
            value = R.drawable.back_arc1,
            cat = "architecture",
            headerColor = "#fff3d6",
            showTopLine = false,
            backgroundColor = "#fff3d6",
            lineColor = "#62946e3b",
            textColor = "#727272"
        ),
        Theme(
            id = 13,
            type = "none",
            value = R.drawable.back_arc2,
            cat = "architecture",
            headerColor = "#f1f4f7",
            showTopLine = false,
            backgroundColor = "#f1f4f7",
            lineColor = "#323b7194",
            textColor = "#1a1a1a"
        ),
        Theme(
            id = 14,
            type = "none",
            value = R.drawable.back_arc3,
            cat = "architecture",
            headerColor = "#343434",
            showTopLine = false,
            backgroundColor = "#343434",
            lineColor = "#74d7d7d7",
            textColor = "#ffffff"
        ),
        Theme(
            id = 15,
            type = "none",
            value = R.drawable.back_arc4,
            cat = "architecture",
            headerColor = "#33000000",
            showTopLine = false,
            backgroundColor = "#00000000",
            lineColor = "#00000000",
            textColor = "#1a1a1a"
        ),
        Theme(
            id = 16,
            type = "none",
            value = R.drawable.back_arc5,
            cat = "architecture",
            headerColor = "#33000000",
            showTopLine = false,
            backgroundColor = "#00000000",
            lineColor = "#00000000",
            textColor = "#ffffff"
        ),
        Theme(
            id = 17,
            type = "ads",
            value = R.drawable.back_art01,
            cat = "art",
            headerColor = "#AD8679",
            showTopLine = true,
            backgroundColor = "#00000000",
            lineColor = "#00000000",
            textColor = "#1a1a1a"
        ),
        Theme(
            id = 18,
            type = "ads",
            value = R.drawable.back_art02,
            cat = "art",
            headerColor = "#75849E",
            showTopLine = true,
            backgroundColor = "#00000000",
            lineColor = "#00000000",
            textColor = "#1a1a1a"
        ),
        Theme(
            id = 19,
            type = "ads",
            value = R.drawable.back_art03,
            cat = "art",
            headerColor = "#BC9D99",
            showTopLine = true,
            backgroundColor = "#00000000",
            lineColor = "#00000000",
            textColor = "#1a1a1a"
        ),
        Theme(
            id = 20,
            type = "ads",
            value = R.drawable.back_art04,
            cat = "art",
            headerColor = "#BD868F",
            showTopLine = true,
            backgroundColor = "#00000000",
            lineColor = "#00000000",
            textColor = "#1a1a1a"
        ),
        Theme(
            id = 21,
            type = "ads",
            value = R.drawable.back_art05,
            cat = "art",
            headerColor = "#8DAEAB",
            showTopLine = true,
            backgroundColor = "#00000000",
            lineColor = "#00000000",
            textColor = "#1a1a1a"
        ),
        Theme(
            id = 22,
            type = "ads",
            value = R.drawable.back_art06,
            cat = "art",
            headerColor = "#8DAEAB",
            showTopLine = true,
            backgroundColor = "#00000000",
            lineColor = "#00000000",
            textColor = "#1a1a1a"
        ),
        Theme(
            id = 23,
            type = "none",
            value = R.drawable.back_lov1,
            cat = "love",
            headerColor = "#cfcbf7",
            showTopLine = false,
            backgroundColor = "#cfcbf7",
            lineColor = "#a69ae7",
            textColor = "#1a1a1a"
        ),
        Theme(
            id = 24,
            type = "none",
            value = R.drawable.back_lov2,
            cat = "love",
            headerColor = "#ffb0d6",
            showTopLine = false,
            backgroundColor = "#ffb0d6",
            lineColor = "#cbff99b9",
            textColor = "#1a1a1a"
        ),
        Theme(
            id = 25,
            type = "none",
            value = R.drawable.back_lov3,
            cat = "love",
            headerColor = "#f0ccc4",
            showTopLine = false,
            backgroundColor = "#f0ccc4",
            lineColor = "#a0d9aaa0",
            textColor = "#1a1a1a"
        ),
        Theme(
            id = 30,
            type = "none",
            value = R.drawable.back_min3,
            cat = "minimalist",
            headerColor = "#33000000",
            showTopLine = false,
            backgroundColor = "#00000000",
            lineColor = "#00000000",
            textColor = "#1a1a1a"
        ),
        Theme(
            id = 31,
            type = "ads",
            value = R.drawable.back_min4,
            cat = "minimalist",
            headerColor = "#CF806C",
            showTopLine = true,
            backgroundColor = "#00000000",
            lineColor = "#00000000",
            textColor = "#1a1a1a"
        ),
        Theme(
            id = 32,
            type = "ads",
            value = R.drawable.back_min5,
            cat = "minimalist",
            headerColor = "#CF806C",
            showTopLine = true,
            backgroundColor = "#00000000",
            lineColor = "#00000000",
            textColor = "#1a1a1a"
        ),
        Theme(
            id = 33,
            type = "none",
            value = R.drawable.back_nat1,
            cat = "nature",
            headerColor = "#bac794",
            showTopLine = false,
            backgroundColor = "#bac794",
            lineColor = "#6281943b",
            textColor = "#ffffff"
        ),
        Theme(
            id = 34,
            type = "none",
            value = R.drawable.back_nat2,
            cat = "nature",
            headerColor = "#649793",
            showTopLine = false,
            backgroundColor = "#649793",
            lineColor = "#4f40535a",
            textColor = "#ffffff"
        ),
        Theme(
            id = 35,
            type = "none",
            value = R.drawable.back_nat3,
            cat = "nature",
            headerColor = "#99c084",
            showTopLine = false,
            backgroundColor = "#99c084",
            lineColor = "#4f405a4a",
            textColor = "#ffffff"
        ),
        Theme(
            id = 41,
            type = "ads",
            value = R.drawable.back_pap01,
            cat = "paper",
            headerColor = "#845209",
            showTopLine = true,
            backgroundColor = "#00000000",
            lineColor = "#00000000",
            textColor = "#1a1a1a"
        ),
        Theme(
            id = 42,
            type = "ads",
            value = R.drawable.back_pap02,
            cat = "paper",
            headerColor = "#CCA982",
            showTopLine = true,
            backgroundColor = "#00000000",
            lineColor = "#00000000",
            textColor = "#1a1a1a"
        ),
        Theme(
            id = 43,
            type = "ads",
            value = R.drawable.back_pap03,
            cat = "paper",
            headerColor = "#CCA982",
            showTopLine = true,
            backgroundColor = "#00000000",
            lineColor = "#00000000",
            textColor = "#1a1a1a"
        ),
        Theme(
            id = 44,
            type = "ads",
            value = R.drawable.back_pap04,
            cat = "paper",
            headerColor = "#CCA982",
            showTopLine = true,
            backgroundColor = "#00000000",
            lineColor = "#00000000",
            textColor = "#1a1a1a"
        ),
        Theme(
            id = 45,
            type = "ads",
            value = R.drawable.back_pap05,
            cat = "paper",
            headerColor = "#83682F",
            showTopLine = true,
            backgroundColor = "#00000000",
            lineColor = "#00000000",
            textColor = "#1a1a1a"
        ),
        Theme(
            id = 46,
            type = "ads",
            value = R.drawable.back_pap06,
            cat = "paper",
            headerColor = "#714533",
            showTopLine = true,
            backgroundColor = "#00000000",
            lineColor = "#00000000",
            textColor = "#1a1a1a"
        ),
        Theme(
            id = 47,
            type = "ads",
            value = R.drawable.back_pap07,
            cat = "paper",
            headerColor = "#B1825C",
            showTopLine = true,
            backgroundColor = "#00000000",
            lineColor = "#00000000",
            textColor = "#1a1a1a"
        ),
        Theme(
            id = 48,
            type = "none",
            value = R.drawable.back_tec1,
            cat = "technology",
            headerColor = "#05121e",
            showTopLine = false,
            backgroundColor = "#05121e",
            lineColor = "#c191b5d0",
            textColor = "#ffffff"
        ),
        Theme(
            id = 49,
            type = "none",
            value = R.drawable.back_tec2,
            cat = "technology",
            headerColor = "#05121e",
            showTopLine = false,
            backgroundColor = "#05121e",
            lineColor = "#c191b5d0",
            textColor = "#ffffff"
        ),
        Theme(
            id = 50,
            type = "none",
            value = R.drawable.back_tec3,
            cat = "technology",
            headerColor = "#016064",
            showTopLine = false,
            backgroundColor = "#016064",
            lineColor = "#c191b5d0",
            textColor = "#ffffff"
        ),
        Theme(
            id = 51,
            type = "none",
            value = R.drawable.back_tec4,
            cat = "technology",
            headerColor = "#013964",
            showTopLine = false,
            backgroundColor = "#013964",
            lineColor = "#c191b5d0",
            textColor = "#ffffff"
        ),
        Theme(
            id = 54,
            type = "ads",
            value = R.drawable.back_wat01,
            cat = "watercolor",
            headerColor = "#DC3610",
            showTopLine = true,
            backgroundColor = "#00000000",
            lineColor = "#00000000",
            textColor = "#1a1a1a"
        ),
        Theme(
            id = 55,
            type = "ads",
            value = R.drawable.back_wat02,
            cat = "watercolor",
            headerColor = "#797979",
            showTopLine = true,
            backgroundColor = "#00000000",
            lineColor = "#00000000",
            textColor = "#1a1a1a"
        ),
        Theme(
            id = 56,
            type = "ads",
            value = R.drawable.back_wat03,
            cat = "watercolor",
            headerColor = "#797979",
            showTopLine = true,
            backgroundColor = "#00000000",
            lineColor = "#00000000",
            textColor = "#1a1a1a"
        )
    )


    // Optimized lookup structures
    private val themesById: Map<Int, Theme> = themes.associateBy { it.id }
    private val themesByCategory: Map<String, List<Theme>> = themes.groupBy { it.cat }
    private val solidTheme = themes.first { it.cat == "solid" }

    /**
     * Get themes by category with solid theme always first (if requested category is not "solid")
     */
    fun getThemesByCategory(category: String): List<Theme> {
        return when {
            category == "solid" -> themesByCategory["solid"] ?: emptyList()
            else -> listOf(solidTheme) + (themesByCategory[category] ?: emptyList())
        }
    }

    // Other optimized functions remain the same
    fun getThemeById(id: Int): Theme? = themesById[id]
    fun getAllCategories(): Set<String> = themesByCategory.keys

    var tabs = listOf("Color", "Art", "Nature", "Paper", "Minimalist", "Technology", "Love", "Water color")
}