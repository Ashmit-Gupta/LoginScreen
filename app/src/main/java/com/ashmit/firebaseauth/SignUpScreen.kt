package com.ashmit.firebaseauth

import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ashmit.firebaseauth.databinding.ActivitySignUpScreenBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthCredential
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.actionCodeSettings

class SignUpScreen : AppCompatActivity() {
    private val binding: ActivitySignUpScreenBinding by lazy {
        ActivitySignUpScreenBinding.inflate(layoutInflater)
    }

    private lateinit var googleSignInClient : GoogleSignInClient

    private lateinit var auth: FirebaseAuth
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var username: String
    private lateinit var repeatedPwd: String
    private val context= this@SignUpScreen

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initializing FirebaseAuth
        auth = FirebaseAuth.getInstance()


        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()


        // Google sign-in button
        googleSignInClient = GoogleSignIn.getClient(context, gso)
        binding.googleSignUpBtn.setOnClickListener {
            val signInClient = googleSignInClient.signInIntent
            launcher.launch(signInClient)
        }



        val actionCodeSettings = actionCodeSettings {
            url = "https://ashmit.page.link/Tbeh"
            handleCodeInApp = true
            setIOSBundleId("IOS mai khol rha h kya chore ")
            setAndroidPackageName(
                "com.ashmit.firebaseauth",
                true,
                "1"
            )

        }
        binding.signUpBtn.setOnClickListener {
            getSignUpInputs()
            if (checkCredentials()) {
                //sending the link on the email
                auth.sendSignInLinkToEmail(email , actionCodeSettings).addOnCompleteListener{
                    task->
                    if(task.isSuccessful){
                        Toast.makeText(this, "Email Sent", Toast.LENGTH_LONG).show()
                    }
                }.addOnFailureListener{
                    Toast.makeText(this, "Failed to Send Email ${it.message}", Toast.LENGTH_LONG).show()
                    Log.d("Email Failed" , "${it.message}")
                }

                //
                val intent = intent
                val emailLink = intent.data.toString()
                if(auth.isSignInWithEmailLink(emailLink)){
//                    val email =

                    auth.signInWithEmailLink(email , emailLink).addOnCompleteListener{
                        task->
                        if(task.isSuccessful){
                            Toast.makeText(this, "signIn Successful ", Toast.LENGTH_SHORT).show()
                            val result = task.result

                            if(result.additionalUserInfo?.isNewUser != true){
                                Toast.makeText(context, "This user Already Exits , Please Login Again", Toast.LENGTH_SHORT).show()
                                intentPassing(context , LoginScreen::class.java)
                            }else{
                                intentPassing(this, MainActivity::class.java)
                                finish()
                            }

                        }else{
                            Log.e(TAG, "Error signing in with email link", task.exception)
                        }
                    }.addOnFailureListener{
                        Toast.makeText(this, "Failed to Register please check you id and password : ${it.message}", Toast.LENGTH_SHORT).show()
                    }
                }


//                auth.createUserWithEmailAndPassword(email, password)
//                    .addOnCompleteListener(context) { task ->
//                        if (task.isSuccessful) {
//                            Toast.makeText(this, "Registered Successfully", Toast.LENGTH_SHORT).show()
//                            intentPassing(this, LoginScreen::class.java)
//                            finish()
//                        } else {
//                            Toast.makeText(this, "Failed to Register: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
//                        }
//                    }
            }
        }

        binding.loginBtn.setOnClickListener {
            intentPassing(this, LoginScreen::class.java)
        }
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            if (task.isSuccessful) {
                val account: GoogleSignInAccount? = task.result
                val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
                auth.signInWithCredential(credential).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Successful", Toast.LENGTH_LONG).show()
                        intentPassing( context , MainActivity::class.java)
                    } else {
                        Toast.makeText(this, "Failed", Toast.LENGTH_LONG).show()
                    }
                }.addOnFailureListener { exception ->
                    Log.d("FAILED", exception.message.toString())
                }
            } else {
                Log.d("SIGN-IN-FAILED", "Google sign-in task failed: ${task.exception?.message}")
                Toast.makeText(this, "Failed", Toast.LENGTH_LONG).show()
            }
        } else {
            Log.d("SIGN-IN-FAILED", "Result code: ${result.resultCode}")
            Toast.makeText(this, "Failed", Toast.LENGTH_LONG).show()
        }
    }

    // Getting data from the user
    private fun getSignUpInputs() {
        email = binding.emailId.text.toString()
        password = binding.passwordSignUp.text.toString()
        username = binding.nameSignUp.text.toString()
        repeatedPwd = binding.repeatPwdSignUp.text.toString()
    }

    // This function is used to pass intent
    private fun intentPassing(context: Context, targetActivity: Class<out AppCompatActivity>) {
        startActivity(Intent(context, targetActivity))
    }

    // Checking the user credentials
    private fun checkCredentials(): Boolean {
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show()
            return false
        }
        if (email.isEmpty() || password.isEmpty() || username.isEmpty() || repeatedPwd.isEmpty()) {
            Toast.makeText(this, "Please Enter All the Details", Toast.LENGTH_SHORT).show()
            return false
        }
        if (password != repeatedPwd) {
            Toast.makeText(this, "Repeat password Must be Same", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }


}

