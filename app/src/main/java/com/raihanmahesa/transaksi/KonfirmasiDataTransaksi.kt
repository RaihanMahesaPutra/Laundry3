package com.raihanmahesa.transaksi

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.raihanmahesa.laundry.R
import com.raihanmahesa.modeldata.model_tambahan
import java.io.Serializable

class KonfirmasiDataTransaksi : AppCompatActivity() {

    private lateinit var tvNamaPelanggan: TextView
    private lateinit var tvNoHP: TextView
    private lateinit var tvNamaLayanan: TextView
    private lateinit var tvHargaLayanan: TextView
    private lateinit var listLayananTambahan: ListView
    private lateinit var tvTotalBayar: TextView
    private lateinit var btnBatal: Button
    private lateinit var btnPembayaran: Button

    private var tambahanList = ArrayList<model_tambahan>()
    private var totalHarga = 0

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_konfirmasi_data_transaksi)

        // Initialize views
        initViews()

        // Get data from intent with safe handling
        val namaPelanggan = intent.getStringExtra("nama_pelanggan") ?: ""
        val nomorHp = intent.getStringExtra("nomor_hp") ?: ""
        val namaLayanan = intent.getStringExtra("nama_layanan") ?: ""
        val hargaLayanan = intent.getStringExtra("harga_layanan") ?: "0"

        // Safely retrieve the ArrayList with proper casting
        // First, retrieve the serializable extra
        val serializableExtra = intent.getSerializableExtra("layanan_tambahan")

        // Then try to cast it safely
        @Suppress("UNCHECKED_CAST")
        tambahanList = try {
            serializableExtra as? ArrayList<model_tambahan> ?: ArrayList()
        } catch (e: Exception) {
            // If casting fails, log the error and use an empty list
            e.printStackTrace()
            ArrayList()
        }

        // Set text views
        tvNamaPelanggan.text = namaPelanggan
        tvNoHP.text = nomorHp
        tvNamaLayanan.text = namaLayanan
        tvHargaLayanan.text = "Rp$hargaLayanan"

        // Create a list of formatted strings for the ListView
        val tambahanStrings = ArrayList<String>()
        for (tambahan in tambahanList) {
            val nama = tambahan.namaTambahan ?: "Unknown"
            val harga = tambahan.hargaTambahan ?: "0"
            tambahanStrings.add("$nama - Rp$harga")
        }

        // Set the adapter for ListView
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            tambahanStrings
        )
        listLayananTambahan.adapter = adapter

        // Calculate total price safely
        try {
            totalHarga = hargaLayanan.toIntOrNull() ?: 0
            for (tambahan in tambahanList) {
                val hargaTambahan = tambahan.hargaTambahan?.toIntOrNull() ?: 0
                totalHarga += hargaTambahan
            }
        } catch (e: Exception) {
            e.printStackTrace()
            totalHarga = 0
            Toast.makeText(this, getString(R.string.error_calculating_total_price), Toast.LENGTH_SHORT).show()
        }

        tvTotalBayar.text = "Rp$totalHarga"

        // Set click listeners
        btnBatal.setOnClickListener {
            finish()
        }

        btnPembayaran.setOnClickListener {
            val dialogView = layoutInflater.inflate(R.layout.dialogmod_pembayaran, null)

            val dialog = android.app.AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(true)
                .create()

            // Daftar metode pembayaran dan ID card-nya
            val metodeList = listOf(
                Pair("Bayar Nanti", R.id.cardBayarNanti),
                Pair("Tunai", R.id.cardTunai),
                Pair("QRIS", R.id.cardQRIS),
                Pair("DANA", R.id.cardDANA),
                Pair("GoPay", R.id.cardGoPay),
                Pair("OVO", R.id.cardOVO)
            )

            // Tangani klik masing-masing metode pembayaran
            for ((namaMetode, idCard) in metodeList) {
                val card = dialogView.findViewById<androidx.cardview.widget.CardView>(idCard)
                card?.setOnClickListener {
                    Toast.makeText(this, getString(R.string.payment_method_selected, namaMetode), Toast.LENGTH_SHORT).show()

                    // Pindah ke InvoiceActivity dengan membawa data transaksi
                    val invoiceIntent = Intent(this, InvoiceActivity::class.java)
                    invoiceIntent.putExtra("nama_pelanggan", namaPelanggan)
                    invoiceIntent.putExtra("nomor_hp", nomorHp)
                    invoiceIntent.putExtra("nama_layanan", namaLayanan)
                    invoiceIntent.putExtra("harga_layanan", hargaLayanan)
                    invoiceIntent.putExtra("total_harga", totalHarga)
                    invoiceIntent.putExtra("metode_pembayaran", namaMetode)
                    invoiceIntent.putExtra("layanan_tambahan", tambahanList as Serializable)

                    startActivity(invoiceIntent)
                    dialog.dismiss()
                    finish() // Menutup activity konfirmasi setelah menuju invoice
                }
            }

            // Tombol batal
            val btnBatal = dialogView.findViewById<TextView>(R.id.tvBatal)
            btnBatal?.setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()
        }

    }

    private fun initViews() {
        tvNamaPelanggan = findViewById(R.id.tvNamaPelangganKonfirmasi)
        tvNoHP = findViewById(R.id.tvNoHPKonfimasi)
        tvNamaLayanan = findViewById(R.id.tvNamaLayananKonfimasi)
        tvHargaLayanan = findViewById(R.id.tvHargaLayananKonfimasi)
        listLayananTambahan = findViewById(R.id.listLayananTambahanKonfimasi)
        tvTotalBayar = findViewById(R.id.tvTotalHarga)
        btnBatal = findViewById(R.id.btnBatal)
        btnPembayaran = findViewById(R.id.btnPembayaran)
    }
}
