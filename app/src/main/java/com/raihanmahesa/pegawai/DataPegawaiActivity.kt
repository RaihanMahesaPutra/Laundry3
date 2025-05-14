package com.raihanmahesa.pegawai
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*
import com.raihanmahesa.adapter.AdapterDataPegawai
import com.raihanmahesa.laundry.R
import com.raihanmahesa.modeldata.model_pegawai
import com.raihanmahesa.pelanggan.TambahPelangganActivity

class DataPegawaiActivity : AppCompatActivity() {
    val database = FirebaseDatabase.getInstance()
    val myRef = database.getReference("pegawai")
    lateinit var rvDATA_PEGAWAI: RecyclerView
    lateinit var fabDATA_PEGAWAI_TambahPegawai: FloatingActionButton
    lateinit var pegawaiList: ArrayList<model_pegawai>

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_data_pegawai)

        init()

        val layoutManager = LinearLayoutManager(this)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        rvDATA_PEGAWAI.layoutManager = layoutManager
        rvDATA_PEGAWAI.setHasFixedSize(true)
        pegawaiList = arrayListOf<model_pegawai>()
        getDate()
        val tambahpegawai = findViewById<FloatingActionButton>(R.id.fabDATA_PENGGUNA_TambahPegawai)
        tambahpegawai.setOnClickListener {
            val intent = Intent(this, TambahPegawaiActivity::class.java)
            startActivity(intent)
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        fabDATA_PEGAWAI_TambahPegawai.setOnClickListener {
            val intent = Intent(this, TambahPegawaiActivity::class.java)
            startActivity(intent)
        }
    }

    fun init() {
        rvDATA_PEGAWAI = findViewById(R.id.rvDATA_PEGAWAI)
        fabDATA_PEGAWAI_TambahPegawai = findViewById(R.id.fabDATA_PENGGUNA_TambahPegawai)
    }

    fun getDate() {
        val query = myRef.orderByChild("idPegawai").limitToLast(100)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    pegawaiList.clear()
                    for (dataSnapshot in snapshot.children) {
                        val pegawai = dataSnapshot.getValue(model_pegawai::class.java)
                        if (pegawai != null) {
                            pegawaiList.add(pegawai)
                        }
                    }
                    val adapter = AdapterDataPegawai(pegawaiList)
                    rvDATA_PEGAWAI.adapter = adapter
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DataPegawaiActivity, "Data Gagal Dimuat", Toast.LENGTH_SHORT).show()
            }
        })
    }
}