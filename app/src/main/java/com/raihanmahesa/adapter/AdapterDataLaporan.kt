package com.raihanmahesa.laporan

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.raihanmahesa.laundry.R
import com.raihanmahesa.modeldata.StatusLaporan
import com.raihanmahesa.modeldata.model_laporan
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class AdapterDataLaporan(
    private val laporanList: MutableList<model_laporan>,
    private val onStatusChangeListener: OnStatusChangeListener? = null,
    private val onDeleteListener: OnDeleteListener? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface OnStatusChangeListener {
        fun onStatusChanged(position: Int, newStatus: StatusLaporan)
    }

    interface OnDeleteListener {
        fun onDeleteItem(position: Int)
    }

    companion object {
        const val TYPE_SUDAH_DIBAYAR = 0
        const val TYPE_BELUM_DIBAYAR = 1
        const val TYPE_SELESAI = 2

        fun formatCurrency(amount: Int): String {
            val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
            return format.format(amount).replace("IDR", "Rp")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (laporanList[position].status) {
            StatusLaporan.SUDAH_DIBAYAR -> TYPE_SUDAH_DIBAYAR
            StatusLaporan.BELUM_DIBAYAR -> TYPE_BELUM_DIBAYAR
            StatusLaporan.SELESAI -> TYPE_SELESAI
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_SUDAH_DIBAYAR -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.card_data_transaksi_sudahdibayar, parent, false)
                SudahDibayarViewHolder(view)
            }
            TYPE_BELUM_DIBAYAR -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.card_data_transaksi_belumdibayar, parent, false)
                BelumDibayarViewHolder(view)
            }
            TYPE_SELESAI -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.card_data_transaksi_selesai, parent, false)
                SelesaiViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val laporan = laporanList[position]
        when (holder) {
            is SudahDibayarViewHolder -> holder.bind(laporan, position)
            is BelumDibayarViewHolder -> holder.bind(laporan, position)
            is SelesaiViewHolder -> holder.bind(laporan, position)
        }
    }

    override fun getItemCount(): Int = laporanList.size

    private fun setupLongClickDelete(itemView: View, position: Int) {
        itemView.setOnLongClickListener {
            val context = itemView.context
            val laporan = laporanList[position]

            AlertDialog.Builder(context)
                .setTitle("Hapus Transaksi")
                .setMessage("Apakah Anda yakin ingin menghapus transaksi:\n\n" +
                        "No. Transaksi: ${laporan.noTransaksi}\n" +
                        "Pelanggan: ${laporan.namaPelanggan}\n" +
                        "Tanggal: ${laporan.tanggal}")
                .setPositiveButton("Hapus") { _, _ ->
                    onDeleteListener?.onDeleteItem(position)
                }
                .setNegativeButton("Batal", null)
                .show()

            true
        }
    }

    // Helper function untuk menghitung total layanan tambahan
    private fun getTotalAdditionalServices(laporan: model_laporan): Int {
        var total = 0

        // Hitung berdasarkan field yang ada di model_laporan
        laporan.parfum?.let { if (it.isNotEmpty() && it != "Tidak") total++ }
        laporan.setrika?.let { if (it == "Ya" || it == true.toString()) total++ }
        laporan.antar?.let { if (it == "Ya" || it == true.toString()) total++ }

        // Tambahkan field layanan tambahan lainnya sesuai kebutuhan
        // Contoh jika ada field lain:
        // laporan.express?.let { if (it == "Ya" || it == true.toString()) total++ }
        // laporan.plastik?.let { if (it == "Ya" || it == true.toString()) total++ }

        return total
    }

    // Helper function untuk menampilkan text total layanan tambahan - DIMODIFIKASI
    private fun getAdditionalServicesText(laporan: model_laporan): String {
        val total = getTotalAdditionalServices(laporan)

        return when (total) {
            0 -> "0 Layanan Tambahan"
            1 -> "1 Layanan Tambahan"
            else -> "$total Layanan Tambahan"
        }
    }

    // ViewHolder untuk Sudah Dibayar
    inner class SudahDibayarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvNamaPelanggan: TextView = itemView.findViewById(R.id.tvCARD_PELANGGAN_nama)
        private val tvDateTime: TextView = itemView.findViewById(R.id.tvDateTime)
        private val tvLayanan: TextView = itemView.findViewById(R.id.tvLayanan)
        private val tvTotalAmount: TextView = itemView.findViewById(R.id.tvTotalAmount)

        private val btnSelesai: Button? = try {
            itemView.findViewById(R.id.btnPickup)
        } catch (e: Exception) {
            null
        }

        fun bind(laporan: model_laporan, position: Int) {
            tvNamaPelanggan.text = laporan.namaPelanggan ?: "Unknown"
            tvDateTime.text = laporan.tanggal ?: ""
            tvLayanan.text = laporan.namaLayanan ?: "Unknown Service"
            tvTotalAmount.text = formatCurrency(laporan.totalHarga ?: 0)

            // Setup long click untuk delete
            setupLongClickDelete(itemView, position)

            // Set listener untuk tombol selesai
            btnSelesai?.setOnClickListener {
                onStatusChangeListener?.onStatusChanged(position, StatusLaporan.SELESAI)
            }
        }
    }

    // ViewHolder untuk Belum Dibayar
    inner class BelumDibayarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvNamaPelanggan: TextView = itemView.findViewById(R.id.tvCARD_PELANGGAN_nama)
        private val tvDateTime: TextView = itemView.findViewById(R.id.tvDateTime)
        private val tvLayanan: TextView = itemView.findViewById(R.id.tvLayanan)
        private val tvTotalAmount: TextView = itemView.findViewById(R.id.tvTotalAmount)

        private val btnBayar: Button? = try {
            itemView.findViewById(R.id.btnPickup)
        } catch (e: Exception) {
            null
        }

        fun bind(laporan: model_laporan, position: Int) {
            tvNamaPelanggan.text = laporan.namaPelanggan ?: "Unknown"
            tvDateTime.text = laporan.tanggal ?: ""
            tvLayanan.text = laporan.namaLayanan ?: "Unknown Service"
            tvTotalAmount.text = formatCurrency(laporan.totalHarga ?: 0)

            // Setup long click untuk delete
            setupLongClickDelete(itemView, position)

            // Set listener untuk tombol bayar
            btnBayar?.setOnClickListener {
                onStatusChangeListener?.onStatusChanged(position, StatusLaporan.SUDAH_DIBAYAR)
            }
        }
    }

    // Helper function untuk format tanggal pengambilan
    private fun formatTanggalPengambilan(tanggalPengambilan: String?): String {
        // Jika tanggal pengambilan kosong atau null, return string kosong
        if (tanggalPengambilan.isNullOrEmpty()) return ""

        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("EEEE, dd MMMM yyyy 'pukul' HH:mm", Locale("id", "ID"))
            val date = inputFormat.parse(tanggalPengambilan)

            val calendar = Calendar.getInstance()
            val today = Calendar.getInstance()
            val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }

            calendar.time = date ?: return ""

            when {
                isSameDay(calendar, today) -> {
                    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                    "Hari ini pukul ${timeFormat.format(date)}"
                }
                isSameDay(calendar, yesterday) -> {
                    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                    "Kemarin pukul ${timeFormat.format(date)}"
                }
                isWithinWeek(calendar, today) -> {
                    val dayFormat = SimpleDateFormat("EEEE 'pukul' HH:mm", Locale("id", "ID"))
                    dayFormat.format(date)
                }
                else -> outputFormat.format(date)
            }
        } catch (e: Exception) {
            // Jika terjadi error parsing, return string kosong
            ""
        }
    }

    private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    private fun isWithinWeek(pickupDate: Calendar, today: Calendar): Boolean {
        val daysDiff = ((today.timeInMillis - pickupDate.timeInMillis) / (24 * 60 * 60 * 1000)).toInt()
        return daysDiff in 2..6
    }

    // ViewHolder untuk Selesai
    inner class SelesaiViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvNamaPelanggan: TextView = itemView.findViewById(R.id.tvCARD_PELANGGAN_nama)
        private val tvDateTime: TextView = itemView.findViewById(R.id.tvDateTime)
        private val tvLayanan: TextView = itemView.findViewById(R.id.tvLayanan)
        private val tvTotalAmount: TextView = itemView.findViewById(R.id.tvTotalAmount)

        private val tvTanggalPengambilan: TextView? = try {
            itemView.findViewById(R.id.tvTanggalPengambilan)
        } catch (e: Exception) {
            null
        }

        fun bind(laporan: model_laporan, position: Int) {
            tvNamaPelanggan.text = laporan.namaPelanggan ?: "Unknown"
            tvDateTime.text = laporan.tanggal ?: ""
            tvLayanan.text = laporan.namaLayanan ?: "Unknown Service"
            tvTotalAmount.text = formatCurrency(laporan.totalHarga ?: 0)

            // Set tanggal pengambilan
            val tanggalPengambilanText = formatTanggalPengambilan(laporan.tanggalPengambilan)
            tvTanggalPengambilan?.text = tanggalPengambilanText

            // Jika tidak ada tanggal pengambilan, sembunyikan TextView
            if (tanggalPengambilanText.isEmpty()) {
                tvTanggalPengambilan?.visibility = View.GONE
            } else {
                tvTanggalPengambilan?.visibility = View.VISIBLE
            }

            // Setup long click untuk delete
            setupLongClickDelete(itemView, position)
        }
    }
}