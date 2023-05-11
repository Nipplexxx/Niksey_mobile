package com.example.lotlinmessenger.utillits

import com.example.lotlinmessenger.database.*

enum class AppStates(val state: String) {
    /* Класс перечисление состояний приложения*/
    ONLINE("online"),
    OFFLINE("offline"),
    TYPING("prints");

    companion object {
        fun updateState(appStates: AppStates) {
            if (AUTH.currentUser != null) {
                REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID).child(CHILD_STATE)
                    .setValue(appStates.state)
                    .addOnSuccessListener { USER.state = appStates.state }
                    .addOnFailureListener { }
            }
        }
    }
}