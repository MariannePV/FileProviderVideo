package com.example.fileprovider

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.icu.util.LocaleData
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.MediaController
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.viewbinding.BuildConfig
import com.example.fileprovider.databinding.ActivityMainBinding
import java.io.File
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    //Binding
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //Configuració del botó per capturar el vídeo
        binding.btnTakeVideo.setOnClickListener()
        {
            //Fent servir File Provider ara haurem de gestionar millor el retorn de l'Intent de la Càmera
            //ACTION_IMAGE_CAPTURE per capturar fotos
            //ACTION_VIDEO_CAPTURE per capturar vídeos
            val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE).also{
                it.resolveActivity(packageManager).also{component->
                    //File pot ser un fitxer emmagatzemat a la memòria, no cal que estigui al magatzem del dispositiu

                    //Creem i gestionem l'arxiu de vídeo
                    createVideoFile()

                    //Uri sí que queda emmagatzemat a una ruta del magatzem del dispositiu
                    //Obtenim la Uri del vídeo fent ús del FileProvider
                    val videoUri: Uri = FileProvider.getUriForFile(this,"com.example.fileprovider.fileprovider", file)

                    //Agreguem l'Uri a l'intent
                    it.putExtra(MediaStore.EXTRA_OUTPUT, videoUri)
                    //Hem reanomenat l'iterador per defecte a component per poder continuar tinguen accés a l'iterador it que fa referència a l'intent. Sinó no ens deixaria
                }
            }
            //Ara cridarem el launch passant el l'intent modificat
            startForResult.launch(intent)
            //also vol dir que sobre aquest intent també farem més coses(also)

        }
    }

    //Creem una variable global perquè file el necessitarem a més d'un lloc.
    private lateinit var file:File

    //Mètode per crear un arxiu de vídeo en el directori de películes
    private fun createVideoFile() {
        //Necessitem accedir a un directori extern
        //Enviroment.DIRECTORY_PICTURES retorna la ruta on es guarden les images al dispositiu
        //Environment.DIRECTORY_MOVIES retorna la ruta on es guarden els vídeos al dispositiu
        val dir = getExternalFilesDir(Environment.DIRECTORY_MOVIES)

        //Crearem un fitxer amb la data actual, el meu nom i l'extensió corresponent, en aquest cas .mp4
        val dataAvui: String = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())

        file = File.createTempFile("MARIANNEPULGAR_${dataAvui}_",".mp4", dir)
    }

    //Resultat de la captura de vídeo
    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
    {
        //Configurem el reproductor de vídeo i mostrem el vídeo que hem gravat
        val mediaController = MediaController(this)
        val videoUri = Uri.parse(file.toString())
        val videoView = binding.visualVideo
        videoView.setVideoURI(videoUri)

        mediaController.setAnchorView(videoView)
        videoView.setMediaController(mediaController)

        videoView.start()
    }
}