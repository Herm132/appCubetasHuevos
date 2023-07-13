package com.hermdev.appmovilcubetashuevos

// Definición de la clase TableRowData para representar los datos de una fila
data class TableRowData(
    val date: String,   // Fecha de la fila
    val hour: String,    // Hora de la fila
    val cuvettes: String, // Número de cubetas de la fila
    val eggs: String   // Número de huevos de la fila
)
