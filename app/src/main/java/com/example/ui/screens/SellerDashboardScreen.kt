package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Factory
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.model.B2BProduct
import com.example.model.ProductCategory
import com.example.ui.components.QrCodeView
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerDashboardScreen(
  products: List<B2BProduct>,
  onAddProduct: (B2BProduct) -> Unit,
  onUpdateProduct: (B2BProduct) -> Unit,
  onDeleteProduct: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current

  // Store information
  var shopName by remember { mutableStateOf("Surat Global Textiles Ltd") }
  var shopCity by remember { mutableStateOf("Surat") }
  var whatsappPhone by remember { mutableStateOf("919811223344") }

  val storeSlug = remember(shopName) {
    shopName.lowercase().replace(" ", "-").replace("[^a-z0-9-]".toRegex(), "")
  }
  val storeUrl = "https://marketb2b.in/store/$storeSlug"

  // Product form state
  var editingProductId by remember { mutableStateOf<String?>(null) }
  var formProductName by remember { mutableStateOf("") }
  var formCategory by remember { mutableStateOf(ProductCategory.CLOTHES_FASHION) }
  var isCategoryDropdownOpen by remember { mutableStateOf(false) }
  var formBasePrice by remember { mutableStateOf("") }
  var formMoq by remember { mutableStateOf("") }
  var formHasTieredPricing by remember { mutableStateOf(false) }
  var formTieredPrice by remember { mutableStateOf("") }
  var formLocation by remember { mutableStateOf("Surat") }
  var formImageUrl by remember {
    mutableStateOf("https://images.unsplash.com/photo-1521572267360-ee0c2909d518?w=600&auto=format&fit=crop&q=80")
  }
  var formDescription by remember { mutableStateOf("") }
  var formErrorMessage by remember { mutableStateOf<String?>(null) }

  // Delete confirmation dialog
  var productToDeleteId by remember { mutableStateOf<String?>(null) }

  val sellerProducts = remember(products) {
    products.filter { it.isSellerOwned || it.shopName.equals(shopName, ignoreCase = true) }
  }

  fun resetForm() {
    editingProductId = null
    formProductName = ""
    formBasePrice = ""
    formMoq = ""
    formHasTieredPricing = false
    formTieredPrice = ""
    formDescription = ""
    formErrorMessage = null
  }

  fun populateFormForEdit(product: B2BProduct) {
    editingProductId = product.id
    formProductName = product.name
    formCategory = product.category
    formBasePrice = product.basePrice.toString()
    formMoq = product.moq.toString()
    formHasTieredPricing = product.hasTieredPricing
    formTieredPrice = product.tieredPrice100Plus?.toString() ?: ""
    formLocation = product.location
    formImageUrl = product.imageUrl
    formDescription = product.description
    formErrorMessage = null
  }

  fun submitProductForm() {
    if (formProductName.isBlank()) {
      formErrorMessage = "Product Name is required"
      return
    }
    val price = formBasePrice.toDoubleOrNull()
    if (price == null || price <= 0.0) {
      formErrorMessage = "Please enter a valid wholesale base price"
      return
    }
    val moq = formMoq.toIntOrNull()
    if (moq == null || moq <= 0) {
      formErrorMessage = "Please enter a valid Minimum Order Quantity (MOQ)"
      return
    }

    val tieredPrice = if (formHasTieredPricing) formTieredPrice.toDoubleOrNull() else null
    if (formHasTieredPricing && (tieredPrice == null || tieredPrice >= price)) {
      formErrorMessage = "100+ pcs slab price must be lower than base rate (₹$price)"
      return
    }

    formErrorMessage = null

    if (editingProductId != null) {
      val updated = B2BProduct(
        id = editingProductId!!,
        name = formProductName.trim(),
        category = formCategory,
        basePrice = price,
        moq = moq,
        hasTieredPricing = formHasTieredPricing,
        tieredPrice100Plus = tieredPrice,
        location = formLocation.ifBlank { shopCity },
        shopName = shopName,
        whatsappNumber = whatsappPhone,
        imageUrl = formImageUrl,
        isVerified = true,
        sampleMoq = maxOf(2, moq / 5),
        description = formDescription.trim(),
        isSellerOwned = true
      )
      onUpdateProduct(updated)
      Toast.makeText(context, "Product updated successfully!", Toast.LENGTH_SHORT).show()
    } else {
      val newProduct = B2BProduct(
        id = "seller-prod-${System.currentTimeMillis()}",
        name = formProductName.trim(),
        category = formCategory,
        basePrice = price,
        moq = moq,
        hasTieredPricing = formHasTieredPricing,
        tieredPrice100Plus = tieredPrice,
        location = formLocation.ifBlank { shopCity },
        shopName = shopName,
        whatsappNumber = whatsappPhone,
        imageUrl = formImageUrl,
        isVerified = true,
        sampleMoq = maxOf(2, moq / 5),
        description = formDescription.trim(),
        isSellerOwned = true
      )
      onAddProduct(newProduct)
      Toast.makeText(context, "Product uploaded to catalog!", Toast.LENGTH_SHORT).show()
    }
    resetForm()
  }

  fun shareStoreOnWhatsApp() {
    val shareMsg = """
*🏭 Welcome to $shopName's Digital Wholesale Storefront!*
━━━━━━━━━━━━━━━━━━━━━━━━━━━━
Browse our factory catalog, wholesale rates & slab discounts online:
$storeUrl

📍 Factory Location: $shopCity
📞 WhatsApp Inquiries: +$whatsappPhone
""".trimIndent()

    try {
      val encoded = URLEncoder.encode(shareMsg, StandardCharsets.UTF_8.toString())
      val url = "https://wa.me/?text=$encoded"
      val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
      context.startActivity(intent)
    } catch (e: Exception) {
      val sendIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, shareMsg)
      }
      context.startActivity(Intent.createChooser(sendIntent, "Share Wholesale Store"))
    }
  }

  fun copyStoreLink() {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("B2B Store Link", storeUrl)
    clipboard.setPrimaryClip(clip)
    Toast.makeText(context, "Digital Store link copied!", Toast.LENGTH_SHORT).show()
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .padding(horizontal = 16.dp),
    contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // 1. Digital Storefront & QR Code Banner
    item {
      Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)), // Rich slate dark
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("seller_storefront_card")
      ) {
        Column(modifier = Modifier.padding(20.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = Color(0xFF0F766E).copy(alpha = 0.3f),
              border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF14B8A6))
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.Factory,
                  contentDescription = null,
                  tint = Color(0xFF2DD4BF),
                  modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "Digital Storefront & QR Tools",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF2DD4BF)
                )
              }
            }

            Text(
              text = "Live Store",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF94A3B8)
            )
          }

          Spacer(modifier = Modifier.height(14.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
          ) {
            // Interactive QR Code Canvas
            QrCodeView(
              data = storeUrl,
              size = 120.dp,
              modifier = Modifier.testTag("store_qr_code")
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = shopName,
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
              )
              Text(
                text = "📍 $shopCity • WhatsApp: +$whatsappPhone",
                fontSize = 11.sp,
                color = Color(0xFF94A3B8)
              )
              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = "Scan to view digital catalog on mobile. Instant B2B inquiries routed to your WhatsApp.",
                fontSize = 10.sp,
                color = Color(0xFFCBD5E1),
                lineHeight = 14.sp
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Generated Link Row
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFF1E293B),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
              ) {
                Icon(
                  imageVector = Icons.Default.Link,
                  contentDescription = null,
                  tint = Color(0xFF38BDF8),
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = storeUrl,
                  fontSize = 11.sp,
                  color = Color(0xFFBAE6FD),
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
              }
              TextButton(
                onClick = { copyStoreLink() },
                modifier = Modifier.testTag("copy_store_url_button")
              ) {
                Text("Copy", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF38BDF8))
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Share Actions
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Button(
              onClick = { shareStoreOnWhatsApp() },
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier
                .weight(1f)
                .height(44.dp)
                .testTag("share_store_whatsapp_button")
            ) {
              Icon(Icons.Default.Share, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Share on WhatsApp", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
          }
        }
      }
    }

    // 2. Product Upload / Edit Form
    item {
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("product_upload_form_card")
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = if (editingProductId != null) "Edit Wholesale Product" else "Upload New Wholesale Product",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "Add factory rates, minimum quantity & tiered slab pricing",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }

            if (editingProductId != null) {
              TextButton(onClick = { resetForm() }) {
                Text("Cancel", color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
              }
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Image Preview and Preset Selector
          Text("Product Image (Instant Preview)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
          Spacer(modifier = Modifier.height(6.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
              AsyncImage(
                model = ImageRequest.Builder(context)
                  .data(formImageUrl)
                  .crossfade(true)
                  .build(),
                contentDescription = "Image preview",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
              )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
              Text("Select Category Sample Image:", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
              Spacer(modifier = Modifier.height(4.dp))
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                listOf(
                  "T-Shirts" to "https://images.unsplash.com/photo-1521572267360-ee0c2909d518?w=600&auto=format&fit=crop&q=80",
                  "Chargers" to "https://images.unsplash.com/photo-1583863788434-e58a36330cf0?w=600&auto=format&fit=crop&q=80",
                  "Footwear" to "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=600&auto=format&fit=crop&q=80",
                  "Cosmetics" to "https://images.unsplash.com/photo-1556228720-195a672e8a03?w=600&auto=format&fit=crop&q=80",
                  "Kitchen" to "https://images.unsplash.com/photo-1602143407151-7111542de6e8?w=600&auto=format&fit=crop&q=80"
                ).forEach { (label, url) ->
                  Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (formImageUrl == url) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.clickable { formImageUrl = url }
                  ) {
                    Text(
                      text = label,
                      fontSize = 11.sp,
                      fontWeight = if (formImageUrl == url) FontWeight.Bold else FontWeight.Normal,
                      modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                  }
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Product Name
          OutlinedTextField(
            value = formProductName,
            onValueChange = { formProductName = it },
            label = { Text("Product Name *") },
            placeholder = { Text("e.g. 65W GaN Fast Charger Adapters") },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("product_name_input"),
            singleLine = true
          )

          Spacer(modifier = Modifier.height(10.dp))

          // Category Dropdown
          ExposedDropdownMenuBox(
            expanded = isCategoryDropdownOpen,
            onExpandedChange = { isCategoryDropdownOpen = !isCategoryDropdownOpen }
          ) {
            OutlinedTextField(
              value = formCategory.displayName,
              onValueChange = {},
              readOnly = true,
              label = { Text("Category *") },
              trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isCategoryDropdownOpen) },
              modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
                .testTag("category_dropdown")
            )

            ExposedDropdownMenu(
              expanded = isCategoryDropdownOpen,
              onDismissRequest = { isCategoryDropdownOpen = false }
            ) {
              ProductCategory.values().filter { it != ProductCategory.ALL }.forEach { cat ->
                DropdownMenuItem(
                  text = { Text(cat.displayName) },
                  onClick = {
                    formCategory = cat
                    isCategoryDropdownOpen = false
                  }
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Base Price & MOQ
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            OutlinedTextField(
              value = formBasePrice,
              onValueChange = { formBasePrice = it },
              label = { Text("Base Price (₹/pc) *") },
              placeholder = { Text("₹180") },
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
              modifier = Modifier
                .weight(1f)
                .testTag("base_price_input"),
              singleLine = true
            )

            OutlinedTextField(
              value = formMoq,
              onValueChange = { formMoq = it },
              label = { Text("MOQ (Pieces) *") },
              placeholder = { Text("50") },
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
              modifier = Modifier
                .weight(1f)
                .testTag("moq_input"),
              singleLine = true
            )
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Slab / Tiered Pricing Toggle Card
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFFFEF3C7).copy(alpha = 0.5f),
            modifier = Modifier
              .fillMaxWidth()
              .border(1.dp, Color(0xFFFDE68A), RoundedCornerShape(12.dp))
          ) {
            Column(modifier = Modifier.padding(12.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(Icons.Default.TrendingDown, contentDescription = null, tint = Color(0xFFB45309), modifier = Modifier.size(18.dp))
                  Spacer(modifier = Modifier.width(6.dp))
                  Column {
                    Text("Enable 100+ Pieces Slab Pricing", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF92400E))
                    Text("Volume discount to attract bulk orders", fontSize = 10.sp, color = Color(0xFFB45309))
                  }
                }
                Switch(
                  checked = formHasTieredPricing,
                  onCheckedChange = { formHasTieredPricing = it },
                  colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFFD97706)),
                  modifier = Modifier.testTag("slab_pricing_switch")
                )
              }

              AnimatedVisibility(visible = formHasTieredPricing) {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                  OutlinedTextField(
                    value = formTieredPrice,
                    onValueChange = { formTieredPrice = it },
                    label = { Text("100+ Pcs Discounted Rate (₹ / pc)") },
                    placeholder = { Text("e.g. ₹155 (lower than base rate)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                      .fillMaxWidth()
                      .testTag("tiered_price_input"),
                    singleLine = true
                  )
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Location and Shop details
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            OutlinedTextField(
              value = formLocation,
              onValueChange = { formLocation = it },
              label = { Text("City / Location *") },
              modifier = Modifier
                .weight(1f)
                .testTag("seller_location_input"),
              singleLine = true
            )

            OutlinedTextField(
              value = whatsappPhone,
              onValueChange = { whatsappPhone = it },
              label = { Text("WhatsApp Phone *") },
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
              modifier = Modifier
                .weight(1f)
                .testTag("seller_whatsapp_input"),
              singleLine = true
            )
          }

          if (formErrorMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = formErrorMessage!!,
              color = MaterialTheme.colorScheme.error,
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold
            )
          }

          Spacer(modifier = Modifier.height(16.dp))

          Button(
            onClick = { submitProductForm() },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp)
              .testTag("submit_product_button")
          ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = if (editingProductId != null) "Update Wholesale Product" else "Publish Wholesale Product",
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp
            )
          }
        }
      }
    }

    // 3. My Uploaded Products List
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "My Uploaded Products (${sellerProducts.size})",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = "Manage Active Items",
          fontSize = 11.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }

    if (sellerProducts.isEmpty()) {
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Icon(Icons.Default.Storefront, contentDescription = null, tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(40.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text("No products uploaded yet", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text("Use the upload form above to add your first item.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
          }
        }
      }
    } else {
      items(sellerProducts, key = { it.id }) { product ->
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("seller_product_card_${product.id}")
        ) {
          Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            AsyncImage(
              model = ImageRequest.Builder(context).data(product.imageUrl).crossfade(true).build(),
              contentDescription = product.name,
              contentScale = ContentScale.Crop,
              modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = product.name,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
              Text(
                text = "${product.category.displayName} • ${product.location}",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                Text(
                  text = "₹${product.basePrice.toInt()}/pc (MOQ ${product.moq})",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.primary
                )
                if (product.hasTieredPricing && product.tieredPrice100Plus != null) {
                  Text(
                    text = "100+: ₹${product.tieredPrice100Plus.toInt()}",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFD97706)
                  )
                }
              }
            }

            // Edit and Delete buttons
            Row {
              IconButton(
                onClick = { populateFormForEdit(product) },
                modifier = Modifier.testTag("edit_product_${product.id}")
              ) {
                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
              }
              IconButton(
                onClick = { productToDeleteId = product.id },
                modifier = Modifier.testTag("delete_product_${product.id}")
              ) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
              }
            }
          }
        }
      }
    }
  }

  // Delete Confirmation Alert
  if (productToDeleteId != null) {
    AlertDialog(
      onDismissRequest = { productToDeleteId = null },
      title = { Text("Delete Product", fontWeight = FontWeight.Bold) },
      text = { Text("Are you sure you want to delete this product from your wholesale catalog?") },
      confirmButton = {
        Button(
          onClick = {
            onDeleteProduct(productToDeleteId!!)
            productToDeleteId = null
            Toast.makeText(context, "Product removed from catalog", Toast.LENGTH_SHORT).show()
          },
          colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
          Text("Delete")
        }
      },
      dismissButton = {
        TextButton(onClick = { productToDeleteId = null }) {
          Text("Cancel")
        }
      }
    )
  }
}
