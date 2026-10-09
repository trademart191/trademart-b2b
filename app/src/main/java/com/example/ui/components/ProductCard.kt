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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.model.B2BProduct

@Composable
fun ProductCard(
  product: B2BProduct,
  onProductClick: (B2BProduct) -> Unit,
  onRequestSample: (B2BProduct) -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current

  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(16.dp))
      .clickable { onProductClick(product) }
      .testTag("product_card_${product.id}")
  ) {
    Column {
      // Image Banner & Badges
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(170.dp)
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

        // Category Tag (Top Left)
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = Color(0xCC0F172A),
          modifier = Modifier
            .padding(10.dp)
            .align(Alignment.TopStart)
        ) {
          Text(
            text = product.category.displayName,
            color = Color.White,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          )
        }

        // Location Tag (Top Right)
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
            Icon(
              imageVector = Icons.Default.LocationOn,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
              text = product.location,
              color = MaterialTheme.colorScheme.onSurface,
              fontSize = 11.sp,
              fontWeight = FontWeight.SemiBold
            )
          }
        }

        // MOQ Floating Badge (Bottom Right over Image)
        Surface(
          shape = RoundedCornerShape(topStart = 10.dp),
          color = Color(0xFF1E3A8A),
          modifier = Modifier.align(Alignment.BottomEnd)
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Inventory2,
              contentDescription = null,
              tint = Color.White,
              modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "MOQ: ${product.moq} pcs",
              color = Color.White,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }

      // Content Details
      Column(modifier = Modifier.padding(14.dp)) {
        // Shop Name & Verified Badge
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.fillMaxWidth()
        ) {
          Text(
            text = product.shopName,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
          )

          if (product.isVerified) {
            Spacer(modifier = Modifier.width(4.dp))
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier
                .background(Color(0xFFDBEAFE), RoundedCornerShape(4.dp))
                .padding(horizontal = 5.dp, vertical = 2.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Verified,
                contentDescription = "Verified Wholesaler",
                tint = Color(0xFF1D4ED8),
                modifier = Modifier.size(12.dp)
              )
              Spacer(modifier = Modifier.width(3.dp))
              Text(
                text = "Verified Wholesaler",
                color = Color(0xFF1D4ED8),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Product Name
        Text(
          text = product.name,
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface,
          maxLines = 2,
          overflow = TextOverflow.Ellipsis,
          lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Pricing Block
        Row(
          verticalAlignment = Alignment.Bottom,
          horizontalArrangement = Arrangement.SpaceBetween,
          modifier = Modifier.fillMaxWidth()
        ) {
          Column {
            Text(
              text = "Base Wholesale Price",
              fontSize = 10.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(verticalAlignment = Alignment.Bottom) {
              Text(
                text = "₹${product.basePrice.toInt()}",
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
              )
              Text(
                text = " / piece",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
              )
            }
          }

          // Slab / Tiered Pricing Tag
          if (product.hasTieredPricing && product.tieredPrice100Plus != null) {
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = Color(0xFFFEF3C7),
              modifier = Modifier
                .border(1.dp, Color(0xFFFDE68A), RoundedCornerShape(8.dp))
                .padding(1.dp)
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.TrendingDown,
                  contentDescription = null,
                  tint = Color(0xFFB45309),
                  modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Column {
                  Text(
                    text = "₹${product.tieredPrice100Plus.toInt()} @ 100+ pcs",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF92400E)
                  )
                  val savings = ((product.basePrice - product.tieredPrice100Plus) / product.basePrice * 100).toInt()
                  Text(
                    text = "Save $savings%",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFFB45309)
                  )
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Action Buttons Row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedButton(
            onClick = { onRequestSample(product) },
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
              .weight(0.42f)
              .height(40.dp)
              .testTag("request_sample_card_button_${product.id}")
          ) {
            Text(
              text = "Sample (${product.sampleMoq} pc)",
              fontSize = 11.sp,
              fontWeight = FontWeight.SemiBold,
              maxLines = 1
            )
          }

          Button(
            onClick = { onProductClick(product) },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
              .weight(0.58f)
              .height(40.dp)
              .testTag("inquire_order_card_button_${product.id}")
          ) {
            Icon(
              imageVector = Icons.Default.ShoppingBag,
              contentDescription = null,
              modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
              text = "Bulk Order",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              maxLines = 1
            )
          }
        }
      }
    }
  }
}
