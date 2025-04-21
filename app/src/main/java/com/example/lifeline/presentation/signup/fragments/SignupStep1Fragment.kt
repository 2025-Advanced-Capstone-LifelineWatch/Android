package com.example.lifeline.presentation.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.lifeline.R

class SignupStep1Fragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_signup_step1, container, false)

        val nextButton = view.findViewById<View>(R.id.btn_register_1)
        nextButton.setOnClickListener {
            (activity as? SignupActivity)?.goToStep(2)
        }

        return view
    }
}
