package com.example.niksey.ui.screens.register

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.niksey.MainActivity
import com.example.niksey.R
import com.example.niksey.database.AUTH
import com.example.niksey.database.NODE_USERS
import com.example.niksey.database.REF_DATABASE_ROOT
import com.example.niksey.database.generateRandomFullname
import com.example.niksey.database.generateRandomUsername
import com.example.niksey.models.UserModel
import com.example.niksey.utillits.showToast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider

class EnteredFragment : Fragment(R.layout.fragment_entered) {
    private val RC_SIGN_IN = 9001

    @SuppressLint("LongLogTag")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val signInButton: SignInButton? = view.findViewById(R.id.sign_in_button)
        val anonymousSignInButton: Button? = view.findViewById(R.id.anonymous_sign_in_button)

        signInButton?.setOnClickListener {
            Log.d("EnteredFragment", "Google Sign-In button clicked")
            signInWithGoogle()
        }

        anonymousSignInButton?.setOnClickListener {
            Log.d("EnteredFragment", "Anonymous Sign-In button clicked")
            signInAnonymously()
        }
    }

    @SuppressLint("LongLogTag")
    private fun signInWithGoogle() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        val googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
        val signInIntent = googleSignInClient.signInIntent
        Log.d("EnteredFragment", "Starting Google Sign-In intent")
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    @SuppressLint("LongLogTag")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                Log.d("EnteredFragment", "Google Sign-In successful, account: ${account?.email}")
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                Log.e("EnteredFragment", "Google Sign-In failed", e)
                showToast(getString(R.string.auth_failed))
            }
        }
    }

    @SuppressLint("LongLogTag")
    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount?) {
        val credential = GoogleAuthProvider.getCredential(acct?.idToken, null)
        AUTH.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("EnteredFragment", "Firebase authentication with Google successful")
                showToast(getString(R.string.welcome))
                navigateToMainActivity()
            } else {
                Log.e("EnteredFragment", "Firebase authentication with Google failed", task.exception)
                showToast(task.exception?.message.toString())
            }
        }
    }

    @SuppressLint("LongLogTag")
    private fun signInAnonymously() {
        AUTH.signInAnonymously().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("EnteredFragment", "Anonymous sign-in successful")
                val randomUsername = generateRandomUsername()
                val randomFullname = generateRandomFullname()

                val uid = AUTH.currentUser?.uid ?: ""
                val user = UserModel(id = uid, username = randomUsername, fullname = randomFullname)

                REF_DATABASE_ROOT.child(NODE_USERS).child(uid).setValue(user)
                    .addOnSuccessListener {
                        showToast(getString(R.string.welcome))
                        navigateToMainActivity()
                    }
                    .addOnFailureListener {
                        showToast(it.message.toString())
                    }
            } else {
                Log.e("EnteredFragment", "Anonymous sign-in failed", task.exception)
                showToast(task.exception?.message.toString())
            }
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(activity, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}
