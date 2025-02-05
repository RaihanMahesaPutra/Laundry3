package com.raihanmahesa

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.raihanmahesa.laundry.R
import com.raihanmahesa.pegawai.DataPegawaiActivity
import com.raihanmahesa.pelanggan.DataPelangganActivity
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class MainActivity : AppCompatActivity() {
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

        val tvDate = findViewById<TextView>(R.id.tvDate)
        tvDate.text = getCurrentDate()

        val tvGreeting = findViewById<TextView>(R.id.tvGreeting)
        tvGreeting.text = getGreetingMessage()

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
    }

    private fun getGreetingMessage(): String {
        val currentTime = LocalTime.now()
        return when {
            currentTime.hour in 5..10 -> "Selamat Pagi"
            currentTime.hour in 11..14 -> "Selamat Siang"
            currentTime.hour in 15..18 -> "Selamat Sore"
            else -> "Selamat Malam"
        }
    }

    private fun getCurrentDate(): String {
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")
        return currentDate.format(formatter)
    }
}
