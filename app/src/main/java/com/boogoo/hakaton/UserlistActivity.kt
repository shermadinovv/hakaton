package com.boogoo.hakaton;

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.boogoo.hakaton.R
import com.boogoo.hakaton.adapters.AdsRcAdapter
import com.boogoo.hakaton.adapters.InfoListAdapter
import com.boogoo.hakaton.adapters.QuizListAdapter
import com.boogoo.hakaton.databinding.ActivityEditAdsBinding
import com.boogoo.hakaton.databinding.ActivityUserlistBinding
import com.boogoo.hakaton.databinding.UserssInfoItemBinding
import com.boogoo.hakaton.model.UserInfo
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class UserlistActivity : AppCompatActivity() {


    lateinit var rootElement : ActivityUserlistBinding
    lateinit var adapter: InfoListAdapter
    lateinit var infoModelList : MutableList<UserInfo>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_userlist)

        infoModelList = mutableListOf()
        getDataFromFirebase()


    }

    private fun setupRecyclerView(){
        adapter = InfoListAdapter(infoModelList)
        rootElement.userList.layoutManager = LinearLayoutManager(this@UserlistActivity)
        rootElement.userList.adapter = adapter
    }

    private fun getDataFromFirebase(){
        val databaseReference = Firebase.database.getReference("usersInfo")

        databaseReference.get().addOnSuccessListener { dataSnapshot ->
            if (dataSnapshot.exists()) {
                for (snapshot in dataSnapshot.children) {
                    val infoModel = snapshot.getValue(UserInfo::class.java)
                    if (infoModel != null) {
                        infoModelList.add(infoModel)
                    }
                }
                setupRecyclerView()
            } else {
                // Handle case where no data exists at the specified path
            }
        }.addOnFailureListener { exception ->
            // Handle any errors that occur during the data retrieval process
        }


    }
}