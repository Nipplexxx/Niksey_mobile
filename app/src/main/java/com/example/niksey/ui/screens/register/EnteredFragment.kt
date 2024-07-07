package com.example.niksey.ui.screens.register

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class EnteredFragment : Fragment(R.layout.fragment_entered) {
    private val RC_SIGN_IN = 9001

    @SuppressLint("LongLogTag")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val signInButton: SignInButton? = view.findViewById(R.id.sign_in_button)
        val anonymousSignInButton: Button? = view.findViewById(R.id.anonymous_sign_in_button)
        val emailSignInButton: Button? = view.findViewById(R.id.email_sign_in_button)
        val emailEditText: EditText? = view.findViewById(R.id.email_edit_text)
        val passwordEditText: EditText? = view.findViewById(R.id.password_edit_text)

        signInButton?.setOnClickListener {
            Log.d("EnteredFragment", "Google Sign-In button clicked")
            signInWithGoogle()
        }

        anonymousSignInButton?.setOnClickListener {
            Log.d("EnteredFragment", "Anonymous Sign-In button clicked")
            signInAnonymously()
        }

        emailSignInButton?.setOnClickListener {
            val email = emailEditText?.text.toString()
            val password = passwordEditText?.text.toString()
            fetchUserCredentialsAndSignIn(email, password)
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
        FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("EnteredFragment", "Firebase authentication with Google successful")

                // Get user details
                val user = FirebaseAuth.getInstance().currentUser
                val uid = user?.uid ?: ""
                val email = user?.email ?: ""
                val randomUsername = generateRandomUsername()
                val randomFullname = generateRandomFullname()

                // Create user model
                val userModel = UserModel(id = uid, username = randomUsername, fullname = randomFullname, email = email)

                // Save user to database
                REF_DATABASE_ROOT.child(NODE_USERS).child(uid).setValue(userModel)
                    .addOnSuccessListener {
                        showToast(getString(R.string.welcome))
                        navigateToMainActivity()
                    }
                    .addOnFailureListener {
                        showToast(it.message.toString())
                    }
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

    @SuppressLint("LongLogTag")
    private fun fetchUserCredentialsAndSignIn(email: String, password: String) {
        REF_DATABASE_ROOT.child(NODE_USERS).orderByChild("email").equalTo(email).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (userSnapshot in dataSnapshot.children) {
                        val userModel = userSnapshot.getValue(UserModel::class.java)
                        if (userModel?.email == email && userModel.password == password) {
                            signInWithEmail(email, password)
                            return
                        }
                    }
                    showToast(getString(R.string.auth_failed))
                } else {
                    showToast(getString(R.string.auth_failed))
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("EnteredFragment", "Database error: ${databaseError.message}")
                showToast(getString(R.string.auth_failed))
            }
        })
    }

    @SuppressLint("LongLogTag")
    private fun signInWithEmail(email: String, password: String) {
        AUTH.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("EnteredFragment", "Email sign-in successful")

                // Get user details
                val user = FirebaseAuth.getInstance().currentUser
                val uid = user?.uid ?: ""
                val randomUsername = generateRandomUsername()
                val randomFullname = generateRandomFullname()

                // Create user model
                val userModel = UserModel(id = uid, username = randomUsername, fullname = randomFullname, email = email)

                // Save user to database
                REF_DATABASE_ROOT.child(NODE_USERS).child(uid).setValue(userModel)
                    .addOnSuccessListener {
                        showToast(getString(R.string.welcome))
                        navigateToMainActivity()
                    }
                    .addOnFailureListener {
                        showToast(it.message.toString())
                    }
            } else {
                Log.e("EnteredFragment", "Email sign-in failed", task.exception)
                showToast(task.exception?.message.toString())
            }
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(activity, MainActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }
}
