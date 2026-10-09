package com.example.model

enum class UserRole {
  BUYER,
  SELLER
}

enum class ProductCategory(val displayName: String) {
  ALL("All Categories"),
  MOBILE_ACCESSORIES("Mobile Accessories"),
  CLOTHES_FASHION("Clothes & Fashion"),
  SHOES_FOOTWEAR("Shoes & Footwear"),
  COSMETICS_PERSONAL_CARE("Cosmetics & Personal Care"),
  HOME_KITCHEN("Home & Kitchen Items"),
  OTHERS("Others")
}

data class B2BProduct(
  val id: String,
  val name: String,
  val category: ProductCategory,
  val basePrice: Double,
  val moq: Int,
  val hasTieredPricing: Boolean = false,
  val tieredPrice100Plus: Double? = null,
  val location: String,
  val shopName: String,
  val whatsappNumber: String,
  val imageUrl: String,
  val isVerified: Boolean = true,
  val sampleMoq: Int = 10,
  val description: String = "",
  val isSellerOwned: Boolean = false
)

data class BuyerRequirement(
  val id: String,
  val buyerName: String,
  val businessName: String,
  val title: String,
  val category: ProductCategory,
  val quantityNeeded: Int,
  val targetPricePerPiece: Double,
  val location: String,
  val phone: String,
  val notes: String = "",
  val postedTimeAgo: String = "Just now",
  val status: String = "Open for Quotes"
)

data class ProformaInvoice(
  val invoiceNumber: String,
  val date: String,
  val product: B2BProduct,
  val quantity: Int,
  val unitPrice: Double,
  val isTieredApplied: Boolean,
  val isSampleOrder: Boolean,
  val buyerName: String,
  val buyerBusiness: String,
  val deliveryCity: String,
  val subtotal: Double,
  val gstAmount: Double,
  val grandTotal: Double
)
