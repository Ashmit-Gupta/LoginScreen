package com.ashmit.firebaseauth

import android.content.Intent
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.ashmit.firebaseauth.databinding.FragmentResetPasswordBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ResetPasswordFragment : Fragment() {

    private var _binding: FragmentResetPasswordBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentResetPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.resetBtn.setOnClickListener {
            val email = binding.resetPwdEmail.text.toString()
            FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(requireContext(), "Email Sent", Toast.LENGTH_SHORT).show()
                        CoroutineScope(Dispatchers.Main).launch {
                            delay(5000)
                            startActivity(Intent(requireContext(), LoginScreen::class.java))
                            requireActivity().finish()
                        }
                    } else {
                        Toast.makeText(requireContext(), "Failed to Send Email", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }


}

