package com.kittys.premium.features.address

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
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import com.kittys.premium.R
import com.kittys.premium.core.ui.components.*
import com.kittys.premium.core.ui.theme.MikoTypography
import com.kittys.premium.core.ui.theme.MikoColors

data class SavedAddress(
    val id       : String,
    val label    : String,
    val fullName : String,
    val line1    : String,
    val city     : String,
    val district : String,
    val phone    : String,
    val isDefault: Boolean = false
)

@Composable
fun AddressScreen(navController: NavController) {
    var addresses by remember {
        mutableStateOf(listOf(
            SavedAddress("a1","🏠 Home","Amali Perera","No. 12, Temple Rd","Maharagama","Colombo","+94 71 234 5678", true),
            SavedAddress("a2","🏢 Work","Amali Perera","Level 3, ABC Building, Galle Rd","Colombo 03","Colombo","+94 71 234 5678")
        ))
    }
    var showForm by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize().background(MikoColors.NeuBackground)) {
        Column(modifier = Modifier.fillMaxSize()) {
            MikoTopBar(title = "Saved Addresses", onBackClick = { navController.popBackStack() })
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                addresses.forEach { addr ->
                    Row(
                        verticalAlignment = Alignment.Top,
                        modifier = Modifier
                            .fillMaxWidth()
                            .neuRaised(cornerRadius = 18.dp, shadowOffset = 6.dp)
                            .padding(16.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(44.dp).clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (addr.isDefault) Brush.linearGradient(MikoColors.GradientBlue)
                                    else                Brush.linearGradient(listOf(MikoColors.NeuShadowDark, MikoColors.NeuShadowDark))
                                )
                        ) { Text(addr.label.take(2), fontSize = 20.sp) }
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(addr.label.drop(3), style = MikoTypography.titleSmall)
                                if (addr.isDefault) {
                                    Spacer(Modifier.width(8.dp))
                                    Box(
                                        modifier = Modifier.clip(RoundedCornerShape(6.dp))
                                            .background(MikoColors.BluePrimary.copy(alpha = 0.12f))
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Text("Default", style = MikoTypography.labelSmall.copy(color = MikoColors.BluePrimary))
                                    }
                                }
                            }
                            Text(addr.fullName, style = MikoTypography.bodySmall.copy(color = MikoColors.TextMuted))
                            Text("${addr.line1}, ${addr.city}, ${addr.district}", style = MikoTypography.bodySmall.copy(color = MikoColors.TextMuted))
                            Text(addr.phone, style = MikoTypography.bodySmall.copy(color = MikoColors.TextMuted))
                            Spacer(Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text("Edit", style = MikoTypography.labelSmall.copy(color = MikoColors.BluePrimary),
                                    modifier = Modifier.clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {})
                                Text("Delete", style = MikoTypography.labelSmall.copy(color = MikoColors.Error),
                                    modifier = Modifier.clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                                        addresses = addresses.filter { it.id != addr.id }
                                    })
                                if (!addr.isDefault) {
                                    Text("Set Default", style = MikoTypography.labelSmall.copy(color = MikoColors.TextMuted),
                                        modifier = Modifier.clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                                            addresses = addresses.map { it.copy(isDefault = it.id == addr.id) }
                                        })
                                }
                            }
                        }
                    }
                }
                GradientButton(
                    text     = "+ Add New Address",
                    onClick  = { showForm = true },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(80.dp))
            }
        }
    }
}


// ═══════════════════════════════════════════════════════
//   PAYMENT METHODS SCREEN
// ═══════════════════════════════════════════════════════

