package com.boogoo.hakaton.dialoghelper

import android.app.AlertDialog
import android.view.View
import android.widget.Toast
import com.boogoo.hakaton.MainActivity
import com.boogoo.hakaton.R
import com.boogoo.hakaton.accounthelper.AccountHelper
import com.boogoo.hakaton.databinding.SignDialogBinding
import com.boogoo.hakaton.model.Ad
import com.boogoo.hakaton.model.users
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class DialogHelper(act:MainActivity) {
    private val act = act
    val accHelper = AccountHelper(act)


    fun createSignDialog(index:Int){
        val builder = AlertDialog.Builder(act)
        val rootDialogElement = SignDialogBinding.inflate(act.layoutInflater)
        val view = rootDialogElement.root
        builder.setView(view)

        setDialogState(index, rootDialogElement)

        val dialog = builder.create()

        dialog.setCanceledOnTouchOutside(false)
        rootDialogElement.btSignUpIn.setOnClickListener{
            setOnClickSignUpIn(index, rootDialogElement, dialog)
        }
        rootDialogElement.btForgetP.setOnClickListener{
            setOnClickResetPassword(rootDialogElement, dialog)
        }
        rootDialogElement.btGoogleSignIn.setOnClickListener{
            accHelper.signInWithGoogle()
            dialog?.dismiss()
        }

        rootDialogElement.tvDialogMessage2.setOnClickListener{
            if(index == DialogConst.SIGN_UP_STATE){
                createSignDialog(DialogConst.SIGN_IN_STATE)
                dialog?.dismiss()
            }
            else{
                createSignDialog(DialogConst.SIGN_UP_STATE)
                dialog?.dismiss()
            }
        }

        dialog.show()
    }

    private fun setOnClickResetPassword(rootDialogElement: SignDialogBinding, dialog: AlertDialog?) {
        if(rootDialogElement.edSignEmail.text.isNotEmpty()){
            act.mAuth.sendPasswordResetEmail(rootDialogElement.edSignEmail.text.toString()).addOnCompleteListener{ task->
                if(task.isSuccessful){
                    Toast.makeText(act, R.string.email_reset_password_was_send, Toast.LENGTH_LONG).show()
                }
            }
            dialog?.dismiss()
        }
        else{
            rootDialogElement.tvDialogMessage.visibility = View.VISIBLE
        }
    }




    private fun setOnClickSignUpIn(index: Int, rootDialogElement: SignDialogBinding, dialog: AlertDialog) {
        dialog?.dismiss()
        if(index == DialogConst.SIGN_UP_STATE){
            accHelper.signUpWithEmail(rootDialogElement.edSignEmail.text.toString(),
                rootDialogElement.edSignPassword.text.toString(), rootDialogElement)
        }
        else{
            accHelper.signInWithEmail(rootDialogElement.edSignEmail.text.toString(),
                rootDialogElement.edSignPassword.text.toString())
        }

    }










    private fun setDialogState(index: Int, rootDialogElement: SignDialogBinding) {
        if(index == DialogConst.SIGN_UP_STATE){
            rootDialogElement.tvSignUp.text = act.resources.getString(R.string.ad_sign_up)
            rootDialogElement.btSignUpIn.text = act.resources.getString(R.string.sign_up_action)
            rootDialogElement.tvDialogMessage2.text = act.resources.getString(R.string.iHaveAcc)
            rootDialogElement.edName.visibility = View.VISIBLE
            rootDialogElement.edLastName.visibility = View.VISIBLE
            rootDialogElement.edNumber.visibility = View.VISIBLE
            rootDialogElement.btGoogleSignIn.visibility = View.GONE

        }
        else{

            rootDialogElement.tvSignUp.text = act.resources.getString(R.string.ad_sign_in)
            rootDialogElement.btSignUpIn.text = act.resources.getString(R.string.sign_in_action)
            rootDialogElement.tvDialogMessage2.text = act.resources.getString(R.string.iDontHaveAcc)
            rootDialogElement.btForgetP.visibility = View.VISIBLE
            rootDialogElement.edName.visibility = View.GONE
            rootDialogElement.edLastName.visibility = View.GONE
            rootDialogElement.edNumber.visibility = View.GONE
            rootDialogElement.checkBox.visibility = View.GONE
        }
    }
}