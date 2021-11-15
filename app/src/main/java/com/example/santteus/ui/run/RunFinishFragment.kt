package com.example.santteus.ui.run

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.santteus.databinding.FragmentRunFinishBinding
import com.example.santteus.ui.run.dialog.RunCompleteFragment


class RunFinishFragment(time: String, timeSeconds: Int, distance: String, step: Int) :
    DialogFragment() {

    lateinit var binding: FragmentRunFinishBinding
    private val viewModel: RunViewModel by activityViewModels()

    var userTime = time
    var userTimeSeconds = timeSeconds
    var userDistance = distance
    var userStep = step

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRunFinishBinding.inflate(requireActivity().layoutInflater)
        binding.vm = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        setListeners()
        setRunView()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        //
    }

    private fun setListeners() {
        binding.btnRunSave.setOnClickListener {
            RunCompleteFragment().show(parentFragmentManager, "complete")
            viewModel.requestSetUserWalk(viewModel.userWalk.value!!)
            dialog?.dismiss()
        }

    }

    private fun setRunView() {
        viewModel.requestUserWalk(this,userTime, userTimeSeconds, userDistance, userStep)
    }


}