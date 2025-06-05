package com.raihanmahesa.pegawai
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.FirebaseDatabase
import com.raihanmahesa.laundry.R
import com.raihanmahesa.modeldata.model_pegawai

class TambahPegawaiActivity : AppCompatActivity() {
    val database = FirebaseDatabase.getInstance()
    val myRef = database.getReference("pegawai")
    lateinit var tvJudul: TextView
    lateinit var etNama: EditText
    lateinit var etAlamat: EditText
    lateinit var etNoHP: EditText
    lateinit var etCabang: EditText
    lateinit var btSimpan: Button

    var pegawaiId: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tambah_pegawai)
        init()
        val dataIntent = intent
        if (dataIntent != null) {
            pegawaiId = dataIntent.getStringExtra("idPegawai")
            val nama = dataIntent.getStringExtra("namaPegawai")
            val alamat = dataIntent.getStringExtra("alamatPegawai")
            val noHp = dataIntent.getStringExtra("noHpPegawai")
            val cabang = dataIntent.getStringExtra("idCabang")


            if (pegawaiId != null) {
                tvJudul.text = getString(R.string.edit_employee_data)
                btSimpan.text = getString(R.string.update)
                etNama.setText(nama)
                etAlamat.setText(alamat)
                etNoHP.setText(noHp)
                etCabang.setText(cabang)
            }
        }
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
        tvJudul = findViewById(R.id.tvpegawaijudul)
        etNama = findViewById(R.id.etpegawainama)
        etAlamat = findViewById(R.id.etpegawaialamat)
        etNoHP = findViewById(R.id.etpegawainohp)
        etCabang = findViewById(R.id.etpegawaicabang)
        btSimpan = findViewById(R.id.btpegawaisimpan)
    }

    fun cekValidasi() {
        val nama = etNama.text.toString()
        val alamat = etAlamat.text.toString()
        val noHp = etNoHP.text.toString()
        val cabang = etCabang.text.toString()

        if (nama.isEmpty()) {
            etNama.error = this.getString(R.string.validasi_nama_pegawai)
            Toast.makeText(this, this.getString(R.string.validasi_nama_pegawai),Toast.LENGTH_SHORT).show()
            etNama.requestFocus()
            return
        }
        if (alamat.isEmpty()) {
            etAlamat.error = this.getString(R.string.validasi_alamat_pegawai)
            Toast.makeText(this, this.getString(R.string.validasi_alamat_pegawai),Toast.LENGTH_SHORT).show()
            etAlamat.requestFocus()
            return
        }
        if (noHp.isEmpty()) {
            etNoHP.error = this.getString(R.string.validasi_nohp_pegawai)
            Toast.makeText(this, this.getString(R.string.validasi_nohp_pegawai),Toast.LENGTH_SHORT).show()
            etNoHP.requestFocus()
            return
        }
        if (cabang.isEmpty()) {
            etCabang.error = this.getString(R.string.validasi_cabang_pelanggan)
            Toast.makeText(this, this.getString(R.string.validasi_cabang_pelanggan),Toast.LENGTH_SHORT).show()
            etCabang.requestFocus()
            return
        }
        if (pegawaiId == null) {
            simpan()
        } else {
            update()
        }
    }

    fun simpan() {
        val pegawaiBaru = myRef.push()
        val pegawaiid = pegawaiBaru.key
        val data = model_pegawai(
            pegawaiid.toString(),
            etNama.text.toString(),
            etAlamat.text.toString(),
            etNoHP.text.toString(),
            etCabang.text.toString(),
        )
        pegawaiBaru.setValue(data)
            .addOnSuccessListener {
                Toast.makeText(
                    this,
                    this.getString(R.string.tambah_pegawai_sukses),
                    Toast.LENGTH_SHORT
                )
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(
                    this,
                    this.getString(R.string.tambah_pegawai_gagal),
                    Toast.LENGTH_SHORT
                )
            }
    }

    fun update() {
        val dataUpdate = mapOf(
            "namaPelanggan" to etNama.text.toString(),
            "alamatPelanggan" to etAlamat.text.toString(),
            "noHPPelanggan" to etNoHP.text.toString(),
            "idCabang" to etCabang.text.toString()
        )

        myRef.child(pegawaiId ?: "").updateChildren(dataUpdate)
            .addOnSuccessListener {
                Toast.makeText(this, getString(R.string.employee_update_success), Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, getString(R.string.employee_update_failed), Toast.LENGTH_SHORT).show()
            }
    }


}