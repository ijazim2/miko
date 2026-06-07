package com.kittys.premium.features.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

// ════════════════════════════════════════════════════════════════
//   MIKO — Admin ViewModel
// ════════════════════════════════════════════════════════════════

data class AdminStats(
    val totalUsers: Int = 0,
    val totalVendors: Int = 0,
    val totalOrders: Int = 0,
    val totalRevenueLkr: Int = 0,
    val todayOrders: Int = 0,
    val todayRevenue: Int = 0,
    val pendingVendors: Int = 0,
    val openTickets: Int = 0
)

data class FlaggedItem(
    val id: String,
    val type: String,        // vendor | review | order | user
    val reason: String,
    val target: String,
    val timeAgo: String
)

data class AdminUiState(
    val isLoading: Boolean = false,
    val selectedTab: Int = 0,
    val stats: AdminStats = AdminStats(),
    val flaggedItems: List<FlaggedItem> = emptyList(),
    val recentSignups: List<String> = emptyList(),
    val topVendors: List<Pair<String, Int>> = emptyList(),
    val weeklyRevenue: List<Float> = emptyList()
)

@HiltViewModel
class AdminViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(AdminUiState())
    val uiState: StateFlow<AdminUiState> = _uiState.asStateFlow()

    init { loadDashboard() }

    fun loadDashboard() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            // TODO: replace with real Supabase admin queries.
            // Requires service_role key — call via Edge Function for security:
            // supabase.functions.invoke("admin-stats")
            _uiState.update {
                it.copy(
                    isLoading = false,
                    stats = AdminStats(
                        totalUsers      = 4821,
                        totalVendors    = 67,
                        totalOrders     = 12430,
                        totalRevenueLkr = 8_450_000,
                        todayOrders     = 143,
                        todayRevenue    = 287_600,
                        pendingVendors  = 5,
                        openTickets     = 12
                    ),
                    flaggedItems = listOf(
                        FlaggedItem("f1", "vendor", "Duplicate products",       "Little Stars Shop", "2h ago"),
                        FlaggedItem("f2", "review", "Inappropriate content",    "Review #4492",      "4h ago"),
                        FlaggedItem("f3", "order",  "Multiple failed payments", "Order #B9F2",       "5h ago"),
                        FlaggedItem("f4", "user",   "Suspicious activity",      "user@email.com",    "1d ago")
                    ),
                    recentSignups = listOf("Amali P.", "Nisha R.", "Kamal S.", "Priya T.", "Saman W."),
                    topVendors = listOf(
                        "Little Stars Fashion" to 284500,
                        "Kids World LK"        to 198200,
                        "Baby Boutique"        to 156800,
                        "Tiny Trends"          to 134100
                    ),
                    weeklyRevenue = listOf(680000f, 920000f, 540000f, 1100000f, 870000f, 1340000f, 990000f)
                )
            }
        }
    }

    fun resolveFlag(id: String) {
        _uiState.update { state ->
            state.copy(flaggedItems = state.flaggedItems.filter { it.id != id })
        }
    }

    fun setTab(index: Int) {
        _uiState.update { it.copy(selectedTab = index) }
    }

    fun approveVendor(vendorId: String) {
        viewModelScope.launch {
            // TODO: supabase.postgrest["vendors"].update(mapOf("verified" to true)) { ... }
            _uiState.update { state ->
                state.copy(
                    stats = state.stats.copy(
                        pendingVendors = (state.stats.pendingVendors - 1).coerceAtLeast(0)
                    )
                )
            }
        }
    }

    fun suspendUser(userId: String) {
        viewModelScope.launch {
            // TODO: supabase.postgrest["users"].update(mapOf("suspended" to true)) { ... }
        }
    }
}