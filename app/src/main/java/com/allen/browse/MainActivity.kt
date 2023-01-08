package com.allen.browse

import android.Manifest
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.text.TextUtils
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.allen.browse.Calculation.Hasilnya
import com.allen.browse.Data.VM
import com.allen.browse.Dialog.DialogClass
import com.allen.browse.databinding.ActivityMainBinding
import java.io.File
import kotlin.math.pow

class MainActivity : AppCompatActivity() {
    var izinstate = 0
    private val reCode = 200
    private val VMget: VM by viewModels()
    /*  private lateinit var binding: ActivityMainBinding*/

    @RequiresApi(Build.VERSION_CODES.N)
    fun calculation() {
        val dx = VMget.dx
        val dy = VMget.dy
        //mencari mean
        VMget.MeanX = dx.sum().div(dx.count())
        VMget.MeanY = dy.sum().div(dy.count())
        //mencari 14a1 dan 62a1
        dy.forEach {
            VMget.a1 += it
        }
        for (i in 0 until dx.size) {
            VMget.a2 += dx[i] * dy[i]
        }
        VMget.variance = dx.stream().mapToDouble {
            (it - VMget.MeanX).pow(2)
        }.sum()

        //Covariance
        for (i in 0 until dx.size) {
            val xPart = dx[i] - VMget.MeanX
            val yPart = dy[i] - VMget.MeanY
            VMget.covariance += xPart * yPart
        }
        VMget.beta1 = VMget.covariance / VMget.variance
        VMget.beta0 = VMget.MeanY - (VMget.MeanX * VMget.beta1)
        for (i in 0 until dx.size) {
            val calculate = VMget.beta0 + (VMget.beta1 * dx[i])
            VMget.predicted.add(i, calculate)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainDisplay()
        }
        /* binding = ActivityMainBinding.inflate(layoutInflater)*/
        /*setContentView(binding.root)*/
        val intens = Intent(Intent.ACTION_GET_CONTENT)
        intens.addCategory(Intent.CATEGORY_OPENABLE)
        intens.type = "text/*"
        setupPermission()
        /*binding.apply {
            access.setOnClickListener {
                if (izinstate != 1) {
                    val dialog = DialogClass().show(supportFragmentManager, "")
                } else {
                    startActivityForResult(Intent.createChooser(intens, "Select a File"), 111)
                }

            }
        }*/
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun getdata(x: Uri?) {
        val dx = VMget.dx
        val dy = VMget.dy
        val selectedPath = x?.let { getRealPath(this@MainActivity, it) }
        val fileku = File("$selectedPath")
        fileku.forEachLine {
            val split = it.split(",")
            dx.add(split[0].toDouble())
            dy.add(split[1].toDouble())
        }
        calculation()
        val transfer = arrayListOf<Double>()
        val TransferX = arrayListOf<Double>()
        val TransferY = arrayListOf<Double>()
        for (i in 0 until VMget.dx.size) {
            transfer.add(i, VMget.predicted[i])
            TransferX.add(i, VMget.dx[i])
            TransferY.add(i, VMget.dy[i])
        }
        val intens = Intent(this@MainActivity, Hasilnya::class.java)
        intens.putExtra("Transfers", transfer)
        intens.putExtra("TransfersX", TransferX)
        intens.putExtra("TransfersY", TransferY)
        startActivity(intens)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 111) {
            val datawhole = data?.data
            datawhole?.let {
                getdata(it)
            }
        }
    }

    // Perizinan
    fun setupPermission() {
        val izin =
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        if (izin != PackageManager.PERMISSION_GRANTED) {
            izinstate = 0
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            makeIzin()
        } else {
            izinstate = 1
        }
    }

    private fun makeIzin() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            reCode
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            reCode -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Izin tidak diperbolehkan oleh User", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(this, "Izin diberikan oleh User", Toast.LENGTH_SHORT).show()
                    izinstate = 1
                }
            }
        }
    }

    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    private fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri.authority
    }

    private fun getRealPath(context: Context, uri: Uri): String? {
        val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]

                if ("primary".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                }
            } else if (isDownloadsDocument(uri)) {
                var cursor: Cursor? = null
                try {
                    cursor = context.contentResolver.query(
                        uri,
                        arrayOf(MediaStore.MediaColumns.DISPLAY_NAME),
                        null,
                        null,
                        null
                    )
                    cursor?.moveToNext()
                    val fileName = cursor?.getString(0)
                    val path = Environment.getExternalStorageDirectory()
                        .toString() + "/Download/" + fileName
                    if (!TextUtils.isEmpty(path)) {
                        return path
                    }
                } finally {
                    cursor?.close()
                }
                val id = DocumentsContract.getDocumentId(uri)
                if (id.startsWith("raw:")) {
                    return id.replaceFirst("raw:".toRegex(), "")
                }
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads"),
                    java.lang.Long.valueOf(id)
                )

                return getDataColumn(context, contentUri, null, null)
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]

                var contentUri: Uri? = null
                when (type) {
                    "image" -> contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    "video" -> contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    "audio" -> contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }

                val selection = "_id=?"
                val selectionArgs = arrayOf(split[1])

                return getDataColumn(context, contentUri, selection, selectionArgs)
            }// MediaProvider
            // DownloadsProvider
        } else if ("content".equals(uri.scheme!!, ignoreCase = true)) {

            // Return the remote address
            return if (isGooglePhotosUri(uri)) uri.lastPathSegment else getDataColumn(
                context,
                uri,
                null,
                null
            )
        } else if ("file".equals(uri.scheme!!, ignoreCase = true)) {
            return uri.path
        }// File
        // MediaStore (and general)
        return null
    }

    private fun getDataColumn(
        context: Context, uri: Uri?, selection: String?,
        selectionArgs: Array<String>?
    ): String? {

        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)

        try {
            cursor =
                context.contentResolver.query(uri!!, projection, selection, selectionArgs, null)
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(index)
            }
        } finally {
            cursor?.close()
        }
        return null
    }

}

@Composable
fun MainDisplay() {
    val context = LocalContext.current
    Scaffold(topBar = {
        TopAppBar(title = {
            Text(text = stringResource(R.string.app_title))
        })
    }, floatingActionButton = {
        FloatingActionButton(onClick = {
            Toast.makeText(
                context,
                "Tekan pada tombol diatas untuk import data .csv",
                Toast.LENGTH_SHORT
            ).show()
        }) {
            Image(
                painter = painterResource(id = R.drawable.ic_question),
                contentDescription = ""
            )
        }
    }, content = {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_storage),
                contentDescription = "", modifier = Modifier.size(100.dp)
            )
        }
    })
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun MainPreview() {
    MainDisplay()
}