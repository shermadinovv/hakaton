package com.boogoo.hakaton

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.boogoo.hakaton.adapters.QuizListAdapter
import com.boogoo.hakaton.databinding.ActivityTestsBinding
import com.boogoo.hakaton.model.QuizModel
import com.google.firebase.database.FirebaseDatabase

class TestsActivity : AppCompatActivity() {


    private lateinit var rootElement: ActivityTestsBinding
    lateinit var quizzadapter: QuizListAdapter
    lateinit var quizModelList : MutableList<QuizModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rootElement = ActivityTestsBinding.inflate(layoutInflater)
        val view = rootElement.root

        setContentView(view)


        quizModelList = mutableListOf()
        getDataFromFirebase()
    }


    private fun setupRecyclerView(){
        quizzadapter = QuizListAdapter(quizModelList)
        rootElement.rcTests.layoutManager = LinearLayoutManager(this@TestsActivity)
        rootElement.rcTests.adapter = quizzadapter
    }

    private fun getDataFromFirebase(){
        FirebaseDatabase.getInstance().reference
            .get()
            .addOnSuccessListener { dataSnapshot->
                if(dataSnapshot.exists()){
                    for (snapshot in dataSnapshot.children){
                        val quizModel = snapshot.getValue(QuizModel::class.java)
                        if (quizModel != null) {
                            quizModelList.add(quizModel)
                        }
                    }
                }
                setupRecyclerView()
            }


    }
}