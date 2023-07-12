package com.hermdev.appmovilcubetashuevos

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {
    //Crear variable que esta en la vista con su tipo respectivo
    private lateinit var ivPhoto: AppCompatImageView
    private lateinit var btnCamera: AppCompatImageButton
    private lateinit var btnHistory: AppCompatImageButton
    private lateinit var btnInformation: AppCompatImageButton


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

    }

    // Crear acciones de click de cada botón
    private fun initListeners() {

        btnCamera.setOnClickListener {
            // val intent = Intent(this, DetectorActivity::class.java)
            //startActivity(intent)
            openCamare()

        }
        btnHistory.setOnClickListener {
            // val intent = Intent(this, HistoryActivity::class.java)
            //  startActivity(intent)

        }
        btnInformation.setOnClickListener {

        }
    }

    @SuppressLint("SuspiciousIndentation")
    private fun openCamare() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, 1)
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            val extras: Bundle? = data?.extras
            val imgBitmap: Bitmap? = extras?.get("data") as Bitmap?
            //sendPhoto(imgBitmap)
            ivPhoto.setImageBitmap(imgBitmap)
        }
    }

    fun sendPhoto(imgBits: Bitmap?) {

        // Creamos un cliente
        val okHttpClient = OkHttpClient()
        //Creamos objeto json
        val jsonObject = JSONObject()
        //Definir parametros
        jsonObject.put("datos", imgBits)
        //Especificar el tipo de contenido en el encabezado
        val jsonMediaType = "application/json; charset=utf-8".toMediaType()
        //Definir el body de la peticion
        val requestBody = jsonObject.toString().toRequestBody(jsonMediaType)
        // Construimos la peticion con un request
        val solicitud = Request.Builder()
            .url("http://192.168.100.5:5000/valor")
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
                    Log.i("Flask", "$e")
                }
            }

            //En caso de que se comunique
            override fun onResponse(call: Call, response: Response) {
                GlobalScope.launch(Dispatchers.Main) {
                    val respuesta = response.body?.string()
                    val jsonResponse = JSONObject(respuesta)
                    val textoRespuesta = jsonResponse.optString("respuesta")
                    //tvTotal.text = textoRespuesta
                    Log.i("Flask", "$textoRespuesta")
                }
            }
        })

    }
}