package com.feature.edureminder.texteditor.paragraph.type

@ConsistentCopyVisibility
public data class UnorderedListStyleType private constructor(
    internal val prefixes: List<String>,
) {
    public companion object {
        public fun from(vararg prefix: String): UnorderedListStyleType {
            return UnorderedListStyleType(prefix.toList())
        }

        public fun from(prefixes: List<String>): UnorderedListStyleType {
            return UnorderedListStyleType(prefixes)
        }

        public val Disc: UnorderedListStyleType = UnorderedListStyleType(
            prefixes = listOf("•")
        )

        public val Circle: UnorderedListStyleType = UnorderedListStyleType(
            prefixes = listOf("◦")
        )

        public val Square: UnorderedListStyleType = UnorderedListStyleType(
            prefixes = listOf("▪")
        )
        public val Arrow: UnorderedListStyleType = UnorderedListStyleType(
            prefixes = listOf("➔")
        )
        public val Check: UnorderedListStyleType = UnorderedListStyleType(
            prefixes = listOf("✓")
        )
        public val Fire: UnorderedListStyleType = UnorderedListStyleType(
            prefixes = listOf("\uD83D\uDD25")
        )
        public val Watermelon: UnorderedListStyleType = UnorderedListStyleType(
            prefixes = listOf("🍉")
        )
        public val Star: UnorderedListStyleType = UnorderedListStyleType(
            prefixes = listOf("⭐")
        )
        public val Black: UnorderedListStyleType = UnorderedListStyleType(
            prefixes = listOf("⬛")
        )
        public val RightFinger: UnorderedListStyleType = UnorderedListStyleType(
            prefixes = listOf("\uD83D\uDC49")
        )
        public val Apple: UnorderedListStyleType = UnorderedListStyleType(
            prefixes = listOf("🍎")
        )
        public val Heart: UnorderedListStyleType = UnorderedListStyleType(
            prefixes = listOf("❤️")
        )
        public val Cherry: UnorderedListStyleType = UnorderedListStyleType(
            prefixes = listOf("🍒")
        )
        public val Strawberry: UnorderedListStyleType = UnorderedListStyleType(
            prefixes = listOf("🍓")
        )


//        val a =listOf("•","➔","✓","\uD83D\uDD25","🍉","⭐","⬛","\uD83D\uDC49","🍎","❤️","🍒","🍓")

    }
}