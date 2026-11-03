package com.sarvadiverse.husenhisab.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.sarvadiverse.husenhisab.model.DailyWorkEntry
import com.sarvadiverse.husenhisab.model.EntryType
import com.sarvadiverse.husenhisab.model.LedgerEntry
import com.sarvadiverse.husenhisab.model.LedgerType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AppViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val _workEntries = MutableStateFlow<List<DailyWorkEntry>>(emptyList())
    val workEntries = _workEntries.asStateFlow()

    private val _smallPoleLedger = MutableStateFlow<List<LedgerEntry>>(emptyList())
    val smallPoleLedger = _smallPoleLedger.asStateFlow()

    private val _largePoleLedger = MutableStateFlow<List<LedgerEntry>>(emptyList())
    val largePoleLedger = _largePoleLedger.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val currentUserId: String?
        get() = auth.currentUser?.uid

    init {
        auth.addAuthStateListener { firebaseAuth ->
            if (firebaseAuth.currentUser != null) {
                loadAllData()
            } else {
                _workEntries.value = emptyList()
                _smallPoleLedger.value = emptyList()
                _largePoleLedger.value = emptyList()
            }
        }
    }

    fun loadAllData() {
        val uid = currentUserId ?: return
        _isLoading.value = true

        db.collection("users").document(uid).collection("work_entries")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val entries = snapshot.documents.mapNotNull { doc ->
                        val lines = doc.getLong("linesCompleted")?.toInt() ?: 0
                        val smallPoles = doc.getLong("smallPolesCount")?.toInt() ?: 0
                        val largePoles = doc.getLong("largePolesCount")?.toInt() ?: 0
                        @Suppress("UNCHECKED_CAST")
                        val smallHelpers = doc.get("smallPoleHelpers") as? List<String> ?: emptyList()
                        @Suppress("UNCHECKED_CAST")
                        val largeHelpers = doc.get("largePoleHelpers") as? List<String> ?: emptyList()
                        val dateStr = doc.getString("dateString") ?: ""
                        val ts = doc.getLong("timestamp") ?: System.currentTimeMillis()

                        DailyWorkEntry(
                            id = doc.id,
                            dateString = dateStr,
                            timestamp = ts,
                            linesCompleted = lines,
                            smallPolesCount = smallPoles,
                            largePolesCount = largePoles,
                            smallPoleHelpers = smallHelpers,
                            largePoleHelpers = largeHelpers
                        )
                    }
                    _workEntries.value = entries
                }
                _isLoading.value = false
            }

        db.collection("users").document(uid).collection("ledgers")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { doc ->
                        val ledgerTypeStr = doc.getString("ledgerType") ?: LedgerType.SMALL_POLE.name
                        val typeStr = doc.getString("type") ?: EntryType.JAMA.name
                        LedgerEntry(
                            id = doc.id,
                            ledgerType = LedgerType.valueOf(ledgerTypeStr),
                            personName = doc.getString("personName") ?: "",
                            amount = doc.getDouble("amount") ?: 0.0,
                            type = EntryType.valueOf(typeStr),
                            note = doc.getString("note") ?: "",
                            timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis()
                        )
                    }
                    _smallPoleLedger.value = list.filter { it.ledgerType == LedgerType.SMALL_POLE }
                    _largePoleLedger.value = list.filter { it.ledgerType == LedgerType.LARGE_POLE }
                }
            }
    }

    fun saveWorkEntry(entry: DailyWorkEntry, onComplete: () -> Unit) {
        val uid = currentUserId ?: return
        val docRef = if (entry.id.isEmpty()) {
            db.collection("users").document(uid).collection("work_entries").document()
        } else {
            db.collection("users").document(uid).collection("work_entries").document(entry.id)
        }

        val data = hashMapOf(
            "dateString" to entry.dateString,
            "timestamp" to entry.timestamp,
            "linesCompleted" to entry.linesCompleted,
            "smallPolesCount" to entry.smallPolesCount,
            "largePolesCount" to entry.largePolesCount,
            "smallPoleHelpers" to entry.smallPoleHelpers,
            "largePoleHelpers" to entry.largePoleHelpers
        )

        docRef.set(data).addOnSuccessListener { onComplete() }
    }

    fun deleteWorkEntry(entryId: String) {
        val uid = currentUserId ?: return
        db.collection("users").document(uid).collection("work_entries").document(entryId).delete()
    }

    fun saveLedgerEntry(entry: LedgerEntry, onComplete: () -> Unit) {
        val uid = currentUserId ?: return
        val docRef = if (entry.id.isEmpty()) {
            db.collection("users").document(uid).collection("ledgers").document()
        } else {
            db.collection("users").document(uid).collection("ledgers").document(entry.id)
        }

        val data = hashMapOf(
            "ledgerType" to entry.ledgerType.name,
            "personName" to entry.personName,
            "amount" to entry.amount,
            "type" to entry.type.name,
            "note" to entry.note,
            "timestamp" to entry.timestamp
        )

        docRef.set(data).addOnSuccessListener { onComplete() }
    }

    fun deleteLedgerEntry(entryId: String) {
        val uid = currentUserId ?: return
        db.collection("users").document(uid).collection("ledgers").document(entryId).delete()
    }

    fun calculateTodayEarnings(): BigDecimal {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        return _workEntries.value
            .filter { it.dateString == today }
            .fold(BigDecimal.ZERO) { acc, item -> acc.add(item.husenTotalEarnings) }
    }

    fun calculateThisMonthEarnings(): BigDecimal {
        val thisMonth = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date())
        return _workEntries.value
            .filter { it.dateString.startsWith(thisMonth) }
            .fold(BigDecimal.ZERO) { acc, item -> acc.add(item.husenTotalEarnings) }
    }

    fun logout() {
        auth.signOut()
    }
}
