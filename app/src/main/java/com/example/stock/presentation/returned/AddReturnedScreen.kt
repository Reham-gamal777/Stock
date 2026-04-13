package com.example.stock.presentation.returned

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
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.stock.Domain.model.Customer
import com.example.stock.Domain.model.Item
import com.example.stock.presentation.outbound.SelectionDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReturnedScreen(
    onBack: () -> Unit,
    viewModel: ReturnedViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    
    var showCustomerDialog by remember { mutableStateOf(false) }
    var showItemDialog by remember { mutableStateOf(false) }
    
    var selectedItem by remember { mutableStateOf<Item?>(null) }
    var amount by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("إضافة مرتجع جديد", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                        }
                    }
                )
            },
            bottomBar = {
                Button(
                    onClick = { 
                        viewModel.saveReturned()
                        onBack()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    enabled = state.selectedCustomer != null && state.newReturnedItems.isNotEmpty()
                ) {
                    Icon(Icons.Default.Save, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("حفظ المرتجع", fontSize = 18.sp, fontWeight = FontWeight.Bold)
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
                // Customer Selection
                item {
                    Text("بيانات العميل", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.height(8.dp))
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

                // Add Items Section
                item {
                    Text("إضافة أصناف المرتجع", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedCard(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = { showItemDialog = true },
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
                            ) {
                                Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Text(selectedItem?.itemName ?: "اختر الصنف", modifier = Modifier.weight(1f))
                                    Icon(Icons.Default.ArrowDropDown, null)
                                }
                            }
                            
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = amount,
                                    onValueChange = { if (it.all { c -> c.isDigit() }) amount = it },
                                    label = { Text("الكمية") },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                OutlinedTextField(
                                    value = price,
                                    onValueChange = { if (it.isEmpty() || it.toDoubleOrNull() != null) price = it },
                                    label = { Text("السعر") },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                            }
                            
                            Button(
                                onClick = { 
                                    selectedItem?.let { 
                                        viewModel.addItemToNewReturn(it, amount.toIntOrNull() ?: 0, price.toDoubleOrNull() ?: 0.0)
                                        selectedItem = null
                                        amount = ""
                                        price = ""
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                enabled = selectedItem != null && amount.isNotEmpty() && price.isNotEmpty()
                            ) {
                                Text("إضافة للقائمة")
                            }
                        }
                    }
                }

                // List of items
                items(state.newReturnedItems) { detail ->
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
                            IconButton(onClick = { viewModel.removeDetailFromNewReturn(detail) }) {
                                Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red)
                            }
                        }
                    }
                }
            }
        }

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
            SelectionDialog(
                title = "اختر الصنف",
                items = state.allItems,
                onDismiss = { showItemDialog = false },
                onSelect = { 
                    selectedItem = it
                    showItemDialog = false
                },
                itemLabel = { it.itemName }
            )
        }
    }
}
