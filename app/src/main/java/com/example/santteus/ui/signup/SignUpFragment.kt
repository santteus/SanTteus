package com.example.santteus.ui.signup

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.santteus.R
import com.example.santteus.databinding.FragmentSignUpBinding
import com.example.santteus.domain.entity.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

import android.provider.MediaStore
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide


class SignUpFragment : Fragment() {

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private lateinit var binding : FragmentSignUpBinding
    private val REQUST_CODE_GALLERY=10

    private val database by lazy { FirebaseDatabase.getInstance() }
    private val userRef = database.getReference("users")
    private val fbStorage by lazy { FirebaseStorage.getInstance() }

    private val viewModel:SignUpViewModel by viewModels()
    var profile : Uri? =null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_sign_up, container, false)
        binding.vm=viewModel
        binding.lifecycleOwner=viewLifecycleOwner
        setListeners()
        return binding.root
    }

    private fun setListeners(){
        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when(checkedId){
                R.id.radio_woman -> viewModel.sex.value=true
                R.id.radio_man -> viewModel.sex.value=false
            }
        }
        binding.imgUserProfile.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = MediaStore.Images.Media.CONTENT_TYPE
            startActivityForResult(intent, REQUST_CODE_GALLERY)
        }
        binding.btnSignUpCreateUser.setOnClickListener {
            createUser(viewModel.email.value!!,viewModel.password.value!!)
        }
        binding.imgbtnSignUpBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun createUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    val userId = auth.currentUser?.uid
                    val storageReference: StorageReference = fbStorage.reference
                        .child("usersprofileImages").child("uid/$userId")
                    storageReference.putFile(profile!!).addOnCompleteListener {

                        if (userId != null) {
                            val user = User(
                                email,
                                password,
                                viewModel.birth.value!!,
                                viewModel.sex.value!!,
                                viewModel.kg.value!!.toInt(),
                                it.result.toString(),
                                User.Walk("", "",0,0,0,0)
                            )
                            userRef.child(userId).setValue(user)
                            Toast.makeText(requireContext(), "회원가입 성공", Toast.LENGTH_SHORT).show()
                            findNavController().popBackStack()
                        }
                    }


                } else {
                    Toast.makeText(requireContext(), "회원가입222 실패", Toast.LENGTH_SHORT).show()

                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "회원가입 실패", Toast.LENGTH_SHORT).show()
            }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUST_CODE_GALLERY && resultCode == Activity.RESULT_OK){
            if(data?.data == null) return

            profile=data.data!!
            Glide.with(this)
                .load(profile)
                .circleCrop()
                .into(binding.imgUserProfile)

        }

    }


}