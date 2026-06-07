package com.kittys.premium.core.common

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

// ═══════════════════════════════════════════════════════
//   MIKO — Extensions & Utilities
// ═══════════════════════════════════════════════════════

fun Int.toLkr(): String {
    val formatter = NumberFormat.getNumberInstance(Locale.US)
    return "LKR ${formatter.format(this)}"
}

fun Int.toShortFormat(): String = when {
    this >= 1_000_000 -> "${(this / 1_000_000f).let { if (it % 1 == 0f) it.toInt() else String.format("%.1f", it) }}M"
    this >= 1_000     -> "${(this / 1_000f).let { if (it % 1 == 0f) it.toInt() else String.format("%.1f", it) }}K"
    else              -> "$this"
}

fun discountPercent(original: Int, sale: Int): Int {
    if (original <= 0 || sale >= original) return 0
    return ((original - sale).toFloat() / original * 100).toInt()
}

fun String.capitaliseFirst(): String =
    if (isEmpty()) this else this[0].uppercaseChar() + substring(1)

fun String.truncate(max: Int): String =
    if (length <= max) this else "${take(max)}…"

fun String.isValidEmail(): Boolean =
    android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()

fun String.isValidSriLankanPhone(): Boolean {
    val clean = replace(" ", "").replace("-", "")
    return Regex("^(\\+94|0)7[0-9]{8}$").matches(clean)
}

fun String.maskCard(): String =
    if (length >= 4) "•••• •••• •••• ${takeLast(4)}" else this

fun String.toDisplayDate(): String {
    return try {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val out = SimpleDateFormat("d MMM yyyy", Locale.getDefault())
        out.format(sdf.parse(this) ?: return this)
    } catch (_: Exception) { this }
}

fun Long.toTimeAgo(): String {
    val diff = System.currentTimeMillis() - this
    return when {
        diff < 60_000          -> "just now"
        diff < 3_600_000       -> "${diff / 60_000}m ago"
        diff < 86_400_000      -> "${diff / 3_600_000}h ago"
        diff < 2_592_000_000L  -> "${diff / 86_400_000}d ago"
        else                   -> "${diff / 2_592_000_000L}mo ago"
    }
}

fun <T> List<T>.getOrDefaultAt(index: Int, default: T): T =
    if (index in indices) this[index] else default

fun Context.toast(message: String, long: Boolean = false) =
    Toast.makeText(this, message, if (long) Toast.LENGTH_LONG else Toast.LENGTH_SHORT).show()

fun Context.openUrl(url: String) = try {
    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
} catch (_: Exception) {}

fun Context.openWhatsApp(phone: String, message: String = "") = try {
    val url = "https://wa.me/$phone${if (message.isNotEmpty()) "?text=${Uri.encode(message)}" else ""}"
    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
} catch (_: Exception) {
    toast("WhatsApp not installed")
}

fun Context.shareText(text: String, title: String = "Share") {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    }
    startActivity(Intent.createChooser(intent, title))
}

@Composable
fun Dp.toPx(): Float {
    val density = LocalDensity.current
    return with(density) { this@toPx.toPx() }
}

fun <T, R> Result<T>.mapSuccess(transform: (T) -> R): Result<R> = when (this) {
    is Result.Success -> Result.Success(transform(data))
    is Result.Error   -> Result.Error(message)
    is Result.Loading -> Result.Loading
}

inline fun <T> Result<T>.onSuccess(block: (T) -> Unit): Result<T> {
    if (this is Result.Success) block(data)
    return this
}

inline fun <T> Result<T>.onError(block: (String?) -> Unit): Result<T> {
    if (this is Result.Error) block(message)
    return this
}