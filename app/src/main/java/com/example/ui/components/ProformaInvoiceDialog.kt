package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.ProformaInvoice
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun ProformaInvoiceDialog(
  invoice: ProformaInvoice,
  onDismiss: () -> Unit
) {
  val context = LocalContext.current

  val formattedProformaMessage = """
*🏢 B2B PROFORMA INVOICE & ORDER INQUIRY*
━━━━━━━━━━━━━━━━━━━━━━━━━━━━
*Estimate Ref:* #${invoice.invoiceNumber}
*Date:* ${invoice.date}

*Wholesaler:* ${invoice.product.shopName} (${invoice.product.location})
*Product:* ${invoice.product.name}
*Category:* ${invoice.product.category.displayName}
*Order Type:* ${if (invoice.isSampleOrder) "📦 Sample Test Batch" else "🏭 Bulk Wholesale Order"}

*QUANTITY & PRICING BREAKDOWN:*
• Quantity: ${invoice.quantity} pieces
• Rate per piece: ₹${invoice.unitPrice.toInt()} ${if (invoice.isTieredApplied) "(Tier-2 Slab Applied!)" else ""}
• Subtotal: ₹${invoice.subtotal.toInt()}
• GST (18% B2B Est.): ₹${invoice.gstAmount.toInt()}
• *GRAND TOTAL ORDER VALUE: ₹${invoice.grandTotal.toInt()}*

*BUYER / DELIVERY DETAILS:*
• Buyer Firm: ${invoice.buyerBusiness}
• Contact Person: ${invoice.buyerName}
• Delivery Destination: ${invoice.deliveryCity}

_Generated via TradeMart B2B Wholesale Marketplace_
_Please confirm dispatch timeline & bank payment details._
""".trimIndent()

  fun sendWhatsApp() {
    try {
      val encodedText = URLEncoder.encode(formattedProformaMessage, StandardCharsets.UTF_8.toString())
      val phoneClean = invoice.product.whatsappNumber.replace("+", "").replace(" ", "").trim()
      val url = "https://wa.me/$phoneClean?text=$encodedText"
      val intent = Intent(Intent.ACTION_VIEW).apply {
        data = Uri.parse(url)
      }
      context.startActivity(intent)
    } catch (e: Exception) {
      // Fallback to generic text share intent if WhatsApp direct cannot be opened
      val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, formattedProformaMessage)
      }
      context.startActivity(Intent.createChooser(shareIntent, "Share B2B Proforma via"))
    }
  }

  fun copyToClipboard() {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("B2B Proforma Invoice", formattedProformaMessage)
    clipboard.setPrimaryClip(clip)
    Toast.makeText(context, "Proforma Invoice copied to clipboard!", Toast.LENGTH_SHORT).show()
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
        .testTag("proforma_invoice_dialog")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState())
          .padding(20.dp)
      ) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Description,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Proforma Invoice #${invoice.invoiceNumber}",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold
            )
          }
          IconButton(
            onClick = onDismiss,
            modifier = Modifier.testTag("close_proforma_dialog_button")
          ) {
            Icon(Icons.Default.Close, contentDescription = "Close")
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Printable Bill Look Container
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = Color(0xFFF8FAFC),
          modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
            .padding(16.dp)
        ) {
          Column {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Column {
                Text("SUPPLIER / WHOLESALER", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
                Text(invoice.product.shopName, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                Text("City: ${invoice.product.location}", fontSize = 11.sp, color = Color(0xFF475569))
              }
              Column(horizontalAlignment = Alignment.End) {
                Text("ESTIMATE REF", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
                Text(invoice.invoiceNumber, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Text("Date: ${invoice.date}", fontSize = 11.sp, color = Color(0xFF475569))
              }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = Color(0xFFCBD5E1))
            Spacer(modifier = Modifier.height(12.dp))

            // Bill To
            Column {
              Text("BUYER / CONFINED TO", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
              Text(invoice.buyerBusiness, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
              Text("Attn: ${invoice.buyerName} • Delivery: ${invoice.deliveryCity}", fontSize = 11.sp, color = Color(0xFF475569))
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Line items header
            Surface(
              color = Color(0xFFE2E8F0),
              shape = RoundedCornerShape(6.dp),
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text("Item / Description", fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Text("Qty", fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(40.dp))
                Text("Rate", fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(55.dp))
                Text("Total", fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(65.dp))
              }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(
                text = "${invoice.product.name} ${if (invoice.isSampleOrder) "(Sample)" else ""}",
                fontSize = 11.sp,
                color = Color(0xFF1E293B),
                modifier = Modifier.weight(1f)
              )
              Text("${invoice.quantity}", fontSize = 11.sp, modifier = Modifier.width(40.dp))
              Text("₹${invoice.unitPrice.toInt()}", fontSize = 11.sp, modifier = Modifier.width(55.dp))
              Text("₹${invoice.subtotal.toInt()}", fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(65.dp))
            }

            Spacer(modifier = Modifier.height(10.dp))
            Divider(color = Color(0xFFCBD5E1))
            Spacer(modifier = Modifier.height(10.dp))

            // Totals
            Column(horizontalAlignment = Alignment.End, modifier = Modifier.fillMaxWidth()) {
              Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Subtotal:", fontSize = 12.sp, color = Color(0xFF64748B))
                Text("₹${invoice.subtotal.toInt()}", fontSize = 12.sp, fontWeight = FontWeight.Medium)
              }
              Spacer(modifier = Modifier.height(4.dp))
              Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("B2B GST (18%):", fontSize = 12.sp, color = Color(0xFF64748B))
                Text("₹${invoice.gstAmount.toInt()}", fontSize = 12.sp)
              }
              Spacer(modifier = Modifier.height(6.dp))
              Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("ESTIMATED TOTAL:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                Text(
                  "₹${invoice.grandTotal.toInt()}",
                  fontSize = 16.sp,
                  fontWeight = FontWeight.ExtraBold,
                  color = MaterialTheme.colorScheme.primary
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // WhatsApp inquiry callout
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = Color(0xFFDCFCE7),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.Send,
              contentDescription = null,
              tint = Color(0xFF15803D),
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "This will format a clean WhatsApp proforma inquiry and open direct chat with ${invoice.product.shopName}.",
              fontSize = 11.sp,
              color = Color(0xFF166534)
            )
          }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Action Buttons: Send WhatsApp and Copy Text
        Button(
          onClick = { sendWhatsApp() },
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .testTag("send_whatsapp_inquiry_button")
        ) {
          Icon(Icons.Default.Send, contentDescription = null, tint = Color.White)
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Send WhatsApp Inquiry",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          OutlinedButton(
            onClick = { copyToClipboard() },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .weight(1f)
              .height(46.dp)
              .testTag("copy_proforma_button")
          ) {
            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Copy Text", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
          }

          OutlinedButton(
            onClick = {
              val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, formattedProformaMessage)
              }
              context.startActivity(Intent.createChooser(shareIntent, "Share B2B Proforma"))
            },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .weight(1f)
              .height(46.dp)
              .testTag("share_other_apps_button")
          ) {
            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Share Proforma", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
          }
        }
      }
    }
  }
}
