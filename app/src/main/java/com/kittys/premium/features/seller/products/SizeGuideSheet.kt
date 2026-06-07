package com.kittys.premium.features.seller.products

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.kittys.premium.core.ui.theme.MikoColors
import com.kittys.premium.core.ui.components.*
import com.kittys.premium.core.ui.theme.MikoTypography

// ═══════════════════════════════════════════════════════
//   SIZE GUIDE BOTTOM SHEET
//   Call from ProductDetailScreen when user taps "Size Guide →"
// ═══════════════════════════════════════════════════════

data class SizeRow(
    val label  : String,   // e.g. "0-3M", "4Y"
    val age    : String,   // e.g. "0–3 months", "3–4 years"
    val height : String,   // e.g. "56–62 cm"
    val weight : String,   // e.g. "4–6 kg"
    val chest  : String    // e.g. "40 cm"
)

val babyGirlSizes = listOf(
    SizeRow("0-3M",  "0–3 months",  "56–62 cm",  "4–6 kg",   "40 cm"),
    SizeRow("3-6M",  "3–6 months",  "62–68 cm",  "6–8 kg",   "42 cm"),
    SizeRow("6-12M", "6–12 months", "68–80 cm",  "8–10 kg",  "45 cm"),
    SizeRow("1Y",    "1 year",      "80–86 cm",  "10–12 kg", "48 cm"),
    SizeRow("2Y",    "2 years",     "86–92 cm",  "12–14 kg", "50 cm"),
    SizeRow("3Y",    "3 years",     "92–98 cm",  "14–16 kg", "52 cm"),
    SizeRow("4Y",    "4 years",     "98–104 cm", "16–18 kg", "54 cm"),
    SizeRow("5Y",    "5 years",     "104–110 cm","18–20 kg", "56 cm"),
    SizeRow("6Y",    "6 years",     "110–116 cm","20–23 kg", "58 cm"),
    SizeRow("7Y",    "7 years",     "116–122 cm","23–26 kg", "60 cm"),
    SizeRow("8Y",    "8 years",     "122–128 cm","26–30 kg", "62 cm"),
    SizeRow("10Y",   "10 years",    "128–140 cm","30–38 kg", "66 cm"),
    SizeRow("12Y+",  "12+ years",   "140–152 cm","38–48 kg", "70 cm")
)

val boysSizes = listOf(
    SizeRow("0-3M",  "0–3 months",  "56–62 cm",  "4–6 kg",   "41 cm"),
    SizeRow("3-6M",  "3–6 months",  "62–68 cm",  "6–8 kg",   "43 cm"),
    SizeRow("6-12M", "6–12 months", "68–80 cm",  "8–11 kg",  "46 cm"),
    SizeRow("1Y",    "1 year",      "80–86 cm",  "11–13 kg", "49 cm"),
    SizeRow("2Y",    "2 years",     "86–92 cm",  "13–15 kg", "51 cm"),
    SizeRow("3Y",    "3 years",     "92–98 cm",  "15–17 kg", "53 cm"),
    SizeRow("4Y",    "4 years",     "98–104 cm", "17–19 kg", "55 cm"),
    SizeRow("5Y",    "5 years",     "104–110 cm","19–21 kg", "57 cm"),
    SizeRow("6Y",    "6 years",     "110–116 cm","21–24 kg", "59 cm"),
    SizeRow("7Y",    "7 years",     "116–122 cm","24–27 kg", "61 cm"),
    SizeRow("8Y",    "8 years",     "122–128 cm","27–32 kg", "63 cm"),
    SizeRow("10Y",   "10 years",    "128–140 cm","32–40 kg", "67 cm"),
    SizeRow("12Y+",  "12+ years",   "140–152 cm","40–50 kg", "72 cm")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SizeGuideBottomSheet(
    category  : String = "Girls",   // "Girls" | "Boys" | "Babies"
    onDismiss : () -> Unit
) {
    var selectedGender by remember { mutableStateOf(category) }
    val sizes          = if (selectedGender == "Boys") boysSizes else babyGirlSizes

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor   = MikoColors.NeuSurface,
        shape            = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            // Handle bar
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(MikoColors.Divider)
                    .align(Alignment.CenterHorizontally)
            )
            Spacer(Modifier.height(16.dp))

            // Header
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically,
                modifier              = Modifier.fillMaxWidth()
            ) {
                Text("Size Guide", style = MikoTypography.headlineSmall)
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(MikoColors.NeuShadowDark.copy(alpha = 0.2f))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication        = null
                        ) { onDismiss() }
                ) {
                    Text("✕", fontSize = 14.sp, color = MikoColors.TextMuted)
                }
            }

            Spacer(Modifier.height(16.dp))

            // AI tip banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Brush.linearGradient(MikoColors.GradientViolet))
                    .padding(14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("✦", fontSize = 18.sp, color = Color.White)
                    Spacer(Modifier.width(10.dp))
                    Text(
                        "Add your child's profile and Kitty AI will automatically pick the right size for every product.",
                        style = MikoTypography.bodySmall.copy(
                            color      = Color.White.copy(alpha = 0.92f),
                            lineHeight = 18.sp
                        )
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Gender toggle
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                listOf("Girls", "Boys").forEach { g ->
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .then(
                                if (selectedGender == g)
                                    Modifier.background(Brush.linearGradient(MikoColors.GradientBlue))
                                else
                                    Modifier.neuRaised(cornerRadius = 12.dp, shadowOffset = 4.dp)
                            )
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication        = null
                            ) { selectedGender = g }
                    ) {
                        Text(
                            if (g == "Girls") "👗 Girls" else "👦 Boys",
                            style = MikoTypography.titleSmall.copy(
                                color = if (selectedGender == g) Color.White else MikoColors.TextSecondary
                            )
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // How to measure card
            NeuCard(modifier = Modifier.fillMaxWidth(), padding = PaddingValues(16.dp)) {
                Text("How to Measure", style = MikoTypography.titleSmall)
                Spacer(Modifier.height(10.dp))
                listOf(
                    "📏 Height" to "Stand straight, measure from top of head to floor",
                    "⚖️ Weight" to "Use a digital scale in kg",
                    "👕 Chest"  to "Measure around the fullest part of the chest"
                ).forEach { (label, tip) ->
                    Row(modifier = Modifier.padding(vertical = 4.dp)) {
                        Text(label, style = MikoTypography.labelSmall.copy(
                            color = MikoColors.BluePrimary, fontWeight = FontWeight.SemiBold
                        ), modifier = Modifier.width(80.dp))
                        Text(tip, style = MikoTypography.bodySmall.copy(
                            color = MikoColors.TextSecondary, lineHeight = 17.sp
                        ))
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // Size table header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                    .background(Brush.linearGradient(MikoColors.GradientBlue))
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                TableHeaderCell("Size",   Modifier.weight(1f))
                TableHeaderCell("Age",    Modifier.weight(1.5f))
                TableHeaderCell("Height", Modifier.weight(1.5f))
                TableHeaderCell("Weight", Modifier.weight(1.5f))
                TableHeaderCell("Chest",  Modifier.weight(1.2f))
            }

            // Size rows
            sizes.forEachIndexed { index, row ->
                val isEven = index % 2 == 0
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            if (isEven) MikoColors.NeuSurface
                            else        MikoColors.NeuBackground
                        )
                        .padding(horizontal = 14.dp, vertical = 9.dp)
                ) {
                    // Size label with gradient if highlighted
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(MikoColors.BluePrimary.copy(alpha = 0.10f))
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Text(
                            row.label,
                            style = MikoTypography.labelSmall.copy(
                                color      = MikoColors.BluePrimary,
                                fontWeight = FontWeight.Bold
                            ),
                            textAlign = TextAlign.Center
                        )
                    }
                    Spacer(Modifier.width(6.dp))
                    TableCell(row.age,    Modifier.weight(1.5f))
                    TableCell(row.height, Modifier.weight(1.5f))
                    TableCell(row.weight, Modifier.weight(1.5f))
                    TableCell(row.chest,  Modifier.weight(1.2f))
                }
                HorizontalDivider(color = MikoColors.Divider.copy(alpha = 0.4f))
            }

            Spacer(Modifier.height(12.dp))

            // Disclaimer
            Text(
                "* Sizes may vary slightly between brands. When in doubt, size up.",
                style     = MikoTypography.labelSmall.copy(
                    color     = MikoColors.TextMuted,
                    textAlign = TextAlign.Center
                ),
                modifier  = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(36.dp))
        }
    }
}

@Composable
private fun TableHeaderCell(text: String, modifier: Modifier) {
    Text(
        text      = text,
        style     = MikoTypography.labelSmall.copy(
            color      = Color.White,
            fontWeight = FontWeight.Bold,
            textAlign  = TextAlign.Center
        ),
        modifier  = modifier
    )
}

@Composable
private fun TableCell(text: String, modifier: Modifier) {
    Text(
        text      = text,
        style     = MikoTypography.labelSmall.copy(
            color     = MikoColors.TextSecondary,
            textAlign = TextAlign.Center,
            fontSize  = 11.sp
        ),
        modifier  = modifier
    )
}
