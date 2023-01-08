package com.allen.browse.Dialog

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.allen.browse.MainActivity
import java.lang.IllegalStateException

class DialogClass:DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle("Perizinan Penyimpanan")
            builder.setMessage("Perizinan untuk Penyimpanan Storage Diperlukan")
                    .setPositiveButton("Izinkan", DialogInterface.OnClickListener { dialog, which ->
                        val izinkan = (activity as MainActivity).setupPermission()
                        val get = (activity as MainActivity)
                        Toast.makeText(context?.applicationContext, "Diizinkan", Toast.LENGTH_SHORT).show()
                    })
                    .setNegativeButton("Tidak",
                            DialogInterface.OnClickListener { dialog, which ->
                                Toast.makeText(context?.applicationContext, "Izin diperlukan untuk menggunakan Aplikasi", Toast.LENGTH_SHORT).show()
                            })
            builder.create()
        }?:throw IllegalStateException("Activity cannot null")
    }
}