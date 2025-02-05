package com.raihanmahesa.pegawai

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.raihanmahesa.laundry.R

class DataPegawaiActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_data_pegawai)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val fabDATA_PENGGUNA_TambahPegawai = findViewById<FloatingActionButton>(R.id.fabDATA_PENGGUNA_TambahPegawai)
        fabDATA_PENGGUNA_TambahPegawai.setOnClickListener {
            val intent = Intent(this, TambahPegawaiActivity::class.java)
            startActivity(intent)
        }
    }
}