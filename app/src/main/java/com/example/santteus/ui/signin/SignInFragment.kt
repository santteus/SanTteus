package com.example.santteus.ui.signin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.santteus.R
import com.example.santteus.databinding.FragmentSignInBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase


class SignInFragment : Fragment() {

    lateinit var binding:FragmentSignInBinding
    private var auth : FirebaseAuth? = null
    private val viewModel:SignInViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_sign_in, container, false)
        binding.vm=viewModel
        binding.lifecycleOwner=viewLifecycleOwner
        auth = FirebaseAuth.getInstance()
        setListeners()
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        if(auth?.currentUser != null){
            findNavController().navigate(R.id.action_signInFragment_to_navigation_home)
        }
    }

    private fun setListeners(){
        binding.btnSignIn.setOnClickListener {
            signIn(viewModel.id.value!!,viewModel.pw.value!!)

        }
        binding.tvToSignUp.setOnClickListener {
            findNavController().navigate(R.id.action_signInFragment_to_signUpFragment)
        }
    }
    private fun signIn(email: String, password: String) {

        if (email.isNotEmpty() && password.isNotEmpty()) {
            auth?.signInWithEmailAndPassword(email, password)?.addOnCompleteListener {task->
                if (task.isSuccessful) {
                    Toast.makeText(
                        context, "로그인에 성공 하였습니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                    findNavController().navigate(R.id.action_signInFragment_to_navigation_home)
                    //moveMainPage(auth?.currentUser)
                } else {
                    Toast.makeText(
                        context, "로그인에 실패 하였습니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

}