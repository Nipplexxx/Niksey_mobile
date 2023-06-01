package com.example.lotlinmessenger.ui.screens.settings

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.lotlinmessenger.R
import com.example.lotlinmessenger.database.*
import com.example.lotlinmessenger.ui.screens.base_fragment.BaseFragment
import com.example.lotlinmessenger.utillits.*
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
        view?.findViewById<TextView>(R.id.settings_status)?.text = USER.state
        view?.findViewById<TextView>(R.id.settings_username)?.text = USER.username
        view?.findViewById<ConstraintLayout>(R.id.settings_btn_change_username)
            ?.setOnClickListener { replaceFragment(ChangeUsernameFragment()) }
        view?.findViewById<ConstraintLayout>(R.id.settings_btn_change_bio)
            ?.setOnClickListener { replaceFragment(ChangeBioFragment()) }
        view?.findViewById<CircleImageView>(R.id.settings_shange_photo)
            ?.setOnClickListener { changePhotoUser() }
        view?.findViewById<CircleImageView>(R.id.settings_user_photo)
            ?.donwloadAndSetImage(USER.photoUrl)
    }

    private fun changePhotoUser() {
        CropImage.activity()
            .setAspectRatio(1, 1)
            .setRequestedSize(600, 600)
            .setCropShape(CropImageView.CropShape.OVAL)
            .start(APP_ACTIVITY, this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE
            && resultCode == RESULT_OK && data != null){
            val uri = CropImage.getActivityResult(data).uri
            val path = REF_STORAGE_ROOT.child(FOLDER_PROFILE_IMAGE)
                .child(CURRENT_UID)
            putImageToStorage(uri,path) {
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        activity?.menuInflater?.inflate(R.menu.settings_actions_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
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
