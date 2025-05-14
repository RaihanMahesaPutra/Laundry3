package com.raihanmahesa.adapter
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import com.raihanmahesa.laundry.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import com.raihanmahesa.modeldata.model_pelanggan
import com.raihanmahesa.pelanggan.TambahPelangganActivity

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

    @SuppressLint("MissingInflatedId")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listPelanggan[position]
        holder.tvCARD_PELANGGAN_ID.text = item.idPelanggan
        holder.tvCARD_PELANGGAN_NAMA.text = item.namaPelanggan
        holder.tvCARD_PELANGGAN_ALAMAT.text = item.alamatPelanggan
        holder.tvCARD_PELANGGAN_NOHP.text = item.noHPPelanggan
        holder.tvCARD_PELANGGAN_cabang.text = item.idCabang
        // Klik tombol lihat
        holder.btn_lihat.setOnClickListener {
            val dialogView = LayoutInflater.from(holder.itemView.context)
                .inflate(R.layout.dialogmod_pelanggan, null)

            val dialogBuilder = AlertDialog.Builder(holder.itemView.context)
                .setView(dialogView)
                .setCancelable(true)

            val alertDialog = dialogBuilder.create()

            // Temukan semua TextView di dialog
            val tvId = dialogView.findViewById<TextView>(R.id.tvDIALOG_PELANGGAN_ID)
            val tvNama = dialogView.findViewById<TextView>(R.id.tvDIALOG_PELANGGAN_NAMA)
            val tvAlamat = dialogView.findViewById<TextView>(R.id.tvDIALOG_PELANGGAN_ALAMAT)
            val tvNoHP = dialogView.findViewById<TextView>(R.id.tvDIALOG_PELANGGAN_NOHP)
            val tvCabang = dialogView.findViewById<TextView>(R.id.tvDIALOG_PELANGGAN_CABANG)

            val btEdit = dialogView.findViewById<Button>(R.id.btDIALOG_MOD_PELANGGAN_Edit)
            val btHapus = dialogView.findViewById<Button>(R.id.btDIALOG_MOD_PELANGGAN_Hapus)

            // Cek null sebelum setText
            tvId?.text = item.idPelanggan
            tvNama?.text = item.namaPelanggan
            tvAlamat?.text = item.alamatPelanggan
            tvNoHP?.text = item.noHPPelanggan
            tvCabang?.text = item.idCabang // opsional

            btEdit?.setOnClickListener {
                val intent = Intent(holder.itemView.context, TambahPelangganActivity::class.java)
                intent.putExtra("idPelanggan", item.idPelanggan)
                intent.putExtra("namaPelanggan", item.namaPelanggan)
                intent.putExtra("alamatPelanggan", item.alamatPelanggan)
                intent.putExtra("noHpPelanggan", item.noHPPelanggan)
                intent.putExtra("idCabang", item.idCabang)
                holder.itemView.context.startActivity(intent)
                alertDialog.dismiss()
            }

            btHapus?.setOnClickListener {
                AlertDialog.Builder(holder.itemView.context)
                    .setTitle("Konfirmasi Hapus")
                    .setMessage("Yakin ingin menghapus data ini?")
                    .setPositiveButton("Ya") { _, _ ->
                        val dbRef = FirebaseDatabase.getInstance()
                            .getReference("Pelanggan")
                            .child(item.idPelanggan ?: "")

                        dbRef.removeValue().addOnSuccessListener {
                            listPelanggan.removeAt(position)
                            notifyItemRemoved(position)
                            notifyItemRangeChanged(position, listPelanggan.size)
                            Toast.makeText(holder.itemView.context, "Data berhasil dihapus", Toast.LENGTH_SHORT).show()
                            alertDialog.dismiss()
                        }.addOnFailureListener {
                            Toast.makeText(holder.itemView.context, "Gagal: ${it.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .setNegativeButton("Tidak") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }

            alertDialog.show()
        }
    }

    override fun getItemCount(): Int {
        return listPelanggan.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val btn_lihat = itemView.findViewById<View>(R.id.btn_lihat)
        val tvCARD_PELANGGAN_ID = itemView.findViewById<TextView>(R.id.tvCARD_PELANGGAN_ID)
        val tvCARD_PELANGGAN_NAMA = itemView.findViewById<TextView>(R.id.tvCARD_PELANGGAN_nama)
        val tvCARD_PELANGGAN_ALAMAT = itemView.findViewById<TextView>(R.id.tvCARD_PELANGGAN_alamat)
        val tvCARD_PELANGGAN_NOHP = itemView.findViewById<TextView>(R.id.tvCARD_PELANGGAN_nohp)
        val tvCARD_PELANGGAN_cabang = itemView.findViewById<TextView>(R.id.tvCARD_PELANGGAN_cabang)
    }
}