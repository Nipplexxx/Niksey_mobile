package com.example.lotlinmessenger.ui.screens.settings

import android.widget.EditText
import com.example.lotlinmessenger.R
import com.example.lotlinmessenger.database.USER
import com.example.lotlinmessenger.database.setNameToDatabase
import com.example.lotlinmessenger.ui.screens.base.BaseChangeFragment
import com.example.lotlinmessenger.utillits.*

@Suppress("DEPRECATION")
class ChangeNameFragment : BaseChangeFragment(R.layout.fragment_change_name) {
    override fun onResume() {
        super.onResume()
        initFullnameList()
    }

    private fun initFullnameList() {
        val fullnameList = USER.fullname.split(" ")
        if (fullnameList.size > 1) {
            view?.findViewById<EditText>(R.id.settings_input_name)?.setText(fullnameList[0])
            view?.findViewById<EditText>(R.id.settings_input_surname)?.setText(fullnameList[1])
        } else view?.findViewById<EditText>(R.id.settings_input_name)?.setText(fullnameList[0])
    }

    override fun change() {
        val name = view?.findViewById<EditText>(R.id.settings_input_name)?.text.toString()
        val surname = view?.findViewById<EditText>(R.id.settings_input_surname)?.text.toString()
        if (name.isEmpty()) {
            showToast(getString(R.string.settings_toast_name_is_empty))
        } else {
            val fullname = "$name $surname"
            setNameToDatabase(fullname)
        }
    }
}