package com.example.lotlinmessenger.ui.screens.register

import android.app.NotificationManager.IMPORTANCE_DEFAULT
import android.os.Build
import android.widget.EditText
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import com.chaos.view.PinView
import com.example.lotlinmessenger.R
import com.example.lotlinmessenger.database.*
import com.example.lotlinmessenger.utillits.*
import com.google.firebase.auth.PhoneAuthProvider
import android.app.NotificationChannel as NotificationChannel1

class EnterCodeFragment(private val phoneNumber: String, val id: String) :
    Fragment(R.layout.fragment_enter_code) {

    override fun onStart() {
        super.onStart()
        APP_ACTIVITY.title = phoneNumber
        view?.findViewById<PinView>(R.id.register_input_code)?.addTextChangedListener(
            AppTextWatcher {
                val string = view?.findViewById<PinView>(R.id.register_input_code)?.text.toString()
                if (string.length == 6) {
                    enterCode()
                }
            })
    }

    private fun enterCode() {
        /* Функция проверяет код, если все нормально, производит создания информации о пользователе в базе данных.*/
        val code = view?.findViewById<PinView>(R.id.register_input_code)?.text.toString()
        val credential = PhoneAuthProvider.getCredential(id, code)
        AUTH.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val uid = AUTH.currentUser?.uid.toString()
                val dateMap = mutableMapOf<String, Any>()
                dateMap[CHILD_ID] = uid
                dateMap[CHILD_PHONE] = phoneNumber


                REF_DATABASE_ROOT.child(NODE_USERS).child(uid)
                    .addListenerForSingleValueEvent(AppValueEventListener{

                        if (!it.hasChild(CHILD_USERNAME)){
                            dateMap[CHILD_USERNAME] = uid
                        }

                        REF_DATABASE_ROOT.child(
                            NODE_PHONES
                        ).child(phoneNumber).setValue(uid)
                            .addOnFailureListener { showToast(it.message.toString()) }
                            .addOnSuccessListener {
                                REF_DATABASE_ROOT.child(
                                    NODE_USERS
                                ).child(uid).updateChildren(dateMap)
                                    .addOnSuccessListener {
                                        showToast(getString(R.string.welcome))
                                        restartActivity()
                                    }
                                    .addOnFailureListener { showToast(it.message.toString()) }
                            }
                    })
            } else showToast(task.exception?.message.toString())
        }
    }
}

