package com.kittys.premium.features.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// ════════════════════════════════════════════════════════════════
//   MIKO — Profile ViewModel
// ════════════════════════════════════════════════════════════════

data class ProfileState(
    val userName: String = "Amali Perera",
    val userEmail: String = "amali@email.com",
    val userInitial: String = "A",
    val avatarUrl: String? = null,

    // Quick stats (header grid)
    val ordersCount: Int = 12,
    val wishlistCount: Int = 8,
    val reviewsCount: Int = 5,
    val kidsCount: Int = 2,

    // Loyalty
    val loyaltyPoints: Int = 1280,

    // Seller status
    val isSeller: Boolean = false,
    val storeName: String = "",
    val sellerLevel: String = "",

    // Admin
    val isAdmin: Boolean = false,

    val isLoading: Boolean = false,
    val isLoggedOut: Boolean = false
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    // private val authRepository: AuthRepository,
    // private val sellerRepository: SellerRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    init { load() }

    private fun load() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            // TODO: real Supabase fetch
            // val user = authRepository.getCurrentUser()
            // val seller = sellerRepository.getSellerProfile(user.id)
            // _state.update { it.copy(
            //     userName = user.fullName,
            //     userEmail = user.email,
            //     userInitial = user.fullName.firstOrNull()?.uppercase().orEmpty(),
            //     isSeller = seller != null,
            //     storeName = seller?.storeName.orEmpty(),
            //     sellerLevel = seller?.level?.displayName.orEmpty(),
            //     isAdmin = user.role == "admin"
            // )}
            _state.update { it.copy(isLoading = false) }
        }
    }

    fun logout() {
        viewModelScope.launch {
            // TODO: authRepository.signOut()
            _state.update { it.copy(isLoggedOut = true) }
        }
    }
}