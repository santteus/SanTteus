package com.example.santteus.ui.record

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.santteus.databinding.FragmentRecordBinding

class RecordFragment : Fragment() {

    private lateinit var dashboardViewModel: RecordViewModel
    private var _binding: FragmentRecordBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    lateinit var recyclerItemAdapter: RecyclerItemAdapter
    val datas = mutableListOf<BadgeData>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dashboardViewModel =
            ViewModelProvider(this).get(RecordViewModel::class.java)

        _binding = FragmentRecordBinding.inflate(inflater, container, false)
        val root: View = binding.root



        initRecycler()


        return root
    }


    private fun initRecycler() {
        recyclerItemAdapter = RecyclerItemAdapter(requireContext())
        binding.recyclerview.adapter = recyclerItemAdapter

        datas.apply {
            add(BadgeData("보라매공원"))
            add(BadgeData("모시마루"))
            add(BadgeData("거북이마을"))
            add(BadgeData("천년의숲길"))

            recyclerItemAdapter.datas = datas
            recyclerItemAdapter.notifyDataSetChanged()


            //        val textView: TextView = binding.textDashboard
//        dashboardViewModel.text.observe(viewLifecycleOwner, Observer {
//            textView.text = it
//        })
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
        }
    }
}