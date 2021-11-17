package com.example.santteus.ui.record

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.santteus.R
import com.example.santteus.databinding.FragmentRecordBinding
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.*
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet

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


//        basicSetting()
//        setxAxis()
//        setLeftXaxis()
//        setRightXaxis()
//        createBarChart()
        create()

        initRecycler()


        return root
    }

    private fun create() {
        var barChart: BarChart = binding.barChart // barChart 생성

        val entries = ArrayList<BarEntry>()
        entries.add(BarEntry(1.2f,10000.0f))
        entries.add(BarEntry(2.2f,5000.0f))
        entries.add(BarEntry(3.2f,7000.0f))
        entries.add(BarEntry(4.2f,9000.0f))
        entries.add(BarEntry(5.2f,7000.0f))
        entries.add(BarEntry(6.2f,13000.0f))
        entries.add(BarEntry(7.2f,9000.0f))

        barChart.run {
            description.isEnabled = false // 차트 옆에 별도로 표기되는 description을 안보이게 설정 (false)
            setMaxVisibleValueCount(7) // 최대 보이는 그래프 개수를 7개로 지정
            setPinchZoom(false) // 핀치줌(두손가락으로 줌인 줌 아웃하는것) 설정
            setDrawBarShadow(false) //그래프의 그림자
            setDrawGridBackground(false)//격자구조 넣을건지
            axisRight.run { //왼쪽 축. 즉 Y방향 축을 뜻한다.
                axisMaximum = 15000f //100 위치에 선을 그리기 위해 101f로 맥시멈값 설정
                axisMinimum = 0f // 최소값 0
                granularity = 5000f // 50 단위마다 선을 그리려고 설정.
                setDrawLabels(true) // 값 적는거 허용 (0, 50, 100)
                setDrawGridLines(false) //격자 라인 활용
                setDrawAxisLine(false) // 축 그리기 설정
                axisLineColor = ContextCompat.getColor(context,R.color.santtues_C4C4C4) // 축 색깔 설정
                gridColor = ContextCompat.getColor(context,R.color.santtues_C4C4C4) // 축 아닌 격자 색깔 설정
                textColor = ContextCompat.getColor(context,R.color.santtues_C4C4C4) // 라벨 텍스트 컬러 설정
                textSize = 13f //라벨 텍스트 크기
            }
            xAxis.run {
                position = XAxis.XAxisPosition.BOTTOM //X축을 아래에다가 둔다.
                granularity = 1f // 1 단위만큼 간격 두기
                setDrawAxisLine(true) // 축 그림
                setDrawGridLines(false) // 격자

                textColor = ContextCompat.getColor(context,R.color.santtues_C4C4C4) //라벨 색상
                textSize = 12f // 텍스트 크기
                valueFormatter = MyXAxisFormatter() // X축 라벨값(밑에 표시되는 글자) 바꿔주기 위해 설정
            }
            axisLeft.isEnabled = false// 오른쪽 Y축을 안보이게 해줌.
            setTouchEnabled(false) // 그래프 터치해도 아무 변화없게 막음
            animateY(1000) // 밑에서부터 올라오는 애니매이션 적용
            legend.isEnabled = false //차트 범례 설정
        }

        var set = BarDataSet(entries,"DataSet") // 데이터셋 초기화
        set.color = ContextCompat.getColor(requireContext(),R.color.santtues_FF947C) // 바 그래프 색 설정

        val dataSet :ArrayList<IBarDataSet> = ArrayList()
        dataSet.add(set)
        val data = BarData(dataSet)
        data.barWidth = 0.7f //막대 너비 설정
        barChart.run {
            this.data = data //차트의 데이터를 data로 설정해줌.
            setFitBars(true)
            invalidate()
        }
    }

    inner class MyXAxisFormatter : ValueFormatter() {
        private val days = arrayOf("월","화","수","목","금","토","일")
        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            return days.getOrNull(value.toInt()-1) ?: value.toString()
        }
    }


    // 배지
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



        }
    }
}