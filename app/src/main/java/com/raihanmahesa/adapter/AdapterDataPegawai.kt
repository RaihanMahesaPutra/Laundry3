package com.raihanmahesa.adapter
import android.content.Context
import android.content.Intent
import com.raihanmahesa.laundry.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.raihanmahesa.modeldata.model_pegawai
import com.raihanmahesa.pegawai.TambahPegawaiActivity

class AdapterDataPegawai(
    private val listPegawai: ArrayList<model_pegawai>) :
RecyclerView.Adapter<AdapterDataPegawai.ViewHolder>() {
    lateinit var appContext: Context
    lateinit var databaseReference: DatabaseReference
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_data_pegawai, parent, false)
        appContext = parent.context
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

        holder.cvCARD_PEGAWAI.setOnClickListener {
            val intent = Intent(appContext, TambahPegawaiActivity::class.java)
            intent.putExtra("judul", "Edit Pegawai")
            intent.putExtra("idPegawai", item.idPegawai)
            intent.putExtra("namaPegawai", item.namaPegawai)
            intent.putExtra("alamatPegawai", item.alamatPegawai)
            intent.putExtra("noHPPegawai", item.noHPPegawai)
            intent.putExtra("idCabang", item.idCabang)
            appContext.startActivity(intent)
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