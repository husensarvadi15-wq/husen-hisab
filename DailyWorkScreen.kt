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
import com.sarvadiverse.husenhisab.model.DailyWorkEntry
import com.sarvadiverse.husenhisab.ui.theme.DarkCard
import com.sarvadiverse.husenhisab.ui.theme.PrimaryYellow
import com.sarvadiverse.husenhisab.ui.theme.PureBlack
import com.sarvadiverse.husenhisab.viewmodel.AppViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DailyWorkScreen(viewModel: AppViewModel) {
    val context = LocalContext.current
    val workList by viewModel.workEntries.collectAsState()

    var dateStr by remember {
        mutableStateOf(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()))
    }
    var linesCompleted by remember { mutableStateOf("") }
    var smallPolesCount by remember { mutableStateOf("") }
    var largePolesCount by remember { mutableStateOf("") }

    val allHelpers = listOf("Husen", "Jumabhai", "Sultan", "Salimbhai")
    val selectedSmallHelpers = remember { mutableStateListOf("Husen", "Jumabhai") }
    val selectedLargeHelpers = remember { mutableStateListOf("Husen", "Jumabhai") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(PureBlack)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text("દૈનિક કામની એન્ટ્રી", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = PrimaryYellow)
        }

        item {
            OutlinedTextField(
                value = dateStr,
                onValueChange = { dateStr = it },
                label = { Text("તારીખ (YYYY-MM-DD)", color = Color.Gray) },
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
            OutlinedTextField(
                value = linesCompleted,
                onValueChange = { linesCompleted = it },
                label = { Text("નાની લાઈન સંખ્યા (₹૧૦૦૦/લાઈન)", color = Color.Gray) },
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
        }

        item {
            OutlinedTextField(
                value = smallPolesCount,
                onValueChange = { smallPolesCount = it },
                label = { Text("નાના પોલ લોડિંગ સંખ્યા (₹૪/પોલ)", color = Color.Gray) },
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
            Spacer(modifier = Modifier.height(4.dp))
            Text("નાના પોલ હેલ્પર્સ પસંદ કરો:", fontSize = 13.sp, color = PrimaryYellow)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                allHelpers.forEach { helper ->
                    val isSelected = selectedSmallHelpers.contains(helper)
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            if (isSelected) selectedSmallHelpers.remove(helper) else selectedSmallHelpers.add(helper)
                        },
                        label = { Text(helper, color = if (isSelected) PureBlack else Color.White) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryYellow,
                            containerColor = DarkCard
                        )
                    )
                }
            }
        }

        item {
            OutlinedTextField(
                value = largePolesCount,
                onValueChange = { largePolesCount = it },
                label = { Text("મોટા પોલ લોડિંગ સંખ્યા (₹૮/પોલ)", color = Color.Gray) },
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
            Spacer(modifier = Modifier.height(4.dp))
            Text("મોટા પોલ હેલ્પર્સ પસંદ કરો:", fontSize = 13.sp, color = PrimaryYellow)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                allHelpers.forEach { helper ->
                    val isSelected = selectedLargeHelpers.contains(helper)
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            if (isSelected) selectedLargeHelpers.remove(helper) else selectedLargeHelpers.add(helper)
                        },
                        label = { Text(helper, color = if (isSelected) PureBlack else Color.White) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryYellow,
                            containerColor = DarkCard
                        )
                    )
                }
            }
        }

        item {
            Button(
                onClick = {
                    val lines = linesCompleted.toIntOrNull() ?: 0
                    val smallPoles = smallPolesCount.toIntOrNull() ?: 0
                    val largePoles = largePolesCount.toIntOrNull() ?: 0

                    val newEntry = DailyWorkEntry(
                        dateString = dateStr,
                        linesCompleted = lines,
                        smallPolesCount = smallPoles,
                        largePolesCount = largePoles,
                        smallPoleHelpers = selectedSmallHelpers.toList(),
                        largePoleHelpers = selectedLargeHelpers.toList()
                    )

                    viewModel.saveWorkEntry(newEntry) {
                        Toast.makeText(context, "રેકોર્ડ સાચવવામાં આવ્યો", Toast.LENGTH_SHORT).show()
                        linesCompleted = ""
                        smallPolesCount = ""
                        largePolesCount = ""
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryYellow)
            ) {
                Text("હિસાબ સેવ કરો", color = PureBlack, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }

        item {
            Spacer(modifier = Modifier.height(10.dp))
            Text("પાછલા રેકોર્ડ્સ:", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PrimaryYellow)
        }

        items(workList) { entry ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = DarkCard)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "તારીખ: ${entry.dateString}", fontWeight = FontWeight.Bold, color = PrimaryYellow)
                        IconButton(onClick = { viewModel.deleteWorkEntry(entry.id) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                        }
                    }
                    Text("• લાઈન આવક (Husen): ₹${entry.lineEarnings}", color = Color.White, fontSize = 14.sp)
                    Text("• નાના પોલ કુલ: ₹${entry.smallPoleTotalEarnings} (${entry.smallPoleHelpers.size} જણા)", color = Color.LightGray, fontSize = 13.sp)
                    Text("• મોટા પોલ કુલ: ₹${entry.largePoleTotalEarnings} (${entry.largePoleHelpers.size} જણા)", color = Color.LightGray, fontSize = 13.sp)
                    Divider(color = Color.DarkGray, modifier = Modifier.padding(vertical = 6.dp))
                    Text(
                        text = "હુસેનની કુલ કમાણી (જુમાભાઈ સહિત): ₹${entry.husenTotalEarnings}",
                        fontWeight = FontWeight.Bold,
                        color = PrimaryYellow,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}
