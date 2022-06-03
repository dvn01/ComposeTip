package com.dairymaster.composetip.util

fun calculateTotalTip(totalBill: Double, tipPercentage: Int): Double {
    return if (totalBill > 1 && totalBill.toString().isNotEmpty()) {
        totalBill * tipPercentage / 100
    }  else {
        0.0
    }
}

fun calculateTotal(totalBill: Double, splitBy: Int, percent: Int): Double{
    val bill = calculateTotalTip(totalBill= totalBill, tipPercentage = percent) + totalBill
    return (bill/splitBy)
}