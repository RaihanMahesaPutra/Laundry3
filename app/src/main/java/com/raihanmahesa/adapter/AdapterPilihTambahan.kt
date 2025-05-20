package com.raihanmahesa.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.raihanmahesa.adapter.AdapterPilihPelanggan.ViewHolder
import com.raihanmahesa.laundry.R
import com.raihanmahesa.modeldata.model_tambahan

class AdapterPilihTambahan(private val listTambahan: MutableList<model_tambahan>) :
    RecyclerView.Adapter<AdapterPilihTambahan.ViewHolder>() {

    private val TAG = "AdapterPilihTambahan"
    lateinit var appContext: Context

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): com.raihanmahesa.adapter.AdapterPilihTambahan.ViewHolder {
        Log.d(TAG, "onCreateViewHolder called")
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_pilih_tambahan, parent, false)
        appContext = parent.context
        return com.raihanmahesa.adapter.AdapterPilihTambahan.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val nomor = position + 1
        val item = listTambahan[position]

        holder.tvID.text = "[$nomor]"
        holder.tvNama.text = item.namaTambahan ?: "Tidak ada nama"
        holder.tvHarga.text = "Harga: Rp ${item.hargaTambahan ?: "0"}"

        holder.cvCARD.setOnClickListener {
            val intent = Intent()
            intent.putExtra("idTambahan", item.idTambahan)
            intent.putExtra("namaTambahan", item.namaTambahan)
            intent.putExtra("hargaTambahan", item.hargaTambahan)
            (appContext as Activity).setResult(Activity.RESULT_OK, intent)
            (appContext as Activity).finish()
        }
    }

    override fun getItemCount(): Int = listTambahan.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvID: TextView = itemView.findViewById(R.id.tvCARD_PILIHTAMBAHAN_ID)
        val tvNama: TextView = itemView.findViewById(R.id.tvCARD_PILIHTAMBAHAN_nama)
        val tvHarga: TextView = itemView.findViewById(R.id.tvCARD_PILIHTAMBAHAN_harga)
        val cvCARD: CardView = itemView.findViewById(R.id.cvCARD_PILIHTAMBAHAN)
    }
}
