package com.kittys.premium.features.profile

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import com.kittys.premium.core.ui.components.MikoPrimaryButton
import com.kittys.premium.core.ui.components.MikoSecondaryButton
import com.kittys.premium.core.ui.components.neuRaised
import com.kittys.premium.core.ui.theme.MikoColors
import com.kittys.premium.core.ui.theme.MikoTypography

// ════════════════════════════════════════════════════════════════
//   MIKO — Kids Profiles Screen
//   Add children with age/height/weight → AI size memory
// ════════════════════════════════════════════════════════════════

data class ChildProfileUi(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String,
    val gender: String,       // "girl" | "boy"
    val age: Int,
    val currentSize: String,
    val heightCm: Int? = null,
    val weightKg: Float? = null
) {
    val emoji: String get() = if (gender == "boy") "👦" else "👧"
}

@Composable
fun ChildProfilesScreen(navController: NavController) {

    // Local state (swap for ViewModel/Supabase later)
    val children = remember {
        mutableStateListOf(
            ChildProfileUi(name = "Sasha", gender = "girl", age = 5, currentSize = "4-5Y", heightCm = 108, weightKg = 18f),
            ChildProfileUi(name = "Rahul", gender = "boy", age = 8, currentSize = "7-8Y", heightCm = 128, weightKg = 26f)
        )
    }
    var showForm by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MikoColors.Background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // ─── Top Bar ───
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(40.dp)
                        .neuRaised(cornerRadius = 20.dp, shadowOffset = 4.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { navController.popBackStack() }
                ) {
                    Text("←", fontSize = 18.sp, color = MikoColors.TextPrimary)
                }
                Spacer(Modifier.width(12.dp))
                Text(
                    "Kids Profiles",
                    style = MikoTypography.Title.copy(
                        color = MikoColors.TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            if (children.isEmpty() && !showForm) {
                // ─── Empty state ───
                Box(Modifier.fillMaxSize(), Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("👶", fontSize = 64.sp)
                        Spacer(Modifier.height(16.dp))
                        Text(
                            "No kids profiles yet",
                            style = MikoTypography.Headline.copy(
                                color = MikoColors.TextPrimary, fontWeight = FontWeight.Bold
                            )
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "Add a child to get AI size tips and personalised picks",
                            style = MikoTypography.Body.copy(color = MikoColors.TextMuted)
                        )
                        Spacer(Modifier.height(28.dp))
                        MikoPrimaryButton(
                            text = "+ Add Child",
                            trailingIcon = null,
                            onClick = { showForm = true },
                            modifier = Modifier.width(200.dp)
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    children.forEach { child ->
                        ExistingChildCard(child = child, onRemove = { children.remove(child) })
                    }

                    if (!showForm) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(MikoColors.PastelPink)
                                .border(1.dp, MikoColors.PrimaryStart.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) { showForm = true }
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("+", fontSize = 20.sp, color = MikoColors.PrimaryEnd, fontWeight = FontWeight.Bold)
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    "Add Another Child",
                                    style = MikoTypography.Subtitle.copy(
                                        color = MikoColors.PrimaryEnd, fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }
                        }
                    }

                    if (showForm) {
                        AddChildForm(
                            onSave = { name, gender, age, height, weight ->
                                val sizeGuess = ageToSize(age)
                                children.add(
                                    ChildProfileUi(
                                        name = name,
                                        gender = gender,
                                        age = age,
                                        currentSize = sizeGuess,
                                        heightCm = height,
                                        weightKg = weight
                                    )
                                )
                                showForm = false
                            },
                            onCancel = { showForm = false }
                        )
                    }

                    Spacer(Modifier.height(40.dp))
                }
            }
        }
    }
}

private fun ageToSize(age: Int): String = when {
    age <= 1 -> "0-12M"
    age <= 2 -> "1-2Y"
    age <= 4 -> "2-4Y"
    age <= 6 -> "4-6Y"
    age <= 8 -> "7-8Y"
    age <= 10 -> "9-10Y"
    age <= 12 -> "11-12Y"
    else -> "12Y+"
}

// ─── Existing Child Card ───
@Composable
private fun ExistingChildCard(child: ChildProfileUi, onRemove: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .neuRaised(cornerRadius = 18.dp, shadowOffset = 6.dp)
            .padding(16.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(MikoColors.GradientPrimary))
        ) {
            Text(child.emoji, fontSize = 26.sp)
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                child.name,
                style = MikoTypography.Subtitle.copy(
                    color = MikoColors.TextPrimary, fontWeight = FontWeight.Bold
                )
            )
            Text(
                "${child.age} years old  •  Size ${child.currentSize}",
                style = MikoTypography.Caption.copy(color = MikoColors.TextMuted)
            )
            if (child.heightCm != null && child.weightKg != null) {
                Text(
                    "${child.heightCm}cm  •  ${child.weightKg}kg",
                    style = MikoTypography.Caption.copy(color = MikoColors.TextMuted, fontSize = 10.sp)
                )
            }
        }
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(36.dp)
                .neuRaised(cornerRadius = 10.dp, shadowOffset = 4.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onRemove() }
        ) {
            Text("🗑", fontSize = 14.sp)
        }
    }
}

// ─── Add Child Form ───
@Composable
private fun AddChildForm(
    onSave: (name: String, gender: String, age: Int, height: Int?, weight: Float?) -> Unit,
    onCancel: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("girl") }
    var age by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .neuRaised(cornerRadius = 18.dp, shadowOffset = 8.dp)
            .padding(20.dp)
    ) {
        Text(
            "Add Child Profile",
            style = MikoTypography.Title.copy(
                color = MikoColors.PrimaryEnd, fontWeight = FontWeight.Bold
            )
        )
        Text(
            "This helps MIKO AI give personalised size recommendations",
            style = MikoTypography.Caption.copy(color = MikoColors.TextMuted)
        )

        Spacer(Modifier.height(20.dp))

        // Name
        FieldLabel("Child's Name")
        ChildField(name, { name = it }, "e.g. Sasha, Rahul")

        Spacer(Modifier.height(16.dp))

        // Gender
        FieldLabel("Gender")
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            listOf("girl" to "👧 Girl", "boy" to "👦 Boy").forEach { (value, label) ->
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .then(
                            if (gender == value)
                                Modifier.background(Brush.linearGradient(MikoColors.GradientPrimary))
                            else
                                Modifier.background(MikoColors.SurfaceVariant)
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { gender = value }
                ) {
                    Text(
                        label,
                        style = MikoTypography.Subtitle.copy(
                            color = if (gender == value) Color.White else MikoColors.TextSecondary,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Age
        FieldLabel("Age (years)")
        ChildField(age, { age = it.filter { c -> c.isDigit() } }, "e.g. 5", KeyboardType.Number)

        Spacer(Modifier.height(16.dp))

        // Height & Weight
        FieldLabel("Height & Weight (optional)")
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(Modifier.weight(1f)) {
                ChildField(height, { height = it.filter { c -> c.isDigit() } }, "Height (cm)", KeyboardType.Number)
            }
            Box(Modifier.weight(1f)) {
                ChildField(weight, { weight = it.filter { c -> c.isDigit() || c == '.' } }, "Weight (kg)", KeyboardType.Decimal)
            }
        }

        Spacer(Modifier.height(20.dp))

        // AI tip note
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MikoColors.PastelLavender)
                .padding(12.dp)
        ) {
            Text("✨", fontSize = 16.sp)
            Spacer(Modifier.width(8.dp))
            Text(
                "MIKO AI will use this to suggest the perfect size for every product automatically.",
                style = MikoTypography.Caption.copy(color = MikoColors.AccentBright, lineHeight = 16.sp)
            )
        }

        Spacer(Modifier.height(20.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MikoSecondaryButton(
                text = "Cancel",
                onClick = onCancel,
                modifier = Modifier.weight(1f),
                height = 50.dp
            )
            Box(Modifier.weight(1f)) {
                MikoPrimaryButton(
                    text = "Save Child",
                    trailingIcon = null,
                    enabled = name.isNotBlank() && age.isNotBlank(),
                    onClick = {
                        if (name.isNotBlank() && age.isNotBlank()) {
                            onSave(
                                name,
                                gender,
                                age.toIntOrNull() ?: 0,
                                height.toIntOrNull(),
                                weight.toFloatOrNull()
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    height = 50.dp
                )
            }
        }
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(
        text,
        style = MikoTypography.Label.copy(color = MikoColors.TextSecondary)
    )
    Spacer(Modifier.height(8.dp))
}

@Composable
private fun ChildField(
    value: String,
    onChange: (String) -> Unit,
    hint: String,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MikoColors.SurfaceVariant)
            .border(1.dp, MikoColors.BorderLight, RoundedCornerShape(12.dp))
            .padding(horizontal = 14.dp)
    ) {
        BasicTextField(
            value = value,
            onValueChange = onChange,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            textStyle = TextStyle(
                fontSize = 15.sp,
                color = MikoColors.TextPrimary,
                fontWeight = FontWeight.Medium
            ),
            cursorBrush = SolidColor(MikoColors.PrimaryStart),
            modifier = Modifier.fillMaxWidth().padding(vertical = 14.dp),
            decorationBox = { inner ->
                if (value.isEmpty()) {
                    Text(hint, style = MikoTypography.Body.copy(color = MikoColors.TextMuted))
                }
                inner()
            }
        )
    }
}