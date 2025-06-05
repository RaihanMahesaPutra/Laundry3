package com.raihanmahesa.modeldata

data class model_laporan(
    // Ubah 'val' menjadi 'var' dan berikan nilai default untuk setiap properti
    var noTransaksi: String = "",
    var tanggal: String = "",
    var namaPelanggan: String = "",
    var namaLayanan: String = "",
    var totalHarga: Int = 0, // Nilai default untuk Int
    var status: StatusLaporan = StatusLaporan.BELUM_DIBAYAR, // Nilai default untuk enum
    var tanggalPengambilan: String? = null,
    // Field untuk layanan tambahan
    var parfum: String? = null,
    var setrika: String? = null,
    var antar: String? = null,
    // Field tambahan lainnya sesuai kebutuhan
    var beratLaundry: Double? = null,
    var catatan: String? = null,
    var alamat: String? = null,
    var nomorTelepon: String? = null
)