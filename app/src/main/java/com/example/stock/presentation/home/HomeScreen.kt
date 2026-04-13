package com.example.stock.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.AssignmentReturn
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToOutbound: () -> Unit,
    onNavigateToInbound: () -> Unit,
    onNavigateToStock: () -> Unit,
    onNavigateToCustomer: () -> Unit,
    onNavigateToReturned: () -> Unit,
    onNavigateToPayment: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    // Force RTL for Arabic UI
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "إدارة المخزن",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { /* TODO */ }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                )
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                item {
                    HomeCard(
                        title = "الصادر",
                        subtitle = "شحنة ${state.outboundCount}",
                        icon = Icons.Outlined.LocalShipping,
                        backgroundColor = Color(0xFFE3F2FD),
                        subtitleColor = Color(0xFF1E88E5),
                        onClick = onNavigateToOutbound
                    )
                }
                item {
                    HomeCard(
                        title = "الوارد",
                        subtitle = "طلب ${state.inboundCount}",
                        icon = Icons.Outlined.Inventory2,
                        backgroundColor = Color(0xFFFFF3E0),
                        subtitleColor = Color(0xFFFB8C00),
                        onClick = onNavigateToInbound
                    )
                }
                item {
                    HomeCard(
                        title = "العملاء",
                        subtitle = "فاتورة ${state.customerCount}",
                        icon = Icons.Outlined.People,
                        backgroundColor = Color(0xFFE8F5E9),
                        subtitleColor = Color(0xFF43A047),
                        onClick = onNavigateToCustomer
                    )
                }
                item {
                    HomeCard(
                        title = "المخازن",
                        subtitle = "مخزن ${state.stockCount}",
                        icon = Icons.Outlined.Warehouse,
                        backgroundColor = Color(0xFFE8EAF6),
                        subtitleColor = Color(0xFF3949AB),
                        onClick = onNavigateToStock
                    )
                }
                item {
                    HomeCard(
                        title = "مرتجع",
                        subtitle = "مرتجع ${state.returnCount}",
                        icon = Icons.AutoMirrored.Outlined.AssignmentReturn,
                        backgroundColor = Color(0xFFFFEBEE),
                        subtitleColor = Color(0xFFE53935),
                        onClick = onNavigateToReturned
                    )
                }
                item {
                    HomeCard(
                        title = "توريد",
                        subtitle = "${state.paymentCount} عملية",
                        icon = Icons.Outlined.Payments,
                        backgroundColor = Color(0xFFF3E5F5),
                        subtitleColor = Color(0xFFBA68C8),
                        onClick = onNavigateToPayment
                    )
                }
            }
        }
    }
}

@Composable
fun HomeCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    backgroundColor: Color,
    subtitleColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Background gradient pattern
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(backgroundColor, Color.White.copy(alpha = 0.4f)),
                            startX = 0f,
                            endX = 1500f
                        )
                    )
            )

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = subtitle,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            fontSize = 14.sp,
                            color = subtitleColor,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Material Icon with color matching the subtitle
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(60.dp),
                    tint = subtitleColor.copy(alpha = 0.8f)
                )
            }
        }
    }
}
