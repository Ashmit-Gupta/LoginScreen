package com.ashmit.firebaseauth

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.provider.CalendarContract.Colors
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.registerForActivityResult
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ashmit.firebaseauth.Firebase.FirebaseAuthHelper
import com.ashmit.firebaseauth.databinding.ActivityLoginScreenBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LoginScreen : AppCompatActivity() {
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var binding: ActivityLoginScreenBinding
    private lateinit var auth: FirebaseAuth

    //checking if the user has already logged in or not , when the application opens it will checks the onstart for that whether the user has already logged in or not
    override fun onStart() {
        super.onStart()
        //checking if the current user is already logged in or not
        val currentUser: FirebaseUser? = auth.currentUser
        if (currentUser != null) { // this means that the user has already logged in
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.frameLayoutMain) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Set the modified SpannableStringBuilder to the TextView using View Binding
        try{
            binding.signUp.text =
                ClickableStringBuilder().clickableText("Not yet registered? SignUp Now",19) {
                    startActivity(Intent(this@LoginScreen, SignUpScreen::class.java))
                } //Setting text applies all spans and styles defined in spannableStringBuilder to binding.signUp.
            binding.signUp.movementMethod = android.text.method.LinkMovementMethod.getInstance() //LinkMovementMethod.getInstance() enables the TextView (binding.signUp) to recognize and react to click events on spans (like ClickableSpan).

        }catch (e:Exception){
            Log.d("SpannableStr" , e.message.toString())
            Toast.makeText(this, e.message.toString(), Toast.LENGTH_SHORT).show()
        }

        //init the firebase auth
        auth = FirebaseAuth.getInstance()

        //setting the login button
        binding.loginBtn.setOnClickListener {
            password = binding.loginPassword.text.toString()
            email = binding.loginEmail.text.toString()
            if (password.isEmpty() || email.isEmpty()) {
                Toast.makeText(this, "Please Enter All the Details", Toast.LENGTH_SHORT).show()
            } else {
                FirebaseAuthHelper(this).login(email , password) {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            }
        }

        //setting the clickable link on the forgot pwd
        binding.forgotPwd.text = ClickableStringBuilder().clickableText("Forgot Password?" , 0) {
            supportFragmentManager.beginTransaction()
                .add(R.id.frameLayoutMain, ResetPasswordFragment()).commit()
        }
        binding.forgotPwd.movementMethod = android.text.method.LinkMovementMethod.getInstance()


        //setting the google login btn
        val googleLogin = GoogleLogin(this)
        //creating a launcher that is the db of google
        val launcher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    googleLogin.handleGoogleSignInResult(result.data, this)
                }
            }
        binding.googleLoginBtn.setOnClickListener {
            val signInIntent = googleLogin.getSignInIntent()
            launcher.launch(signInIntent)
        }
    }


}

