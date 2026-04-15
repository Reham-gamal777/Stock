package com.example.stock.presentation.outbound

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
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
                // رقم الفاتورة
                item {
                    OutlinedTextField(
                        value = invoiceNumber,
                        onValueChange = { invoiceNumber = it },
                        label = { Text("رقم الفاتورة") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = { Icon(Icons.Default.Numbers, null) }
                    )
                }

                // اختيار العميل
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable { showCustomerDialog = true },
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Person, null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.width(12.dp))
                            Text(state.selectedCustomer?.customerName ?: "اختر العميل", fontWeight = FontWeight.Bold)
                            Spacer(Modifier.weight(1f))
                            Icon(Icons.Default.ArrowDropDown, null)
                        }
                    }
                }

                // الأصناف المختارة
                item {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("الأصناف المختارة", fontWeight = FontWeight.Bold)
                        Button(onClick = { showItemDialog = true }) {
                            Icon(Icons.Default.Add, null)
                            Spacer(Modifier.width(4.dp))
                            Text("إضافة صنف")
                        }
                    }
                }

                items(state.newOutboundItems) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text(item.itemName, fontWeight = FontWeight.Bold)
                                Text("${item.amount} قطعة × ${item.price} ج.م", fontSize = 12.sp, color = Color.Gray)
                            }
                            Text("${item.total} ج.م", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            IconButton(onClick = { viewModel.removeDetailFromNewOutbound(item) }) {
                                Icon(Icons.Default.Delete, null, tint = Color.Red)
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
                onSelect = { viewModel.selectCustomer(it); showCustomerDialog = false },
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

@Composable
fun AddItemDialog(
    items: List<Item>,
    onDismiss: () -> Unit,
    onConfirm: (Item, Int, Double) -> Unit
) {
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }
    var selectedItem by remember { mutableStateOf<Item?>(null) }
    var amountText by remember { mutableStateOf(TextFieldValue("1")) }
    var priceText by remember { mutableStateOf(TextFieldValue("")) }

    val filteredItems = items.filter { it.itemName.contains(searchQuery.text, ignoreCase = true) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("إضافة صنف للفاتورة", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                
                if (selectedItem == null) {
                    // حقل البحث
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("ابحث عن صنف...") },
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = { Icon(Icons.Default.Search, null) },
                        shape = RoundedCornerShape(8.dp)
                    )
                    
                    // قائمة النتائج
                    LazyColumn(modifier = Modifier.heightIn(max = 250.dp)) {
                        if (filteredItems.isEmpty()) {
                            item {
                                Text("لا يوجد أصناف بهذا الاسم", color = Color.Gray, modifier = Modifier.padding(16.dp))
                            }
                        }
                        items(filteredItems) { item ->
                            ListItem(
                                headlineContent = { Text(item.itemName, fontWeight = FontWeight.Medium) },
                                leadingContent = { Icon(Icons.Default.Inventory2, null, tint = Color.Gray) },
                                modifier = Modifier.clickable { selectedItem = item }
                            )
                            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                        }
                    }
                } else {
                    // واجهة إدخال الكمية والسعر بعد اختيار الصنف
                    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFFF3E5F5))) {
                        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Inventory2, null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.width(8.dp))
                            Text(selectedItem!!.itemName, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                            IconButton(onClick = { selectedItem = null }) { Icon(Icons.Default.Close, null) }
                        }
                    }
                    
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = amountText,
                            onValueChange = { amountText = it },
                            label = { Text("الكمية") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(8.dp)
                        )
                        OutlinedTextField(
                            value = priceText,
                            onValueChange = { priceText = it },
                            label = { Text("السعر") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                    
                    Button(
                        onClick = { 
                            onConfirm(selectedItem!!, amountText.text.toIntOrNull() ?: 1, priceText.text.toDoubleOrNull() ?: 0.0) 
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(8.dp),
                        enabled = priceText.text.isNotEmpty() && amountText.text.isNotEmpty()
                    ) {
                        Text("إضافة للفاتورة", fontWeight = FontWeight.Bold)
                    }
                }
                TextButton(onClick = onDismiss, modifier = Modifier.align(Alignment.End)) {
                    Text("إلغاء", color = Color.Gray)
                }
            }
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
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }
    val filteredItems = items.filter { itemLabel(it).contains(searchQuery.text, ignoreCase = true) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.7f),
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
                    leadingIcon = { Icon(Icons.Default.Search, null) }
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
