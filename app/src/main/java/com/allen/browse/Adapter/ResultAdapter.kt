package com.allen.browse.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.allen.browse.Data.Lists
import com.allen.browse.databinding.IsiBinding

class ResultAdapter(private val mylist:ArrayList<Double>,
                    private val mylistX:ArrayList<Double>,
                    private val mylistY:ArrayList<Double>
                    ):RecyclerView.Adapter<ResultAdapter.ViewHolders>() {
    class ViewHolders(private val binding: IsiBinding) : RecyclerView.ViewHolder(binding.root) {
      val mytext = binding.posisidata
        val prediksi = binding.prediksi
        val hasilX = binding.hasilX
        val hasilY = binding.hasilY
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolders {
        return ViewHolders(IsiBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolders, position: Int) {
        val current = mylist[position]
        val currentX = mylistX[position]
        val currentY = mylistY[position]
        holder.prediksi.isEnabled = false
        holder.hasilX.isEnabled = false
        holder.hasilY.isEnabled = false
        holder.mytext.text = "${position+1}"
        holder.prediksi.setText(current.toString())
        holder.hasilX.setText(currentX.toString())
        holder.hasilY.setText(currentY.toString())
    }

    override fun getItemCount(): Int = mylist.size
}