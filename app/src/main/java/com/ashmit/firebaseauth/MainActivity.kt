package com.ashmit.firebaseauth

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ashmit.firebaseauth.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.squareup.picasso.Picasso

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private lateinit var auth: FirebaseAuth
    private lateinit var customDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //dialog box appears when the user tries to exit the app
        onBackPressedDispatcher.addCallback(this , object:OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                showDialogBox()
            }
        })


        auth = FirebaseAuth.getInstance()


        //sign out
        binding.btnLogOut.setOnClickListener {
            //logout
            dialogBox(
                this,
                "Log Out",
                "Are you sure you want to log out ?",
                R.drawable.baseline_logout_24
            ) {
                auth.signOut()
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build()
                GoogleSignIn.getClient(this , gso).signOut()
                Toast.makeText(this, "Logout Successfull", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, LoginScreen::class.java))
                finish()
            }
        }


        //Custom dialog box for the delete account

        customDialog = Dialog(this)
//            customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//            customDialog.setContentView(R.layout.custome_dialog_box)
        customDialog.setContentView(R.layout.db_layout)
        customDialog.setCancelable(false)

        val btnYes = customDialog.findViewById<Button>(R.id.btnDBYes)
        val btnNo = customDialog.findViewById<Button>(R.id.btnDBNo)

        btnYes.setOnClickListener {
            val confirmPwd = customDialog.findViewById<EditText>(R.id.dialogBoxPassword).text.toString()
            if (confirmPwd.isEmpty()) {
                Toast.makeText(this, "Please Enter your password", Toast.LENGTH_SHORT).show()
            } else {
                val currUserEmail = auth.currentUser?.email.toString()
                val credential = EmailAuthProvider.getCredential(currUserEmail, confirmPwd)
                auth.currentUser?.reauthenticate(credential)
                    ?.addOnCompleteListener() { task ->
                        if(task.isSuccessful){
                            deleteAccount()
                        }
                    }?.addOnFailureListener { task ->
                        Toast.makeText(this, "Wrong Password : ${task.message}", Toast.LENGTH_SHORT).show()
                        Log.d("ACCDELETEDFAILED" , task.message.toString())
                    }
            }
        }
        btnNo.setOnClickListener {
            customDialog.dismiss()
        }

        binding.btnDeleteAcc.setOnClickListener {
            customDialog.show()
        }


        //displaying the user' data
        displayData()

        //updating users data
        binding.UpdateEmail.setOnClickListener {
            reAuthentication()
        }

    }

    //creating a custom alert dialog box , where the user will enter his password to confirm that he wants to delete his account
    private fun dialogBox(
        context: Context,
        title: String,
        message: String,
        icon: Int,
        onConfirm: () -> Unit
    ) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setIcon(icon)
        builder.setPositiveButton("Yes") { dialogInterface, which ->
            onConfirm()
        }
        builder.setNegativeButton("No") { dialogInterface, which ->
            Toast.makeText(this, "Account Not Deleted", Toast.LENGTH_SHORT).show()
        }
        val alertDialog = builder.create()
        alertDialog.show()
    }


    //deleting a account permanently
    private fun deleteAccount() {
        auth.currentUser?.delete()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Account Deleted Successfully", Toast.LENGTH_SHORT)
                    .show()
                startActivity(Intent(this , SignUpScreen::class.java))
            }
        }?.addOnFailureListener { task ->
            Toast.makeText(this, "Failed ${task.message}", Toast.LENGTH_SHORT).show()
            Log.d("NULLPOINTER", "Failed ${task.message}")
        }
    }

    //this dialog box shows when the user tries to exit the app
    private fun showDialogBox(){

        AlertDialog.Builder(this)
            .setMessage("Are you sure u want to exit ??")
            .setCancelable(false)
            .setPositiveButton("Yes"){
                _,_->
                finish()
            }
            .setNegativeButton("No"){
                dialog,_ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun displayData(){
        auth.currentUser?.let {
            val name = it.displayName
            val email = it.email
            val photo = it.photoUrl
            val emailVerified = it.isEmailVerified

            binding.textView.text = name
            binding.textView2.text = email
            Picasso.get()
                .load(photo)
                .into(binding.img)
        }

    }
    private fun reAuthentication(){
        val intent = intent
        val emailLink = intent.data.toString()
        if (!FirebaseAuth.getInstance().isSignInWithEmailLink(emailLink)) {
            Toast.makeText(this, "Invalid email link", Toast.LENGTH_SHORT).show()
            return
        }
        val credentials = EmailAuthProvider.getCredentialWithLink(auth.currentUser?.email.toString(),emailLink )
        auth.currentUser?.reauthenticateAndRetrieveData(credentials)
            ?.addOnCompleteListener{ task->
                if(task.isSuccessful){
                    Toast.makeText(this, "Re-Authentication Successful", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this, "Re-Authentication Failed", Toast.LENGTH_SHORT).show()
                }
            }?.addOnFailureListener{
                Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
            }
    }


}