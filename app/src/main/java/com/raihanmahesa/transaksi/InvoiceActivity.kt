package com.raihanmahesa.transaksi

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import com.raihanmahesa.laporan.DataLaporanActivity
import com.raihanmahesa.laundry.R
import com.raihanmahesa.modeldata.StatusLaporan
import com.raihanmahesa.modeldata.model_laporan
import com.raihanmahesa.modeldata.model_tambahan
import kotlinx.coroutines.*
import java.io.IOException
import java.io.OutputStream
import java.net.URLEncoder
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class InvoiceActivity : AppCompatActivity() {

    // Header views
    private lateinit var tvBusinessName: TextView
    private lateinit var tvBranch: TextView

    // Transaction Details views
    private lateinit var tvTransactionId: TextView
    private lateinit var tvDate: TextView
    private lateinit var tvCustomer: TextView
    private lateinit var tvEmployee: TextView

    // Main Service views
    private lateinit var tvMainService: TextView
    private lateinit var tvMainServicePrice: TextView

    // Additional Services views
    private lateinit var tvAdditionalServicesHeader: TextView
    private lateinit var rvAdditionalServices: RecyclerView
    private lateinit var tvSubtotalAdditional: TextView

    // Total view
    private lateinit var tvTotal: TextView

    // Buttons
    private lateinit var btnWhatsapp: Button
    private lateinit var btnPrint: Button

    // Data variables
    private var namaPelanggan: String = ""
    private var nomorHp: String = ""
    private var namaLayanan: String = ""
    private var hargaLayanan: String = "0"
    private var totalHarga: Int = 0
    private var metodePembayaran: String = ""
    private var tambahanList: ArrayList<model_tambahan> = ArrayList()
    private var noTransaksi: String = ""
    private var tanggalTransaksi: String = ""

    // Coroutine scope for background tasks
    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    companion object {
        private const val REQUEST_BLUETOOTH_PERMISSION = 1001
        private const val REQUEST_ENABLE_BT = 1002
        private const val PRINTER_MAC_ADDRESS = "DC:0D:51:A7:FF:7A" // Ganti dengan MAC address printer Anda
        private const val SPP_UUID = "00001101-0000-1000-8000-00805f9b34fb"
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_invoice)

        setupWindowInsets()
        initViews()
        extractIntentData()
        setupInvoiceData()
        setupClickListeners()
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun initViews() {
        // Header
        tvBusinessName = findViewById(R.id.tv_business_name)
        tvBranch = findViewById(R.id.tv_branch)

        // Transaction Details
        tvTransactionId = findViewById(R.id.tv_transaction_id)
        tvDate = findViewById(R.id.tv_date)
        tvCustomer = findViewById(R.id.tv_customer)
        tvEmployee = findViewById(R.id.tv_employee)

        // Main Service
        tvMainService = findViewById(R.id.tv_main_service)
        tvMainServicePrice = findViewById(R.id.tv_main_service_price)

        // Additional Services
        tvAdditionalServicesHeader = findViewById(R.id.tv_additional_services_header)
        rvAdditionalServices = findViewById(R.id.rv_additional_services)
        tvSubtotalAdditional = findViewById(R.id.tv_subtotal_additional)

        // Total
        tvTotal = findViewById(R.id.tv_total)

        // Buttons
        btnWhatsapp = findViewById(R.id.btn_whatsapp)
        btnPrint = findViewById(R.id.btn_print)
    }

    private fun extractIntentData() {
        namaPelanggan = intent.getStringExtra("nama_pelanggan") ?: ""
        nomorHp = intent.getStringExtra("nomor_hp") ?: ""
        namaLayanan = intent.getStringExtra("nama_layanan") ?: ""
        hargaLayanan = intent.getStringExtra("harga_layanan") ?: "0"
        totalHarga = intent.getIntExtra("total_harga", 0)
        metodePembayaran = intent.getStringExtra("metode_pembayaran") ?: ""

        val serializableExtra = intent.getSerializableExtra("layanan_tambahan")
        tambahanList = try {
            @Suppress("UNCHECKED_CAST")
            serializableExtra as? ArrayList<model_tambahan> ?: ArrayList()
        } catch (e: Exception) {
            e.printStackTrace()
            ArrayList()
        }

        noTransaksi = generateNoTransaksi()
        tanggalTransaksi = getCurrentDateTime()
    }

    private fun setupInvoiceData() {
        // Header info
        tvBusinessName.text = "Mahsok Laundry"
        tvBranch.text = "Solo"

        // Transaction details
        tvTransactionId.text = noTransaksi
        tvDate.text = tanggalTransaksi
        tvCustomer.text = namaPelanggan
        tvEmployee.text = "Admin"

        // Main service
        tvMainService.text = namaLayanan
        tvMainServicePrice.text = formatCurrency(hargaLayanan.toIntOrNull() ?: 0)

        // Additional services
        setupAdditionalServices()

        // Total
        tvTotal.text = formatCurrency(totalHarga)
    }

    private fun setupAdditionalServices() {
        if (tambahanList.isEmpty()) {
            hideAdditionalServices()
            tvSubtotalAdditional.text = formatCurrency(0)
            return
        }

        // Show additional services section
        tvAdditionalServicesHeader.visibility = View.VISIBLE

        // Setup RecyclerView
        rvAdditionalServices.layoutManager = LinearLayoutManager(this)
        val adapter = AdditionalServicesAdapter(tambahanList)
        rvAdditionalServices.adapter = adapter
        rvAdditionalServices.visibility = View.VISIBLE

        // Calculate subtotal
        val subtotal = tambahanList.sumOf {
            it.hargaTambahan?.toIntOrNull() ?: 0
        }
        tvSubtotalAdditional.text = formatCurrency(subtotal)
    }

    private fun hideAdditionalServices() {
        tvAdditionalServicesHeader.visibility = View.GONE
        rvAdditionalServices.visibility = View.GONE
    }

    private fun setupClickListeners() {
        btnWhatsapp.setOnClickListener {
            shareToWhatsApp()
            // Tambahkan laporan setelah kirim WhatsApp
            addLaporanToDataLaporan()
        }

        btnPrint.setOnClickListener {
            printReceipt()
        }
    }

    private fun formatCurrency(amount: Int): String {
        val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        return format.format(amount).replace("IDR", "Rp")
    }

    private fun shareToWhatsApp() {
        val message = createWhatsAppMessage()
        val phoneNumber = formatPhoneNumber(nomorHp)
        val encodedMessage = URLEncoder.encode(message, "UTF-8")
        val url = "https://wa.me/$phoneNumber?text=$encodedMessage"

        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
            showToast(getString(R.string.whatsapp_message_sent_en))
        } catch (e: Exception) {
            showToast(getString(R.string.failed_to_open_whatsapp_en))
            e.printStackTrace()
        }
    }

    private fun createWhatsAppMessage(): String {
        return """
            Halo $namaPelanggan 👋,
            
            🔖 *MAHSOK LAUNDRY - SOLO*
            
            🆔 *ID Transaksi:* $noTransaksi  
            📅 *Tanggal:* $tanggalTransaksi  
            👤 *Pelanggan:* $namaPelanggan  
            
            🧺 *Layanan Utama:* $namaLayanan  
            💰 *Total Bayar:* ${formatCurrency(totalHarga)}  
            
            🙏 Terima kasih telah mempercayakan cucian Anda kepada kami.  
            Kami akan memberikan pelayanan terbaik untuk Anda!
            
            📍 Mahsok Laundry - Cabang Solo  
        """.trimIndent()
    }

    private fun formatPhoneNumber(phoneNumber: String): String {
        return if (phoneNumber.startsWith("0")) {
            "62${phoneNumber.substring(1)}"
        } else {
            phoneNumber
        }
    }

    private fun generateNoTransaksi(): String {
        val timestamp = System.currentTimeMillis()
        return "TRX${timestamp.toString().takeLast(8)}"
    }

    private fun getCurrentDateTime(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return sdf.format(Date())
    }

    // ==================== LAPORAN MANAGEMENT ====================

    private fun addLaporanToDataLaporan() {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formattedDate = sdf.format(Date())

        val status = determinePaymentStatus(metodePembayaran)

        // Konversi layanan tambahan ke format yang bisa disimpan - DIPERBAIKI
        val layananTambahanMap = mutableMapOf<String, String>()

        // Set default values untuk layanan tambahan
        layananTambahanMap["parfum"] = "Tidak"
        layananTambahanMap["setrika"] = "Tidak"
        layananTambahanMap["antar"] = "Tidak"

        // Debug log
        android.util.Log.d("InvoiceDebug", "Processing ${tambahanList.size} additional services:")

        // Update berdasarkan layanan tambahan yang dipilih
        tambahanList.forEach { tambahan ->
            val namaLayanan = tambahan.namaTambahan?.lowercase()?.trim() ?: ""
            android.util.Log.d("InvoiceDebug", "Processing service: '$namaLayanan'")

            when {
                namaLayanan.contains("parfum") -> {
                    // Untuk parfum, simpan jenis parfumnya (bukan hanya "Ya")
                    layananTambahanMap["parfum"] = tambahan.namaTambahan ?: "Ya"
                    android.util.Log.d("InvoiceDebug", "Set parfum to: ${tambahan.namaTambahan}")
                }
                namaLayanan.contains("setrika") -> {
                    layananTambahanMap["setrika"] = "Ya"
                    android.util.Log.d("InvoiceDebug", "Set setrika to: Ya")
                }
                namaLayanan.contains("antar") || namaLayanan.contains("jemput") -> {
                    layananTambahanMap["antar"] = "Ya"
                    android.util.Log.d("InvoiceDebug", "Set antar to: Ya")
                }
                // Tambahkan layanan tambahan lainnya sesuai kebutuhan
                namaLayanan.contains("express") -> {
                    layananTambahanMap["express"] = "Ya"
                }
                namaLayanan.contains("plastik") -> {
                    layananTambahanMap["plastik"] = "Ya"
                }
            }
        }

        // Debug log hasil akhir
        android.util.Log.d("InvoiceDebug", "Final layanan tambahan mapping:")
        layananTambahanMap.forEach { (key, value) ->
            android.util.Log.d("InvoiceDebug", "$key: $value")
        }

        val newLaporan = model_laporan(
            noTransaksi = noTransaksi,
            tanggal = formattedDate,
            namaPelanggan = namaPelanggan,
            namaLayanan = namaLayanan,
            totalHarga = totalHarga,
            status = status,
            // Tambahkan field layanan tambahan dengan data yang sudah diperbaiki
            parfum = layananTambahanMap["parfum"],
            setrika = layananTambahanMap["setrika"],
            antar = layananTambahanMap["antar"],
            // Tambahkan field lainnya jika ada
            // express = layananTambahanMap["express"],
            // plastik = layananTambahanMap["plastik"],
            // tanggalPengambilan akan diset ketika status berubah ke SELESAI
            tanggalPengambilan = null
        )

        // Simpan ke Firebase
        val database = FirebaseDatabase.getInstance().getReference("Laporan")
        database.child(noTransaksi).setValue(newLaporan)
            .addOnSuccessListener {
                android.util.Log.d("InvoiceDebug", "Data saved successfully to Firebase")
                showToast(this.getString(R.string.firebase_success_message))
            }
            .addOnFailureListener { exception ->
                android.util.Log.e("InvoiceDebug", "Failed to save data to Firebase", exception)
                showToast(this.getString(R.string.firebase_failure_message))
            }
    }

    private fun determinePaymentStatus(metodePembayaran: String): StatusLaporan {
        return when (metodePembayaran.lowercase()) {
            "cash", "dana", "gopay", "ovo", "shopeepay", "transfer bank" -> StatusLaporan.SUDAH_DIBAYAR
            "nanti" -> StatusLaporan.BELUM_DIBAYAR
            else -> StatusLaporan.BELUM_DIBAYAR // Default untuk metode tidak dikenal
        }
    }

    // ==================== BLUETOOTH PRINTING ====================

    private fun printReceipt() {
        if (checkBluetoothPermissions()) {
            connectAndPrint(PRINTER_MAC_ADDRESS)
        } else {
            requestBluetoothPermissions()
        }
    }

    private fun checkBluetoothPermissions(): Boolean {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            val bluetoothPermission = ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.BLUETOOTH
            ) == PackageManager.PERMISSION_GRANTED

            val bluetoothAdminPermission = ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.BLUETOOTH_ADMIN
            ) == PackageManager.PERMISSION_GRANTED

            bluetoothPermission && bluetoothAdminPermission
        }
    }

    private fun requestBluetoothPermissions() {
        val permissions = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            arrayOf(
                android.Manifest.permission.BLUETOOTH_CONNECT,
                android.Manifest.permission.BLUETOOTH_SCAN
            )
        } else {
            arrayOf(
                android.Manifest.permission.BLUETOOTH,
                android.Manifest.permission.BLUETOOTH_ADMIN,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
        }

        ActivityCompat.requestPermissions(this, permissions, REQUEST_BLUETOOTH_PERMISSION)
    }

    private fun connectAndPrint(macAddress: String) {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        if (bluetoothAdapter == null) {
            showToast(getString(R.string.bluetooth_not_supported))
            return
        }

        if (!bluetoothAdapter.isEnabled) {
            showToast(getString(R.string.bluetooth_not_enabled))
            return
        }

        if (!checkBluetoothPermissions()) {
            showToast(getString(R.string.bluetooth_permission_not_available))
            return
        }

        coroutineScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    printToBluetoothDevice(bluetoothAdapter, macAddress)
                }

                if (result) {
                    showToast(getString(R.string.print_success))
                    // Tambahkan laporan setelah berhasil cetak
                    addLaporanToDataLaporan()
                    goToDataLaporan()
                } else {
                    showToast(getString(R.string.print_failed))
                }
            } catch (e: Exception) {
                showToast("Error: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    private fun goToDataLaporan() {
        // Pindah ke DataLaporanActivity
        val intent = Intent(this, DataLaporanActivity::class.java)
        startActivity(intent)
        // Optional: finish current activity agar tidak bisa kembali ke invoice
        finish()
    }

    private suspend fun printToBluetoothDevice(
        bluetoothAdapter: BluetoothAdapter,
        macAddress: String
    ): Boolean {
        var socket: BluetoothSocket? = null
        var outputStream: OutputStream? = null

        return try {
            val device: BluetoothDevice = bluetoothAdapter.getRemoteDevice(macAddress)
            val uuid = UUID.fromString(SPP_UUID)

            socket = device.createRfcommSocketToServiceRecord(uuid)
            socket.connect()

            outputStream = socket.outputStream
            val receiptData = generateReceiptData()
            outputStream.write(receiptData.toByteArray())
            outputStream.flush()

            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        } catch (e: SecurityException) {
            e.printStackTrace()
            false
        } finally {
            try {
                outputStream?.close()
                socket?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun generateReceiptData(): String {
        val receipt = StringBuilder()

        // ESC/POS Commands for formatting
        val ESC = "\u001B"
        val INIT = "$ESC@"
        val CENTER = "${ESC}a\u0001"
        val LEFT = "${ESC}a\u0000"
        val BOLD_ON = "${ESC}E\u0001"
        val BOLD_OFF = "${ESC}E\u0000"
        val CUT = "${ESC}i"

        receipt.apply {
            append(INIT) // Initialize printer
            append(CENTER)
            append(BOLD_ON)
            append("MAHSOK LAUNDRY\n")
            append("SOLO\n")
            append(BOLD_OFF)
            append("================================\n")
            append(LEFT)
            append("\n")

            // Transaction Details
            append("ID Transaksi: $noTransaksi\n")
            append("Tanggal: $tanggalTransaksi\n")
            append("Pelanggan: $namaPelanggan\n")
            append("Kasir: Admin\n")
            append("--------------------------------\n")

            // Main Service
            append(BOLD_ON)
            append("LAYANAN UTAMA:\n")
            append(BOLD_OFF)
            append("$namaLayanan\n")
            append("${formatCurrency(hargaLayanan.toIntOrNull() ?: 0)}\n")
            append("--------------------------------\n")

            // Additional Services
            if (tambahanList.isNotEmpty()) {
                append(BOLD_ON)
                append("LAYANAN TAMBAHAN:\n")
                append(BOLD_OFF)
                tambahanList.forEachIndexed { index, tambahan ->
                    val harga = tambahan.hargaTambahan?.toIntOrNull() ?: 0
                    append("${index + 1}. ${tambahan.namaTambahan}\n")
                    append("   ${formatCurrency(harga)}\n")
                }
                append("--------------------------------\n")
            }

            // Total
            append(BOLD_ON)
            append("TOTAL: ${formatCurrency(totalHarga)}\n")
            append(BOLD_OFF)
            append("================================\n")
            append(CENTER)
            append("\n")
            append("Terima kasih atas kepercayaan\n")
            append("Anda kepada kami!\n")
            append("\n")
            append("================================\n")
            append("\n\n\n")
            append(CUT) // Cut paper
        }

        return receipt.toString()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            REQUEST_BLUETOOTH_PERMISSION -> {
                if (grantResults.isNotEmpty() &&
                    grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                    printReceipt()
                } else {
                    showToast(this.getString(R.string.bluetooth_permission_needed))
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }

    // ==================== ADAPTER CLASS ====================

    inner class AdditionalServicesAdapter(
        private val additionalServices: List<model_tambahan>
    ) : RecyclerView.Adapter<AdditionalServicesAdapter.ViewHolder>() {

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tvNumber: TextView = itemView.findViewById(R.id.tv_number)
            val tvServiceName: TextView = itemView.findViewById(R.id.tv_service_name)
            val tvServicePrice: TextView = itemView.findViewById(R.id.tv_service_price)
        }

        override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ViewHolder {
            val view = layoutInflater.inflate(R.layout.item_additional_service, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val service = additionalServices[position]
            holder.tvNumber.text = (position + 1).toString()
            holder.tvServiceName.text = service.namaTambahan ?: "Unknown"
            holder.tvServicePrice.text = formatCurrency(service.hargaTambahan?.toIntOrNull() ?: 0)
        }

        override fun getItemCount(): Int = additionalServices.size
    }
}