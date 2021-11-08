package com.example.santteus.ui.run

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.example.santteus.R
import com.example.santteus.databinding.FragmentRunCompleteBinding
import com.example.santteus.databinding.FragmentRunFinishBinding
import com.example.santteus.domain.entity.Walk
import com.google.firebase.database.*


class RunFinishFragment : DialogFragment() {

    lateinit var binding : FragmentRunFinishBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentRunFinishBinding.inflate(requireActivity().layoutInflater)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setListeners()

        return binding.root
    }

    fun setListeners(){
        val database : FirebaseDatabase = FirebaseDatabase.getInstance()
        val myRef : DatabaseReference = database.getReference("walk")
        myRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val listData: MutableList<Walk> = mutableListOf<Walk>()
                //myRef.child("")
                var sum =0
                for (userSnapshot in dataSnapshot.children){
                    /*if (userSnapshot.key.equals("email")) {
                        longToast(snapshot.value)
                    }*/
                    /*     userSnapshot.child("MESURE_AGRDE_FLAG_NM").value


                     val getData :Walk? = userSnapshot.getValue(Walk::class.java)
                     if (getData != null) {
                         listData.add(getData)
                     }*/
                    //val post: Post? = dataSnapshot.getValue(Post::class.java)
                    if(userSnapshot.child("MESURE_AGRDE_FLAG_NM").value=="10대"){
                        sum += (userSnapshot.child("AVRG_PACE_CO").value as Long).toInt()


                    }
                    Log.d("asdf",
                        sum.toString()
                    )
                }

            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })

    }


}