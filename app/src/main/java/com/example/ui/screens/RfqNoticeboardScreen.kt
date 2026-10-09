package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Send
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.BuyerRequirement
import com.example.model.ProductCategory
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RfqNoticeboardScreen(
  requirements: List<BuyerRequirement>,
  onPostRequirement: (BuyerRequirement) -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  var isPostDialogOpen by remember { mutableStateOf(false) }

  // Form state
  var formTitle by remember { mutableStateOf("") }
  var formCategory by remember { mutableStateOf(ProductCategory.CLOTHES_FASHION) }
  var isCategoryDropdownOpen by remember { mutableStateOf(false) }
  var formQty by remember { mutableStateOf("") }
  var formTargetPrice by remember { mutableStateOf("") }
  var formCity by remember { mutableStateOf("Guwahati") }
  var formBuyerName by remember { mutableStateOf("Sharma Retailers") }
  var formPhone by remember { mutableStateOf("919871112233") }
  var formNotes by remember { mutableStateOf("") }
  var formError by remember { mutableStateOf<String?>(null) }

  fun submitRfq() {
    if (formTitle.isBlank()) {
      formError = "Please describe the required item"
      return
    }
    val qty = formQty.toIntOrNull()
    if (qty == null || qty <= 0) {
      formError = "Please enter quantity needed"
      return
    }
    val targetPrice = formTargetPrice.toDoubleOrNull()
    if (targetPrice == null || targetPrice <= 0) {
      formError = "Please enter your target price per piece"
      return
    }

    val newReq = BuyerRequirement(
      id = "rfq-${System.currentTimeMillis()}",
      buyerName = formBuyerName.ifBlank { "Retail Buyer" },
      businessName = formBuyerName.ifBlank { "Retail Store" },
      title = formTitle.trim(),
      category = formCategory,
      quantityNeeded = qty,
      targetPricePerPiece = targetPrice,
      location = formCity.ifBlank { "India" },
      phone = formPhone.ifBlank { "919876543210" },
      notes = formNotes.trim(),
      postedTimeAgo = "Just now"
    )

    onPostRequirement(newReq)
    isPostDialogOpen = false
    formTitle = ""
    formQty = ""
    formTargetPrice = ""
    formNotes = ""
    formError = null
    Toast.makeText(context, "Requirement posted to public noticeboard!", Toast.LENGTH_SHORT).show()
  }

  fun sendWhatsAppQuote(req: BuyerRequirement) {
    val quoteMsg = """
*🏭 B2B WHOLESALER QUOTE RESPONSE*
━━━━━━━━━━━━━━━━━━━━━━━━━━━━
Hello ${req.buyerName}, I saw your RFQ on TradeMart B2B noticeboard:
*"${req.title}"*

• Quantity Needed: ${req.quantityNeeded} pieces
• Target Rate: ₹${req.targetPricePerPiece.toInt()}/pc
• Destination: ${req.location}

We are verified manufacturers and can supply this at competitive wholesale rates. Let's connect!
""".trimIndent()

    try {
      val encoded = URLEncoder.encode(quoteMsg, StandardCharsets.UTF_8.toString())
      val cleanPhone = req.phone.replace("+", "").replace(" ", "").trim()
      val url = "https://wa.me/$cleanPhone?text=$encoded"
      val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
      context.startActivity(intent)
    } catch (e: Exception) {
      val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, quoteMsg)
      }
      context.startActivity(Intent.createChooser(shareIntent, "Quote to Buyer via"))
    }
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
  ) {
    // Header Banner
    Surface(
      color = MaterialTheme.colorScheme.surface,
      shadowElevation = 2.dp,
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Buyer RFQ Noticeboard",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "Live bulk requirements from retailers across India",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        Button(
          onClick = { isPostDialogOpen = true },
          colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.testTag("open_post_rfq_dialog_button")
        ) {
          Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Post RFQ", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
      }
    }

    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp),
      contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      items(requirements, key = { it.id }) { req ->
        Card(
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("rfq_card_${req.id}")
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            // Category & Time
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Surface(
                shape = RoundedCornerShape(6.dp),
                color = MaterialTheme.colorScheme.primaryContainer
              ) {
                Text(
                  text = req.category.displayName,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.onPrimaryContainer,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
              }

              Text(
                text = req.postedTimeAgo,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Title
            Text(
              text = req.title,
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface,
              lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Key requirement stats
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(
                modifier = Modifier.padding(12.dp),
                horizontalArrangement = Arrangement.SpaceAround
              ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                  Text("Quantity Needed", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                  Text("${req.quantityNeeded} pcs", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                  Text("Target Rate", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                  Text("₹${req.targetPricePerPiece.toInt()} / pc", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                  Text("Destination", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(12.dp))
                    Text(req.location, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                  }
                }
              }
            }

            if (req.notes.isNotBlank()) {
              Spacer(modifier = Modifier.height(10.dp))
              Text(
                text = "\"${req.notes}\"",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
              )
            }

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(10.dp))

            // Buyer info & WhatsApp Quote Action
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text("Posted by:", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(req.buyerName, fontSize = 12.sp, fontWeight = FontWeight.Bold)
              }

              Button(
                onClick = { sendWhatsAppQuote(req) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("quote_whatsapp_button_${req.id}")
              ) {
                Icon(Icons.Default.Send, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Quote on WhatsApp", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      }
    }
  }

  // Post Requirement Modal Dialog
  if (isPostDialogOpen) {
    Dialog(
      onDismissRequest = { isPostDialogOpen = false },
      properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
      Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
          .fillMaxWidth(0.95f)
          .padding(vertical = 16.dp)
          .testTag("post_rfq_dialog")
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Post Buyer Requirement (RFQ)",
              style = MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.Bold
            )
            IconButton(onClick = { isPostDialogOpen = false }) {
              Icon(Icons.Default.Close, contentDescription = "Close")
            }
          }

          Text(
            text = "Broadcast your wholesale requirements to verified local suppliers",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Spacer(modifier = Modifier.height(14.dp))

          OutlinedTextField(
            value = formTitle,
            onValueChange = { formTitle = it },
            label = { Text("Item Needed / Requirement Title *") },
            placeholder = { Text("e.g. 500 pcs Cotton Polo Collared T-Shirts") },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("rfq_title_input"),
            singleLine = true
          )

          Spacer(modifier = Modifier.height(10.dp))

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

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            OutlinedTextField(
              value = formQty,
              onValueChange = { formQty = it },
              label = { Text("Quantity Needed *") },
              placeholder = { Text("300") },
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
              modifier = Modifier
                .weight(1f)
                .testTag("rfq_qty_input"),
              singleLine = true
            )

            OutlinedTextField(
              value = formTargetPrice,
              onValueChange = { formTargetPrice = it },
              label = { Text("Target Rate (₹/pc) *") },
              placeholder = { Text("₹140") },
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
              modifier = Modifier
                .weight(1f)
                .testTag("rfq_price_input"),
              singleLine = true
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            OutlinedTextField(
              value = formCity,
              onValueChange = { formCity = it },
              label = { Text("Delivery City *") },
              modifier = Modifier
                .weight(1f)
                .testTag("rfq_city_input"),
              singleLine = true
            )

            OutlinedTextField(
              value = formPhone,
              onValueChange = { formPhone = it },
              label = { Text("Contact Phone *") },
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
              modifier = Modifier
                .weight(1f)
                .testTag("rfq_phone_input"),
              singleLine = true
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          OutlinedTextField(
            value = formBuyerName,
            onValueChange = { formBuyerName = it },
            label = { Text("Your Firm / Business Name *") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
          )

          Spacer(modifier = Modifier.height(10.dp))

          OutlinedTextField(
            value = formNotes,
            onValueChange = { formNotes = it },
            label = { Text("Specifications / Delivery Requirements") },
            placeholder = { Text("Color specs, fabric GSM, or urgent delivery deadlines...") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2
          )

          if (formError != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(formError!!, color = MaterialTheme.colorScheme.error, fontSize = 12.sp, fontWeight = FontWeight.Bold)
          }

          Spacer(modifier = Modifier.height(18.dp))

          Button(
            onClick = { submitRfq() },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp)
              .testTag("submit_rfq_button")
          ) {
            Icon(Icons.Default.Campaign, contentDescription = null)
            Spacer(modifier = Modifier.width(6.dp))
            Text("Broadcast to Local Wholesalers", fontWeight = FontWeight.Bold, fontSize = 14.sp)
          }
        }
      }
    }
  }
}
