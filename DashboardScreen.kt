package com.sarvadiverse.husenhisab.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sarvadiverse.husenhisab.model.EntryType
import com.sarvadiverse.husenhisab.ui.theme.DarkCard
import com.sarvadiverse.husenhisab.ui.theme.PrimaryYellow
import com.sarvadiverse.husenhisab.ui.theme.PureBlack
import com.sarvadiverse.husenhisab.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: AppViewModel,
    onNavigateToWork: () -> Unit,
    onNavigateToLedger: (isLarge: Boolean) -> Unit,
    onNavigateToReports: () -> Unit,
    onLogout: () -> Unit
) {
    val todayEarnings = viewModel.calculateTodayEarnings()
    val monthEarnings = viewModel.calculateThisMonthEarnings()
    val smallLedger by viewModel.smallPoleLedger.collectAsState()
    val largeLedger by viewModel.largePoleLedger.collectAsState()

    val totalSmallJama = smallLedger.filter { it.type == EntryType.JAMA }.sumOf { it.amount }
    val totalSmallUdhar = smallLedger.filter { it.type == EntryType.UDHAR }.sumOf { it.amount }

    val totalLargeJama = largeLedger.filter { it.type == EntryType.JAMA }.sumOf { it.amount }
    val totalLargeUdhar = largeLedger.filter { it.type == EntryType.UDHAR }.sumOf { it.amount }

    val grandJama = totalSmallJama + totalLargeJama
    val grandUdhar = totalSmallUdhar + totalLargeUdhar
    val grandBalance = grandJama - grandUdhar

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("હુસેન હિસાબ", fontWeight = FontWeight.Bold, color = PrimaryYellow) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PureBlack),
                actions = {
                    IconButton(onClick = {
                        viewModel.logout()
                        onLogout()
                    }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Logout", tint = PrimaryYellow)
                    }
                }
            )
        },
        containerColor = PureBlack
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onNavigateToWork,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryYellow),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("+ દૈનિક કામ", color = PureBlack, fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = onNavigateToReports,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = DarkCard),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("રિપોર્ટ્સ", color = PrimaryYellow, fontWeight = FontWeight.Bold)
                }
            }

            DashboardStatCard(title = "આજની કુલ કમાણી", value = "₹$todayEarnings", highlight = true)
            DashboardStatCard(title = "આ મહિનાની કુલ કમાણી", value = "₹$monthEarnings", highlight = true)

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                DashboardStatCard(
                    title = "નાના પોલ જમા/ઉધાર",
                    value = "જ: ₹$totalSmallJama\nઉ: ₹$totalSmallUdhar",
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateToLedger(false) }
                )
                DashboardStatCard(
                    title = "મોટા પોલ જમા/ઉધાર",
                    value = "જ: ₹$totalLargeJama\nઉ: ₹$totalLargeUdhar",
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateToLedger(true) }
                )
            }

            DashboardStatCard(title = "કુલ જમા રકમ", value = "₹$grandJama", valueColor = Color(0xFF00E676))
            DashboardStatCard(title = "કુલ ઉધાર રકમ", value = "₹$grandUdhar", valueColor = Color(0xFFFF5252))
            DashboardStatCard(
                title = "બાકી રકમ (Balance)",
                value = "₹$grandBalance",
                highlight = true,
                valueColor = if (grandBalance >= 0) PrimaryYellow else Color(0xFFFF5252)
            )
        }
    }
}

@Composable
fun DashboardStatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    highlight: Boolean = false,
    valueColor: Color = Color.White,
    onClick: (() -> Unit)? = null
) {
    Card(
        onClick = { onClick?.invoke() },
        enabled = onClick != null,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCard)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, fontSize = 13.sp, color = if (highlight) PrimaryYellow else Color.LightGray)
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = valueColor)
        }
    }
}
