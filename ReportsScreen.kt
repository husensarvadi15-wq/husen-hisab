package com.sarvadiverse.husenhisab.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.sarvadiverse.husenhisab.model.DailyWorkEntry
import com.sarvadiverse.husenhisab.ui.theme.DarkCard
import com.sarvadiverse.husenhisab.ui.theme.PrimaryYellow
import com.sarvadiverse.husenhisab.ui.theme.PureBlack
import com.sarvadiverse.husenhisab.viewmodel.AppViewModel
import java.io.File
import java.io.FileOutputStream

@Composable
fun ReportsScreen(viewModel: AppViewModel) {
    val context = LocalContext.current
    val workList by viewModel.workEntries.collectAsState()

    val totalWorkIncome = workList.fold(java.math.BigDecimal.ZERO) { acc, e -> acc.add(e.husenTotalEarnings) }
    val totalSmallPoles = workList.sumOf { it.smallPolesCount }
    val totalLargePoles = workList.sumOf { it.largePolesCount }
    val totalLines = workList.sumOf { it.linesCompleted }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PureBlack)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("કુલ હિસાબ રિપોર્ટ", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = PrimaryYellow)

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("કુલ નાની લાઈનો: $totalLines", color = Color.White, fontSize = 15.sp)
                Text("કુલ નાના પોલ લોડિંગ: $totalSmallPoles", color = Color.White, fontSize = 15.sp)
                Text("કુલ મોટા પોલ લોડિંગ: $totalLargePoles", color = Color.White, fontSize = 15.sp)
                Divider(color = Color.DarkGray, modifier = Modifier.padding(vertical = 4.dp))
                Text(
                    "હુસેનની સર્વકુલ આવક: ₹$totalWorkIncome",
                    color = PrimaryYellow,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Button(
            onClick = {
                exportDataToCSV(context, workList)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryYellow),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text("Excel (CSV) ફાઈલ ડાઉનલોડ / શેર કરો", color = PureBlack, fontWeight = FontWeight.Bold)
        }
    }
}

fun exportDataToCSV(context: Context, list: List<DailyWorkEntry>) {
    try {
        val fileName = "husen_hisab_report.csv"
        val file = File(context.cacheDir, fileName)
        val fos = FileOutputStream(file)
        val writer = fos.bufferedWriter()

        writer.write("Tariqh,Nani Line,Nani Line Income,Small Poles,Large Poles,Husen Total Income\n")
        list.forEach { item ->
            writer.write("${item.dateString},${item.linesCompleted},${item.lineEarnings},${item.smallPolesCount},${item.largePolesCount},${item.husenTotalEarnings}\n")
        }
        writer.flush()
        writer.close()

        val uri: Uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(sendIntent, "Excel ફાઇલ શેર કરો"))
    } catch (e: Exception) {
        Toast.makeText(context, "એક્સપોર્ટ કરવામાં ભૂલ આવી: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}
