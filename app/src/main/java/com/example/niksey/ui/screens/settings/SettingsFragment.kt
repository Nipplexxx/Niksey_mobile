package com.example.niksey.ui.screens.settings

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.niksey.R
import com.example.niksey.database.AUTH
import com.example.niksey.database.CURRENT_UID
import com.example.niksey.database.FOLDER_PROFILE_IMAGE
import com.example.niksey.database.REF_STORAGE_ROOT
import com.example.niksey.database.USER
import com.example.niksey.database.donwloadAndSetImage
import com.example.niksey.database.getUrlFromStorage
import com.example.niksey.database.putImageToStorage
import com.example.niksey.database.putUrlToDatabase
import com.example.niksey.database.removePhotoUser
import com.example.niksey.ui.screens.base_fragment.BaseFragment
import com.example.niksey.utillits.APP_ACTIVITY
import com.example.niksey.utillits.AppStates
import com.example.niksey.utillits.replaceFragment
import com.example.niksey.utillits.restartActivity
import com.example.niksey.utillits.showToast
import com.mikepenz.materialize.util.KeyboardUtil
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import de.hdodenhof.circleimageview.CircleImageView

class SettingsFragment : BaseFragment(R.layout.fragment_settings) {

    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.title = getString(R.string.personal_account)
        setHasOptionsMenu(true)
        initFields()
        KeyboardUtil.hideKeyboard(activity)
    }

    private fun initFields() {
        view?.findViewById<TextView>(R.id.settings_bio)?.text = USER.bio
        view?.findViewById<TextView>(R.id.settings_full_name)?.text = USER.fullname
        view?.findViewById<TextView>(R.id.settings_phone_number)?.text = USER.phone
        view?.findViewById<TextView>(R.id.settings_status)?.text = if (AppStates.getCurrentState() == AppStates.ONLINE) "Online" else "Offline"
        view?.findViewById<TextView>(R.id.settings_username)?.text = USER.username
        view?.findViewById<ConstraintLayout>(R.id.settings_btn_change_username)
            ?.setOnClickListener { replaceFragment(ChangeUsernameFragment()) }
        view?.findViewById<ConstraintLayout>(R.id.settings_btn_change_bio)
            ?.setOnClickListener { replaceFragment(ChangeBioFragment()) }
        view?.findViewById<CircleImageView>(R.id.settings_shange_photo)
            ?.setOnClickListener { changePhotoUser() }
        view?.findViewById<CircleImageView>(R.id.settings_user_photo)
            ?.donwloadAndSetImage(USER.photoUrl)
        view?.findViewById<TextView>(R.id.settings_email)?.text = maskString(USER.email)
        view?.findViewById<TextView>(R.id.settings_password)?.text = maskString(USER.password)
    }

    private fun changePhotoUser() {
        CropImage.activity()
            .setAspectRatio(1, 1)
            .setRequestedSize(250, 250)
            .setCropShape(CropImageView.CropShape.OVAL)
            .start(APP_ACTIVITY, this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            val uri = CropImage.getActivityResult(data).uri
            val path = REF_STORAGE_ROOT.child(FOLDER_PROFILE_IMAGE).child(CURRENT_UID)
            putImageToStorage(uri, path) {
                getUrlFromStorage(path) {
                    putUrlToDatabase(it) {
                        view?.findViewById<CircleImageView>(R.id.settings_user_photo)
                            ?.donwloadAndSetImage(it)
                        showToast(getString(R.string.toast_data_update))
                        USER.photoUrl = it
                    }
                }
            }
        }
    }

    private fun maskString(input: String): String {
        return if (input.length > 3) {
            input.substring(0, 3) + "*".repeat(input.length - 3)
        } else {
            input
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        activity?.menuInflater?.inflate(R.menu.settings_actions_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.settings_menu_delete_photo -> removePhotoUser(USER.id) {
                showToast(getString(R.string.remove_photo_user))
                restartActivity()
            }
            R.id.settings_menu_exit -> {
                AppStates.updateState(AppStates.OFFLINE)
                AUTH.signOut()
                restartActivity()
            }
            R.id.settings_menu_change_name -> replaceFragment(ChangeNameFragment())
        }
        return true
    }
}
