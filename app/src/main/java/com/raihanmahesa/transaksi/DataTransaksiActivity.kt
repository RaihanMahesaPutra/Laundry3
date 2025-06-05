package com.raihanmahesa.transaksi

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.FirebaseApp
import com.raihanmahesa.adapter.AdapterPilihTambahan
import com.raihanmahesa.laundry.R
import com.raihanmahesa.modeldata.model_tambahan
import java.io.Serializable

class DataTransaksiActivity : AppCompatActivity() {
    private lateinit var tvNamaPelanggan: TextView
    private lateinit var tvNoHp: TextView
    private lateinit var tvNamaLayanan: TextView
    private lateinit var tvHargaLayanan: TextView
    private lateinit var rvLayananTambahan: RecyclerView
    private lateinit var btnPilihPelanggan: Button
    private lateinit var btnPilihLayanan: Button
    private lateinit var btnProses: Button
    private lateinit var btnTambahan: Button

    private val dataList = mutableListOf<model_tambahan>()

    private val pilihPelanggan = 1
    private val pilihLayanan = 2
    private val pilihLayananTambahan = 3

    private var idPelanggan = ""
    private var idCabang = ""
    private var namaPelanggan = ""
    private var noHP = ""
    private var idLayanan = ""
    private var namaLayanan = ""
    private var hargaLayanan = ""
    private var idPegawai = ""

    private lateinit var sharedPref: SharedPreferences

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_data_transaksi)

        FirebaseApp.initializeApp(this)

        // Set activity title
        title = getString(R.string.data_transaksi_title)

        sharedPref = getSharedPreferences("user_data", MODE_PRIVATE)
        idCabang = sharedPref.getString("idCabang", "") ?: ""
        idPegawai = sharedPref.getString("idPegawai", "") ?: ""

        initViews()
        setupRecyclerView()
        setupClickListeners()
        setupWindowInsets()
    }

    private fun initViews() {
        tvNamaPelanggan = findViewById(R.id.tvNamaPelanggan)
        tvNoHp = findViewById(R.id.tvPelangganNoHP)
        tvNamaLayanan = findViewById(R.id.tvNamaLayanan)
        tvHargaLayanan = findViewById(R.id.tvLayananHarga)
        rvLayananTambahan = findViewById(R.id.rvLayananTambahan)
        btnPilihPelanggan = findViewById(R.id.btnPilihPelanggan)
        btnPilihLayanan = findViewById(R.id.btnPilihLayanan)
        btnTambahan = findViewById(R.id.btnTambahan)
        btnProses = findViewById(R.id.btnProses)

        // Set button text from string resources
        btnPilihPelanggan.text = getString(R.string.pilih_pelanggan)
        btnPilihLayanan.text = getString(R.string.pilih_layanan)
        btnTambahan.text = getString(R.string.tambah_layanan)
        btnProses.text = getString(R.string.proses)

        // Set initial text for TextViews
        tvNamaPelanggan.text = getString(R.string.nama_pelanggan, getString(R.string.belum_dipilih))
        tvNoHp.text = getString(R.string.no_hp, getString(R.string.kosong))
        tvNamaLayanan.text = getString(R.string.nama_layanan, getString(R.string.belum_dipilih))
        tvHargaLayanan.text = getString(R.string.harga_layanan, getString(R.string.kosong))
    }

    private fun setupRecyclerView() {
        rvLayananTambahan.layoutManager = LinearLayoutManager(this).apply {
            reverseLayout = true
        }
        rvLayananTambahan.setHasFixedSize(true)
    }

    private fun setupClickListeners() {
        btnPilihPelanggan.setOnClickListener {
            val intent = Intent(this, PilihPelangganActivity::class.java)
            startActivityForResult(intent, pilihPelanggan)
        }

        btnPilihLayanan.setOnClickListener {
            val intent = Intent(this, PilihLayananActivity::class.java)
            startActivityForResult(intent, pilihLayanan)
        }

        btnTambahan.setOnClickListener {
            val intent = Intent(this, PilihLayananTambahanActivity::class.java)
            startActivityForResult(intent, pilihLayananTambahan)
        }

        btnProses.setOnClickListener {
            if (validateData()) {
                val intent = Intent(this@DataTransaksiActivity, KonfirmasiDataTransaksi::class.java)
                intent.putExtra("nama_pelanggan", namaPelanggan)
                intent.putExtra("nomor_hp", noHP)
                intent.putExtra("nama_layanan", namaLayanan)
                intent.putExtra("harga_layanan", hargaLayanan)
                intent.putExtra("layanan_tambahan", ArrayList(dataList))
                startActivity(intent)
            }
        }
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun validateData(): Boolean {
        if (namaPelanggan.isEmpty()) {
            Toast.makeText(this, getString(R.string.pilih_pelanggan_dulu), Toast.LENGTH_SHORT).show()
            return false
        }

        if (namaLayanan.isEmpty()) {
            Toast.makeText(this, getString(R.string.pilih_layanan_utama_dulu), Toast.LENGTH_SHORT).show()
            return false
        }

        if (hargaLayanan.isEmpty() || hargaLayanan == "0") {
            Toast.makeText(this, getString(R.string.harga_layanan_tidak_valid), Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    @Deprecated("This method has been deprecated in favor of using the Activity Result API")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && data != null) {
            when (requestCode) {
                pilihPelanggan -> {
                    idPelanggan = data.getStringExtra("idPelanggan").orEmpty()
                    namaPelanggan = data.getStringExtra("nama").orEmpty()
                    noHP = data.getStringExtra("noHP").orEmpty()

                    tvNamaPelanggan.text = getString(R.string.nama_pelanggan, namaPelanggan)
                    tvNoHp.text = getString(R.string.no_hp, noHP)
                }

                pilihLayanan -> {
                    idLayanan = data.getStringExtra("idLayanan").orEmpty()
                    namaLayanan = data.getStringExtra("nama").orEmpty()
                    hargaLayanan = data.getStringExtra("harga").orEmpty()

                    tvNamaLayanan.text = getString(R.string.nama_layanan, namaLayanan)
                    tvHargaLayanan.text = getString(R.string.harga_layanan, hargaLayanan)
                }

                pilihLayananTambahan -> {
                    val idTambahan = data.getStringExtra("idTambahan").orEmpty()
                    val namaTambahan = data.getStringExtra("namaTambahan").orEmpty()
                    val hargaTambahan = data.getStringExtra("hargaTambahan").orEmpty()

                    val tambahan = model_tambahan(
                        idTambahan = idTambahan,
                        namaTambahan = namaTambahan,
                        hargaTambahan = hargaTambahan
                    )
                    dataList.add(tambahan)

                    // Update the RecyclerView adapter correctly
                    if (rvLayananTambahan.adapter == null) {
                        rvLayananTambahan.adapter = AdapterPilihTambahan(
                            dataList,
                            enableLongDelete = true
                        ) { position ->
                            dataList.removeAt(position)
                            rvLayananTambahan.adapter?.notifyItemRemoved(position)
                        }
                    } else {
                        (rvLayananTambahan.adapter as AdapterPilihTambahan).notifyDataSetChanged()
                    }
                }
            }
        } else if (resultCode == RESULT_CANCELED) {
            val msg = when (requestCode) {
                pilihPelanggan -> getString(R.string.batal_memilih_pelanggan)
                pilihLayanan -> getString(R.string.batal_memilih_layanan)
                pilihLayananTambahan -> getString(R.string.batal_memilih_layanan_tambahan)
                else -> getString(R.string.aksi_dibatalkan)
            }
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }
    }
}