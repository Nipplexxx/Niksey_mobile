package com.example.lotlinmessenger.ui.screens.groups

import android.net.Uri
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.lotlinmessenger.R
import com.example.lotlinmessenger.database.createGroupToDatabase
import com.example.lotlinmessenger.models.CommonModel
import com.example.lotlinmessenger.ui.screens.base.BaseFragment
import com.example.lotlinmessenger.ui.screens.main_list.MainListFragment
import com.example.lotlinmessenger.utillits.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mikepenz.materialize.util.KeyboardUtil.hideKeyboard

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
                showToast("Введите имя")
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

    }

    private fun initRecyclerView() {
        mRecyclerView = view?.findViewById<RecyclerView>(R.id.create_group_recycle_view) !!
        mAdapter = AddContactsAdapter()
        mRecyclerView.adapter = mAdapter
        listContacts.forEach {  mAdapter.updateListItems(it) }
    }
}