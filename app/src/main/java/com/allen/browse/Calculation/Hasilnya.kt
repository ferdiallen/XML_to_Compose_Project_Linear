package com.allen.browse.Calculation

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.allen.browse.Adapter.ResultAdapter
import com.allen.browse.Data.VM
import com.allen.browse.databinding.HasilBinding

class Hasilnya:AppCompatActivity() {
    lateinit var binding:HasilBinding
    lateinit var VMget:VM
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        VMget = ViewModelProvider(this).get(VM::class.java)
        super.onCreate(savedInstanceState)
        binding = HasilBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val getter: ArrayList<*> = intent.getSerializableExtra("Transfers") as ArrayList<*>
        val getterX:ArrayList<*> = intent.getSerializableExtra("TransfersX") as ArrayList<*>
        val getterY = intent.getSerializableExtra("TransfersY") as ArrayList<*>
        val adapter = ResultAdapter(getter as ArrayList<Double>, getterX as ArrayList<Double>, getterY as ArrayList<Double>)
        binding.apply {
            myrecycle.adapter = adapter
            myrecycle.setHasFixedSize(true)
        }
    }

}