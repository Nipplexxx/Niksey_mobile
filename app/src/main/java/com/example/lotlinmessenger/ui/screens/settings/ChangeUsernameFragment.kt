package com.example.lotlinmessenger.ui.screens.settings

import android.widget.EditText
import com.example.lotlinmessenger.R
import com.example.lotlinmessenger.database.*
import com.example.lotlinmessenger.ui.screens.base.BaseChangeFragment
import com.example.lotlinmessenger.utillits.*
import java.util.*

@Suppress("DEPRECATION")
class ChangeUsernameFragment : BaseChangeFragment(R.layout.fragment_change_username) {
    lateinit var mNewUsername: String

    override fun onResume() {
        super.onResume()
        view?.findViewById<EditText>(R.id.settings_input_username)?.setText(USER.username)
    }

    override fun change() {
        mNewUsername = view?.findViewById<EditText>(R.id.settings_input_username)?.text.toString()
            .lowercase(Locale.getDefault())
        if (mNewUsername.isEmpty()) {
            showToast("Поле пустое")
        } else {
            REF_DATABASE_ROOT.child(NODE_USERNAMES)
                .addListenerForSingleValueEvent(AppValueEventListener {
                    if (it.hasChild(mNewUsername)) {
                        showToast("Такой пользователь уже существует")
                    } else {
                        changeUsername()
                    }
                })
        }
    }

    private fun changeUsername() {

        REF_DATABASE_ROOT.child(NODE_USERNAMES).child(mNewUsername).setValue(CURRENT_UID)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    updateCurrentUsername(mNewUsername)
                }
            }
    }
}