package com.ashmit.firebaseauth

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.registerForActivityResult
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ashmit.firebaseauth.databinding.ActivityLoginScreenBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LoginScreen : AppCompatActivity() {
    private lateinit var email : String
    private lateinit var password : String
    private lateinit var binding: ActivityLoginScreenBinding
    private lateinit var auth :FirebaseAuth

    //checking if the user has already logged in or not , when the application opens it will checks the onstart for that whether the user has already logged in or not
    override fun onStart() {
        super.onStart()
        //checking if the current user is already logged in or not
        val currentUser :FirebaseUser? = auth.currentUser
        if (currentUser != null){ // this means that the user has already logged in
            startActivity(Intent(this ,MainActivity::class.java ))
            finish()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Retrieve the string resource for "Not yet registered? Sign Up Now"
        val signUpText = getString(R.string.notRegisterd)
        //span is a feature that allows to style and manipulate text in textview such as bold , italics color change , clickable links or any custom behaviour
        // Create a SpannableStringBuilder to apply styling and click functionality
        //SpannableString: Immutable, used for static text with fixed spans.
        //SpannableStringBuilder: Mutable, used for dynamic text with changeable spans.
        val spannableStringBuilder = SpannableStringBuilder(signUpText)

        // Create a ClickableSpan for the entire string, ClickableSpan is a subclass of CharacterStyle that allows you to make a portion of the text clickable. , in this we are just telling that when we click on that str what will happens thats it
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                // Handle click action, e.g., navigate to another activity
                val intent = Intent(this@LoginScreen, SignUpScreen::class.java)
                startActivity(intent)
            }
        }

        // Apply ClickableSpan to the entire spannable text
        /*setSpan() method attaches a span (in this case, ClickableSpan) to a specific portion of the text (signUpText).
        Parameters:
            clickableSpan: The span to attach.
            0: Start index of the span (beginning of signUpText).
            signUpText.length: End index of the span (end of signUpText).
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE: Flag indicating how the span should behave (exclusive to exclusive).
            values of textspan
            SPAN_INCLUSIVE_EXCLUSIVE: Includes the start character but excludes the end character.
            SPAN_EXCLUSIVE_INCLUSIVE: Excludes the start character but includes the end character.
            SPAN_INCLUSIVE_INCLUSIVE: Includes both the start and end characters.
                    */

//        Log.d("VALUE" , spannableStringBuilder.substring(20,30))
        spannableStringBuilder.setSpan(
            clickableSpan,
            20, //start
            signUpText.length, //end
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE /**/
        )

        // Optionally, apply text color to the clickable text
        spannableStringBuilder.setSpan(
            ForegroundColorSpan(Color.BLUE), // Adjust color as needed
            20,
            signUpText.length,
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        // Set the modified SpannableStringBuilder to the TextView using View Binding
        binding.signUp.text = spannableStringBuilder //Setting text applies all spans and styles defined in spannableStringBuilder to binding.signUp.
        binding.signUp.movementMethod = android.text.method.LinkMovementMethod.getInstance() //LinkMovementMethod.getInstance() enables the TextView (binding.signUp) to recognize and react to click events on spans (like ClickableSpan).

        //init the firebase auth
        auth = FirebaseAuth.getInstance()

        //setting the login button
        binding.loginBtn.setOnClickListener{
            password = binding.loginPassword.text.toString()
            email = binding.loginEmail.text.toString()

            if(password.isEmpty() || email.isEmpty()){
                Toast.makeText(this, "Please Enter All the Details", Toast.LENGTH_SHORT).show()
            }else{
                auth.signInWithEmailAndPassword(email ,password)
                    .addOnCompleteListener{task->
                        if(task.isSuccessful){
                            Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this ,MainActivity::class.java ))
                            finish()
                        }else{
                            Toast.makeText(this, "Failed to Register please check you id and password : ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }

        //setting the google login btn
        val googleLogin = GoogleLogin(this)
        //creating a launcher that is the db of google
        val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result->
            if(result.resultCode == Activity.RESULT_OK){
                googleLogin.handleGoogleSignInResult(result.data , this)
            }
        }
        binding.googleLoginBtn.setOnClickListener{
            val signInIntent = googleLogin.getSignInIntent()
            launcher.launch(signInIntent)
        }
    }
}

