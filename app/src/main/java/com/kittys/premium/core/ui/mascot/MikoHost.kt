package com.kittys.premium.core.ui.mascot

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kittys.premium.R
import com.kittys.premium.core.ui.system.MikoState
import com.kittys.premium.core.ui.system.MikoViewModel

// ════════════════════════════════════════════════════════════════
//   MIKO HOST — drop in anywhere. Single renderer swap point.
//   Pass the hoisted MikoViewModel so all reactions share one Miko.
// ════════════════════════════════════════════════════════════════

private val staticArt = MikoArtSet(
    idle = R.drawable.miko_character   // change if your drawable name differs
)

@Composable
private fun rememberMikoRenderer(): MikoRenderer = remember {
    StaticImageMikoRenderer(staticArt)
    // LATER: LottieMikoRenderer(MikoLottieSet(idle = R.raw.miko_idle, ...))
}

@Composable
fun MikoHost(
    viewModel: MikoViewModel,
    modifier: Modifier = Modifier,
    size: Dp = 56.dp,
    stateOverride: MikoState? = null
) {
    val ui by viewModel.ui.collectAsState()
    val renderer = rememberMikoRenderer()
    MikoComposable(
        state = stateOverride ?: ui.state,
        renderer = renderer,
        modifier = modifier,
        size = size
    )
}
