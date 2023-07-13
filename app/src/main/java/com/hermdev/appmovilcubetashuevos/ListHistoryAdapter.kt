package com.hermdev.appmovilcubetashuevos
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// Clase adaptadora para el RecyclerView que muestra los datos de la tabla
class ListHistoryAdapter(private val dataList: List<TableRowData>) : RecyclerView.Adapter<ListHistoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Inflar el diseño de la fila de la tabla
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_table_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Obtener los datos de la fila en la posición actual
        val rowData = dataList[position]
        // Vincular los datos a las vistas en el ViewHolder
        holder.bind(rowData)
    }

    override fun getItemCount(): Int {
        // Devolver la cantidad total de filas de datos
        return dataList.size
    }

    // Clase ViewHolder que contiene las referencias a las vistas de cada fila
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewFecha: TextView = itemView.findViewById(R.id.textViewFecha)
        private val textViewHora: TextView = itemView.findViewById(R.id.textViewHora)
        private val textViewCubetas: TextView = itemView.findViewById(R.id.textViewCubetas)
        private val textViewHuevos: TextView = itemView.findViewById(R.id.textViewHuevos)

        // Método para vincular los datos de la fila a las vistas
        fun bind(rowData: TableRowData) {
            textViewFecha.text = rowData.date
            textViewHora.text = rowData.hour
            textViewCubetas.text = rowData.cuvettes
            textViewHuevos.text = rowData.eggs
        }
    }
}
