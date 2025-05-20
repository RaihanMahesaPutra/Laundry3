package com.raihanmahesa.modeldata

class  model_transaksi(
    val idTransaksi: String = "",
    val idPelanggan: String = "",
    val namaPelanggan: String = "",
    val idLayanan: String = "",
    val namaLayanan: String = "",
    val hargaLayanan: String = "",
    val tambahan: List<model_tambahan> = emptyList(),
    val tanggal: String = "",
    val idPegawai: String = "",
    val idCabang: String = ""
)