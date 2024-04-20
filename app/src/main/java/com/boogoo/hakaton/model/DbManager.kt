package com.boogoo.hakaton.model


import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class DbManager  {
    val db = Firebase.database.getReference("main")
    val auth = Firebase.auth

    fun  publishAd(ad: Ad){
        if(auth.uid != null)db.child(ad.key ?: "empty").child(auth.uid!!).child("ad").setValue(ad)
    }

    fun getMyAds(readDataCallBack: ReadDataCallBack){
        val query = db.orderByChild(auth.uid + "/ad/uid").equalTo(auth.uid)
        readDataFromDb(query, readDataCallBack)
    }

    fun getAllAds(readDataCallBack: ReadDataCallBack){
        val query = db.orderByChild(auth.uid + "/ad/price")
        readDataFromDb(query, readDataCallBack)
    }

    private fun readDataFromDb(query: Query,  readDataCallBack: ReadDataCallBack?){
        query.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val adArray = ArrayList<Ad>( )
                for(item in snapshot.children){
                    val ad = item.children.iterator().next().child("ad").getValue(Ad::class.java)
                    if(ad != null)adArray.add(ad)
                }
                readDataCallBack?.readData(adArray)
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    interface ReadDataCallBack  {
        fun readData(list: ArrayList<Ad>){

        }
    }
}