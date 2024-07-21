package com.ashmit.firebaseauth.NavigationDrawer

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import com.ashmit.firebaseauth.Firebase.FirebaseAuthHelper
import com.ashmit.firebaseauth.LoginScreen
import com.ashmit.firebaseauth.R
import com.ashmit.firebaseauth.databinding.FragmentUpdateEmailBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class UpdateEmailFragment : Fragment() {

    private var _binding: FragmentUpdateEmailBinding? = null
    private val binding get() = _binding!!
    lateinit var updateEmailDB : Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        _binding = FragmentUpdateEmailBinding.inflate(inflater , container , false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

    fun updateEmailDialogBox(){
        //updating users data
        updateEmailDB = Dialog(requireContext())
        updateEmailDB.setContentView(R.layout.db_layout)
        updateEmailDB.setCancelable(false)

        val btnYesUpdatePwd = updateEmailDB.findViewById<Button>(R.id.btnDBYes)
        val btnNoUpdatePwd = updateEmailDB.findViewById<Button>(R.id.btnDBNo)
        val title = updateEmailDB.findViewById<TextView>(R.id.dialogBoxTitle)
        val message = updateEmailDB.findViewById<TextView>(R.id.dialogBoxMessage)
        val email = updateEmailDB.findViewById<EditText>(R.id.dialogBoxPassword)

        title.text = "Update Email"
        message.text = "Please Enter your email for authentication"
        email.hint = "Enter your Email"
        email.inputType = android.text.InputType.TYPE_CLASS_TEXT

        updateEmailDB.show()

        btnYesUpdatePwd.setOnClickListener {
            if(email.text.isEmpty()){
                Toast.makeText(requireContext(), "please Enter your email", Toast.LENGTH_SHORT).show()
            }else{

                FirebaseAuthHelper(requireContext()).updateEmail(email.toString())
//                updateEmail(email.toString())
            }
        }
        btnNoUpdatePwd.setOnClickListener {
            updateEmailDB.dismiss()
        }
    }
}