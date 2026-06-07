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
import java.util.UUID
import javax.inject.Inject

// ════════════════════════════════════════════════════════════════
//   MIKO — Escrow ViewModel (self-contained)
// ════════════════════════════════════════════════════════════════

data class EscrowUiState(
    val escrow: EscrowTransaction? = null,
    val isLoading: Boolean = false,
    val isSubmitting: Boolean = false,
    val error: String? = null,
    val success: String? = null
)

@HiltViewModel
class EscrowViewModel @Inject constructor(
    // private val escrowRepository: EscrowRepository  // wire real repo later
) : ViewModel() {

    private val _uiState = MutableStateFlow(EscrowUiState())
    val uiState: StateFlow<EscrowUiState> = _uiState.asStateFlow()

    fun loadOrder(orderId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            delay(400) // simulate fetch
            _uiState.update {
                it.copy(
                    isLoading = false,
                    escrow = sampleEscrow(orderId)
                )
            }
        }
    }

    /** Customer confirms delivery — releases money to vendor. */
    fun confirmDelivery(
        orderId: String,
        rating: Int,
        photoUrls: List<String>,
        notes: String
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, error = null) }
            delay(900) // simulate network

            val proof = DeliveryProof(
                photoUrls = photoUrls,
                confirmedAt = System.currentTimeMillis(),
                rating = rating,
                notes = notes
            )
            // TODO: escrowRepository.confirmDelivery(orderId, proof)
            _uiState.update { state ->
                state.copy(
                    isSubmitting = false,
                    success = "Payment released to vendor! 🎉",
                    escrow = state.escrow?.copy(
                        status = EscrowStatus.RELEASED_TO_VENDOR,
                        confirmationProof = proof,
                        releasedAt = System.currentTimeMillis()
                    )
                )
            }
        }
    }

    /** Customer reports damaged/wrong product — opens dispute. */
    fun reportDamage(
        orderId: String,
        type: DamageType,
        description: String,
        photoUrls: List<String>,
        refundRequested: Boolean,
        videoUrl: String? = null
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, error = null) }
            delay(900)

            val report = DamageReport(
                id = "RPT-${UUID.randomUUID().toString().take(8)}",
                orderId = orderId,
                customerId = "",  // filled from auth in real repo
                type = type,
                description = description,
                photoUrls = photoUrls,
                videoUrl = videoUrl,
                reportedAt = System.currentTimeMillis(),
                refundRequested = refundRequested
            )
            // TODO: escrowRepository.reportDamage(orderId, report)
            _uiState.update { state ->
                state.copy(
                    isSubmitting = false,
                    success = "Report submitted for review.",
                    escrow = state.escrow?.copy(
                        status = EscrowStatus.DISPUTED,
                        damageReport = report
                    )
                )
            }
        }
    }

    fun clearMessages() = _uiState.update { it.copy(error = null, success = null) }

    // ─── Sample data ───
    private fun sampleEscrow(orderId: String): EscrowTransaction {
        val now = System.currentTimeMillis()
        val deliveredAt = now - (2 * 24 * 60 * 60 * 1000L)         // delivered 2 days ago
        val autoRelease = deliveredAt + (7 * 24 * 60 * 60 * 1000L) // +7 days
        return EscrowTransaction(
            id = "ESC-${orderId.take(6)}",
            orderId = orderId,
            customerId = "cust_1",
            vendorId = "vendor_1",
            amountLkr = 5400,
            platformFeeLkr = 270,        // 5%
            vendorPayoutLkr = 5130,
            status = EscrowStatus.AWAITING_CONFIRMATION,
            createdAt = now - (4 * 24 * 60 * 60 * 1000L),
            deliveredAt = deliveredAt,
            autoReleaseAt = autoRelease
        )
    }
}