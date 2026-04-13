package com.example.stock.presentation.outbound

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.stock.Domain.model.Customer
import com.example.stock.Domain.model.Item

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddOutboundScreen(
    onBack: () -> Unit,
    viewModel: OutboundViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    
    var invoiceNumber by remember { mutableStateOf("") }
    var moneyReceived by remember { mutableStateOf("") }
    
    var showCustomerDialog by remember { mutableStateOf(false) }
    var showItemDialog by remember { mutableStateOf(false) }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("إضافة صادر جديد", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                        }
                    }
                )
            },
            bottomBar = {
                Surface(shadowElevation = 8.dp) {
                    Button(
                        onClick = { 
                            viewModel.saveOutbound(invoiceNumber, moneyReceived.toIntOrNull() ?: 0)
                            onBack()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        enabled = state.selectedCustomer != null && state.newOutboundItems.isNotEmpty() && invoiceNumber.isNotEmpty()
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("حفظ الفاتورة", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color(0xFFF8F9FA))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Invoice Details
                item {
                    Text("بيانات الفاتورة الأساسية", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = invoiceNumber,
                        onValueChange = { invoiceNumber = it },
                        label = { Text("رقم الفاتورة") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = { Icon(Icons.Default.Numbers, contentDescription = null) }
                    )
                }

                // Customer Selection
                item {
                    OutlinedCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        onClick = { showCustomerDialog = true }
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text("العميل", fontSize = 12.sp, color = Color.Gray)
                                Text(
                                    state.selectedCustomer?.customerName ?: "اضغط لاختيار العميل",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }
                            Spacer(Modifier.weight(1f))
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(20.dp))
                        }
                    }
                }

                // Items Section Header
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("الأصناف المختارة", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Button(
                            onClick = { showItemDialog = true },
                            contentPadding = PaddingValues(horizontal = 12.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("إضافة صنف")
                        }
                    }
                }

                // Selected Items List
                if (state.newOutboundItems.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .background(Color.White, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("قم بإضافة أصناف للفاتورة", color = Color.Gray)
                        }
                    }
                } else {
                    items(state.newOutboundItems) { detail ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(detail.itemName, fontWeight = FontWeight.Bold)
                                    Text("${detail.amount} قطعة × ${detail.price} ج.م", fontSize = 12.sp, color = Color.Gray)
                                }
                                Text("${detail.total} ج.م", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                IconButton(onClick = { viewModel.removeDetailFromNewOutbound(detail) }) {
                                    Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red)
                                }
                            }
                        }
                    }
                }

                // Money Received & Summary
                item {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    OutlinedTextField(
                        value = moneyReceived,
                        onValueChange = { if (it.all { char -> char.isDigit() }) moneyReceived = it },
                        label = { Text("المبلغ المحصل") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = { Icon(Icons.Default.Payments, contentDescription = null) },
                        suffix = { Text("ج.م") }
                    )
                }

                item {
                    val total = state.newOutboundItems.sumOf { it.total }
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("إجمالي الفاتورة:", fontWeight = FontWeight.Bold)
                            Text("$total ج.م", fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32), fontSize = 18.sp)
                        }
                    }
                }
            }
        }

        // --- Dialogs ---

        if (showCustomerDialog) {
            SelectionDialog(
                title = "اختر العميل",
                items = state.allCustomers,
                onDismiss = { showCustomerDialog = false },
                onSelect = { 
                    viewModel.selectCustomer(it)
                    showCustomerDialog = false
                },
                itemLabel = { it.customerName }
            )
        }

        if (showItemDialog) {
            AddItemDialog(
                items = state.allItems,
                onDismiss = { showItemDialog = false },
                onConfirm = { item, amount, price ->
                    viewModel.addItemToNewOutbound(item, amount, price)
                    showItemDialog = false
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> SelectionDialog(
    title: String,
    items: List<T>,
    onDismiss: () -> Unit,
    onSelect: (T) -> Unit,
    itemLabel: (T) -> String
) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredItems = items.filter { itemLabel(it).contains(searchQuery, ignoreCase = true) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.7f),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("بحث...") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
                )
                Spacer(Modifier.height(8.dp))
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(filteredItems) { item ->
                        ListItem(
                            headlineContent = { Text(itemLabel(item)) },
                            modifier = Modifier.clickable { onSelect(item) }
                        )
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}

@Composable
fun AddItemDialog(
    items: List<Item>,
    onDismiss: () -> Unit,
    onConfirm: (Item, Int, Double) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedItem by remember { mutableStateOf<Item?>(null) }
    var amount by remember { mutableStateOf("1") }
    var price by remember { mutableStateOf("") }

    val filteredItems = items.filter { it.itemName.contains(searchQuery, ignoreCase = true) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("إضافة صنف للفاتورة", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                
                if (selectedItem == null) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("ابحث عن صنف...") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                    LazyColumn(modifier = Modifier.heightIn(max = 200.dp)) {
                        items(filteredItems) { item ->
                            ListItem(
                                headlineContent = { Text(item.itemName) },
                                modifier = Modifier.clickable { selectedItem = item }
                            )
                            HorizontalDivider()
                        }
                    }
                } else {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(selectedItem!!.itemName, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                            TextButton(onClick = { selectedItem = null }) { Text("تغيير") }
                        }
                    }
                    
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = amount,
                            onValueChange = { if (it.all { c -> c.isDigit() }) amount = it },
                            label = { Text("الكمية") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = price,
                            onValueChange = { if (it.isEmpty() || it.toDoubleOrNull() != null) price = it },
                            label = { Text("السعر") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    
                    Button(
                        onClick = { 
                            selectedItem?.let { 
                                onConfirm(it, amount.toIntOrNull() ?: 1, price.toDoubleOrNull() ?: 0.0) 
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = selectedItem != null && price.isNotEmpty()
                    ) {
                        Text("إضافة")
                    }
                }
                
                TextButton(onClick = onDismiss, modifier = Modifier.align(Alignment.End)) {
                    Text("إلغاء")
                }
            }
        }
    }
}
