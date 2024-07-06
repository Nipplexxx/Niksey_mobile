package com.example.niksey.ui.screens.groups_messages

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.niksey.R
import com.example.niksey.database.createGroupToDatabase
import com.example.niksey.models.CommonModel
import com.example.niksey.ui.screens.base_fragment.BaseFragment
import com.example.niksey.ui.screens.main_list.MainListFragment
import com.example.niksey.utillits.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mikepenz.materialize.util.KeyboardUtil.hideKeyboard
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView

class CreateGroupFragment(private var listContacts:List<CommonModel>)
    :BaseFragment(R.layout.fragment_create_group) {

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: AddContactsAdapter
    private var mUri = Uri.EMPTY


    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.title = getString(R.string.create_group)
        hideKeyboard(activity)
        initRecyclerView()
        view?.findViewById<ImageView>(R.id.create_group_photo)?.setOnClickListener { addPhoto()  }
        view?.findViewById<FloatingActionButton>(R.id.create_group_btn_complete)?.setOnClickListener {
            val nameGroup = view?.findViewById<EditText>(R.id.create_group_input_name)?.text.toString()
            if (nameGroup.isEmpty()){
                showToast(getString(R.string.enter_a_name))
            } else {
                createGroupToDatabase(nameGroup,mUri,listContacts){
                    replaceFragment(MainListFragment())
                }
            }
        }
        view?.findViewById<EditText>(R.id.create_group_input_name)?.requestFocus()
        view?.findViewById<TextView>(R.id.create_group_counts)?.text = getPlurals(listContacts.size)
    }

    private fun addPhoto() {
        CropImage.activity()
            .setAspectRatio(1, 1)
            .setRequestedSize(250, 250)
            .setCropShape(CropImageView.CropShape.OVAL)
            .start(APP_ACTIVITY,this)
    }

    private fun initRecyclerView() {
        mRecyclerView = view?.findViewById<RecyclerView>(R.id.create_group_recycle_view) !!
        mAdapter = AddContactsAdapter()
        mRecyclerView.adapter = mAdapter
        listContacts.forEach {  mAdapter.updateListItems(it) }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        /* Активность которая запускается для получения картинки для фото пользователя */
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE
            && resultCode == Activity.RESULT_OK && data != null
        ) {
            mUri = CropImage.getActivityResult(data).uri
            view?.findViewById<ImageView>(R.id.create_group_photo)?.setImageURI(mUri)
        }
    }
}