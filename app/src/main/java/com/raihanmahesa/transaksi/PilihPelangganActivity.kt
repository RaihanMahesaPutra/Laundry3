package com.raihanmahesa.transaksi

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.raihanmahesa.adapter.AdapterPilihPelanggan
import com.raihanmahesa.laundry.R
import com.raihanmahesa.modeldata.model_pelanggan

class PilihPelangganActivity : AppCompatActivity() {
    private val TAG = "PilihPelanggan"
    val database = FirebaseDatabase.getInstance()
    val myRef = database.getReference("pelanggan")
    private lateinit var rvPilihPelanggan: RecyclerView
    private lateinit var searchView: SearchView
    private var idCabang: String = ""
    private lateinit var tvKosong: TextView
    lateinit var listPelanggan: ArrayList<model_pelanggan>
    private lateinit var adapter: AdapterPilihPelanggan
    private lateinit var filteredList: ArrayList<model_pelanggan>

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pilih_pelanggan)

        Log.d(TAG, "Activity created")

        tvKosong = findViewById(R.id.tvKosong)
        rvPilihPelanggan = findViewById(R.id.rvPILIH_PELANGGAN)
        searchView = findViewById(R.id.searchView)

        listPelanggan = ArrayList()
        filteredList = ArrayList()

        rvPilihPelanggan.layoutManager = LinearLayoutManager(this)

        idCabang = intent.getStringExtra("idCabang") ?: ""
        Log.d(TAG, "idCabang: $idCabang")

        setupSearchView()
        getData()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupSearchView() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText)
                return true
            }
        })
    }

    private fun filterList(query: String?) {
        Log.d(TAG, "Filtering list with query: $query")
        filteredList.clear()

        if (query.isNullOrEmpty()) {
            filteredList.addAll(listPelanggan)
        } else {
            val searchText = query.toLowerCase().trim()
            for (pelanggan in listPelanggan) {
                if (pelanggan.namaPelanggan?.toLowerCase()?.contains(searchText) == true ||
                    pelanggan.alamatPelanggan?.toLowerCase()?.contains(searchText) == true ||
                    pelanggan.noHPPelanggan?.toLowerCase()?.contains(searchText) == true) {
                    filteredList.add(pelanggan)
                }
            }
        }

        updateRecyclerView()
    }

    private fun updateRecyclerView() {
        Log.d(TAG, "Updating RecyclerView with ${filteredList.size} items")

        if (filteredList.isEmpty()) {
            tvKosong.visibility = View.VISIBLE
            tvKosong.text = "Tidak ada pelanggan yang cocok"
        } else {
            tvKosong.visibility = View.GONE
        }

        // Create a new adapter with the filtered list
        adapter = AdapterPilihPelanggan(filteredList)
        rvPilihPelanggan.adapter = adapter
    }

    fun getData() {
        Log.d(TAG, "getData() called")

        // Jika idCabang kosong, tampilkan semua pelanggan
        val query = if (idCabang.isEmpty()) {
            myRef.limitToLast(100)
        } else {
            myRef.orderByChild("idCabang").equalTo(idCabang).limitToLast(100)
        }

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d(TAG, "onDataChange: data exists: ${snapshot.exists()}, count: ${snapshot.childrenCount}")

                if (snapshot.exists()) {
                    tvKosong.visibility = View.GONE
                    listPelanggan.clear()

                    for (snap in snapshot.children) {
                        try {
                            val pelanggan = snap.getValue(model_pelanggan::class.java)
                            if (pelanggan != null) {
                                listPelanggan.add(pelanggan)
                                Log.d(TAG, "Added pelanggan: ${pelanggan.namaPelanggan}")
                            } else {
                                Log.e(TAG, "pelanggan is null for key: ${snap.key}")
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "Error parsing pelanggan: ${e.message}")
                        }
                    }

                    Log.d(TAG, "Total pelanggan loaded: ${listPelanggan.size}")

                    // Initialize the filtered list with all data
                    filteredList.clear()
                    filteredList.addAll(listPelanggan)

                    // Set adapter
                    if (listPelanggan.isNotEmpty()) {
                        adapter = AdapterPilihPelanggan(filteredList)
                        rvPilihPelanggan.adapter = adapter
                    } else {
                        tvKosong.visibility = View.VISIBLE
                    }
                } else {
                    Log.d(TAG, "No data found")
                    tvKosong.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Database error: ${error.message}")
                tvKosong.visibility = View.VISIBLE
                tvKosong.text = "Error: ${error.message}"
                Toast.makeText(this@PilihPelangganActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}