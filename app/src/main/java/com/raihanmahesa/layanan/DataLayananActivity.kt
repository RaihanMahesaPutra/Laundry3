package com.raihanmahesa.layanan

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.raihanmahesa.adapter.AdapterDataLayanan
import com.raihanmahesa.laundry.R
import com.raihanmahesa.modeldata.model_layanan

class DataLayananActivity : AppCompatActivity() {
    val database = FirebaseDatabase.getInstance()
    val myRef = database.getReference("layanan")
    lateinit var rvDATA_LAYANAN: RecyclerView
    lateinit var fabDATA_LAYANAN_TambahLayanan: FloatingActionButton
    lateinit var layananList: ArrayList<model_layanan>

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_data_layanan)

        init()

        val layoutManager = LinearLayoutManager(this)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        rvDATA_LAYANAN.layoutManager = layoutManager
        rvDATA_LAYANAN.setHasFixedSize(true)

        layananList = arrayListOf()
        getData()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        fabDATA_LAYANAN_TambahLayanan.setOnClickListener {
            val intent = Intent(this, TambahLayananActivity::class.java)
            startActivity(intent)
        }
    }

    fun init() {
        rvDATA_LAYANAN = findViewById(R.id.rvDATA_LAYANAN)
        fabDATA_LAYANAN_TambahLayanan = findViewById(R.id.fabDATA_PENGGUNA_TambahLayanan)
    }

    fun getData() {
        val query = myRef.orderByChild("idLayanan").limitToLast(100)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    layananList.clear()
                    for (dataSnapshot in snapshot.children) {
                        val layanan = dataSnapshot.getValue(model_layanan::class.java)
                        if (layanan != null) {
                            layananList.add(layanan)
                        }
                    }

                    val adapter = AdapterDataLayanan(layananList) { selectedLayanan ->
                        val idToDelete = selectedLayanan.idLayanan
                        if (idToDelete != null) {
                            AlertDialog.Builder(this@DataLayananActivity)
                                .setTitle("Konfirmasi Hapus")
                                .setMessage("Apakah Anda yakin ingin menghapus layanan \"${selectedLayanan.namaLayanan}\"?")
                                .setPositiveButton("Hapus") { _, _ ->
                                    myRef.child(idToDelete).removeValue()
                                        .addOnSuccessListener {
                                            Toast.makeText(
                                                this@DataLayananActivity,
                                                "Data berhasil dihapus",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                        .addOnFailureListener {
                                            Toast.makeText(
                                                this@DataLayananActivity,
                                                "Gagal menghapus data",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                }
                                .setNegativeButton("Batal", null)
                                .show()
                        }
                    }

                    rvDATA_LAYANAN.adapter = adapter
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DataLayananActivity, "Data Gagal Dimuat", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
