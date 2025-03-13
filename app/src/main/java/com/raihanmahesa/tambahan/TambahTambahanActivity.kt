package com.raihanmahesa.tambahan

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.FirebaseDatabase
import com.raihanmahesa.laundry.R
import com.raihanmahesa.modeldata.model_pelanggan
import com.raihanmahesa.modeldata.model_tambahan

class TambahTambahanActivity : AppCompatActivity() {
    val database = FirebaseDatabase.getInstance()
    val myRef = database.getReference("tambahan")
    lateinit var tvJudul: TextView
    lateinit var etNama: EditText
    lateinit var etHarga: EditText
    lateinit var etCabang: EditText
    lateinit var btSimpan: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tambah_tambahan)
        init()
        btSimpan.setOnClickListener{
            cekValidasi()
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }


    fun init(){
        tvJudul = findViewById(R.id.tvtambahanjudul)
        etNama = findViewById(R.id.ettambahannama)
        etHarga = findViewById(R.id.ettambahanharga)
        etCabang = findViewById(R.id.ettambahancabang)
        btSimpan = findViewById(R.id.bttambahansimpan)
    }

    fun cekValidasi() {
        val nama = etNama.text.toString()
        val alamat = etHarga.text.toString()
        val cabang = etCabang.text.toString()

        if (nama.isEmpty()) {
            etNama.error = this.getString(R.string.validasi_nama_tambahan)
            Toast.makeText(this, this.getString(R.string.validasi_nama_tambahan), Toast.LENGTH_SHORT).show()
            etNama.requestFocus()
            return
        }
        if (alamat.isEmpty()) {
            etHarga.error = this.getString(R.string.validasi_alamat_tambahan)
            Toast.makeText(this, this.getString(R.string.validasi_alamat_tambahan), Toast.LENGTH_SHORT).show()
            etHarga.requestFocus()
            return
        }
        if (cabang.isEmpty()) {
            etCabang.error = this.getString(R.string.validasi_cabang_tambahan)
            Toast.makeText(this, this.getString(R.string.validasi_cabang_tambahan), Toast.LENGTH_SHORT).show()
            etCabang.requestFocus()
            return
        }
        simpan()
    }

    fun simpan() {
        val tambahanBaru = myRef.push()
        val tambahanid = tambahanBaru.key
        val data = model_tambahan(
            tambahanid.toString(),
            etNama.text.toString(),
            etHarga.text.toString(),
            etCabang.text.toString(),
        )
        tambahanBaru.setValue(data)
            .addOnSuccessListener {
                Toast.makeText(
                    this,
                    this.getString(R.string.tambah_tambahan_sukses),
                    Toast.LENGTH_SHORT
                )
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(
                    this,
                    this.getString(R.string.tambah_tambahan_gagal),
                    Toast.LENGTH_SHORT
                )
            }
    }
}