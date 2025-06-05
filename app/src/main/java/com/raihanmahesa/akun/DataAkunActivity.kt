package com.raihanmahesa.akun

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.raihanmahesa.LoginActivity
import com.raihanmahesa.laundry.R
import com.raihanmahesa.modeldata.model_user
import com.google.firebase.database.*

class DataAkunActivity : AppCompatActivity() {

    companion object {
        private const val PREF_NAME = "LoginPrefs"
        private const val KEY_IS_LOGGED_IN = "isLoggedIn"
        private const val KEY_LOGIN_TIME = "loginTime"
        private const val KEY_USER_NAME = "userName"
        private const val KEY_USER_PHONE = "userPhone"
        private const val SESSION_DURATION_MS = 30 * 60 * 1000L // 30 menit
        private const val TAG = "DataAkunActivity"

        fun start(context: Context) {
            val intent = Intent(context, DataAkunActivity::class.java)
            context.startActivity(intent)
        }
    }

    private lateinit var tvNamaLengkap: TextView
    private lateinit var tvNomorTelepon: TextView
    private lateinit var tvPassword: TextView
    private lateinit var btnEditProfile: Button
    private lateinit var btnSignOut: Button
    private lateinit var ivBack: ImageView

    private lateinit var database: DatabaseReference
    private var currentUser: model_user? = null
    private var firebaseListener: ValueEventListener? = null
    private var databaseQuery: DatabaseReference? = null
    private var isActivityDestroyed = false

    // Activity result launcher untuk menangani hasil dari EditProfileActivity
    private val editProfileLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            // Profile berhasil diupdate, reload data
            loadProfileData()
            Toast.makeText(this, getString(R.string.profile_refreshed), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_data_akun)

        // Handle system bars untuk edge-to-edge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews()
        initComponents()

        if (checkLoginStatus()) {
            loadProfileData()
            setupClickListeners()
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh data ketika kembali ke activity ini
        if (checkLoginStatus()) {
            loadProfileData()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        isActivityDestroyed = true
        // Remove Firebase listener untuk avoid memory leak
        cleanupFirebaseListener()
    }

    private fun cleanupFirebaseListener() {
        try {
            firebaseListener?.let { listener ->
                databaseQuery?.removeEventListener(listener)
            }
            firebaseListener = null
            databaseQuery = null
        } catch (e: Exception) {
            Log.e(TAG, "Error cleaning up Firebase listener: ${e.message}")
        }
    }

    private fun initViews() {
        try {
            tvNamaLengkap = findViewById(R.id.tvNamaLengkap)
            tvNomorTelepon = findViewById(R.id.tvNomorTelepon)
            tvPassword = findViewById(R.id.tvPassword)
            btnEditProfile = findViewById(R.id.btnEditProfile)
            btnSignOut = findViewById(R.id.btnSignOut)
            ivBack = findViewById(R.id.ivBack)
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing views: ${e.message}")
            Toast.makeText(this, getString(R.string.error_loading_interface), Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun initComponents() {
        try {
            database = FirebaseDatabase.getInstance().reference
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing Firebase: ${e.message}")
            Toast.makeText(this, getString(R.string.error_connecting_database), Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkLoginStatus(): Boolean {
        return try {
            val sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            val isLoggedIn = sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
            val loginTime = sharedPreferences.getLong(KEY_LOGIN_TIME, 0L)
            val currentTime = System.currentTimeMillis()
            val sessionExpired = (currentTime - loginTime) > SESSION_DURATION_MS

            if (!isLoggedIn || sessionExpired) {
                val message = if (sessionExpired) getString(R.string.session_expired) else getString(R.string.not_logged_in)
                Toast.makeText(this, "$message, ${getString(R.string.please_login_again)}", Toast.LENGTH_SHORT).show()
                redirectToLogin()
                false
            } else {
                true
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error checking login status: ${e.message}")
            redirectToLogin()
            false
        }
    }

    private fun redirectToLogin() {
        try {
            clearLoginSession()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        } catch (e: Exception) {
            Log.e(TAG, "Error redirecting to login: ${e.message}")
        }
    }

    private fun clearLoginSession() {
        try {
            val sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            with(sharedPreferences.edit()) {
                clear()
                apply()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error clearing login session: ${e.message}")
        }
    }

    private fun loadProfileData() {
        try {
            val sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            val phoneNumber = sharedPreferences.getString(KEY_USER_PHONE, null)
            val userName = sharedPreferences.getString(KEY_USER_NAME, null)

            Log.d(TAG, "Loading profile - Phone: $phoneNumber, Name: $userName")

            if (phoneNumber.isNullOrEmpty()) {
                Toast.makeText(this, getString(R.string.user_data_not_found), Toast.LENGTH_SHORT).show()
                redirectToLogin()
                return
            }

            // Set loading state with null checks
            if (::tvNamaLengkap.isInitialized) tvNamaLengkap.text = getString(R.string.loading)
            if (::tvNomorTelepon.isInitialized) tvNomorTelepon.text = phoneNumber
            if (::tvPassword.isInitialized) tvPassword.text = getString(R.string.loading)

            // Load from Firebase
            loadFromFirebase(phoneNumber, userName)

        } catch (e: Exception) {
            Log.e(TAG, "Error loading profile data: ${e.message}")
            Toast.makeText(this, getString(R.string.error_loading_profile), Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadFromFirebase(phoneNumber: String, cachedUserName: String?) {
        try {
            val phoneKey = getPhoneKey(phoneNumber)
            databaseQuery = database.child("users").child(phoneKey)

            firebaseListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (isActivityDestroyed) return

                    if (snapshot.exists()) {
                        try {
                            currentUser = snapshot.getValue(model_user::class.java)
                            currentUser?.let { user ->
                                val nama = user.username?.takeIf { it.isNotEmpty() } ?: getString(R.string.name_not_available)
                                val password = user.password ?: ""

                                // Update SharedPreferences dengan data terbaru
                                updateUserDataInPrefs(nama)
                                displayUserData(phoneNumber, nama, password)

                                Log.d(TAG, "Data loaded from Firebase successfully")
                            } ?: run {
                                Log.w(TAG, "User object is null")
                                handleFirebaseError(phoneNumber, cachedUserName)
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "Error parsing user data: ${e.message}")
                            handleFirebaseError(phoneNumber, cachedUserName)
                        }
                    } else {
                        Log.w(TAG, "User data not found in Firebase")
                        handleFirebaseError(phoneNumber, cachedUserName)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    if (isActivityDestroyed) return
                    Log.e(TAG, "Firebase error: ${error.message}")
                    handleFirebaseError(phoneNumber, cachedUserName)
                }
            }

            databaseQuery?.addListenerForSingleValueEvent(firebaseListener!!)

        } catch (e: Exception) {
            Log.e(TAG, "Error setting up Firebase listener: ${e.message}")
            handleFirebaseError(phoneNumber, cachedUserName)
        }
    }

    private fun handleFirebaseError(phoneNumber: String, cachedUserName: String?) {
        if (isActivityDestroyed) return

        try {
            val displayName = cachedUserName?.takeIf { it.isNotEmpty() } ?: getString(R.string.data_not_available)
            displayUserData(phoneNumber, displayName, "***")

            if (cachedUserName.isNullOrEmpty()) {
                runOnUiThread {
                    Toast.makeText(this@DataAkunActivity, getString(R.string.failed_load_server_data), Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error handling Firebase error: ${e.message}")
        }
    }

    private fun updateUserDataInPrefs(nama: String) {
        try {
            val sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            with(sharedPreferences.edit()) {
                putString(KEY_USER_NAME, nama)
                apply()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error updating user data in preferences: ${e.message}")
        }
    }

    private fun displayUserData(phoneNumber: String, nama: String, password: String) {
        if (isActivityDestroyed) return

        runOnUiThread {
            try {
                if (::tvNamaLengkap.isInitialized) tvNamaLengkap.text = nama
                if (::tvNomorTelepon.isInitialized) tvNomorTelepon.text = phoneNumber
                if (::tvPassword.isInitialized) tvPassword.text = censorPassword(password)
            } catch (e: Exception) {
                Log.e(TAG, "Error displaying user data: ${e.message}")
            }
        }
    }

    private fun censorPassword(password: String): String {
        return when {
            password.isEmpty() || password == "***" -> "***"
            password.length <= 3 -> "***"
            else -> "***" + password.substring(3)
        }
    }

    private fun getPhoneKey(phoneNumber: String): String {
        return phoneNumber.replace(Regex("[^0-9]"), "")
    }

    private fun setupClickListeners() {
        try {
            ivBack.setOnClickListener {
                finish()
            }

            btnEditProfile.setOnClickListener {
                openEditProfile()
            }

            btnSignOut.setOnClickListener {
                showSignOutDialog()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error setting up click listeners: ${e.message}")
        }
    }

    private fun openEditProfile() {
        try {
            val intent = Intent(this, EditProfileActivity::class.java)
            editProfileLauncher.launch(intent)
        } catch (e: Exception) {
            Log.e(TAG, "Error opening edit profile: ${e.message}")
            Toast.makeText(this, getString(R.string.error_opening_edit_profile), Toast.LENGTH_SHORT).show()
        }
    }

    private fun showSignOutDialog() {
        try {
            if (isActivityDestroyed || isFinishing) return

            AlertDialog.Builder(this)
                .setTitle(getString(R.string.sign_out_title))
                .setMessage(getString(R.string.sign_out_message))
                .setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                    dialog.dismiss()
                    signOut()
                }
                .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                    dialog.dismiss()
                }
                .setCancelable(true)
                .show()
        } catch (e: Exception) {
            Log.e(TAG, "Error showing sign out dialog: ${e.message}")
            // Fallback: langsung sign out jika dialog gagal
            signOut()
        }
    }

    private fun signOut() {
        try {
            clearLoginSession()
            cleanupFirebaseListener()

            runOnUiThread {
                Toast.makeText(this@DataAkunActivity, getString(R.string.sign_out_success), Toast.LENGTH_SHORT).show()
            }

            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        } catch (e: Exception) {
            Log.e(TAG, "Error during sign out: ${e.message}")
            finish() // At least close the current activity
        }
    }
}