package com.ashmit.firebaseauth.NavigationDrawer

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.ashmit.firebaseauth.Firebase.FirebaseAuthHelper
import com.ashmit.firebaseauth.R
import com.ashmit.firebaseauth.SignUpScreen
import com.ashmit.firebaseauth.databinding.FragmentDeleteAccBinding

class DeleteAccFragment : Fragment() {

    private var _binding : FragmentDeleteAccBinding ? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDeleteAccBinding.inflate(inflater , container , false)
        val pwd = binding.deleteAccPwdNav.text
        binding.deleteBtnNav.setOnClickListener {
            if(pwd.isBlank()){
                Toast.makeText(requireContext(), "Please Enter your password", Toast.LENGTH_SHORT).show()
            }else{
                FirebaseAuthHelper(requireContext()).deleteAccount(pwd.toString()){
                    Toast.makeText(requireContext(), "Account Deleted Successfully", Toast.LENGTH_SHORT).show()
                    requireActivity().finish()
                    startActivity(Intent(requireContext() , SignUpScreen::class.java))
                }
            }
        }
        // Inflate the layout for this fragment
        return binding.root
    }
}