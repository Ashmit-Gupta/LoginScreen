package com.ashmit.firebaseauth.Firebase

import android.content.Context
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.ashmit.firebaseauth.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FirebaseAuthHelper(private var context: Context) {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // Change: Removed the redundant currUser variable and use auth.currentUser directly
    fun getCurrentUser(): FirebaseUser? = auth.currentUser // returns the current authenticated user from firebase

    fun createUser(email: String, password: String, onComplete: () -> Unit, onFailure: (String) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    sendEmailVerification(onComplete, onFailure)
                } else {
                    Toast.makeText(context, "Sign In Failed : ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener { taskFailure ->
                Toast.makeText(context, taskFailure.message, Toast.LENGTH_SHORT).show()
                Log.d("SIGN_IN_FAILED", taskFailure.message.toString())
            }
    }

    fun login(email: String, password: String, onComplete: () -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "Successfully Logged In", Toast.LENGTH_SHORT).show()
                    onComplete()
                } else {
                    Toast.makeText(context, "Failed to Log In", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener { task ->
                Toast.makeText(context, "Failed to Log In", Toast.LENGTH_SHORT).show()
                Log.d("SIGN_IN_FAILED", task.message.toString())
            }
    }

    fun signOut(onComplete: () -> Unit) {
        auth.signOut()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        GoogleSignIn.getClient(context, gso).signOut().addOnCompleteListener {
            Toast.makeText(context, "Logout Successful", Toast.LENGTH_SHORT).show()
            onComplete()
        }
    }

    fun deleteAccount(password: String, onSuccess: () -> Unit) {
        val currUser = auth.currentUser // Change: Moved the variable inside the function
        if (currUser != null && currUser.email != null) {
            val credential = EmailAuthProvider.getCredential(currUser.email!!, password)
            currUser.reauthenticate(credential).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    currUser.delete().addOnCompleteListener { deleteTask ->
                        if (deleteTask.isSuccessful) {
                            onSuccess()
                        } else {
                            Toast.makeText(context, deleteTask.exception?.message ?: "Unknown error", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(context, task.exception?.message ?: "Re-authentication failed", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                Toast.makeText(context, "Failed to Delete Please try again after some time", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Login Again", Toast.LENGTH_SHORT).show()
        }
    }

    //TODO fix the update email
    fun updateEmail(newEmail: String) {
        val user = auth.currentUser
        if (user != null) {
            user.sendEmailVerification()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(context, "Verification Email Sent", Toast.LENGTH_SHORT).show()

                        // Periodically check for email verification
                        CoroutineScope(Dispatchers.IO).launch {
                            var isEmailVerified = false
                            while (!isEmailVerified) {
                                delay(5000) // Check every 5 seconds
                                user.reload() // Refresh user data
                                if (user.isEmailVerified) {
                                    isEmailVerified = true
                                    user.updateEmail(newEmail).addOnCompleteListener { updateTask ->
                                        CoroutineScope(Dispatchers.Main).launch {
                                            if (updateTask.isSuccessful) {
                                                Toast.makeText(context, "Email Updated", Toast.LENGTH_SHORT).show()
                                            } else {
                                                Toast.makeText(context, "Failed to Update Email", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    }.await()
                                }
                            }
                        }
                    } else {
                        Toast.makeText(context, "Failed to Send Verification Email", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            Toast.makeText(context, "No current user", Toast.LENGTH_SHORT).show()
        }
    }


    private fun sendEmailVerification(onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val currUser = auth.currentUser // Change: Moved the variable inside the function
        if (currUser != null) {
            currUser.sendEmailVerification()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(context, "Verification Email Sent", Toast.LENGTH_SHORT).show()
                        monitorEmailVerification { onSuccess() }
                    } else {
                        onFailure(task.exception?.message ?: "Failed to send verification email")
                    }
                }
        } else {
            onFailure("No current user")
        }
    }

    private fun monitorEmailVerification(onSuccess: () -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                delay(3000)
                try {
                    val currUser = auth.currentUser // Change: Moved the variable inside the loop
                    currUser?.reload()?.await()
                    if (currUser?.isEmailVerified == true) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "SignUp Successful", Toast.LENGTH_SHORT).show()
                            onSuccess()
                        }
                        cancel()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Failed to reload the user", Toast.LENGTH_SHORT).show()
                    }
                    cancel()
                }
            }
        }
    }

    fun displayUserInfo(idName: TextView , idEmail :TextView , idImage:ImageView){
            auth.currentUser?.let {
                val name = it.displayName
                val email = it.email
                val photo = it.photoUrl
                val emailVerified = it.isEmailVerified

                idName.text = name
                idEmail.text = email
                Picasso.get()
                    .load(photo)
                    .into(idImage)
            }?:run{
                idImage.setImageResource(R.drawable.baseline_person_24)
            }

//        }

    }

}
