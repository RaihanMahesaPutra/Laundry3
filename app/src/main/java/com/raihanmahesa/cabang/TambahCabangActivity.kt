package com.raihanmahesa.cabang

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
import com.raihanmahesa.modeldata.model_cabang

class TambahCabangActivity : AppCompatActivity() {
    val database = FirebaseDatabase.getInstance()
    val myRef = database.getReference("cabang")
    lateinit var tvJudul: TextView
    lateinit var etNama: EditText
    lateinit var etAlamat: EditText
    lateinit var etNoHP: EditText
    lateinit var btSimpan: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tambah_cabang)
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
        tvJudul = findViewById(R.id.tvcabangjudul)
        etNama = findViewById(R.id.etcabangnama)
        etAlamat = findViewById(R.id.etcabangalamat)
        etNoHP = findViewById(R.id.etcabangnohp)
        btSimpan = findViewById(R.id.btcabangsimpan)
    }

    fun cekValidasi() {
        val nama = etNama.text.toString()
        val alamat = etAlamat.text.toString()
        val nohp = etNoHP.text.toString()

        if (nama.isEmpty()) {
            etNama.error = this.getString(R.string.validasi_nama_cabang)
            Toast.makeText(this, this.getString(R.string.validasi_nama_cabang), Toast.LENGTH_SHORT).show()
            etNama.requestFocus()
            return
        }
        if (alamat.isEmpty()) {
            etAlamat.error = this.getString(R.string.validasi_alamat_cabang)
            Toast.makeText(this, this.getString(R.string.validasi_alamat_cabang), Toast.LENGTH_SHORT).show()
            etAlamat.requestFocus()
            return
        }
        if (nohp.isEmpty()) {
            etNoHP.error = this.getString(R.string.validasi_nohp_cabang)
            Toast.makeText(this, this.getString(R.string.validasi_nohp_cabang), Toast.LENGTH_SHORT).show()
            etNoHP.requestFocus()
            return
        }
        simpan()
    }

    fun simpan() {
        val cabangBaru = myRef.push()
        val cabangid = cabangBaru.key
        val data = model_cabang(
            cabangid.toString(),
            etNama.text.toString(),
            etAlamat.text.toString(),
            etNoHP.text.toString(),
        )
        cabangBaru.setValue(data)
            .addOnSuccessListener {
                Toast.makeText(
                    this,
                    this.getString(R.string.tambah_cabang_sukses),
                    Toast.LENGTH_SHORT
                )
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(
                    this,
                    this.getString(R.string.tambah_cabang_gagal),
                    Toast.LENGTH_SHORT
                )
            }
    }
}