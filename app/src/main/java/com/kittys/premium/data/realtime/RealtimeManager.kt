package com.kittys.premium.data.realtime

import com.kittys.premium.domain.model.OrderStatus
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.realtime.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import javax.inject.Inject
import javax.inject.Singleton

// ═══════════════════════════════════════════════════════
//   SUPABASE REALTIME MANAGER
//   Listens for live changes on:
//   - orders      → update order status in real time
//   - cart        → sync cart across devices
//   - notifications → push in-app alerts instantly
// ═══════════════════════════════════════════════════════

@Serializable
data class RealtimeOrderPayload(
    val id     : String,
    val status : String
)

@Serializable
data class RealtimeNotifPayload(
    val title : String,
    val body  : String,
    val type  : String
)

sealed class RealtimeEvent {
    data class OrderStatusChanged(val orderId: String, val newStatus: OrderStatus) : RealtimeEvent()
    data class CartUpdated(val userId: String) : RealtimeEvent()
    data class NewNotification(val title: String, val body: String, val type: String) : RealtimeEvent()
}

@Singleton
class RealtimeManager @Inject constructor(
    private val supabase: SupabaseClient
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _events = MutableSharedFlow<RealtimeEvent>(extraBufferCapacity = 16)
    val events: SharedFlow<RealtimeEvent> = _events.asSharedFlow()

    private var ordersChannel: RealtimeChannel? = null
    private var cartChannel  : RealtimeChannel? = null
    private var notifChannel : RealtimeChannel? = null

    // ── Start listening (call from MainActivity or App) ──
    fun startListening() {
        val userId = supabase.auth.currentUserOrNull()?.id ?: return

        scope.launch {
            try {
                listenToOrders(userId)
                listenToCart(userId)
                listenToNotifications(userId)
            } catch (e: Exception) {
                // Realtime not critical — silent fail
            }
        }
    }

    // ── Orders: live status updates ──
    private suspend fun listenToOrders(userId: String) {
        ordersChannel = supabase.realtime.channel("orders:$userId")

        ordersChannel?.postgresChangeFlow<PostgresAction.Update>(schema = "public") {
            table  = "orders"
            filter = "user_id=eq.$userId"
        }?.collect { action ->
            val record = action.record
            val id     = record["id"]?.jsonPrimitive?.content ?: return@collect
            val status = record["status"]?.jsonPrimitive?.content ?: return@collect
            _events.emit(
                RealtimeEvent.OrderStatusChanged(
                    orderId   = id,
                    newStatus = parseStatus(status)
                )
            )
        }

        ordersChannel?.subscribe()
    }

    // ── Cart: sync across devices ──
    private suspend fun listenToCart(userId: String) {
        cartChannel = supabase.realtime.channel("cart:$userId")

        // Any change to this user's cart row triggers a refresh
        cartChannel?.postgresChangeFlow<PostgresAction>(schema = "public") {
            table  = "cart"
            filter = "user_id=eq.$userId"
        }?.collect {
            _events.emit(RealtimeEvent.CartUpdated(userId))
        }

        cartChannel?.subscribe()
    }

    // ── Notifications: instant in-app alerts ──
    private suspend fun listenToNotifications(userId: String) {
        notifChannel = supabase.realtime.channel("notifications:$userId")

        notifChannel?.postgresChangeFlow<PostgresAction.Insert>(schema = "public") {
            table  = "notifications"
            filter = "user_id=eq.$userId"
        }?.collect { action ->
            val record = action.record
            _events.emit(
                RealtimeEvent.NewNotification(
                    title = record["title"]?.jsonPrimitive?.content ?: "",
                    body  = record["body"]?.jsonPrimitive?.content  ?: "",
                    type  = record["type"]?.jsonPrimitive?.content  ?: "general"
                )
            )
        }

        notifChannel?.subscribe()
    }

    // ── Stop when user signs out ──
    fun stopListening() {
        scope.launch {
            try {
                ordersChannel?.unsubscribe()
                cartChannel?.unsubscribe()
                notifChannel?.unsubscribe()
            } catch (_: Exception) {}
        }
    }

    private fun parseStatus(s: String): OrderStatus = when (s.lowercase()) {
        "confirmed"        -> OrderStatus.CONFIRMED
        "processing"       -> OrderStatus.PROCESSING
        "shipped"          -> OrderStatus.SHIPPED
        "out_for_delivery" -> OrderStatus.OUT_FOR_DELIVERY
        "delivered"        -> OrderStatus.DELIVERED
        "cancelled"        -> OrderStatus.CANCELLED
        else               -> OrderStatus.PENDING
    }
}


// ═══════════════════════════════════════════════════════
//   HOW TO USE in MainActivity
// ═══════════════════════════════════════════════════════

/*
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var realtimeManager: RealtimeManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Start realtime after auth check
        lifecycleScope.launch {
            realtimeManager.startListening()
        }

        // Collect events and show snackbars / update UI
        lifecycleScope.launch {
            realtimeManager.events.collect { event ->
                when (event) {
                    is RealtimeEvent.OrderStatusChanged -> {
                        // Show "Your order is now: Shipped!" snackbar
                    }
                    is RealtimeEvent.CartUpdated -> {
                        // Refresh cart count badge
                    }
                    is RealtimeEvent.NewNotification -> {
                        // Show in-app notification banner
                    }
                }
            }
        }

        setContent { KittysPremiumTheme { KittysNavGraph() } }
    }

    override fun onDestroy() {
        super.onDestroy()
        realtimeManager.stopListening()
    }
}
*/
