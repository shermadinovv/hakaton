package com.boogoo.hakaton.act

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import com.boogoo.hakaton.R
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.boogoo.hakaton.adapters.ImageAdapter
import com.boogoo.hakaton.model.Ad
import com.boogoo.hakaton.model.DbManager
import com.boogoo.hakaton.databinding.ActivityEditAdsBinding
import com.boogoo.hakaton.databinding.SignDialogBinding
import com.boogoo.hakaton.diaologs.DialogSpinnerHelper
import com.boogoo.hakaton.frag.FragmentCloseInterface
import com.boogoo.hakaton.model.UserInfo
import com.boogoo.hakaton.model.users
import com.boogoo.hakaton.utils.CityHelper
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.logging.SimpleFormatter

class EditAdsAct : AppCompatActivity(), FragmentCloseInterface {

    lateinit var rootElement : ActivityEditAdsBinding
    lateinit var ImageUri : Uri
    private var dialog = DialogSpinnerHelper()
    private val dbManager = DbManager()
    val auth = Firebase.auth
    val db = Firebase.database.getReference("usersInfo")

    private var Diplom : String? = null
    private var Pic : String? = null
    private var Pass : String? = null
    private var isEditState = false
    private var ad: Ad? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rootElement = ActivityEditAdsBinding.inflate(layoutInflater)
        setContentView(rootElement.root)
        init()


        rootElement.PassFront.setOnClickListener(){
            SelectPassImage()
        }
        rootElement.diplom.setOnClickListener(){
            SelectDiplomImage()
        }
        rootElement.pic.setOnClickListener(){
            SelectPicImage()
        }

        rootElement.btPublish.setOnClickListener{
            Publish()
        }

    }

    private fun init(){

    }

    private fun SelectDiplomImage(){
        val intent = Intent()
        intent.type = "image/*"
        intent.action  = Intent.ACTION_GET_CONTENT

        startActivityForResult(intent,100)
    }

    private fun UploadDiplomImage(){
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Загружается...")
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.show()


        val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
        val now = Date()
        val fileName = formatter.format(now)
        val storageReference = FirebaseStorage.getInstance().getReference("images/$fileName")

        storageReference.putFile(ImageUri).
                addOnSuccessListener {
                    Toast.makeText(this@EditAdsAct,"Загружeно", Toast.LENGTH_SHORT).show()
                    if(progressDialog.isShowing) progressDialog.dismiss()
                    rootElement.diplom.text = "Загружeно"
                    Diplom = ImageUri.toString()
                }.addOnFailureListener(){
                    if(progressDialog.isShowing) progressDialog.dismiss()
                    Toast.makeText(this@EditAdsAct,"Ошибка", Toast.LENGTH_SHORT).show()
                    rootElement.diplom.text = "Не загружeно"
                }
    }

    private fun SelectPassImage(){
        val intent = Intent()
        intent.type = "image/*"
        intent.action  = Intent.ACTION_GET_CONTENT

        startActivityForResult(intent,101)
    }

    private fun UploadDpassImage(){
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Загружается...")
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.show()


        val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
        val now = Date()
        val fileName = formatter.format(now)
        val storageReference = FirebaseStorage.getInstance().getReference("images/$fileName")

        storageReference.putFile(ImageUri).
        addOnSuccessListener {
            Toast.makeText(this@EditAdsAct,"Загружeно", Toast.LENGTH_SHORT).show()
            if(progressDialog.isShowing) progressDialog.dismiss()
            rootElement.PassFront.text = "Загружeно"
            Pass = ImageUri.toString()
        }.addOnFailureListener(){
            if(progressDialog.isShowing) progressDialog.dismiss()
            Toast.makeText(this@EditAdsAct,"Ошибка", Toast.LENGTH_SHORT).show()
            rootElement.PassFront.text = "Не загружeно"
        }
    }

    private fun SelectPicImage(){
        val intent = Intent()
        intent.type = "image/*"
        intent.action  = Intent.ACTION_GET_CONTENT

        startActivityForResult(intent,102)
    }

    private fun UploadPicImage(){
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Загружается...")
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.show()


        val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
        val now = Date()
        val fileName = formatter.format(now)
        val storageReference = FirebaseStorage.getInstance().getReference("images/$fileName")
        Pic = storageReference.toString()

        storageReference.putFile(ImageUri).
        addOnSuccessListener {
            Toast.makeText(this@EditAdsAct,"Загружeно", Toast.LENGTH_SHORT).show()
            if(progressDialog.isShowing) progressDialog.dismiss()
            rootElement.pic.text = "Загружeно"

        }.addOnFailureListener(){
            if(progressDialog.isShowing) progressDialog.dismiss()
            Toast.makeText(this@EditAdsAct,"Ошибка", Toast.LENGTH_SHORT).show()
            rootElement.pic.text = "Не загружeно"
        }
    }





    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 100 && resultCode == RESULT_OK){
            ImageUri = data?.data!!
            UploadDiplomImage()
        }
        if(requestCode == 101 && resultCode == RESULT_OK){
            ImageUri = data?.data!!
            UploadDpassImage()
        }
        if(requestCode == 102 && resultCode == RESULT_OK){
            ImageUri = data?.data!!
            UploadPicImage()
        }
    }


    fun onClickSelectCountry(view: View){
        val listCountry = CityHelper.getAllCountries(this)
        dialog.showSpinnerDialog(this, listCountry, rootElement.tvCountry)
        if(rootElement.tvCity.text.toString() != getString(R.string.select_city)){
            rootElement.tvCity.text = getString(R.string.select_city)
        }
    }

    fun onClickSelectCity(view: View){
        val selectedCountry = rootElement.tvCountry.text.toString()
        if(selectedCountry != getString(R.string.select_country)){
            val listCity = CityHelper.getAllCities(selectedCountry,this)
            dialog.showSpinnerDialog(this, listCity, rootElement.tvCity)
        }
        else{
            Toast.makeText(this, R.string.none_selected_country, Toast.LENGTH_LONG).show()
        }
    }



    fun Publish(){
        val userrInfo = fillUserInfo()
        if(auth.uid != null)db.child(auth.uid.toString()).setValue(userrInfo)
    }

    private fun fillUserInfo(): UserInfo {
        val userInfo: UserInfo
        rootElement.apply{
            userInfo = UserInfo(
                auth.uid.toString(),
                tvCountry.text.toString(),
                tvCity.text.toString(),
                edTel.text.toString(),
                edIndex.text.toString(),
                edSchool.text.toString(),
                edSchoolStart.text.toString(),
                edSchoolFinish.text.toString(),
                edFaculty.text.toString(),
                edBBolum.text.toString(),
                edSchoolScore.text.toString(),
                Diplom,
                edName.text.toString(),
                edLastName.text.toString(),
                Pic,
                Pass
            )
        }
        return userInfo
    }









    override fun onFragClose(list: ArrayList<Bitmap>) {
        rootElement.scroolViewMain.visibility = View.VISIBLE
    }

}
