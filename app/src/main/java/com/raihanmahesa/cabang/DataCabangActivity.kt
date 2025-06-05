package com.raihanmahesa.cabang

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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.raihanmahesa.adapter.AdapterDataCabang
import com.raihanmahesa.laundry.R
import com.raihanmahesa.layanan.TambahLayananActivity
import com.raihanmahesa.modeldata.model_cabang

class DataCabangActivity : AppCompatActivity() {
    val database = FirebaseDatabase.getInstance()
    val myRef = database.getReference("cabang")
    lateinit var rvDATA_CABANG: RecyclerView
    lateinit var fabDATA_CABANG_TambahCabang: FloatingActionButton
    lateinit var cabangList: ArrayList<model_cabang>

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_data_cabang)

        init()

        val layoutManager = LinearLayoutManager(this)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        rvDATA_CABANG.layoutManager = layoutManager
        rvDATA_CABANG.setHasFixedSize(true)

        cabangList = arrayListOf<model_cabang>()
        getDate()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        fabDATA_CABANG_TambahCabang.setOnClickListener {
            val intent = Intent(this, TambahCabangActivity::class.java)
            startActivity(intent)
        }
    }

    fun init() {
        rvDATA_CABANG = findViewById(R.id.rvDATA_CABANG)
        fabDATA_CABANG_TambahCabang = findViewById(R.id.fabDATA_PENGGUNA_TambahCabang)
    }

    fun getDate() {
        val query = myRef.orderByChild("idCabang").limitToLast(100)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    cabangList.clear()
                    for (dataSnapshot in snapshot.children) {
                        val cabang = dataSnapshot.getValue(model_cabang::class.java)
                        if (cabang != null) {
                            cabangList.add(cabang)
                        }
                    }
                    val adapter = AdapterDataCabang(cabangList) { selectedCabang ->
                        selectedCabang.idCabang?.let { id ->
                            android.app.AlertDialog.Builder(this@DataCabangActivity)
                                .setTitle(getString(R.string.delete_branch_title))
                                .setMessage(getString(R.string.confirm_delete_branch_message, selectedCabang.namaCabang))
                                .setPositiveButton(getString(R.string.delete)) { _, _ ->
                                    myRef.child(id).removeValue()
                                        .addOnSuccessListener {
                                            Toast.makeText(
                                                this@DataCabangActivity,
                                                getString(R.string.branch_deleted_success),
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                        .addOnFailureListener {
                                            Toast.makeText(
                                                this@DataCabangActivity,
                                                getString(R.string.branch_delete_failed),
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                }
                                .setNegativeButton(getString(R.string.cancel), null)
                                .show()
                        }
                    }
                    rvDATA_CABANG.adapter = adapter
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DataCabangActivity, getString(R.string.data_load_failed), Toast.LENGTH_SHORT).show()
            }
        })
    }
}