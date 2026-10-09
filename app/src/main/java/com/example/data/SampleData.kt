package com.example.data

import com.example.model.B2BProduct
import com.example.model.BuyerRequirement
import com.example.model.ProductCategory

object SampleData {
  val initialProducts: List<B2BProduct> = listOf(
    B2BProduct(
      id = "prod-1",
      name = "SuperFast 65W GaN Fast Charger Adapters (Type-C + USB-A)",
      category = ProductCategory.MOBILE_ACCESSORIES,
      basePrice = 180.0,
      moq = 50,
      hasTieredPricing = true,
      tieredPrice100Plus = 155.0,
      location = "Delhi",
      shopName = "Apex Mobile Electronics Hub",
      whatsappNumber = "919876543210",
      imageUrl = "https://images.unsplash.com/photo-1583863788434-e58a36330cf0?w=600&auto=format&fit=crop&q=80",
      isVerified = true,
      sampleMoq = 5,
      description = "BIS certified 65W GaN fast chargers. High-efficiency fast charging compatible with all modern smartphones and laptops. Master carton: 100 pcs.",
      isSellerOwned = false
    ),
    B2BProduct(
      id = "prod-2",
      name = "Pure Cotton Oversized Streetwear T-Shirts (220 GSM Bio-Washed)",
      category = ProductCategory.CLOTHES_FASHION,
      basePrice = 160.0,
      moq = 30,
      hasTieredPricing = true,
      tieredPrice100Plus = 135.0,
      location = "Surat",
      shopName = "Surat Global Textiles Ltd",
      whatsappNumber = "919811223344",
      imageUrl = "https://images.unsplash.com/photo-1521572267360-ee0c2909d518?w=600&auto=format&fit=crop&q=80",
      isVerified = true,
      sampleMoq = 5,
      description = "100% Combed Compact Cotton, pre-shrunk, drop-shoulder loose fit. Available in 8 trending pastel colors. Assorted sizing (M to XXL).",
      isSellerOwned = true // Owned by default active seller for quick demo!
    ),
    B2BProduct(
      id = "prod-3",
      name = "Lightweight Breathable Mesh Sports Sneakers (Assorted 6-10)",
      category = ProductCategory.SHOES_FOOTWEAR,
      basePrice = 320.0,
      moq = 24,
      hasTieredPricing = true,
      tieredPrice100Plus = 280.0,
      location = "Mumbai",
      shopName = "StepUp Footwear Manufacturers",
      whatsappNumber = "919822334455",
      imageUrl = "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=600&auto=format&fit=crop&q=80",
      isVerified = true,
      sampleMoq = 4,
      description = "Phylon sole with high-grip TPR inserts. Highly durable sports & casual sneakers. Individual branded box packaging included.",
      isSellerOwned = false
    ),
    B2BProduct(
      id = "prod-4",
      name = "Organic Pure Aloe Vera Soothing Gel (500ml Bulk Salon Tub)",
      category = ProductCategory.COSMETICS_PERSONAL_CARE,
      basePrice = 95.0,
      moq = 40,
      hasTieredPricing = true,
      tieredPrice100Plus = 78.0,
      location = "Bengaluru",
      shopName = "PureGlow Herbal Organics",
      whatsappNumber = "919833445566",
      imageUrl = "https://images.unsplash.com/photo-1556228720-195a672e8a03?w=600&auto=format&fit=crop&q=80",
      isVerified = true,
      sampleMoq = 10,
      description = "Cruelty-free, paraben-free 99% pure aloe vera extract. Sealed tamper-proof tubs suitable for retail resale, spas, and salons.",
      isSellerOwned = true
    ),
    B2BProduct(
      id = "prod-5",
      name = "Double-Wall Insulated Stainless Steel Flasks (750ml Hot & Cold)",
      category = ProductCategory.HOME_KITCHEN,
      basePrice = 125.0,
      moq = 50,
      hasTieredPricing = true,
      tieredPrice100Plus = 105.0,
      location = "Guwahati",
      shopName = "Brahmaputra Housewares Wholesale",
      whatsappNumber = "919844556677",
      imageUrl = "https://images.unsplash.com/photo-1602143407151-7111542de6e8?w=600&auto=format&fit=crop&q=80",
      isVerified = true,
      sampleMoq = 6,
      description = "SS 304 food-grade stainless steel with 24hr temperature retention. Matte powder coated finish in matte black, navy, and army green.",
      isSellerOwned = false
    )
  )

  val initialRequirements: List<BuyerRequirement> = listOf(
    BuyerRequirement(
      id = "rfq-1",
      buyerName = "Ramesh Sharma",
      businessName = "Sharma Retail Emporium",
      title = "Urgent: Need 500 pcs Cotton Polo Collared T-Shirts for Corporate Event",
      category = ProductCategory.CLOTHES_FASHION,
      quantityNeeded = 500,
      targetPricePerPiece = 140.0,
      location = "Guwahati",
      phone = "919871112233",
      notes = "Navy blue and black colors needed. Delivery within 10 days required in Fancy Bazaar.",
      postedTimeAgo = "15 mins ago"
    ),
    BuyerRequirement(
      id = "rfq-2",
      buyerName = "Anand Verma",
      businessName = "Verma Mobile Accessories",
      title = "Looking for 300 units 20W Type-C to Lightning Braided Fast Charging Cables",
      category = ProductCategory.MOBILE_ACCESSORIES,
      quantityNeeded = 300,
      targetPricePerPiece = 45.0,
      location = "Delhi",
      phone = "919899887766",
      notes = "Strictly 1-meter length with 6-month warranty replacement support.",
      postedTimeAgo = "2 hours ago"
    ),
    BuyerRequirement(
      id = "rfq-3",
      buyerName = "Sunita Patil",
      businessName = "Patil Home Essentials",
      title = "Requirement: 80 sets Non-Stick Granite Cookware (3-piece frypan + kadai)",
      category = ProductCategory.HOME_KITCHEN,
      quantityNeeded = 80,
      targetPricePerPiece = 420.0,
      location = "Mumbai",
      phone = "919822446688",
      notes = "Looking for induction bottom compatible sets with glass lids.",
      postedTimeAgo = "5 hours ago"
    )
  )

  val availableCities: List<String> = listOf(
    "All Cities",
    "Delhi",
    "Mumbai",
    "Guwahati",
    "Surat",
    "Bengaluru",
    "Kolkata",
    "Jaipur"
  )

  val defaultSellerShopName = "Surat Global Textiles Ltd"
  val defaultSellerPhone = "919811223344"
  val defaultSellerCity = "Surat"
}
