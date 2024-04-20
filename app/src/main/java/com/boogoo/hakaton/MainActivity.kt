package com.boogoo.hakaton

import android.content.Intent


import com.boogoo.hakaton.databinding.ActivityMainBinding
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.boogoo.hakaton.act.EditAdsAct
import com.boogoo.hakaton.adapters.AdsRcAdapter
import com.boogoo.hakaton.adapters.QuizListAdapter

import com.boogoo.hakaton.dialoghelper.DialogConst
import com.boogoo.hakaton.dialoghelper.DialogHelper
import com.boogoo.hakaton.dialoghelper.GoogleAcConst
import com.boogoo.hakaton.model.QuizModel
import com.boogoo.hakaton.viewmodel.FirebaseViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
     private lateinit var tvAccaunt:TextView
     private lateinit var rootElement: ActivityMainBinding
     private val dialogHelper = DialogHelper(this)
     val mAuth = Firebase.auth
     val adapter =  AdsRcAdapter(mAuth)
     private val firebaseViewModel: FirebaseViewModel by viewModels()
     val db = Firebase.database.getReference("users")

     lateinit var quizzadapter: QuizListAdapter
     lateinit var quizModelList : MutableList<QuizModel>



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rootElement = ActivityMainBinding.inflate(layoutInflater)
        val view = rootElement.root

        setContentView(view)
        init()

//        initRecyclerView()
//        initViewModel()
//        firebaseViewModel.loadAllAds()

        bottomMenuOnClick()
        AllButtonsOnClick()

//        quizModelList = mutableListOf()
//        getDataFromFirebase()
    }

    override fun onResume() {
        super.onResume()
        rootElement.mainContent.bNavView.selectedItemId = R.id.id_home
    }

    override fun onStart() {
        super.onStart()
        uiUpdate(mAuth.currentUser)
    }

    private fun initViewModel(){
        firebaseViewModel.liveAdsData.observe(this) {
            adapter.updateAdapter(it)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == GoogleAcConst.GOOGLE_SIGN_iN_REQUEST_CODE){
            //Log.d("MyLog","Sign in result")
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                if(account != null){
                    dialogHelper.accHelper.signInFirebaseWithGoogle(account.idToken!!)
                }
            }
            catch (e: ApiException){
                Log.d("MyLog", "Api error : ${e.message}")
            }
        }
        //super.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult (requestCode, resultCode, data)
    }




    private fun init(){
        setSupportActionBar(rootElement.mainContent.toolbar)
        var toggle = ActionBarDrawerToggle(this, rootElement.drawerLayout, rootElement.mainContent.toolbar, R.string.open , R.string.close)
        rootElement.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        rootElement.navView.setNavigationItemSelectedListener (this)
        tvAccaunt = rootElement.navView.getHeaderView(0).findViewById(R.id.tvAccountEmail)
    }

    private fun bottomMenuOnClick() = with(rootElement){
        mainContent.bNavView.setOnItemSelectedListener(){ item ->
            when(item.itemId){
                R.id.id_favs -> {
                    Toast.makeText(this@MainActivity,"FAVS", Toast.LENGTH_LONG).show()
                }
                R.id.id_home -> {
//                    firebaseViewModel.loadAllAds()
                    mainContent.toolbar.title = getString(R.string.ad_def)
                }
            }
            true
        }
    }

    private fun AllButtonsOnClick() = with(rootElement){
        mainContent.im1.setOnClickListener(){
            if(mAuth != null){
                db.child(mAuth.uid.toString()).get().addOnSuccessListener {
                    if(it.child("teacher").value == false){
                        var j = Intent(this@MainActivity, EditAdsAct::class.java)
                        startActivity(j)
                        finish()
                    }
                    else {
                        val builder: AlertDialog.Builder = AlertDialog.Builder(this@MainActivity)
                        builder
                            .setMessage("Вы являетесь преподователем")
                            .setTitle("Ошибка!")

                        val dialog: AlertDialog = builder.create()
                        dialog.show()
                    }
                }
            }
        }

        mainContent.im2.setOnClickListener(){
            if(mAuth != null){
                db.child(mAuth.uid.toString()).get().addOnSuccessListener {
                    if(it.child("teacher").value == true){
                        val i = Intent(this@MainActivity, UserlistActivity::class.java)
                        startActivity(i)//
                    }
                    else {
                        val builder: AlertDialog.Builder = AlertDialog.Builder(this@MainActivity)
                        builder
                            .setMessage("Вы не являетесь преподователем")
                            .setTitle("Ошибка!")

                        val dialog: AlertDialog = builder.create()
                        dialog.show()
                    }
                }
            }
        }
    }



//     private fun initRecyclerView(){
//         rootElement.apply {
//             mainContent.rcView.layoutManager = LinearLayoutManager(this@MainActivity)
//             mainContent.rcView.adapter = adapter
//         }
//     }

     override fun onNavigationItemSelected(item: MenuItem): Boolean {
         when(item.itemId){

             R.id.id_sign_up ->{
                 dialogHelper.createSignDialog(DialogConst.SIGN_UP_STATE)
             }
             R.id.id_sign_in ->{
                 dialogHelper.createSignDialog(DialogConst.SIGN_IN_STATE)
             }
             R.id.id_sign_out ->{

                 uiUpdate(null)
                 mAuth.signOut()
                 dialogHelper.accHelper.signOutG()
             }
         }
         rootElement.drawerLayout.closeDrawer(GravityCompat.START);
         return true
     }

    fun uiUpdate(user: FirebaseUser?)  {
        tvAccaunt.text = if(user == null){
            dialogHelper.createSignDialog(DialogConst.SIGN_IN_STATE)
            resources.getString(R.string.not_reg)
        }
        else{
            user.email
        }
    }


//    private fun setupRecyclerView(){
//        quizzadapter = QuizListAdapter(quizModelList)
//        rootElement.mainContent.recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
//        rootElement.mainContent.recyclerView.adapter = quizzadapter
//    }
//
//    private fun getDataFromFirebase(){
//        FirebaseDatabase.getInstance().reference
//            .get()
//            .addOnSuccessListener { dataSnapshot->
//                if(dataSnapshot.exists()){
//                    for (snapshot in dataSnapshot.children){
//                        val quizModel = snapshot.getValue(QuizModel::class.java)
//                        if (quizModel != null) {
//                            quizModelList.add(quizModel)
//                        }
//                    }
//                }
//                setupRecyclerView()
//            }
//
//
//    }



 }