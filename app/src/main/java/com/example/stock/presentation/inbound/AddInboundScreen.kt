package com.example.stock.presentation.inbound

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.stock.Domain.model.Item
import com.example.stock.presentation.outbound.SelectionDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddInboundScreen(
    onBack: () -> Unit,
    viewModel: InboundViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    
    var invoiceNumber by remember { mutableStateOf("") }
    var supplierName by remember { mutableStateOf("") }
    
    var showItemDialog by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf<Item?>(null) }
    var amount by remember { mutableStateOf("") }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("إضافة مورد وتوريد (مشتريات)", fontWeight = FontWeight.Bold) },
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
                        viewModel.saveInbound(invoiceNumber, supplierName)
                        onBack()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(56.dp),
                    shape = RoundedCornerShape(24.dp),
                    enabled = invoiceNumber.isNotEmpty() && state.newInboundItems.isNotEmpty()
                ) {
                    Text("حفظ واعتماد فاتورة الوارد", fontWeight = FontWeight.Bold, fontSize = 16.sp)
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
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF3E5F5)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedTextField(
                                value = invoiceNumber,
                                onValueChange = { invoiceNumber = it },
                                label = { Text("رقم الفاتورة") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White)
                            )
                            
                            OutlinedTextField(
                                value = supplierName,
                                onValueChange = { supplierName = it },
                                label = { Text("اسم المورد / من مخزن") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) },
                                colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White)
                            )

                            OutlinedCard(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Text("إرفاق صورة الفاتورة", color = Color.Gray, modifier = Modifier.weight(1f))
                                    Icon(Icons.Default.PhotoCamera, null, tint = Color.Gray)
                                }
                            }
                        }
                    }
                }

                item {
                    Text("إضافة أصناف للوارد", fontWeight = FontWeight.Bold)
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF3E5F5)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedCard(
                                    modifier = Modifier.weight(2f),
                                    onClick = { showItemDialog = true },
                                    colors = CardDefaults.cardColors(containerColor = Color.White)
                                ) {
                                    Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Text(selectedItem?.itemName ?: "اختر الصنف", modifier = Modifier.weight(1f))
                                        Icon(Icons.Default.ArrowDropDown, null)
                                    }
                                }
                                OutlinedTextField(
                                    value = amount,
                                    onValueChange = { if (it.all { c -> c.isDigit() }) amount = it },
                                    label = { Text("الكمية") },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White)
                                )
                            }
                            
                            Button(
                                onClick = { 
                                    selectedItem?.let { 
                                        viewModel.addItemToNewInbound(it, amount.toIntOrNull() ?: 0)
                                        selectedItem = null
                                        amount = ""
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00897B)),
                                shape = RoundedCornerShape(24.dp),
                                enabled = selectedItem != null && amount.isNotEmpty()
                            ) {
                                Text("إضافة الصنف للقائمة", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFE0E0E0), RoundedCornerShape(4.dp))
                            .padding(12.dp)
                    ) {
                        Text("الصنف", modifier = Modifier.weight(2f), fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                        Text("الكمية", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                    }
                }

                items(state.newInboundItems) { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(item.itemName, modifier = Modifier.weight(2f), textAlign = TextAlign.Center)
                        Text(item.amount.toString(), modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
                    }
                    HorizontalDivider()
                }
            }
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
