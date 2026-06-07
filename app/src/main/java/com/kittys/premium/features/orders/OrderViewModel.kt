package com.kittys.premium.features.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kittys.premium.domain.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// ════════════════════════════════════════════════════════════════
//   MIKO — Order ViewModel (self-contained, sample data)
//   Powers Order History + Order Detail + Tracking
// ════════════════════════════════════════════════════════════════

/** A tracking step shown on the order timeline. */
data class TrackingStep(
    val title: String,
    val description: String,
    val timestamp: String,
    val isDone: Boolean,
    val isCurrent: Boolean = false
)

/** A lightweight order summary for the history list + detail. */
data class CustomerOrder(
    val id: String,
    val items: List<OrderItem>,
    val status: OrderStatus,
    val totalLkr: Int,
    val placedAt: String,
    val vendorName: String,
    val escrow: EscrowTransaction,
    val tracking: List<TrackingStep>,
    val driverName: String? = null,
    val driverEtaMinutes: Int? = null
) {
    val itemCount: Int get() = items.sumOf { it.quantity }
    val firstItemName: String get() = items.firstOrNull()?.name ?: "Order"
}

data class OrderListState(
    val isLoading: Boolean = false,
    val orders: List<CustomerOrder> = emptyList(),
    val filter: OrderStatus? = null
)

@HiltViewModel
class OrderViewModel @Inject constructor() : ViewModel() {

    private val _listState = MutableStateFlow(OrderListState())
    val listState: StateFlow<OrderListState> = _listState.asStateFlow()

    private val _selectedOrder = MutableStateFlow<CustomerOrder?>(null)
    val selectedOrder: StateFlow<CustomerOrder?> = _selectedOrder.asStateFlow()

    init { loadOrders() }

    fun loadOrders() {
        viewModelScope.launch {
            _listState.update { it.copy(isLoading = true) }
            delay(300)
            _listState.update { it.copy(isLoading = false, orders = sampleOrders()) }
        }
    }

    fun setFilter(status: OrderStatus?) = _listState.update { it.copy(filter = status) }

    fun filteredOrders(): List<CustomerOrder> {
        val s = _listState.value
        return if (s.filter == null) s.orders else s.orders.filter { it.status == s.filter }
    }

    fun selectOrder(orderId: String) {
        _selectedOrder.value = _listState.value.orders.find { it.id == orderId }
            ?: sampleOrders().find { it.id == orderId }
    }

    // ════════════════════════════════════════════════════
    //   SAMPLE DATA
    // ════════════════════════════════════════════════════
    private fun sampleOrders(): List<CustomerOrder> {
        val now = System.currentTimeMillis()
        val day = 24 * 60 * 60 * 1000L

        return listOf(
            // 1) Out for delivery — has driver ETA
            CustomerOrder(
                id = "MIKO12345",
                items = listOf(
                    OrderItem("p1", "Lavender Bow Party Dress", "", "4Y", "Pink", 2850, 1)
                ),
                status = OrderStatus.OUT_FOR_DELIVERY,
                totalLkr = 2850,
                placedAt = "Today, 9:14 AM",
                vendorName = "Little Stars Fashion",
                escrow = EscrowTransaction(
                    id = "ESC-12345", orderId = "MIKO12345",
                    customerId = "c1", vendorId = "v1",
                    amountLkr = 2850, platformFeeLkr = 143, vendorPayoutLkr = 2707,
                    status = EscrowStatus.HELD, createdAt = now - day
                ),
                tracking = listOf(
                    TrackingStep("Order Placed", "We received your order", "9:14 AM", true),
                    TrackingStep("Confirmed", "Seller accepted your order", "9:40 AM", true),
                    TrackingStep("Shipped", "Package handed to courier", "11:20 AM", true),
                    TrackingStep("Out for Delivery", "Arriving soon", "Now", true, isCurrent = true),
                    TrackingStep("Delivered", "Package delivered", "—", false)
                ),
                driverName = "Kasun",
                driverEtaMinutes = 25
            ),
            // 2) Awaiting confirmation — escrow countdown active
            CustomerOrder(
                id = "MIKO12344",
                items = listOf(
                    OrderItem("p4", "Newborn Soft Bodysuit Set", "", "3-6M", "White", 2450, 2)
                ),
                status = OrderStatus.DELIVERED,
                totalLkr = 4900,
                placedAt = "2 days ago",
                vendorName = "Baby Boutique",
                escrow = EscrowTransaction(
                    id = "ESC-12344", orderId = "MIKO12344",
                    customerId = "c1", vendorId = "v4",
                    amountLkr = 4900, platformFeeLkr = 245, vendorPayoutLkr = 4655,
                    status = EscrowStatus.AWAITING_CONFIRMATION,
                    createdAt = now - 4 * day,
                    deliveredAt = now - 2 * day,
                    autoReleaseAt = now + 5 * day
                ),
                tracking = listOf(
                    TrackingStep("Order Placed", "We received your order", "4 days ago", true),
                    TrackingStep("Confirmed", "Seller accepted", "4 days ago", true),
                    TrackingStep("Shipped", "Handed to courier", "3 days ago", true),
                    TrackingStep("Delivered", "Package delivered", "2 days ago", true, isCurrent = true)
                )
            ),
            // 3) Completed — released
            CustomerOrder(
                id = "MIKO12343",
                items = listOf(
                    OrderItem("p3", "Boys Denim Dungaree", "", "5Y", "Blue", 3200, 1)
                ),
                status = OrderStatus.DELIVERED,
                totalLkr = 3200,
                placedAt = "Last week",
                vendorName = "Tiny Trends",
                escrow = EscrowTransaction(
                    id = "ESC-12343", orderId = "MIKO12343",
                    customerId = "c1", vendorId = "v3",
                    amountLkr = 3200, platformFeeLkr = 160, vendorPayoutLkr = 3040,
                    status = EscrowStatus.RELEASED_TO_VENDOR,
                    createdAt = now - 10 * day,
                    deliveredAt = now - 8 * day,
                    releasedAt = now - 7 * day
                ),
                tracking = listOf(
                    TrackingStep("Order Placed", "Received", "10 days ago", true),
                    TrackingStep("Confirmed", "Accepted", "10 days ago", true),
                    TrackingStep("Shipped", "Couriered", "9 days ago", true),
                    TrackingStep("Delivered", "Delivered", "8 days ago", true),
                    TrackingStep("Completed", "Payment released", "7 days ago", true, isCurrent = true)
                )
            )
        )
    }
}