package com.hermdev.appmovilcubetashuevos

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.ScaleGestureDetector
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import okhttp3.*
import org.json.JSONObject
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import android.util.Base64
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContentProviderCompat.requireContext

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream

class MainActivity : AppCompatActivity() {
    //Crear variable que esta en la vista con su tipo respectivo
    private lateinit var ivPhoto: AppCompatImageView
    private lateinit var btnCamera: AppCompatImageButton
    private lateinit var btnHistory: AppCompatImageButton
    private lateinit var btnChosser: AppCompatImageButton
    private lateinit var btnInformation: AppCompatImageButton
    private lateinit var tvCuvettes: AppCompatTextView
    private lateinit var tvEggs:AppCompatTextView
    private lateinit var scaleGestureDetector: ScaleGestureDetector
    private var scaleFactor = 1.0f

    private var lastTouchX: Float = 0.toFloat()
    private var lastTouchY: Float = 0.toFloat()
    private var posX: Float = 0.toFloat()
    private var posY: Float = 0.toFloat()



    //Clase iniciar actividad
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initComponents()
        initListeners()
    }

    //Vincular variables con la interfaz para manipularlas
    private fun initComponents() {
        ivPhoto = findViewById(R.id.ivPhoto)
        btnCamera = findViewById(R.id.btnCamera)
        btnHistory = findViewById(R.id.btnHistory)
        btnInformation = findViewById(R.id.btnInformation)
        tvCuvettes=findViewById(R.id.tvCuvettes)
        tvEggs= findViewById(R.id.tvEggs)
        btnChosser = findViewById(R.id.btnChosser)
        scaleGestureDetector = ScaleGestureDetector(this, ScaleListener())
    }

    // Crear acciones de click de cada botón
    private fun initListeners() {
        // Listener del botón "Historial"
        btnHistory.setOnClickListener {
            // Crear una intención para abrir la actividad HistoryActivity
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
        }

        // Listener del botón "Cámara"
        btnCamera.setOnClickListener {
            // Abrir la cámara
            openCamera()
            tvEggs.text = ""
            tvCuvettes.text = ""

        }

        // Listener del botón "Información"
        btnInformation.setOnClickListener {
            openPageWeb()


        }
        // Listener del botón "Chosser"
        btnChosser.setOnClickListener {
            openImagePicker()
            tvEggs.text = ""
            tvCuvettes.text = ""
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, 2)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            when (requestCode) {
                1 -> { // Código de solicitud para la cámara
                    val extras: Bundle? = data?.extras
                    val imgBitmap: Bitmap? = extras?.get("data") as Bitmap?
                    getImageModel(imgBitmap)
                    getNumModel(imgBitmap)
                }
                2 -> { // Código de solicitud para la galería
                    val selectedImageUri = data?.data
                    val imgBitmap: Bitmap? = if (selectedImageUri != null) {
                        val inputStream = contentResolver.openInputStream(selectedImageUri)
                        BitmapFactory.decodeStream(inputStream)
                    } else {
                        null
                    }
                    getImageModel(imgBitmap)
                    getNumModel(imgBitmap)
                }
            }
        }
    }


    // Crear un nuevo registro en el archivo
    private fun newRegister(number: Int) {
        // Obtener la fecha y hora actual
        val currentDate = getCurrentDate()
        val currentTime = getCurrentTime()

        // Calcular el número de cubetas y huevos
        val cuvettes = number
        val eggs = cuvettes * 30


        // Crear un objeto TableRowData con los datos del nuevo registro
        val tableRowData =
            TableRowData(currentDate, currentTime, cuvettes.toString(), eggs.toString())

        // Guardar el nuevo registro en el archivo
        saveTableRowDataToFile(tableRowData)

        runOnUiThread {
            tvEggs.text = eggs.toString()
            tvCuvettes.text = cuvettes.toString()
        }
    }

    // Obtener la fecha actual en formato de cadena de texto
    private fun getCurrentDate(): String {
        // Obtener la fecha actual del calendario
        val currentDate = Calendar.getInstance().time
        // Crear un formato de fecha con el patrón "yyyy-MM-dd"
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        // Formatear la fecha actual utilizando el formato y devolverla como cadena de texto
        return dateFormat.format(currentDate)
    }


    // Obtener la hora actual en formato de cadena de texto
    private fun getCurrentTime(): String {
        // Obtener la hora actual del calendario
        val currentTime = Calendar.getInstance().time
        // Crear un formato de hora con el patrón "HH:mm:ss"
        val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        // Formatear la hora actual utilizando el formato y devolverla como cadena de texto
        return timeFormat.format(currentTime)
    }

    // Guarda los datos de una fila en el archivo "table_data.txt".
    private fun saveTableRowDataToFile(rowData: TableRowData) {
        val filename = "table_data.txt"

        try {
            // Crear un objeto File con la ruta del directorio de archivos de la aplicación y el nombre del archivo
            val file = File(filesDir, filename)
            // Crear un FileWriter con el archivo y el modo de agregado (true)
            val writer = FileWriter(file, true)

            // Escribir los datos de la fila en el archivo, separados por comas y seguidos de un salto de línea
            writer.write("${rowData.date},${rowData.hour},${rowData.cuvettes},${rowData.eggs}\n")

            // Cerrar el FileWriter para liberar los recursos
            writer.close()

        } catch (e: IOException) {
            e.printStackTrace()
            // Manejar el error en caso de que ocurra al guardar el archivo
        }
    }


    // Abre la cámara para capturar una imagen.

    private fun openCamera() {
        // Crear una intención para abrir la cámara
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Verificar si hay una actividad de cámara disponible para manejar la intención
        if (intent.resolveActivity(packageManager) != null) {
            // Iniciar la actividad de la cámara y esperar el resultado (código de solicitud 1)
            startActivityForResult(intent, 1)

        }
    }

    //Se llama cuando se recibe un resultado de una actividad iniciada con `startActivityForResult()`.

    fun convertBitmapToBase64(bitmap: Bitmap?): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        if (bitmap != null) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        }
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    //Enviar foto al modelo
    private fun getNumModel(imgBits: Bitmap?) {

        // Creamos un cliente
        val okHttpClient = OkHttpClient()
        //Creamos objeto json
        val jsonObject = JSONObject()

        val base64Image = convertBitmapToBase64(imgBits)
        //Definir parametros
        jsonObject.put("datos", base64Image)
        //Especificar el tipo de contenido en el encabezado
        val jsonMediaType = "application/json; charset=utf-8".toMediaType()
        //Definir el body de la peticion
        val requestBody = jsonObject.toString().toRequestBody(jsonMediaType)
        // Construimos la peticion con un request
        val solicitud = Request.Builder()
                // 192.168.200.23:5000
            .url("http://192.168.200.11:5000 /numcubetas")
            .post(requestBody)
            .build()

        // Hacemos una llamada de forma asincrona
        okHttpClient.newCall(solicitud).enqueue(object : Callback {
            //En caso de que falle
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    // Mostrar un mensaje breve en la pantalla
                    Toast.makeText(
                        this@MainActivity,
                        "Error al conectar con el servidor",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e("Flask", "Error de conexión: ${e.message}")
                }
            }

            //En caso de que se comunique
            override fun onResponse(call: Call, response: Response) {

                if (!response.isSuccessful) {
                    // Handle the case where the response is not successful
                    return
                }
                val responseBody = response.body?.string()
                try {
                    val jsonObject = JSONObject(responseBody)
                    val number = jsonObject.getInt("number")
                    //Colocar los valores en la vista principal
                    newRegister(number)
                    Log.i("Flask","$number")
                } catch (e: Exception) {
                    // Handle JSON parsing or other exceptions here
                }
            }
        })
    }

    private fun getImageModel2(imgBits: Bitmap?) {
        val url = "http://192.168.100.23:5000/numcubetas"
        val client = OkHttpClient()
        //Creamos objeto json
        val jsonObject = JSONObject()
        val base64Image = convertBitmapToBase64(imgBits)
        //Definir parametros
        jsonObject.put("datos", base64Image)
        //Especificar el tipo de contenido en el encabezado
        val jsonMediaType = "application/json; charset=utf-8".toMediaType()
        //Definir el body de la peticion
        val requestBody = jsonObject.toString().toRequestBody(jsonMediaType)
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    // Mostrar un mensaje breve en la pantalla
                    Toast.makeText(
                        this@MainActivity,
                        "Error al conectar con el servidor",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e("Flask", "Error de conexión: ${e.message}")
                }
            }
            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    Log.e("Flask", "Error en la respuesta")
                }

                val inputStream = response.body?.byteStream()
                val bitmap = BitmapFactory.decodeStream(inputStream)

                // Update the ImageView on the main thread
                runOnUiThread {
                    ivPhoto.setImageBitmap(bitmap)
                }
            }
        })

    }

    private fun getImageModel(imgBits: Bitmap?) {
        val serverUrl = "http://192.168.200.11:5000 /imagenCubetas"


        val client = OkHttpClient()

        val base64Image = convertBitmapToBase64(imgBits)

        val jsonObject = JSONObject().apply {
            put("datos", base64Image)
        }

        val jsonMediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = jsonObject.toString().toRequestBody(jsonMediaType)




        val request = Request.Builder()
            .url(serverUrl)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(
                        this@MainActivity,
                        "Error al conectar con el servidor",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e("Flask", "Error de conexión: ${e.message}")
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) {
                        Log.e("Flask", "Error en la respuesta: ${response.code}")
                    } else {
                        val inputStream = it.body?.byteStream()
                        val bitmap = BitmapFactory.decodeStream(inputStream)

                        runOnUiThread {
                            ivPhoto.setImageBitmap(bitmap)
                        }
                    }
                }
            }
        })
    }




    fun openPageWeb() {
        val url = "https://uceedu-my.sharepoint.com/:b:/g/personal/hereyes_uce_edu_ec/EcjEbt1okphIibcC0bwDlQQB1j1uZD9ccKc0wheR1QWCgg?e=bicdco" // Reemplaza con la URL de la página web que deseas abrir
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }
    override fun onTouchEvent(event: MotionEvent): Boolean {
        scaleGestureDetector.onTouchEvent(event)
        return true
    }
    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(scaleGestureDetector: ScaleGestureDetector): Boolean {
            scaleFactor *= scaleGestureDetector.scaleFactor
            scaleFactor = Math.max(0.1f, Math.min(scaleFactor, 10.0f)) // Limit scale factor

            ivPhoto.scaleX = scaleFactor
            ivPhoto.scaleY = scaleFactor
            return true
        }
    }



}






