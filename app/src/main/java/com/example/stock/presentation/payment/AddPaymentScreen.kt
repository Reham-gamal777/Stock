package com.example.stock.presentation.payment

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.stock.presentation.outbound.SelectionDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPaymentScreen(
    onBack: () -> Unit,
    viewModel: PaymentViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    
    // استخدام TextFieldValue لضمان استقرار الكتابة
    var amountText by remember { mutableStateOf(TextFieldValue("")) }
    var paymentType by remember { mutableStateOf("نقدي") }
    var showCustomerDialog by remember { mutableStateOf(false) }
    
    val paymentTypes = listOf("نقدي", "شيك", "تحويل بنكي", "آجل")

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("إضافة عملية تحصيل", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                        }
                    }
                )
            },
            bottomBar = {
                val canSave = state.selectedCustomer != null && amountText.text.isNotEmpty()
                Button(
                    onClick = { 
                        viewModel.savePayment(amountText.text.toDoubleOrNull() ?: 0.0, paymentType)
                        onBack()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    enabled = canSave,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (canSave) Color(0xFF2196F3) else Color(0xFFE0E0E0)
                    )
                ) {
                    Icon(Icons.Default.Save, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("حفظ العملية", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color(0xFFF5F5F5))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Section 1: Customer Data
                item {
                    Column {
                        Text("بيانات العميل", color = Color(0xFF9C27B0), fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showCustomerDialog = true },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(20.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(20.dp))
                                Spacer(Modifier.weight(1f))
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("العميل", fontSize = 12.sp, color = Color.Gray)
                                    Text(
                                        state.selectedCustomer?.customerName ?: "اختر العميل",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp
                                    )
                                }
                                Spacer(Modifier.width(16.dp))
                                Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF9C27B0), modifier = Modifier.size(30.dp))
                            }
                        }
                    }
                }

                // Section 2: Amount and Type
                item {
                    Column {
                        Text("المبلغ والنوع", color = Color(0xFF9C27B0), fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = amountText,
                            onValueChange = { amountText = it },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedBorderColor = Color(0xFF9C27B0)
                            ),
                            placeholder = { Text("0.0") },
                            leadingIcon = { Text("ج.م", fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 8.dp)) },
                            trailingIcon = { Icon(Icons.Default.AccountBalanceWallet, null) },
                            label = { Text("المبلغ المحصل") }
                        )
                        
                        Spacer(Modifier.height(16.dp))
                        Text("طريقة الدفع", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            paymentTypes.forEach { type ->
                                val isSelected = paymentType == type
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { paymentType = type },
                                    label = { Text(type) },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Color(0xFF9C27B0),
                                        selectedLabelColor = Color.White,
                                        containerColor = Color.White
                                    )
                                )
                            }
                        }
                    }
                }

                // Section 3: Info Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Info, null, tint = Color(0xFFFB8C00))
                            Spacer(Modifier.width(12.dp))
                            Text(
                                "المديونية الحالية لهذا العميل: ${state.selectedCustomer?.customerDebt ?: 0.0} ج.م",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
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
    }
}
