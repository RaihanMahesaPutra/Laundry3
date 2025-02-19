package com.raihanmahesa.adapter
import com.raihanmahesa.laundry.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.raihanmahesa.modeldata.model_pegawai

class AdapterDataPegawai(
    private val listPegawai: ArrayList<model_pegawai>) :
    RecyclerView.Adapter<AdapterDataPegawai.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_data_pegawai, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listPegawai[position]
        holder.tvCARD_PEGAWAI_ID.text = item.idPegawai
        holder.tvCARD_PEGAWAI_NAMA.text = item.namaPegawai
        holder.tvCARD_PEGAWAI_ALAMAT.text = item.alamatPegawai
        holder.tvCARD_PEGAWAI_NOHP.text = item.noHPPegawai
        holder.tvCARD_PEGAWAI_cabang.text = item.idCabang
        holder.cvCARD_PEGAWAI.setOnClickListener {
        }
    }

    override fun getItemCount(): Int {
        return listPegawai.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cvCARD_PEGAWAI = itemView.findViewById<View>(R.id.cvCARD_PEGAWAI)
        val tvCARD_PEGAWAI_ID = itemView.findViewById<TextView>(R.id.tvCARD_PEGAWAI_ID)
        val tvCARD_PEGAWAI_NAMA = itemView.findViewById<TextView>(R.id.tvCARD_PEGAWAI_nama)
        val tvCARD_PEGAWAI_ALAMAT = itemView.findViewById<TextView>(R.id.tvCARD_PEGAWAI_alamat)
        val tvCARD_PEGAWAI_NOHP = itemView.findViewById<TextView>(R.id.tvCARD_PEGAWAI_nohp)
        val tvCARD_PEGAWAI_cabang = itemView.findViewById<TextView>(R.id.tvCARD_PEGAWAI_cabang)
    }
}