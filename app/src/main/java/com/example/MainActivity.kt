package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Factory
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SampleData
import com.example.model.B2BProduct
import com.example.model.BuyerRequirement
import com.example.model.ProformaInvoice
import com.example.model.UserRole
import com.example.ui.components.ProductDetailDialog
import com.example.ui.components.ProformaInvoiceDialog
import com.example.ui.components.RoleSelectionDialog
import com.example.ui.screens.BuyerCatalogScreen
import com.example.ui.screens.RfqNoticeboardScreen
import com.example.ui.screens.SellerDashboardScreen
import com.example.ui.screens.WebPrototypeScreen
import com.example.ui.theme.MyApplicationTheme

enum class MainTab(val title: String, val icon: ImageVector) {
  CATALOG("Catalog", Icons.Default.ShoppingBag),
  RFQ_BOARD("RFQ Notice", Icons.Default.Campaign),
  SELLER_STORE("My Store", Icons.Default.Factory),
  WEB_PROTOTYPE("Web Single-File", Icons.Default.Language)
}

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        B2BMarketplaceApp()
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun B2BMarketplaceApp() {
  // Global Data State
  val products = remember { mutableStateListOf<B2BProduct>().apply { addAll(SampleData.initialProducts) } }
  val requirements = remember { mutableStateListOf<BuyerRequirement>().apply { addAll(SampleData.initialRequirements) } }

  // Navigation & Role State
  var currentRole by remember { mutableStateOf(UserRole.BUYER) }
  var currentTab by remember { mutableStateOf(MainTab.CATALOG) }
  var isFirstLaunchRolePickerOpen by remember { mutableStateOf(true) }
  var isManualRolePickerOpen by remember { mutableStateOf(false) }

  // Modal Sheet / Dialog States
  var selectedProductForDetail by remember { mutableStateOf<B2BProduct?>(null) }
  var isDetailInitialSample by remember { mutableStateOf(false) }
  var activeProformaInvoice by remember { mutableStateOf<ProformaInvoice?>(null) }

  BackHandler(enabled = currentTab != MainTab.CATALOG) {
    currentTab = MainTab.CATALOG
  }

  Scaffold(
    topBar = {
      CenterAlignedTopAppBar(
        title = {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
          ) {
            Box(
              modifier = Modifier
                .size(28.dp)
                .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp)),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "TM",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Black
              )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "TradeMart",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.ExtraBold,
              color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.width(4.dp))
            Surface(
              shape = RoundedCornerShape(4.dp),
              color = MaterialTheme.colorScheme.primaryContainer
            ) {
              Text(
                text = "B2B",
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
              )
            }
          }
        },
        actions = {
          // Role Switcher Pill in Top Bar
          Surface(
            shape = RoundedCornerShape(20.dp),
            color = if (currentRole == UserRole.BUYER) Color(0xFFDBEAFE) else Color(0xFFCCFBF1),
            border = androidx.compose.foundation.BorderStroke(
              1.dp,
              if (currentRole == UserRole.BUYER) Color(0xFF93C5FD) else Color(0xFF5EEAD4)
            ),
            modifier = Modifier
              .padding(end = 8.dp)
              .clip(RoundedCornerShape(20.dp))
              .clickable { isManualRolePickerOpen = true }
              .testTag("switch_role_top_bar_button")
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
              Icon(
                imageVector = if (currentRole == UserRole.BUYER) Icons.Default.ShoppingCart else Icons.Default.Factory,
                contentDescription = null,
                tint = if (currentRole == UserRole.BUYER) Color(0xFF1D4ED8) else Color(0xFF0F766E),
                modifier = Modifier.size(14.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = if (currentRole == UserRole.BUYER) "Buyer" else "Seller",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (currentRole == UserRole.BUYER) Color(0xFF1E40AF) else Color(0xFF0F766E)
              )
              Spacer(modifier = Modifier.width(2.dp))
              Icon(
                imageVector = Icons.Default.SwapHoriz,
                contentDescription = "Switch",
                tint = if (currentRole == UserRole.BUYER) Color(0xFF1D4ED8) else Color(0xFF0F766E),
                modifier = Modifier.size(13.dp)
              )
            }
          }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
          containerColor = MaterialTheme.colorScheme.surface
        )
      )
    },
    bottomBar = {
      NavigationBar(
        modifier = Modifier.navigationBarsPadding(),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
      ) {
        NavigationBarItem(
          selected = currentTab == MainTab.CATALOG,
          onClick = {
            currentTab = MainTab.CATALOG
            currentRole = UserRole.BUYER
          },
          icon = { Icon(Icons.Default.ShoppingBag, contentDescription = "Catalog") },
          label = { Text("Catalog", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = MaterialTheme.colorScheme.primary,
            selectedTextColor = MaterialTheme.colorScheme.primary,
            indicatorColor = MaterialTheme.colorScheme.primaryContainer
          ),
          modifier = Modifier.testTag("nav_item_catalog")
        )

        NavigationBarItem(
          selected = currentTab == MainTab.RFQ_BOARD,
          onClick = { currentTab = MainTab.RFQ_BOARD },
          icon = {
            BadgedBox(
              badge = {
                Badge { Text("${requirements.size}") }
              }
            ) {
              Icon(Icons.Default.Campaign, contentDescription = "RFQ Noticeboard")
            }
          },
          label = { Text("RFQ Notice", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = MaterialTheme.colorScheme.primary,
            selectedTextColor = MaterialTheme.colorScheme.primary,
            indicatorColor = MaterialTheme.colorScheme.primaryContainer
          ),
          modifier = Modifier.testTag("nav_item_rfq")
        )

        NavigationBarItem(
          selected = currentTab == MainTab.SELLER_STORE,
          onClick = {
            currentTab = MainTab.SELLER_STORE
            currentRole = UserRole.SELLER
          },
          icon = { Icon(Icons.Default.Factory, contentDescription = "Seller Store") },
          label = { Text("My Store", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = Color(0xFF0F766E),
            selectedTextColor = Color(0xFF0F766E),
            indicatorColor = Color(0xFFCCFBF1)
          ),
          modifier = Modifier.testTag("nav_item_seller")
        )

        NavigationBarItem(
          selected = currentTab == MainTab.WEB_PROTOTYPE,
          onClick = { currentTab = MainTab.WEB_PROTOTYPE },
          icon = { Icon(Icons.Default.Language, contentDescription = "Web Prototype") },
          label = { Text("Web View", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = MaterialTheme.colorScheme.tertiary,
            selectedTextColor = MaterialTheme.colorScheme.tertiary,
            indicatorColor = MaterialTheme.colorScheme.tertiaryContainer
          ),
          modifier = Modifier.testTag("nav_item_web")
        )
      }
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      when (currentTab) {
        MainTab.CATALOG -> {
          BuyerCatalogScreen(
            products = products,
            onProductClick = { product ->
              selectedProductForDetail = product
              isDetailInitialSample = false
            },
            onRequestSample = { product ->
              selectedProductForDetail = product
              isDetailInitialSample = true
            },
            onOpenRfq = { currentTab = MainTab.RFQ_BOARD }
          )
        }

        MainTab.RFQ_BOARD -> {
          RfqNoticeboardScreen(
            requirements = requirements,
            onPostRequirement = { newReq ->
              requirements.add(0, newReq)
            }
          )
        }

        MainTab.SELLER_STORE -> {
          SellerDashboardScreen(
            products = products,
            onAddProduct = { newProd ->
              products.add(0, newProd)
            },
            onUpdateProduct = { updatedProd ->
              val index = products.indexOfFirst { it.id == updatedProd.id }
              if (index != -1) {
                products[index] = updatedProd
              }
            },
            onDeleteProduct = { prodId ->
              products.removeAll { it.id == prodId }
            }
          )
        }

        MainTab.WEB_PROTOTYPE -> {
          WebPrototypeScreen()
        }
      }
    }
  }

  // 1. Initial Launch Role Selection Dialog
  if (isFirstLaunchRolePickerOpen) {
    RoleSelectionDialog(
      currentRole = currentRole,
      canDismiss = false,
      onSelectRole = { role ->
        currentRole = role
        currentTab = if (role == UserRole.BUYER) MainTab.CATALOG else MainTab.SELLER_STORE
        isFirstLaunchRolePickerOpen = false
      }
    )
  }

  // 2. Manual Role Switcher Dialog
  if (isManualRolePickerOpen) {
    RoleSelectionDialog(
      currentRole = currentRole,
      canDismiss = true,
      onDismiss = { isManualRolePickerOpen = false },
      onSelectRole = { role ->
        currentRole = role
        currentTab = if (role == UserRole.BUYER) MainTab.CATALOG else MainTab.SELLER_STORE
        isManualRolePickerOpen = false
      }
    )
  }

  // 3. Product Detail & Interactive Cart / Order Building Dialog
  if (selectedProductForDetail != null) {
    ProductDetailDialog(
      product = selectedProductForDetail!!,
      initialIsSample = isDetailInitialSample,
      onDismiss = { selectedProductForDetail = null },
      onProceedToProforma = { invoice ->
        selectedProductForDetail = null
        activeProformaInvoice = invoice
      }
    )
  }

  // 4. Proforma Invoice & WhatsApp Order Generator Dialog
  if (activeProformaInvoice != null) {
    ProformaInvoiceDialog(
      invoice = activeProformaInvoice!!,
      onDismiss = { activeProformaInvoice = null }
    )
  }
}
