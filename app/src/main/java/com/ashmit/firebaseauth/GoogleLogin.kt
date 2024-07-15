package com.ashmit.firebaseauth

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class GoogleLogin(private val context: Context) {

    private val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(context.getString(R.string.default_web_client_id))
        .requestEmail()
        .build()

    fun handleGoogleSignInResult(data: Intent?, activity: Activity) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        if (task.isSuccessful) {
            val account: GoogleSignInAccount? = task.result
            val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
            FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "Successful", Toast.LENGTH_LONG).show()
                    activity.startActivity(Intent(activity, MainActivity::class.java))
                } else {
                    Toast.makeText(context, "Failed", Toast.LENGTH_LONG).show()
                }
            }.addOnFailureListener { exception ->
                Log.d("FAILED", exception.message.toString())
            }
        } else {
            Log.d("SIGN-IN-FAILED", "Google sign-in task failed: ${task.exception?.message}")
            Toast.makeText(context, "Failed", Toast.LENGTH_LONG).show()
        }
    }

    fun getSignInIntent(): Intent {
        return GoogleSignIn.getClient(context as Activity, googleSignInOptions).signInIntent
    }
}
