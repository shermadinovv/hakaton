package com.boogoo.hakaton.accounthelper

import android.util.Log
import android.widget.Toast
import com.boogoo.hakaton.MainActivity
import com.boogoo.hakaton.constants.FirebaseAuthConstants
import com.boogoo.hakaton.dialoghelper.GoogleAcConst
import com.boogoo.hakaton.R
import com.boogoo.hakaton.databinding.SignDialogBinding
import com.boogoo.hakaton.model.users
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class AccountHelper(act:MainActivity) {
    private val act = act
    private lateinit var signInClient: GoogleSignInClient

    val db = Firebase.database.getReference("users")
    val auth = Firebase.auth


    private fun fillUser(rootDialogElement: SignDialogBinding): users {
        val userss: users
        rootDialogElement.apply {
            userss = users(
                edName.text.toString(),
                edLastName.text.toString(),
                edNumber.text.toString(),
                db.push().key,
                checkBox.isChecked
            )
        }
        return userss
    }

    fun  publishUser(rootDialogElement: SignDialogBinding){
        val userss = fillUser(rootDialogElement)
        if(auth.uid != null)db.child(auth.uid!!).setValue(userss)
    }

    fun signUpWithEmail(email:String, password:String , rootDialogElement: SignDialogBinding){
        if(email.isNotEmpty() && password.isNotEmpty()){
            act.mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {task->
                if(task.isSuccessful){
                    sendEmailVerification(task.result?.user!!)
                    act.uiUpdate(task.result?.user!!)
                    publishUser(rootDialogElement)
                }
                else{
                    //Toast.makeText(act, act.resources.getString(R.string.sign_up_error), Toast.LENGTH_LONG).show()
                    //Log.d("MyLog", "Exception : ${task.exception}")
                    if(task.exception is FirebaseAuthUserCollisionException){
                        val exception = task.exception as FirebaseAuthUserCollisionException
                        if(exception.errorCode == FirebaseAuthConstants.ERROR_EMAIL_ALREADY_IN_USE){
                            //Toast.makeText(act, FirebaseAuthConstants.ERROR_EMAIL_ALREADY_IN_USE, Toast.LENGTH_LONG).show()
                            //link email
                            linkEmailToG(email,password)
                        }

                    }
                    else if(task.exception is FirebaseAuthInvalidCredentialsException){
                        val exception = task.exception as FirebaseAuthInvalidCredentialsException
                        if(exception.errorCode == FirebaseAuthConstants.ERROR_INVALID_EMAIL){
                            Toast.makeText(act, FirebaseAuthConstants.ERROR_INVALID_EMAIL, Toast.LENGTH_LONG).show()
                        }
                    }
                    if(task.exception is FirebaseAuthWeakPasswordException){
                        val exception = task.exception as FirebaseAuthWeakPasswordException
                        Log.d("MyLog", "Exception : ${exception.errorCode}")
                        if(exception.errorCode == FirebaseAuthConstants.ERROR_WEAK_PASSWORD){
                            Toast.makeText(act, FirebaseAuthConstants.ERROR_WEAK_PASSWORD, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }

    private fun linkEmailToG(email:String, password: String){
        val credential = EmailAuthProvider.getCredential(email,password)
        if(act.mAuth.currentUser != null){
            act.mAuth.currentUser?.linkWithCredential(credential)?.addOnCompleteListener{ task->
                if(task.isSuccessful){
                    Toast.makeText(act, act.resources.getString(R.string.link_done), Toast.LENGTH_LONG).show()
                }

            }
        }
        else{
            Toast.makeText(act, act.resources.getString(R.string.enter_to_g), Toast.LENGTH_LONG).show()
        }
    }

    private fun getSignInClient():GoogleSignInClient{
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(act.getString(R.string.default_web_client_id)).requestEmail().build()
        return GoogleSignIn.getClient(act,gso)
    }

    fun signInWithGoogle(){
        signInClient = getSignInClient()
        val intent = signInClient.signInIntent
        act.startActivityForResult(intent, GoogleAcConst.GOOGLE_SIGN_iN_REQUEST_CODE)
    }

    fun signOutG(){
        getSignInClient().signOut()
    }

    fun signInFirebaseWithGoogle(token: String){
        val credential = GoogleAuthProvider.getCredential(token, null)
        act.mAuth.signInWithCredential(credential).addOnCompleteListener{ task->
            if(task.isSuccessful){
                Toast.makeText(act, "Sign In done!", Toast.LENGTH_LONG).show()
                act.uiUpdate(task.result?.user)
            }
            else{
                Log.d("MyLog", "Google Sign In Exception : ${task.exception}")
            }
        }
    }

    fun signInWithEmail(email:String, password:String){
        if(email.isNotEmpty() && password.isNotEmpty()){
            act.mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {task->
                if(task.isSuccessful){
                    act.uiUpdate(task.result?.user!!)
                }
                else{
                    Log.d("MyLog", "Exception : ${task.exception}")
                    if(task.exception is FirebaseAuthInvalidCredentialsException){
                        val exception = task.exception as FirebaseAuthInvalidCredentialsException
                        //Log.d("MyLog", "Exception : ${task.exception}")
                        if(exception.errorCode == FirebaseAuthConstants.ERROR_INVALID_EMAIL){
                            Toast.makeText(act, FirebaseAuthConstants.ERROR_INVALID_EMAIL, Toast.LENGTH_LONG).show()
                        }
                        else if(exception.errorCode == FirebaseAuthConstants.ERROR_WRONG_PASSWORD){
                            Toast.makeText(act, FirebaseAuthConstants.ERROR_WRONG_PASSWORD, Toast.LENGTH_LONG).show()
                        }
                    }
                    else if(task.exception is FirebaseAuthInvalidUserException) {
                        val exception = task.exception as FirebaseAuthInvalidUserException
                        if(exception.errorCode == FirebaseAuthConstants.ERROR_USER_NOT_FOUND){
                            Toast.makeText(act, FirebaseAuthConstants.ERROR_USER_NOT_FOUND, Toast.LENGTH_LONG).show()
                        }
                        Log.d("MyLog", "Exception 3: ${exception.errorCode}")
                    }
                }
            }
        }
    }

    private fun sendEmailVerification(user:FirebaseUser){
        user.sendEmailVerification().addOnCompleteListener {task->
            if(task.isSuccessful){
                Toast.makeText(act, act.resources.getString(R.string.send_verification_done), Toast.LENGTH_LONG).show()
            }
            else{
                Toast.makeText(act, act.resources.getString(R.string.sign_up_error), Toast.LENGTH_LONG).show()
            }
        }
    }
}