package com.sarvadiverse.husenhisab.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sarvadiverse.husenhisab.model.EntryType
import com.sarvadiverse.husenhisab.model.LedgerEntry
import com.sarvadiverse.husenhisab.model.LedgerType
import com.sarvadiverse.husenhisab.ui.theme.DarkCard
import com.sarvadiverse.husenhisab.ui.theme.PrimaryYellow
import com.sarvadiverse.husenhisab.ui.theme.PureBlack
import com.sarvadiverse.husenhisab.viewmodel.AppViewModel

@Composable
fun LedgerScreen(isLargePole: Boolean, viewModel: AppViewModel) {
    val context = LocalContext.current
    val currentLedgerType = if (isLargePole) LedgerType.LARGE_POLE else LedgerType.SMALL_POLE
    val titleText = if (isLargePole) "મોટા પોલનો હિસાબ" else "નાના પોલનો હિસાબ"

    val ledgerFlow = if (isLargePole) viewModel.largePoleLedger else viewModel.smallPoleLedger
    val ledgerList by ledgerFlow.collectAsState()

    var personName by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(EntryType.JAMA) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredList = ledgerList.filter {
        it.personName.contains(searchQuery, ignoreCase = true) || it.note.contains(searchQuery, ignoreCase = true)
    }

    val totalJama = filteredList.filter { it.type == EntryType.JAMA }.sumOf { it.amount }
    val totalUdhar = filteredList.filter { it.type == EntryType.UDHAR }.sumOf { it.amount }
    val balance = totalJama - totalUdhar

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(PureBlack)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(titleText, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = PrimaryYellow)
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("કુલ જમા", fontSize = 12.sp, color = Color.Gray)
                        Text("₹$totalJama", color = Color(0xFF00E676), fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("કુલ ઉધાર", fontSize = 12.sp, color = Color.Gray)
                        Text("₹$totalUdhar", color = Color(0xFFFF5252), fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("બાકી", fontSize = 12.sp, color = Color.Gray)
                        Text("₹$balance", color = PrimaryYellow, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("નામ અથવા વિગત શોધો...", color = Color.Gray) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = PrimaryYellow,
                    unfocusedBorderColor = Color.DarkGray
                )
            )
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("નવી એન્ટ્રી ઉમેરો", color = PrimaryYellow, fontWeight = FontWeight.Bold)

                    OutlinedTextField(
                        value = personName,
                        onValueChange = { personName = it },
                        label = { Text("વ્યક્તિ / પાર્ટીનું નામ", color = Color.Gray) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = PrimaryYellow,
                            unfocusedBorderColor = Color.DarkGray
                        )
                    )

                    OutlinedTextField(
                        value = amountText,
                        onValueChange = { amountText = it },
                        label = { Text("રકમ (₹)", color = Color.Gray) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = PrimaryYellow,
                            unfocusedBorderColor = Color.DarkGray
                        )
                    )

                    OutlinedTextField(
                        value = note,
                        onValueChange = { note = it },
                        label = { Text("નોંધ / વિગત", color = Color.Gray) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = PrimaryYellow,
                            unfocusedBorderColor = Color.DarkGray
                        )
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        FilterChip(
                            selected = selectedType == EntryType.JAMA,
                            onClick = { selectedType = EntryType.JAMA },
                            label = { Text("જમા (+)") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF00E676),
                                selectedLabelColor = PureBlack
                            )
                        )
                        FilterChip(
                            selected = selectedType == EntryType.UDHAR,
                            onClick = { selectedType = EntryType.UDHAR },
                            label = { Text("ઉધાર (-)") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFFFF5252),
                                selectedLabelColor = PureBlack
                            )
                        )
                    }

                    Button(
                        onClick = {
                            val amt = amountText.toDoubleOrNull() ?: 0.0
                            if (personName.isNotEmpty() && amt > 0.0) {
                                val entry = LedgerEntry(
                                    ledgerType = currentLedgerType,
                                    personName = personName,
                                    amount = amt,
                                    type = selectedType,
                                    note = note
                                )
                                viewModel.saveLedgerEntry(entry) {
                                    Toast.makeText(context, "એન્ટ્રી ઉમેરાઈ ગઈ", Toast.LENGTH_SHORT).show()
                                    personName = ""
                                    amountText = ""
                                    note = ""
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryYellow)
                    ) {
                        Text("એન્ટ્રી સેવ કરો", color = PureBlack, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        items(filteredList) { item ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(item.personName, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
                        if (item.note.isNotEmpty()) {
                            Text(item.note, color = Color.LightGray, fontSize = 13.sp)
                        }
                    }
                    Text(
                        text = (if (item.type == EntryType.JAMA) "+ ₹" else "- ₹") + item.amount.toString(),
                        color = if (item.type == EntryType.JAMA) Color(0xFF00E676) else Color(0xFFFF5252),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    IconButton(onClick = { viewModel.deleteLedgerEntry(item.id) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Gray)
                    }
                }
            }
        }
    }
}
