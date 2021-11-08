package com.example.santteus.ui.mypage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.santteus.R
import com.example.santteus.databinding.FragmentMyPageBinding
import com.example.santteus.ui.run.RunFinishFragment
import com.example.santteus.ui.run.RunStartFragment
import com.example.santteus.ui.run.dialog.RunCompleteFragment

class MyPageFragment : Fragment() {

    private lateinit var notificationsViewModel: MyPageViewModel
    private var _binding: FragmentMyPageBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        notificationsViewModel =
            ViewModelProvider(this).get(MyPageViewModel::class.java)

        _binding = FragmentMyPageBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.btnRun.setOnClickListener {
            RunStartFragment().show(childFragmentManager,"runStart")
           // findNavController().navigate(R.id.action_navigation_my_page_to_runStartFragment)
        }


        return root
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}