package com.example.santteus.ui.record

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.santteus.databinding.FragmentRecordBinding
import com.github.mikephil.charting.components.*
import com.github.mikephil.charting.data.BarEntry
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*

class RecordFragment : Fragment() {

    private lateinit var dashboardViewModel: RecordViewModel
    private var _binding: FragmentRecordBinding? = null

    private var auth: FirebaseAuth? = null


    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()


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


        //create()

        // 탭 설정
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                // 탭이 선택 되었을 때
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // 탭이 선택되지 않은 상태로 변경 되었을 때
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // 이미 선택된 탭이 다시 선택 되었을 때
            }
        })

        // 뷰페이저에 어댑터 연결
        binding.viewPager.adapter = ViewPagerAdapter(this)

        /* 탭과 뷰페이저를 연결, 여기서 새로운 탭을 다시 만드므로 레이아웃에서 꾸미지말고
        여기서 꾸며야함
         */
        TabLayoutMediator(binding.tabLayout, binding.viewPager) {tab, position ->
            when(position) {
                0 -> tab.text = "주"
                1 -> tab.text = "월"
            }
        }.attach()



        initRecycler()



        return root
    }



    // 배지
    private fun initRecycler() {


        recyclerItemAdapter = RecyclerItemAdapter(requireContext())
        binding.recyclerview.adapter = recyclerItemAdapter

        auth = FirebaseAuth.getInstance()
        val myRef =
            database.getReference("users").child(auth?.currentUser?.uid!!).child("data")

        myRef.addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for (snapshot in dataSnapshot.children) {

                    val y = snapshot.child("name").value.toString()

                    Log.d("hi",y)


                    datas.apply {

                        add(BadgeData(y))

                        recyclerItemAdapter.datas = datas
                        recyclerItemAdapter.notifyDataSetChanged()


                        binding.badgeNum.setText(datas.size.toString())

                    }



                }
            }




            override fun onCancelled(databaseError: DatabaseError) {
                //Log.e("MainActivity", String.valueOf(databaseError.tException())); // 에러문 출력
            }
        })





    }

    private fun getDatas(){


    }
}