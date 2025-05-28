package com.raihanmahesa.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.raihanmahesa.laundry.R
import com.raihanmahesa.modeldata.model_tambahan

class AdapterDataTambahan(
    private val listTambahan: ArrayList<model_tambahan>,
    private val onItemLongClick: (model_tambahan) -> Unit
) : RecyclerView.Adapter<AdapterDataTambahan.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_data_tambahan, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listTambahan[position]
        holder.tvCARD_TAMBAHAN_ID.text = item.idTambahan
        holder.tvCARD_TAMBAHAN_NAMA.text = item.namaTambahan
        holder.tvCARD_TAMBAHAN_HARGA.text = item.hargaTambahan
        holder.tvCARD_TAMBAHAN_CABANG.text = item.cabang

        holder.cvCARD_TAMBAHAN.setOnLongClickListener {
            onItemLongClick(item)
            true
        }
    }

    override fun getItemCount(): Int {
        return listTambahan.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cvCARD_TAMBAHAN = itemView.findViewById<View>(R.id.cvCARD_TAMBAHAN)
        val tvCARD_TAMBAHAN_ID = itemView.findViewById<TextView>(R.id.tvCARD_TAMBAHAN_ID)
        val tvCARD_TAMBAHAN_NAMA = itemView.findViewById<TextView>(R.id.tvCARD_TAMBAHAN_nama)
        val tvCARD_TAMBAHAN_HARGA = itemView.findViewById<TextView>(R.id.tvCARD_TAMBAHAN_harga)
        val tvCARD_TAMBAHAN_CABANG = itemView.findViewById<TextView>(R.id.tvCARD_TAMBAHAN_cabang)
    }
}
