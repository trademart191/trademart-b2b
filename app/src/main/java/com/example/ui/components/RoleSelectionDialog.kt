package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Factory
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.UserRole

@Composable
fun RoleSelectionDialog(
  currentRole: UserRole,
  canDismiss: Boolean = true,
  onDismiss: () -> Unit = {},
  onSelectRole: (UserRole) -> Unit
) {
  Dialog(onDismissRequest = { if (canDismiss) onDismiss() }) {
    Card(
      shape = RoundedCornerShape(24.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
      modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)
        .testTag("role_selection_dialog")
    ) {
      Column(
        modifier = Modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        if (canDismiss) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
          ) {
            IconButton(
              onClick = onDismiss,
              modifier = Modifier.testTag("close_role_dialog_button")
            ) {
              Icon(Icons.Default.Close, contentDescription = "Close")
            }
          }
        }

        Box(
          modifier = Modifier
            .size(64.dp)
            .background(
              MaterialTheme.colorScheme.primaryContainer,
              shape = CircleShape
            ),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Business,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(36.dp)
          )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
          text = "Choose Your Role",
          style = MaterialTheme.typography.headlineSmall,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
          text = "Select how you want to experience the B2B Wholesale Marketplace. You can switch anytime from the top bar.",
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Role 1: Buyer (Retailer)
        RoleOptionCard(
          title = "🛒 I am a Buyer (Retailer)",
          subtitle = "Browse factory rates, filter local markets, negotiate bulk orders & request samples directly from verified manufacturers.",
          badge = "Retailers & Shop Owners",
          icon = Icons.Default.ShoppingCart,
          isSelected = currentRole == UserRole.BUYER,
          accentColor = Color(0xFF2563EB),
          onClick = { onSelectRole(UserRole.BUYER) },
          testTag = "select_buyer_role_button"
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Role 2: Seller (Wholesaler)
        RoleOptionCard(
          title = "🏭 I am a Seller (Wholesaler)",
          subtitle = "Upload catalog with MOQs & tiered slab pricing. Share digital storefront with QR code and receive direct WhatsApp orders.",
          badge = "Wholesalers & Manufacturers",
          icon = Icons.Default.Factory,
          isSelected = currentRole == UserRole.SELLER,
          accentColor = Color(0xFF0F766E),
          onClick = { onSelectRole(UserRole.SELLER) },
          testTag = "select_seller_role_button"
        )
      }
    }
  }
}

@Composable
private fun RoleOptionCard(
  title: String,
  subtitle: String,
  badge: String,
  icon: ImageVector,
  isSelected: Boolean,
  accentColor: Color,
  onClick: () -> Unit,
  testTag: String
) {
  val borderColor = if (isSelected) accentColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
  val bgColor = if (isSelected) accentColor.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)

  Surface(
    shape = RoundedCornerShape(16.dp),
    color = bgColor,
    modifier = Modifier
      .fillMaxWidth()
      .border(
        width = if (isSelected) 2.dp else 1.dp,
        color = borderColor,
        shape = RoundedCornerShape(16.dp)
      )
      .clip(RoundedCornerShape(16.dp))
      .clickable(onClick = onClick)
      .testTag(testTag)
  ) {
    Row(
      modifier = Modifier.padding(16.dp),
      verticalAlignment = Alignment.Top
    ) {
      Box(
        modifier = Modifier
          .size(44.dp)
          .background(accentColor.copy(alpha = 0.15f), RoundedCornerShape(10.dp)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = accentColor,
          modifier = Modifier.size(24.dp)
        )
      }

      Spacer(modifier = Modifier.width(14.dp))

      Column(modifier = Modifier.weight(1f)) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween,
          modifier = Modifier.fillMaxWidth()
        ) {
          Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
          if (isSelected) {
            Icon(
              imageVector = Icons.Default.CheckCircle,
              contentDescription = "Selected",
              tint = accentColor,
              modifier = Modifier.size(20.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
          text = badge,
          fontSize = 11.sp,
          fontWeight = FontWeight.SemiBold,
          color = accentColor
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
          text = subtitle,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          lineHeight = 16.sp
        )
      }
    }
  }
}
