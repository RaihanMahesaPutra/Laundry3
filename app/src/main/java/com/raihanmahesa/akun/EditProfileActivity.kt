package com.raihanmahesa.akun

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.*
import com.raihanmahesa.laundry.R
import com.raihanmahesa.modeldata.model_user

class EditProfileActivity : AppCompatActivity() {

    companion object {
        private const val PREF_NAME = "LoginPrefs"
        private const val KEY_IS_LOGGED_IN = "isLoggedIn"
        private const val KEY_LOGIN_TIME = "loginTime"
        private const val KEY_USER_NAME = "userName"
        private const val KEY_USER_PHONE = "userPhone"
        private const val SESSION_DURATION_MS = 30 * 60 * 1000L // 30 menit
        private const val TAG = "EditProfileActivity"

        fun start(context: Context) {
            val intent = Intent(context, EditProfileActivity::class.java)
            context.startActivity(intent)
        }
    }

    private lateinit var edtNama: EditText
    private lateinit var edtPhone: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button
    private lateinit var ivBack: ImageView
    private lateinit var ivTogglePassword: ImageView

    private lateinit var database: DatabaseReference
    private lateinit var sharedPreferences: SharedPreferences
    private var currentUser: model_user? = null
    private var originalPhone: String = ""
    private var isPasswordVisible = false
    private var isActivityDestroyed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_profile)

        // Handle system bars untuk edge-to-edge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews()
        initComponents()

        if (checkLoginStatus()) {
            loadCurrentUserData()
            setupClickListeners()
            setupPasswordToggle()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        isActivityDestroyed = true
    }

    private fun initViews() {
        try {
            edtNama = findViewById(R.id.edtNama)
            edtPhone = findViewById(R.id.edtPhone)
            edtPassword = findViewById(R.id.edtPassword)
            btnSave = findViewById(R.id.btnSave)
            btnCancel = findViewById(R.id.btnCancel)
            ivBack = findViewById(R.id.ivBack)
            ivTogglePassword = findViewById(R.id.ivTogglePassword)
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing views: ${e.message}")
            Toast.makeText(this, getString(R.string.error_loading_interface), Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun initComponents() {
        try {
            database = FirebaseDatabase.getInstance().reference
            sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing components: ${e.message}")
            Toast.makeText(this, getString(R.string.error_connecting_database), Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkLoginStatus(): Boolean {
        return try {
            val isLoggedIn = sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
            val loginTime = sharedPreferences.getLong(KEY_LOGIN_TIME, 0L)
            val currentTime = System.currentTimeMillis()
            val sessionExpired = (currentTime - loginTime) > SESSION_DURATION_MS

            if (!isLoggedIn || sessionExpired) {
                val message = if (sessionExpired) getString(R.string.session_expired) else getString(R.string.not_logged_in)
                Toast.makeText(this, "$message, ${getString(R.string.please_login_again)}", Toast.LENGTH_SHORT).show()
                finish()
                false
            } else {
                true
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error checking login status: ${e.message}")
            finish()
            false
        }
    }

    private fun loadCurrentUserData() {
        try {
            val phoneNumber = sharedPreferences.getString(KEY_USER_PHONE, null)
            val userName = sharedPreferences.getString(KEY_USER_NAME, null)

            if (phoneNumber.isNullOrEmpty()) {
                Toast.makeText(this, getString(R.string.user_data_not_found), Toast.LENGTH_SHORT).show()
                finish()
                return
            }

            originalPhone = phoneNumber
            edtPhone.setText(phoneNumber)
            edtNama.setText(userName ?: "")

            // Load complete data from Firebase
            loadFromFirebase(phoneNumber)

        } catch (e: Exception) {
            Log.e(TAG, "Error loading current user data: ${e.message}")
            Toast.makeText(this, getString(R.string.error_loading_profile), Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadFromFirebase(phoneNumber: String) {
        try {
            val phoneKey = getPhoneKey(phoneNumber)
            database.child("users").child(phoneKey).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (isActivityDestroyed) return

                    if (snapshot.exists()) {
                        try {
                            currentUser = snapshot.getValue(model_user::class.java)
                            currentUser?.let { user ->
                                runOnUiThread {
                                    edtNama.setText(user.username ?: "")
                                    edtPassword.setText(user.password ?: "")
                                }
                                Log.d(TAG, "User data loaded from Firebase")
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "Error parsing user data: ${e.message}")
                            Toast.makeText(this@EditProfileActivity, getString(R.string.error_loading_profile), Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Log.w(TAG, "User data not found in Firebase")
                        Toast.makeText(this@EditProfileActivity, getString(R.string.user_data_not_found), Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    if (isActivityDestroyed) return
                    Log.e(TAG, "Firebase error: ${error.message}")
                    Toast.makeText(this@EditProfileActivity, getString(R.string.failed_load_server_data), Toast.LENGTH_SHORT).show()
                }
            })
        } catch (e: Exception) {
            Log.e(TAG, "Error loading from Firebase: ${e.message}")
            Toast.makeText(this, getString(R.string.error_connecting_database), Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupPasswordToggle() {
        ivTogglePassword.setOnClickListener {
            if (isPasswordVisible) {
                // Hide password
                edtPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                ivTogglePassword.setImageResource(android.R.drawable.ic_menu_view)
                isPasswordVisible = false
            } else {
                // Show password
                edtPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                ivTogglePassword.setImageResource(android.R.drawable.ic_menu_close_clear_cancel)
                isPasswordVisible = true
            }
            // Move cursor to end of text
            edtPassword.setSelection(edtPassword.text.length)
        }
    }

    private fun setupClickListeners() {
        try {
            ivBack.setOnClickListener {
                onBackPressed()
            }

            btnCancel.setOnClickListener {
                onBackPressed()
            }

            btnSave.setOnClickListener {
                saveProfile()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error setting up click listeners: ${e.message}")
        }
    }

    override fun onBackPressed() {
        if (hasDataChanged()) {
            showDiscardChangesDialog()
        } else {
            super.onBackPressed()
        }
    }

    private fun hasDataChanged(): Boolean {
        return try {
            val currentName = edtNama.text.toString().trim()
            val currentPhone = edtPhone.text.toString().trim()
            val currentPassword = edtPassword.text.toString().trim()

            val originalName = currentUser?.username ?: ""
            val originalPassword = currentUser?.password ?: ""

            currentName != originalName ||
                    currentPhone != originalPhone ||
                    currentPassword != originalPassword
        } catch (e: Exception) {
            Log.e(TAG, "Error checking data changes: ${e.message}")
            false
        }
    }

    private fun showDiscardChangesDialog() {
        try {
            AlertDialog.Builder(this)
                .setTitle(getString(R.string.discard_changes_title))
                .setMessage(getString(R.string.discard_changes_message))
                .setPositiveButton(getString(R.string.discard)) { dialog, _ ->
                    dialog.dismiss()
                    super.onBackPressed()
                }
                .setNegativeButton(getString(R.string.keep_editing)) { dialog, _ ->
                    dialog.dismiss()
                }
                .setCancelable(true)
                .show()
        } catch (e: Exception) {
            Log.e(TAG, "Error showing discard dialog: ${e.message}")
            super.onBackPressed()
        }
    }

    private fun saveProfile() {
        try {
            val nama = edtNama.text.toString().trim()
            val phone = edtPhone.text.toString().trim()
            val password = edtPassword.text.toString().trim()

            // Validasi input
            if (nama.isEmpty() || phone.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, getString(R.string.fill_all_fields), Toast.LENGTH_SHORT).show()
                return
            }

            if (phone.length < 10) {
                Toast.makeText(this, getString(R.string.invalid_phone_number), Toast.LENGTH_SHORT).show()
                return
            }

            if (password.length < 6) {
                Toast.makeText(this, getString(R.string.password_min_length), Toast.LENGTH_SHORT).show()
                return
            }

            // Disable tombol save untuk mencegah double click
            btnSave.isEnabled = false
            btnSave.text = getString(R.string.saving)

            // Cek apakah nomor telepon berubah
            if (phone != originalPhone) {
                // Nomor telepon berubah, perlu validasi dan migrasi data
                checkPhoneAndMigrateData(nama, phone, password)
            } else {
                // Nomor telepon sama, langsung update
                updateUserData(nama, phone, password)
            }

        } catch (e: Exception) {
            Log.e(TAG, "Error saving profile: ${e.message}")
            Toast.makeText(this, getString(R.string.error_saving_profile), Toast.LENGTH_SHORT).show()
            resetSaveButton()
        }
    }

    private fun checkPhoneAndMigrateData(nama: String, newPhone: String, password: String) {
        val newPhoneKey = getPhoneKey(newPhone)

        // Cek apakah nomor baru sudah digunakan
        database.child("users").child(newPhoneKey).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (isActivityDestroyed) return

                if (snapshot.exists()) {
                    // Nomor sudah digunakan
                    runOnUiThread {
                        Toast.makeText(this@EditProfileActivity, getString(R.string.phone_already_registered), Toast.LENGTH_SHORT).show()
                        resetSaveButton()
                    }
                } else {
                    // Nomor belum digunakan, lakukan migrasi
                    migrateUserData(nama, newPhone, password)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                if (isActivityDestroyed) return
                Log.e(TAG, "Error checking phone availability: ${error.message}")
                runOnUiThread {
                    Toast.makeText(this@EditProfileActivity, getString(R.string.error_checking_phone), Toast.LENGTH_SHORT).show()
                    resetSaveButton()
                }
            }
        })
    }

    private fun migrateUserData(nama: String, newPhone: String, password: String) {
        val oldPhoneKey = getPhoneKey(originalPhone)
        val newPhoneKey = getPhoneKey(newPhone)
        val updatedUser = model_user(username = nama, password = password)

        // Simpan data baru
        database.child("users").child(newPhoneKey).setValue(updatedUser)
            .addOnSuccessListener {
                // Hapus data lama
                database.child("users").child(oldPhoneKey).removeValue()
                    .addOnSuccessListener {
                        // Update shared preferences
                        updateSharedPreferences(nama, newPhone)

                        runOnUiThread {
                            Toast.makeText(this@EditProfileActivity, getString(R.string.profile_updated_successfully), Toast.LENGTH_SHORT).show()
                            setResult(RESULT_OK)
                            finish()
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.e(TAG, "Error removing old data: ${exception.message}")
                        runOnUiThread {
                            Toast.makeText(this@EditProfileActivity, getString(R.string.profile_updated_with_warning), Toast.LENGTH_SHORT).show()
                            setResult(RESULT_OK)
                            finish()
                        }
                    }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error migrating user data: ${exception.message}")
                runOnUiThread {
                    Toast.makeText(this@EditProfileActivity, getString(R.string.error_saving_profile), Toast.LENGTH_SHORT).show()
                    resetSaveButton()
                }
            }
    }

    private fun updateUserData(nama: String, phone: String, password: String) {
        val phoneKey = getPhoneKey(phone)
        val updatedUser = model_user(username = nama, password = password)

        database.child("users").child(phoneKey).setValue(updatedUser)
            .addOnSuccessListener {
                // Update shared preferences
                updateSharedPreferences(nama, phone)

                runOnUiThread {
                    Toast.makeText(this@EditProfileActivity, getString(R.string.profile_updated_successfully), Toast.LENGTH_SHORT).show()
                    setResult(RESULT_OK)
                    finish()
                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error updating user data: ${exception.message}")
                runOnUiThread {
                    Toast.makeText(this@EditProfileActivity, getString(R.string.error_saving_profile), Toast.LENGTH_SHORT).show()
                    resetSaveButton()
                }
            }
    }

    private fun updateSharedPreferences(nama: String, phone: String) {
        try {
            with(sharedPreferences.edit()) {
                putString(KEY_USER_NAME, nama)
                putString(KEY_USER_PHONE, phone)
                apply()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error updating shared preferences: ${e.message}")
        }
    }

    private fun resetSaveButton() {
        btnSave.isEnabled = true
        btnSave.text = getString(R.string.save)
    }

    private fun getPhoneKey(phoneNumber: String): String {
        return phoneNumber.replace(Regex("[^0-9]"), "")
    }
}