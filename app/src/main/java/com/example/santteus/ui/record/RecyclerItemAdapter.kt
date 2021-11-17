package com.example.santteus.ui.record

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.santteus.R
import java.util.*
import kotlin.random.Random.Default.nextInt


class RecyclerItemAdapter(private val context: Context) : RecyclerView.Adapter<RecyclerItemAdapter.ViewHolder>() {

    var datas = mutableListOf<BadgeData>()




    override fun getItemCount(): Int = datas.size


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(datas[position])

    }

    // 각 항목에 필요한 기능을 구현
    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {

        private val roadname: TextView = itemView.findViewById(R.id.road_name)
        var imgs = arrayOf(R.drawable.badge_green, R.drawable.badge_purple, R.drawable.badge_red, R.drawable.badge_yellow)

        val random = Random()
        val num = random.nextInt(4)

        val rand_img = imgs.get(num)

        fun bind(item: BadgeData) {
            roadname.text = item.roadname
            roadname.background = ContextCompat.getDrawable(roadname.context, rand_img);


        }


    }


}