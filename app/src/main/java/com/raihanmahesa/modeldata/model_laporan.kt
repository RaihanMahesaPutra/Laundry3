package com.raihanmahesa.modeldata

data class model_laporan(
    // Ubah 'val' menjadi 'var' dan berikan nilai default untuk setiap properti
    var noTransaksi: String = "",
    var tanggal: String = "",
    var namaPelanggan: String = "",
    var namaLayanan: String = "",
    var totalHarga: Int = 0, // Nilai default untuk Int
    var status: StatusLaporan = StatusLaporan.BELUM_DIBAYAR // Nilai default untuk enum
)