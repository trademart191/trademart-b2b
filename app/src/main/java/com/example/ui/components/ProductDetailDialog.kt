package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.model.B2BProduct
import com.example.model.ProformaInvoice
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ProductDetailDialog(
  product: B2BProduct,
  initialIsSample: Boolean = false,
  onDismiss: () -> Unit,
  onProceedToProforma: (ProformaInvoice) -> Unit
) {
  val context = LocalContext.current
  var isSampleOrder by remember { mutableStateOf(initialIsSample) }
  var quantity by remember {
    mutableIntStateOf(if (initialIsSample) product.sampleMoq else product.moq)
  }

  // Buyer Info fields
  var buyerName by remember { mutableStateOf("Rajesh Gupta") }
  var buyerBusiness by remember { mutableStateOf("Gupta General Retail Store") }
  var deliveryCity by remember { mutableStateOf("Guwahati") }

  // Calculation logic
  val isTieredApplied by remember {
    derivedStateOf {
      !isSampleOrder && product.hasTieredPricing && product.tieredPrice100Plus != null && quantity >= 100
    }
  }

  val effectiveUnitPrice by remember {
    derivedStateOf {
      if (isTieredApplied && product.tieredPrice100Plus != null) {
        product.tieredPrice100Plus
      } else {
        product.basePrice
      }
    }
  }

  val subtotal by remember {
    derivedStateOf { quantity * effectiveUnitPrice }
  }

  val gstAmount by remember {
    derivedStateOf { subtotal * 0.18 } // 18% GST standard on wholesale invoices
  }

  val grandTotal by remember {
    derivedStateOf { subtotal + gstAmount }
  }

  val isBelowMoqWarning by remember {
    derivedStateOf {
      !isSampleOrder && quantity < product.moq
    }
  }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Card(
      shape = RoundedCornerShape(24.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
      modifier = Modifier
        .fillMaxWidth(0.95f)
        .padding(vertical = 16.dp)
        .testTag("product_detail_dialog")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState())
          .padding(20.dp)
      ) {
        // Header with Close
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "B2B Product Details",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
          IconButton(
            onClick = onDismiss,
            modifier = Modifier.testTag("close_detail_dialog_button")
          ) {
            Icon(Icons.Default.Close, contentDescription = "Close")
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Product Banner
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
          AsyncImage(
            model = ImageRequest.Builder(context)
              .data(product.imageUrl)
              .crossfade(true)
              .build(),
            contentDescription = product.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
          )

          // City badge overlay
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color(0xEEFFFFFF),
            modifier = Modifier
              .padding(10.dp)
              .align(Alignment.TopEnd)
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(13.dp))
              Spacer(modifier = Modifier.width(3.dp))
              Text(product.location, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Title & Description
        Text(
          text = product.name,
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
          text = product.description.ifEmpty { "High-grade wholesale factory batch. Inspected for commercial grade retail redistribution." },
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Wholesaler Info Card
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(42.dp)
                .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.Store, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = product.shopName,
                  fontWeight = FontWeight.Bold,
                  fontSize = 14.sp
                )
                if (product.isVerified) {
                  Spacer(modifier = Modifier.width(4.dp))
                  Icon(
                    Icons.Default.Verified,
                    contentDescription = "Verified",
                    tint = Color(0xFF2563EB),
                    modifier = Modifier.size(14.dp)
                  )
                }
              }
              Text(
                text = "Wholesale Supplier • WhatsApp: +${product.whatsappNumber}",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Mode Selector: Regular Bulk vs Sample Batch
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Button(
            onClick = {
              isSampleOrder = false
              if (quantity < product.moq) quantity = product.moq
            },
            colors = ButtonDefaults.buttonColors(
              containerColor = if (!isSampleOrder) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
              contentColor = if (!isSampleOrder) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
            ),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
              .weight(1f)
              .testTag("bulk_order_mode_button")
          ) {
            Text("🏭 Bulk Wholesale (MOQ ${product.moq})", fontSize = 12.sp, fontWeight = FontWeight.Bold)
          }

          OutlinedButton(
            onClick = {
              isSampleOrder = true
              quantity = product.sampleMoq
            },
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.outlinedButtonColors(
              containerColor = if (isSampleOrder) Color(0xFFDBEAFE) else Color.Transparent,
              contentColor = if (isSampleOrder) Color(0xFF1D4ED8) else MaterialTheme.colorScheme.onSurfaceVariant
            ),
            modifier = Modifier
              .weight(1f)
              .testTag("sample_order_mode_button")
          ) {
            Text("📦 Sample (${product.sampleMoq} pcs)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Price Tiers Card
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = Color(0xFFF8FAFC),
          modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
            .padding(12.dp)
        ) {
          Column {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text("Tier 1 (MOQ ${product.moq} - 99 pcs):", fontSize = 12.sp, color = Color(0xFF475569))
              Text("₹${product.basePrice.toInt()} / pc", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }

            if (product.hasTieredPricing && product.tieredPrice100Plus != null) {
              Spacer(modifier = Modifier.height(6.dp))
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(Icons.Default.TrendingDown, contentDescription = null, tint = Color(0xFFB45309), modifier = Modifier.size(14.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("Tier 2 (100+ pcs Slab):", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFFB45309))
                }
                Text("₹${product.tieredPrice100Plus.toInt()} / pc", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF92400E))
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Interactive Quantity Selector
        Text(
          text = if (isSampleOrder) "Sample Batch Quantity" else "Order Quantity (Pieces)",
          style = MaterialTheme.typography.titleSmall,
          fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
              .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
              .padding(4.dp)
          ) {
            IconButton(
              onClick = {
                val step = if (isSampleOrder) 1 else 10
                if (quantity > step) quantity -= step
              },
              modifier = Modifier.testTag("decrement_quantity_button")
            ) {
              Icon(Icons.Default.Remove, contentDescription = "Decrease")
            }

            Text(
              text = "$quantity",
              fontSize = 20.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 16.dp)
            )

            IconButton(
              onClick = {
                val step = if (isSampleOrder) 1 else 10
                quantity += step
              },
              modifier = Modifier.testTag("increment_quantity_button")
            ) {
              Icon(Icons.Default.Add, contentDescription = "Increase")
            }
          }

          // Quick Preset Buttons
          Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            if (!isSampleOrder) {
              OutlinedButton(
                onClick = { quantity = product.moq },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("preset_moq_button")
              ) {
                Text("${product.moq} pcs", fontSize = 11.sp)
              }

              if (product.hasTieredPricing) {
                Button(
                  onClick = { quantity = 100 },
                  shape = RoundedCornerShape(8.dp),
                  colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                  modifier = Modifier.testTag("preset_100_slab_button")
                ) {
                  Text("100+ pcs", fontSize = 11.sp)
                }
              }
            }
          }
        }

        // MOQ Alert Warning (Crucial requirement!)
        AnimatedVisibility(visible = isBelowMoqWarning) {
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = Color(0xFFFEF2F2),
            modifier = Modifier
              .fillMaxWidth()
              .padding(top = 10.dp)
              .border(1.dp, Color(0xFFFECACA), RoundedCornerShape(10.dp))
          ) {
            Row(
              modifier = Modifier.padding(10.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Default.WarningAmber,
                contentDescription = "Warning",
                tint = Color(0xFFDC2626),
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Minimum Order Quantity (MOQ) is ${product.moq} pcs. Wholesalers do not fulfill regular orders below MOQ. Or toggle 'Sample' for a small test batch.",
                color = Color(0xFFB91C1C),
                fontSize = 11.sp,
                lineHeight = 15.sp
              )
            }
          }
        }

        // Tier discount notification banner
        AnimatedVisibility(visible = isTieredApplied) {
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = Color(0xFFFEF3C7),
            modifier = Modifier
              .fillMaxWidth()
              .padding(top = 10.dp)
              .border(1.dp, Color(0xFFFDE68A), RoundedCornerShape(10.dp))
          ) {
            Row(
              modifier = Modifier.padding(10.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Default.TrendingDown,
                contentDescription = "Discount Applied",
                tint = Color(0xFFB45309),
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              val perPcSaving = product.basePrice - (product.tieredPrice100Plus ?: product.basePrice)
              val totalSaving = (perPcSaving * quantity).toInt()
              Text(
                text = "🎉 Tier-2 Slab discount applied! You save ₹${product.tieredPrice100Plus?.toInt()} vs ₹${product.basePrice.toInt()} (Total savings: ₹$totalSaving)",
                color = Color(0xFF92400E),
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(18.dp))
        Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
        Spacer(modifier = Modifier.height(16.dp))

        // Buyer Delivery Information Form
        Text(
          text = "Buyer Proforma Delivery Details",
          style = MaterialTheme.typography.titleSmall,
          fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = buyerName,
          onValueChange = { buyerName = it },
          label = { Text("Buyer Name / Contact Person") },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("buyer_name_input"),
          singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = buyerBusiness,
          onValueChange = { buyerBusiness = it },
          label = { Text("Retail Shop / Firm Name") },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("buyer_business_input"),
          singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = deliveryCity,
          onValueChange = { deliveryCity = it },
          label = { Text("Delivery City / Destination") },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("delivery_city_input"),
          singleLine = true
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Price Summary Box
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text("Unit Wholesale Rate:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
              Text("₹${effectiveUnitPrice.toInt()} x $quantity pcs", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text("Subtotal:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
              Text("₹${subtotal.toInt()}", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text("Estimated GST (18% B2B):", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
              Text("₹${gstAmount.toInt()}", fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(6.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text("Estimated Total Order:", fontSize = 14.sp, fontWeight = FontWeight.Bold)
              Text(
                "₹${grandTotal.toInt()}",
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Action Button: Send WhatsApp Inquiry / Proforma
        Button(
          onClick = {
            val dateStr = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
            val invoiceNo = "PI-${(1000..9999).random()}"
            val invoice = ProformaInvoice(
              invoiceNumber = invoiceNo,
              date = dateStr,
              product = product,
              quantity = quantity,
              unitPrice = effectiveUnitPrice,
              isTieredApplied = isTieredApplied,
              isSampleOrder = isSampleOrder,
              buyerName = buyerName.ifBlank { "Retail Buyer" },
              buyerBusiness = buyerBusiness.ifBlank { "Retail Store" },
              deliveryCity = deliveryCity.ifBlank { product.location },
              subtotal = subtotal,
              gstAmount = gstAmount,
              grandTotal = grandTotal
            )
            onProceedToProforma(invoice)
          },
          enabled = !isBelowMoqWarning || isSampleOrder,
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF15803D)), // Rich WhatsApp / Commerce Green
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .testTag("generate_proforma_whatsapp_button")
        ) {
          Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = Color.White)
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Generate WhatsApp Proforma & Order",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = Color.White
          )
        }
      }
    }
  }
}
