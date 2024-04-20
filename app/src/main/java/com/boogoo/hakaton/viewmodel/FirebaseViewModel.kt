package com.boogoo.hakaton.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.boogoo.hakaton.model.Ad
import com.boogoo.hakaton.model.DbManager
import java.util.ArrayList

class  FirebaseViewModel: ViewModel() {
    private val dbManager = DbManager()
    val liveAdsData = MutableLiveData<ArrayList<Ad>>()
    fun loadAllAds(){
        dbManager.getAllAds(object: DbManager.ReadDataCallBack{
            override fun readData(list: ArrayList<Ad>) {
                liveAdsData.value = list
            }
        })
    }

    fun loadMyAds(){
        dbManager.getMyAds(object: DbManager.ReadDataCallBack{
            override fun readData(list: ArrayList<Ad>) {
                liveAdsData.value = list
            }
        })
    }




}