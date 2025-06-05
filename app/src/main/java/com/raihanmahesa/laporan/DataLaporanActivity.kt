package com.raihanmahesa.laporan

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.raihanmahesa.laundry.R
import com.raihanmahesa.modeldata.StatusLaporan
import com.raihanmahesa.modeldata.model_laporan
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DataLaporanActivity : AppCompatActivity(), AdapterDataLaporan.OnStatusChangeListener, AdapterDataLaporan.OnDeleteListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AdapterDataLaporan
    private val laporanList = ArrayList<model_laporan>()
    private lateinit var database: DatabaseReference

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_laporan)

        database = FirebaseDatabase.getInstance().getReference("Laporan")

        recyclerView = findViewById(R.id.recycler_view_laporan)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Pass listener ke adapter
        adapter = AdapterDataLaporan(laporanList, this, this)
        recyclerView.adapter = adapter

        loadData()
    }

    private fun loadData() {
        database.orderByChild("tanggal").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                laporanList.clear()
                for (dataSnapshot in snapshot.children) {
                    val laporan = dataSnapshot.getValue(model_laporan::class.java)
                    laporan?.let {
                        laporanList.add(it)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DataLaporanActivity, getString(R.string.data_load_failed), Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onStatusChanged(position: Int, newStatus: StatusLaporan) {
        if (position < laporanList.size) {
            val laporan = laporanList[position]
            laporan.status = newStatus

            // Jika status berubah ke SELESAI, set tanggal pengambilan
            if (newStatus == StatusLaporan.SELESAI) {
                val currentDateTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(
                    Date()
                )
                laporan.tanggalPengambilan = currentDateTime

                // Update status dan tanggal pengambilan di Firebase
                val updateMap = mapOf(
                    "status" to newStatus,
                    "tanggalPengambilan" to currentDateTime
                )

                database.child(laporan.noTransaksi ?: "").updateChildren(updateMap)
                    .addOnSuccessListener {
                        adapter.notifyItemChanged(position)
                        Toast.makeText(this, "Status berhasil diubah menjadi Selesai", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, this.getString(R.string.Gagalmengupdatestatus), Toast.LENGTH_SHORT).show()
                    }
            } else {
                // Update hanya status di Firebase
                database.child(laporan.noTransaksi ?: "").child("status").setValue(newStatus)
                    .addOnSuccessListener {
                        adapter.notifyItemChanged(position)
                        val statusMessage = when (newStatus) {
                            StatusLaporan.SUDAH_DIBAYAR -> this.getString(R.string.StatusberhasildiubahmenjadiSudahDibayar)
                            StatusLaporan.BELUM_DIBAYAR -> "Status berhasil diubah menjadi Belum Dibayar"
                            StatusLaporan.SELESAI -> this.getString(R.string.Laporanberhasildihapus)
                        }
                        Toast.makeText(this, statusMessage, Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, this.getString(R.string.Gagalmengupdatestatus), Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    override fun onDeleteItem(position: Int) {
        if (position < laporanList.size) {
            val laporan = laporanList[position]
            database.child(laporan.noTransaksi ?: "").removeValue()
                .addOnSuccessListener {
                    laporanList.removeAt(position)
                    adapter.notifyItemRemoved(position)
                    Toast.makeText(this, this.getString(R.string.Laporanberhasildihapus), Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, this.getString(R.string.Gagalmenghapuslaporan), Toast.LENGTH_SHORT).show()
                }
        }
    }
}