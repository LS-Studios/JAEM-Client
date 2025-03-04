package de.stubbe.jaem_client.view.variables

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

object Dimensions {
    object Padding {
        val None = 0.dp
        val Tiny = 4.dp
        val Small = 8.dp
        val Medium = 16.dp
        val Large = 24.dp
        val Huge = 64.dp
        val TopBar = PaddingValues(
            top = Tiny,
            start = Small,
            end = Small
        )
    }

    object Border {
        val ThinBorder = 1.dp
    }

    object Spacing {
        val Tiny = 2.dp
        val Small = 8.dp
        val Medium = 16.dp
        val Large = 24.dp
    }

    object FontSize {
        val Medium = 18.sp
    }

    object Shape {
        object RoundedTop {
            val Small = RoundedCornerShape(12.dp, 12.dp, 0.dp, 0.dp)
            val Medium = RoundedCornerShape(24.dp, 24.dp, 0.dp, 0.dp)
        }
        object RoundedBottom {
            val Small = RoundedCornerShape(0.dp, 0.dp, 12.dp, 12.dp)
            val Medium = RoundedCornerShape(0.dp, 0.dp, 24.dp, 24.dp)
        }
        object Rounded {
            val Tiny = RoundedCornerShape(4.dp)
            val Small = RoundedCornerShape(12.dp)
            val Medium = RoundedCornerShape(24.dp)
        }

        val Rectangle = RoundedCornerShape(0.dp)

        object ChatBubbleShape {
            private val offset = 20f

            object Left {
                val Triangle = GenericShape { size, _ ->
                    moveTo(x = size.width, y = offset)
                    lineTo(x = size.width, y = 0f)
                    lineTo(x = size.width - offset, y = 0f)
                }
                val Body = RoundedCornerShape(
                    0.dp,
                    12.dp,
                    12.dp,
                    12.dp
                )
            }

            object Right {
                val Triangle = GenericShape { size, _ ->
                    moveTo(x = 0f, y = offset)
                    lineTo(x = 0f, y = 0f)
                    lineTo(x = 0f + offset, y = 0f)
                }
                val Body = RoundedCornerShape(
                    12.dp,
                    0.dp,
                    12.dp,
                    12.dp
                )
            }
        }
    }

    object Size {
        val SuperTiny = 8.dp
        val Tiny = 20.dp
        val Small = 32.dp
        val Medium = 48.dp
        val Large = 64.dp
        val Huge = 140.dp
        val SuperHuge = 200.dp
        val TopBar = 56.dp
    }

    object Quality {
        val Medium = 300
    }

}