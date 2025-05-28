package com.raihanmahesa.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.raihanmahesa.laundry.R
import com.raihanmahesa.modeldata.model_cabang

class AdapterDataCabang (
    private val listCabang: ArrayList<model_cabang>,
    private val onItemLongClick: (model_cabang) -> Unit) :
    RecyclerView.Adapter<AdapterDataCabang.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_data_cabang, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listCabang[position]
        holder.tvCARD_CABANG_ID.text = item.idCabang
        holder.tvCARD_CABANG_NAMA.text = item.namaCabang
        holder.tvCARD_CABANG_ALAMAT.text = item.alamatCabang
        holder.tvCARD_CABANG_NOHP.text = item.noHPCabang
        holder.cvCARD_CABANG.setOnClickListener {
            onItemLongClick(item)
            true
        }
    }

    override fun getItemCount(): Int {
        return listCabang.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cvCARD_CABANG = itemView.findViewById<View>(R.id.cvCARD_CABANG)
        val tvCARD_CABANG_ID = itemView.findViewById<TextView>(R.id.tvCARD_CABANG_ID)
        val tvCARD_CABANG_NAMA = itemView.findViewById<TextView>(R.id.tvCARD_CABANG_nama)
        val tvCARD_CABANG_ALAMAT = itemView.findViewById<TextView>(R.id.tvCARD_CABANG_alamat)
        val tvCARD_CABANG_NOHP = itemView.findViewById<TextView>(R.id.tvCARD_CABANG_nohp)
    }
}