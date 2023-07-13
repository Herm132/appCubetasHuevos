package com.hermdev.appmovilcubetashuevos


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import java.io.IOException
import java.util.Scanner

class HistoryActivity : AppCompatActivity() {
    //Crear variable que esta en la vista con su tipo respectivo
    private lateinit var rvList: RecyclerView
    private lateinit var btnReturned: AppCompatButton
    //Clase iniciar actividad
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        initComponents()
        initListeners()
        initUI()
    }
    //Vincular variables con la interfaz para manipularlas
    private fun initListeners() {
        btnReturned.setOnClickListener {
            finish()
        }
    }
    // Crear acciones de click de cada botón
    private fun initComponents() {
        rvList = findViewById(R.id.rvList)
        btnReturned = findViewById(R.id.btnReturned)
    }
    // Rellenar  el rvList con los datos del archivo
    private fun initUI() {
        val tableData = loadTableDataFromFile()
        val listHistoryAdapter = ListHistoryAdapter(tableData)
        rvList.adapter = listHistoryAdapter
        // Adjuntar un LinearLayoutManager al RecyclerView
        rvList.layoutManager = LinearLayoutManager(this)

    }



  // Carga los datos de la tabla desde el archivo "table_data.txt".

    private fun loadTableDataFromFile(): List<TableRowData> {
        val filename = "table_data.txt"
        val dataList = mutableListOf<TableRowData>()

        try {
            // Crear un objeto File con la ruta del directorio de archivos de la aplicación y el nombre del archivo
            val file = File(filesDir, filename)
            // Crear un Scanner para leer el archivo
            val reader = Scanner(file)

            // Leer el archivo en orden inverso para obtener los registros más recientes primero
            val lines = mutableListOf<String>()
            while (reader.hasNextLine()) {
                lines.add(reader.nextLine())
            }
            reader.close()

            // Recorrer las líneas del archivo en orden inverso
            for (i in lines.size - 1 downTo 0) {
                val line = lines[i]
                val values = line.split(",")

                // Verificar si la línea contiene los cuatro valores esperados
                if (values.size == 4) {
                    // Obtener los valores individuales de la línea
                    val fecha = values[0]
                    val hora = values[1]
                    val cubetas = values[2]
                    val huevos = values[3]

                    // Crear un objeto TableRowData con los valores y agregarlo a la lista de datos
                    val rowData = TableRowData(fecha, hora, cubetas, huevos)
                    dataList.add(rowData)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return dataList
    }




}