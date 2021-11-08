package com.example.santteus.ui.run.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.santteus.databinding.FragmentRunCompleteBinding


class RunCompleteFragment : DialogFragment() {

    private lateinit var _binding: FragmentRunCompleteBinding
    val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        _binding = FragmentRunCompleteBinding.inflate(requireActivity().layoutInflater)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setListeners()
        return binding.root
    }

    private fun setListeners(){
        binding.btnRunClose.setOnClickListener{
            dismiss()
        }

    }

}