package com.raihanmahesa

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.raihanmahesa.akun.DataAkunActivity
import com.raihanmahesa.cabang.DataCabangActivity
import com.raihanmahesa.laporan.DataLaporanActivity
import com.raihanmahesa.laundry.R
import com.raihanmahesa.layanan.DataLayananActivity
import com.raihanmahesa.pegawai.DataPegawaiActivity
import com.raihanmahesa.pelanggan.DataPelangganActivity
import com.raihanmahesa.tambahan.DataTambahanActivity
import com.raihanmahesa.transaksi.DataTransaksiActivity
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    // Constants
    private val PREF_NAME = "LoginPrefs"
    private val KEY_USER_NAME = "userName"

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inisialisasi SharedPreferences
        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        val tvDate = findViewById<TextView>(R.id.tvDate)
        tvDate.text = getCurrentDate()

        val tvGreeting = findViewById<TextView>(R.id.tvGreeting)
        tvGreeting.text = getGreetingMessage()

        // Menampilkan username
        val tvUsername = findViewById<TextView>(R.id.tvUsername)
        displayUsername(tvUsername)

        val pelangganMenu = findViewById<LinearLayout>(R.id.pelanggan_menu)
        pelangganMenu.setOnClickListener {
            val intent = Intent(this, DataPelangganActivity::class.java)
            startActivity(intent)
        }

        val pegawaiMenu = findViewById<LinearLayout>(R.id.pegawai_menu)
        pegawaiMenu.setOnClickListener {
            val intent = Intent(this, DataPegawaiActivity::class.java)
            startActivity(intent)
        }

        val layananMenu = findViewById<LinearLayout>(R.id.layanan_menu)
        layananMenu.setOnClickListener {
            val intent = Intent(this, DataLayananActivity::class.java)
            startActivity(intent)
        }

        val cabangMenu = findViewById<LinearLayout>(R.id.cabang_menu)
        cabangMenu.setOnClickListener {
            val intent = Intent(this, DataCabangActivity::class.java)
            startActivity(intent)
        }

        val tambahanMenu = findViewById<LinearLayout>(R.id.tambahan_menu)
        tambahanMenu.setOnClickListener {
            val intent = Intent(this, DataTambahanActivity::class.java)
            startActivity(intent)
        }

        val transaksiMenu = findViewById<LinearLayout>(R.id.transaksi_menu)
        transaksiMenu.setOnClickListener {
            val intent = Intent(this, DataTransaksiActivity::class.java)
            startActivity(intent)
        }

        val laporanMenu = findViewById<LinearLayout>(R.id.laporan_menu)
        laporanMenu.setOnClickListener {
            val intent = Intent(this, DataLaporanActivity::class.java)
            startActivity(intent)
        }

        val akunMenu = findViewById<LinearLayout>(R.id.akun_menu)
        akunMenu.setOnClickListener {
            val intent = Intent(this, DataAkunActivity::class.java)
            startActivity(intent)
        }
    }

    private fun displayUsername(tvUsername: TextView) {
        // Coba ambil username dari Intent extra terlebih dahulu
        val usernameFromIntent = intent.getStringExtra("nama")

        if (!usernameFromIntent.isNullOrEmpty()) {
            // Jika ada username dari intent, gunakan itu
            tvUsername.text = "$usernameFromIntent!"
        } else {
            // Jika tidak ada dari intent, ambil dari SharedPreferences
            val usernameFromPrefs = sharedPreferences.getString(KEY_USER_NAME, "")
            if (!usernameFromPrefs.isNullOrEmpty()) {
                tvUsername.text = "Halo, $usernameFromPrefs!"
            } else {
                // Fallback jika tidak ada username
                tvUsername.text = "Halo, Pengguna!"
            }
        }
    }

    private fun getGreetingMessage(): String {
        val currentTime = LocalTime.now()
        return when {
            currentTime.hour in 5..10 -> getString(R.string.greeting_morning)
            currentTime.hour in 11..14 -> getString(R.string.greeting_afternoon)
            currentTime.hour in 15..18 -> getString(R.string.greeting_evening)
            else -> getString(R.string.greeting_night)
        }
    }

    private fun getCurrentDate(): String {
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.getDefault())
        return currentDate.format(formatter)
    }
}