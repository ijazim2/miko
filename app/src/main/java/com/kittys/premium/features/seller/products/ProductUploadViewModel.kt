package com.kittys.premium.features.seller.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kittys.premium.domain.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// ════════════════════════════════════════════════════════════════
//   MIKO — Product Upload ViewModel
//   Manages all state across the 6-step wizard:
//     1 Basics → 2 Audience → 3 Variants → 4 Details → 5 Shipping → 6 Review
// ════════════════════════════════════════════════════════════════

data class ProductUploadState(
    val product       : ProductUpload = ProductUpload(),
    val currentStep   : Int           = 1,
    val isSubmitting  : Boolean       = false,
    val isPublished   : Boolean       = false,
    val isScheduled   : Boolean       = false,
    val error         : String?       = null,
    val draftSaved    : Boolean       = false
)

@HiltViewModel
class ProductUploadViewModel @Inject constructor(
    // private val productRepository: ProductUploadRepository  // inject when data layer ready
) : ViewModel() {

    private val _state = MutableStateFlow(ProductUploadState())
    val state: StateFlow<ProductUploadState> = _state.asStateFlow()

    private inline fun updateProduct(crossinline transform: (ProductUpload) -> ProductUpload) {
        _state.update { it.copy(product = transform(it.product), error = null) }
    }

    // ════════════════════════════════════════════════════
    //   STEP NAVIGATION
    // ════════════════════════════════════════════════════

    fun goToStep(step: Int) = _state.update { it.copy(currentStep = step.coerceIn(1, 6)) }
    fun nextStep()          = _state.update { it.copy(currentStep = (it.currentStep + 1).coerceAtMost(6)) }
    fun previousStep()      = _state.update { it.copy(currentStep = (it.currentStep - 1).coerceAtLeast(1)) }
    fun clearError()        = _state.update { it.copy(error = null) }

    // ════════════════════════════════════════════════════
    //   STEP 1 — BASICS
    // ════════════════════════════════════════════════════

    fun updateName(v: String)            = updateProduct { it.copy(name = v) }
    fun updateDescription(v: String)     = updateProduct { it.copy(description = v) }
    fun updateCategory(c: ProductCategory) = updateProduct { it.copy(category = c, subCategory = "") }
    fun updateSubCategory(s: String)     = updateProduct { it.copy(subCategory = s) }
    fun updateBrand(b: String)           = updateProduct { it.copy(brand = b) }

    fun isStep1Valid(): Boolean = _state.value.product.isStep1Valid()

    // ════════════════════════════════════════════════════
    //   STEP 2 — AUDIENCE
    // ════════════════════════════════════════════════════

    fun updateGender(g: ProductGender) = updateProduct { it.copy(gender = g) }

    fun toggleAgeGroup(a: AgeGroup) = updateProduct { p ->
        val updated = if (p.ageGroups.contains(a)) p.ageGroups - a else p.ageGroups + a
        p.copy(ageGroups = updated)
    }

    fun updateSeason(s: Season)       = updateProduct { it.copy(season = s) }
    fun updateStyle(s: ProductStyle)  = updateProduct { it.copy(style = s) }

    fun isStep2Valid(): Boolean = _state.value.product.isStep2Valid()

    // ════════════════════════════════════════════════════
    //   STEP 3 — VARIANTS
    // ════════════════════════════════════════════════════

    fun toggleMultiVariant(on: Boolean) = updateProduct {
        it.copy(hasVariants = on, variants = listOf(ProductVariant()))
    }

    fun addVariant() = updateProduct {
        if (it.variants.size >= 10) it
        else it.copy(variants = it.variants + ProductVariant())
    }

    fun removeVariant(variantId: String) = updateProduct {
        if (it.variants.size <= 1) it
        else it.copy(variants = it.variants.filterNot { v -> v.id == variantId })
    }

    fun updateVariantColor(variantId: String, color: VariantColor) =
        updateVariant(variantId) { it.copy(color = color) }

    fun addPhotoToVariant(variantId: String, photoUri: String) =
        updateVariant(variantId) {
            if (it.photoUris.size >= 5) it else it.copy(photoUris = it.photoUris + photoUri)
        }

    fun removePhotoFromVariant(variantId: String, photoUri: String) =
        updateVariant(variantId) {
            it.copy(photoUris = it.photoUris.filterNot { p -> p == photoUri })
        }

    fun toggleSizeForVariant(variantId: String, size: String) =
        updateVariant(variantId) { variant ->
            val current = variant.sizeStocks.toMutableMap()
            if (current.containsKey(size)) current.remove(size) else current[size] = 0
            variant.copy(sizeStocks = current)
        }

    fun updateSizeStock(variantId: String, size: String, stock: Int) =
        updateVariant(variantId) { variant ->
            val current = variant.sizeStocks.toMutableMap()
            current[size] = stock.coerceAtLeast(0)
            variant.copy(sizeStocks = current)
        }

    fun updateVariantPrice(variantId: String, priceLkr: Int) =
        updateVariant(variantId) { it.copy(priceLkr = priceLkr.coerceAtLeast(0)) }

    fun updateVariantOriginalPrice(variantId: String, original: Int) =
        updateVariant(variantId) { it.copy(originalPrice = original.coerceAtLeast(0)) }

    fun updateVariantSku(variantId: String, sku: String) =
        updateVariant(variantId) { it.copy(sku = sku) }

    /** Apply the same price to every variant (common case). */
    fun applyPriceToAllVariants(priceLkr: Int, originalPrice: Int) = updateProduct {
        it.copy(variants = it.variants.map { v ->
            v.copy(priceLkr = priceLkr, originalPrice = originalPrice)
        })
    }

    private fun updateVariant(variantId: String, transform: (ProductVariant) -> ProductVariant) =
        updateProduct {
            it.copy(variants = it.variants.map { v -> if (v.id == variantId) transform(v) else v })
        }

    fun isStep3Valid(): Boolean = _state.value.product.isStep3Valid()

    // ════════════════════════════════════════════════════
    //   STEP 4 — DETAILS
    // ════════════════════════════════════════════════════

    fun updateMaterial(v: String)          = updateProduct { it.copy(material = v) }
    fun updateFabricCare(f: FabricCare)    = updateProduct { it.copy(fabricCare = f) }
    fun updatePattern(p: Pattern)          = updateProduct { it.copy(pattern = p) }
    fun updateCountryOfOrigin(c: String)   = updateProduct { it.copy(countryOfOrigin = c) }

    fun toggleSafetyTag(tag: SafetyTag) = updateProduct { p ->
        val updated = if (p.safetyTags.contains(tag)) p.safetyTags - tag else p.safetyTags + tag
        p.copy(safetyTags = updated)
    }

    fun addCustomTag(tag: String) = updateProduct { p ->
        val cleaned = tag.trim().lowercase()
        if (cleaned.isBlank() || p.customTags.contains(cleaned)) p
        else p.copy(customTags = p.customTags + cleaned)
    }

    fun removeCustomTag(tag: String) = updateProduct {
        it.copy(customTags = it.customTags.filterNot { t -> t == tag })
    }

    // ════════════════════════════════════════════════════
    //   STEP 5 — SHIPPING
    // ════════════════════════════════════════════════════

    fun updateWeight(grams: Int) = updateProduct { it.copy(weightGrams = grams.coerceAtLeast(0)) }

    fun updateProcessingTime(p: ProcessingTime) = updateProduct { it.copy(processingTime = p) }

    fun toggleShippingMethod(m: ShippingMethod) = updateProduct { p ->
        val updated = if (p.shippingMethods.contains(m)) p.shippingMethods - m else p.shippingMethods + m
        p.copy(shippingMethods = updated)
    }

    fun toggleFreeShipping(enabled: Boolean, threshold: Int = 0) = updateProduct {
        it.copy(freeShipping = enabled, freeShippingAbove = if (enabled) threshold else 0)
    }

    fun toggleReturns(accepts: Boolean, days: Int = 7) = updateProduct {
        it.copy(acceptsReturns = accepts, returnDays = if (accepts) days else 0)
    }

    fun isStep5Valid(): Boolean = _state.value.product.isStep5Valid()

    // ════════════════════════════════════════════════════
    //   STEP 6 — REVIEW → PUBLISH / DRAFT / SCHEDULE
    // ════════════════════════════════════════════════════

    /** Assigns auto-generated SKUs to any variant missing one. */
    private fun finalizeSkus(product: ProductUpload): ProductUpload =
        product.copy(
            variants = product.variants.map { v ->
                if (v.sku.isBlank()) v.copy(sku = v.generateSku(product.baseSku)) else v
            }
        )

    fun saveDraft() {
        viewModelScope.launch {
            _state.update { it.copy(isSubmitting = true, error = null) }
            val finalized = finalizeSkus(_state.value.product).copy(status = ProductStatus.DRAFT)
            // TODO: productRepository.saveDraft(finalized)
            _state.update { it.copy(isSubmitting = false, draftSaved = true, product = finalized) }
        }
    }

    fun publishProduct() {
        val error = _state.value.product.validationError()
        if (error != null) {
            _state.update { it.copy(error = error) }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isSubmitting = true, error = null) }
            val finalized = finalizeSkus(_state.value.product).copy(status = ProductStatus.PENDING_REVIEW)
            // TODO: productRepository.publishProduct(finalized)
            _state.update { it.copy(isSubmitting = false, isPublished = true, product = finalized) }
        }
    }

    fun scheduleProduct(launchTimestamp: Long) {
        val error = _state.value.product.validationError()
        if (error != null) {
            _state.update { it.copy(error = error) }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isSubmitting = true, error = null) }
            val finalized = finalizeSkus(_state.value.product)
                .copy(status = ProductStatus.DRAFT, launchDate = launchTimestamp)
            // TODO: productRepository.scheduleProduct(finalized, launchTimestamp)
            _state.update { it.copy(isSubmitting = false, isScheduled = true, product = finalized) }
        }
    }
}