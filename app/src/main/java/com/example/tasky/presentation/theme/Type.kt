package com.example.tasky.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.tasky.R

// Set of Material typography styles to start with

val interFont = FontFamily(
    Font(R.font.inter_black, FontWeight.Black),
    Font(R.font.inter_bold, FontWeight.Bold),
    Font(R.font.inter_extrabold, FontWeight.ExtraBold),
    Font(R.font.inter_extralight, FontWeight.ExtraLight),
    Font(R.font.inter_light, FontWeight.Light),
    Font(R.font.inter_medium, FontWeight.Medium),
    Font(R.font.inter_regular, FontWeight.Normal),
    Font(R.font.inter_semibold, FontWeight.SemiBold),
    Font(R.font.inter_thin, FontWeight.Thin)
)

val Typography = Typography(
    titleLarge = TextStyle(
        fontFamily = interFont,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp
    ),
    labelLarge = TextStyle(
        fontFamily = interFont,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp
    ),
    labelMedium = TextStyle(
        fontFamily = interFont,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp
    ),
    labelSmall = TextStyle(
        fontFamily = interFont,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    displayMedium = TextStyle(
        fontFamily = interFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 13.sp
    ),
    displaySmall = TextStyle(
        fontFamily = interFont,
        fontWeight = FontWeight.Bold,
        fontSize = 11.sp
    )
)