package com.kittys.premium.features.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import com.kittys.premium.core.navigation.Screen
import javax.inject.Inject

// ═══════════════════════════════════════════════════════
//   NOTIFICATIONS UI STATE
// ═══════════════════════════════════════════════════════

data class NotificationsUiState(
    val notifications : List<Notification> = emptyList(),
    val unreadCount   : Int                = 0,
    val isLoading     : Boolean            = false
)

// ══════════════════════════════════════════════════════
//   NOTIFICATIONS VIEWMODEL
// ═══════════════════════════════════════════════════════

@HiltViewModel
class NotificationsViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationsUiState())
    val uiState: StateFlow<NotificationsUiState> = _uiState.asStateFlow()

    init { load() }

    private fun load() {
        viewModelScope.launch {
            // Replace with Supabase real-time notification query:
            // supabase.postgrest["notifications"]
            //   .select { filter { eq("user_id", userId) }; order("created_at", DESCENDING) }
            val sample = listOf(
                Notification("1", NotifType.ORDER,
                    "Order Confirmed ✅",
                    "Your order #A3F9 has been confirmed and is being prepared.",
                    "2h", false, Screen.Orders.route),
                Notification("2", NotifType.AI,
                    "Miko AI has new picks ✦",
                    "Based on your child's profile, we found 5 new outfits they'd love!",
                    "5h", false, Screen.AIStylist.route),
                Notification("3", NotifType.PROMO,
                    "Flash Sale — 40% OFF 🎉",
                    "Today only: Premium girls dresses up to 40% off. Shop before midnight!",
                    "8h", false, Screen.Home.route),
                Notification("4", NotifType.ORDER,
                    "Order Shipped 📦",
                    "Your order #B7C2 is on the way! Estimated delivery: tomorrow.",
                    "1d", true, Screen.Orders.route),
                Notification("5", NotifType.WISHLIST,
                    "Price Drop Alert ❤️",
                    "The Summer Floral Dress you saved dropped from LKR 3,200 to LKR 2,400!",
                    "2d", true, Screen.Wishlist.route),
                Notification("6", NotifType.SYSTEM,
                    "Welcome to kitty's premium!",
                    "Add your child's profile to get personalised size tips and AI outfit ideas.",
                    "3d", true, Screen.Profile.route)
            )
            _uiState.update {
                it.copy(
                    notifications = sample,
                    unreadCount   = sample.count { n -> !n.isRead }
                )
            }
        }
    }

    fun markRead(id: String) {
        _uiState.update { state ->
            val updated = state.notifications.map {
                if (it.id == id) it.copy(isRead = true) else it
            }
            state.copy(
                notifications = updated,
                unreadCount   = updated.count { !it.isRead }
            )
        }
    }

    fun markAllRead() {
        _uiState.update { state ->
            state.copy(
                notifications = state.notifications.map { it.copy(isRead = true) },
                unreadCount   = 0
            )
        }
    }

    fun refresh() = load()
}
