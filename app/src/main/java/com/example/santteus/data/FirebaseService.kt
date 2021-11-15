package com.example.santteus.data

import com.example.santteus.domain.entity.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

object FirebaseService {

    private var auth : FirebaseAuth? = null
    private var database : FirebaseDatabase
    var age = 0
    var top = 0
    var cal = 0

    init {
        auth = FirebaseAuth.getInstance()
        database= FirebaseDatabase.getInstance()

    }

     fun getUserWalk(time:String,timeSeconds:Int,distance:String,step:Int): User.Walk {
         try{
             database.reference.child("users").child(auth?.currentUser?.uid!!)
                 .addValueEventListener(object : ValueEventListener {
                     override fun onDataChange(snapshot: DataSnapshot) {
                         val sex = snapshot.child("sex").value.toString()
                         val kg = snapshot.child("kg").value.toString().toInt()
                         cal=(timeSeconds/1000)*kg

                         if(sex == "true") {
                             getWalkAvg("f")
                         }else{
                             getWalkAvg("m")
                         }
                     }

                     override fun onCancelled(error: DatabaseError) {
                     }

                 })


        }catch (e: Exception){

        }

        return User.Walk(time,distance,age,step,cal,top)

    }

    fun getWalkAvg(sex:String){
        val myRef: DatabaseReference = database.getReference("walk_avg").child(sex)
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(userSnapshot: DataSnapshot) {
                val step=0

                val step10 =userSnapshot.child("10대").value.toString().toInt()
                val step20 =userSnapshot.child("20대").value.toString().toInt()
                val step30 =userSnapshot.child("30대").value.toString().toInt()
                val step40 =userSnapshot.child("40대").value.toString().toInt()
                val step50 =userSnapshot.child("50대").value.toString().toInt()
                val step60 =userSnapshot.child("60대").value.toString().toInt()
                val step70 =userSnapshot.child("70대").value.toString().toInt()

                if (step < step10) {
                    age = 10
                    top= ((10000/step10)*100)
                } else if (step > step10 && step < step20
                ) {
                    age = if(step < (step10+step20)/2){
                        10
                    }else{
                        20
                    }

                } else if (step > step20 && step < step30
                ) {
                    age = if(step < (step20+step30)/2){
                        20
                    }else{
                        30
                    }

                } else if (step > step30 && step <step40
                ) {
                    age = if(step < (step30+step40)/2){
                        30
                    }else{
                        40
                    }
                }else if (step > step40 && step < step50
                ) {
                    age = if(step < (step40+step50)/2){
                        40
                    }else{
                        50
                    }

                }  else if (step > step50 && step < step60
                ) {
                    age = if(step < (step50+step60)/2){
                        50
                    }else{
                        60
                    }

                } else if (step > step60 && step < step70
                ) {
                    age = if(step < (step60+step70)/2){
                        60
                    }else{
                        70
                    }

                } else if (step >= step70) {
                    age = 70
                }


            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })


    }
}