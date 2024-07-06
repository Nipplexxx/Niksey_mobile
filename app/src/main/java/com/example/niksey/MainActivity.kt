@file:Suppress("UNUSED_EXPRESSION")

package com.example.niksey

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.niksey.database.AUTH
import com.example.niksey.database.initFirebase
import com.example.niksey.database.initUser
import com.example.niksey.databinding.ActivityMainBinding
import com.example.niksey.ui.objects.AppDrawer
import com.example.niksey.ui.screens.main_list.MainListFragment
import com.example.niksey.ui.screens.register.EnteredFragment
import com.example.niksey.utillits.APP_ACTIVITY
import com.example.niksey.utillits.AppStates
import com.example.niksey.utillits.initContacts
import com.example.niksey.utillits.replaceFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding
    lateinit var mAppDrawer: AppDrawer
    lateinit var mToolbar: Toolbar

    private val REQUEST_EXTERNAL_STORAGE = 1

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        APP_ACTIVITY = this
        initFirebase()
        initUser {
            initApp()
        }
        checkAndRequestPermissions()
    }

    private fun initApp() {
        CoroutineScope(Dispatchers.IO).launch {
            initContacts()
        }
        initFields()
        initFunc()
        AppStates.updateState(AppStates.ONLINE)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
    }

    private fun initFunc() {
        setSupportActionBar(mToolbar)
        if (AUTH.currentUser != null) {
            mAppDrawer.create()
            replaceFragment(MainListFragment(), false)
        } else {
            replaceFragment(EnteredFragment(), false)
        }
    }

    private fun initFields() {
        mToolbar = mBinding.mainToolbar
        mAppDrawer = AppDrawer()
    }

    private fun checkAndRequestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_EXTERNAL_STORAGE)
        } else {
            accessExternalStorage()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_EXTERNAL_STORAGE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                accessExternalStorage()
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun accessExternalStorage() {
        // Example: Check if external storage is available for reading
        if (isExternalStorageReadable()) {
            // Example: List files in the root directory of external storage
            val externalFiles = Environment.getExternalStorageDirectory().listFiles()
            for (file in externalFiles) {
                // Do something with each file in external storage
                println("External file: ${file.name}")
            }
        } else {
            Toast.makeText(this, "External storage is not readable", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isExternalStorageReadable(): Boolean {
        val state = Environment.getExternalStorageState()
        return Environment.MEDIA_MOUNTED == state || Environment.MEDIA_MOUNTED_READ_ONLY == state
    }

    override fun onStop() {
        super.onStop()
        AppStates.updateState(AppStates.OFFLINE)
    }

    override fun onStart() {
        super.onStart()
        AppStates.updateState(AppStates.ONLINE)
    }
}
