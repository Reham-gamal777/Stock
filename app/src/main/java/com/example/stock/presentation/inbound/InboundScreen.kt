package com.example.stock.presentation.inbound

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.stock.Domain.model.Inbound

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InboundScreen(
    viewModel: InboundViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onNavigateToDetails: (InboundUiModel) -> Unit,
    onNavigateToAddInbound: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("أوامر التوريد", fontWeight = FontWeight.Bold, color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color(0xFF9575CD) // Purple color from image
                    )
                )
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color(0xFFF5F5F5)),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Add New Invoice Card
                item {
                    AddInvoiceCard(onClick = onNavigateToAddInbound)
                }

                // Inbound Orders List
                items(state.inbounds) { item ->
                    InboundOrderCard(item = item.inbound) {
                        onNavigateToDetails(item)
                    }
                }
            }
        }
    }
}

@Composable
fun AddInvoiceCard(onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .shadow(4.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left Dollar Icon
            Surface(
                shape = CircleShape,
                color = Color(0xFFE8F5E9),
                modifier = Modifier.size(50.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.AttachMoney, contentDescription = null, tint = Color(0xFF4CAF50))
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text("إضافة فاتورة جديدة", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("أنشئ فاتورة توريد جديدة للمخزن", fontSize = 12.sp, color = Color.Gray)
            }

            // Right Plus Icon
            Surface(
                shape = CircleShape,
                color = Color(0xFFF1F8E9),
                modifier = Modifier.size(45.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color(0xFF4CAF50))
                }
            }
        }
    }
}

@Composable
fun InboundOrderCard(item: Inbound, onClick: () -> Unit) {
    val isCompleted = item.status == "مكتملة"
    val statusColor = if (isCompleted) Color(0xFFE8F5E9) else Color(0xFFFFF3E0)
    val statusTextColor = if (isCompleted) Color(0xFF4CAF50) else Color(0xFFFB8C00)
    val iconBgColor = if (isCompleted) Color(0xFFE8F5E9) else Color(0xFFFFF3E0)
    val iconColor = if (isCompleted) Color(0xFF4CAF50) else Color(0xFFFB8C00)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .shadow(2.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Price and Status on the Left
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "${item.totalAmount} ج.م",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = statusColor,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = item.status,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        fontSize = 10.sp,
                        color = statusTextColor,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Supplier Info in Center
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.weight(2f).padding(horizontal = 8.dp)
            ) {
                Text(
                    text = item.supplierName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.End
                )
                Text(
                    text = item.description,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    textAlign = androidx.compose.ui.text.style.TextAlign.End
                )
                Text(
                    text = item.date,
                    fontSize = 10.sp,
                    color = Color.LightGray,
                    textAlign = androidx.compose.ui.text.style.TextAlign.End
                )
            }

            // Status Icon on the Right
            Surface(
                shape = CircleShape,
                color = iconBgColor,
                modifier = Modifier.size(50.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.Schedule,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}
