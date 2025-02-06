package com.raihanmahesa.adapter
import com.raihanmahesa.laundry.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.raihanmahesa.modeldata.model_pelanggan

class AdapterDataPelanggan(
    private val listPelanggan: ArrayList<model_pelanggan>) :
    RecyclerView.Adapter<AdapterDataPelanggan.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_data_pelanggan, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listPelanggan[position]
        holder.tvCARD_PELANGGAN_ID.text = item.idPelanggan
        holder.tvCARD_PELANGGAN_NAMA.text = item.namaPelanggan
        holder.tvCARD_PELANGGAN_ALAMAT.text = item.alamatPelanggan
        holder.tvCARD_PELANGGAN_NOHP.text = item.noHPPelanggan
        holder.tvCARD_PELANGGAN_cabang.text = item.idCabang
        holder.cvCARD_PELANGGAN.setOnClickListener {
        }
    }

    override fun getItemCount(): Int {
        return listPelanggan.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cvCARD_PELANGGAN = itemView.findViewById<View>(R.id.cvCARD_PELANGGAN)
        val tvCARD_PELANGGAN_ID = itemView.findViewById<TextView>(R.id.tvCARD_PELANGGAN_ID)
        val tvCARD_PELANGGAN_NAMA = itemView.findViewById<TextView>(R.id.tvCARD_PELANGGAN_nama)
        val tvCARD_PELANGGAN_ALAMAT = itemView.findViewById<TextView>(R.id.tvCARD_PELANGGAN_alamat)
        val tvCARD_PELANGGAN_NOHP = itemView.findViewById<TextView>(R.id.tvCARD_PELANGGAN_nohp)
        val tvCARD_PELANGGAN_cabang = itemView.findViewById<TextView>(R.id.tvCARD_PELANGGAN_cabang)
    }
}