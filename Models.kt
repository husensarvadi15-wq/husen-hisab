package com.sarvadiverse.husenhisab.model

import java.math.BigDecimal
import java.math.RoundingMode

enum class LedgerType {
    SMALL_POLE, LARGE_POLE
}

enum class EntryType {
    JAMA, UDHAR
}

data class LedgerEntry(
    val id: String = "",
    val ledgerType: LedgerType = LedgerType.SMALL_POLE,
    val personName: String = "",
    val amount: Double = 0.0,
    val type: EntryType = EntryType.JAMA,
    val note: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

data class DailyWorkEntry(
    val id: String = "",
    val dateString: String = "", // Format: YYYY-MM-DD
    val timestamp: Long = System.currentTimeMillis(),
    val linesCompleted: Int = 0,
    val smallPolesCount: Int = 0,
    val largePolesCount: Int = 0,
    val smallPoleHelpers: List<String> = emptyList(),
    val largePoleHelpers: List<String> = emptyList()
) {
    val lineEarnings: BigDecimal
        get() = BigDecimal(linesCompleted).multiply(BigDecimal(1000))

    val smallPoleTotalEarnings: BigDecimal
        get() = BigDecimal(smallPolesCount).multiply(BigDecimal(4))

    val largePoleTotalEarnings: BigDecimal
        get() = BigDecimal(largePolesCount).multiply(BigDecimal(8))

    val smallPoleSharePerHelper: BigDecimal
        get() = if (smallPoleHelpers.isNotEmpty()) {
            smallPoleTotalEarnings.divide(BigDecimal(smallPoleHelpers.size), 2, RoundingMode.HALF_EVEN)
        } else BigDecimal.ZERO

    val largePoleSharePerHelper: BigDecimal
        get() = if (largePoleHelpers.isNotEmpty()) {
            largePoleTotalEarnings.divide(BigDecimal(largePoleHelpers.size), 2, RoundingMode.HALF_EVEN)
        } else BigDecimal.ZERO

    val husenLineIncome: BigDecimal
        get() = lineEarnings

    val husenSmallPoleShare: BigDecimal
        get() = if (smallPoleHelpers.contains("Husen")) smallPoleSharePerHelper else BigDecimal.ZERO

    val jumabhaiSmallPoleShare: BigDecimal
        get() = if (smallPoleHelpers.contains("Jumabhai")) smallPoleSharePerHelper else BigDecimal.ZERO

    val husenLargePoleShare: BigDecimal
        get() = if (largePoleHelpers.contains("Husen")) largePoleSharePerHelper else BigDecimal.ZERO

    val jumabhaiLargePoleShare: BigDecimal
        get() = if (largePoleHelpers.contains("Jumabhai")) largePoleSharePerHelper else BigDecimal.ZERO

    val husenTotalEarnings: BigDecimal
        get() = husenLineIncome
            .add(husenSmallPoleShare)
            .add(jumabhaiSmallPoleShare)
            .add(husenLargePoleShare)
            .add(jumabhaiLargePoleShare)
}
