package com.example.lotlinmessenger.ui.screens.settings

import android.widget.EditText
import com.example.lotlinmessenger.R
import com.example.lotlinmessenger.database.USER
import com.example.lotlinmessenger.database.setBioToDatabase
import com.example.lotlinmessenger.ui.screens.base.BaseChangeFragment
import com.example.lotlinmessenger.utillits.*

@Suppress("DEPRECATION")
class ChangeBioFragment : BaseChangeFragment(R.layout.fragment_change_bio) {

    override fun onResume() {
        super.onResume()
        view?.findViewById<EditText>(R.id.settings_input_bio)?.setText(USER.bio)
    }

    override fun change() {
        super.change()
        val newBio = view?.findViewById<EditText>(R.id.settings_input_bio)?.text.toString()
        setBioToDatabase(newBio)
    }
}