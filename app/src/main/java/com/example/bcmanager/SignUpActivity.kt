package com.example.bcmanager

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.net.URL
import java.util.*


class SignUpActivity : AppCompatActivity(), View.OnClickListener {

    var firebaseAuth: FirebaseAuth? = null
    private lateinit var httpConnection: HttpConnection
    private lateinit var myApp: BCMApplication

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        Objects.requireNonNull(supportActionBar)!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM // 커스텀 사용
        supportActionBar!!.setCustomView(R.layout.actionbar_title_nobtn) // 커스텀 사용할 파일 위치
        supportActionBar!!.title = "회원가입"

        myApp = application as BCMApplication
        signup.setOnClickListener(this)
        firebaseAuth = FirebaseAuth.getInstance();
    }

    override fun onClick(v: View?) {
        val i = v?.id

        when (i) {
            R.id.signup -> {
                signupwithemailandpassword(signup_email?.text.toString(), signup_pw?.text.toString(), signup_name?.text.toString())
            }
        }
    }

    private fun signupwithemailandpassword(email: String, password: String, name: String) {
        firebaseAuth!!.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this@SignUpActivity, OnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = FirebaseAuth.getInstance().currentUser

                        val profileUpdates = UserProfileChangeRequest.Builder()
                                .setDisplayName(name).build()
                        sendEmailVerification()
                        user!!.updateProfile(profileUpdates)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Log.d("SignUpActivity", "User profile updated.")
                                        Log.d("유저인포", user.uid.toString() + " " + user.displayName.toString() + "  " + user.email.toString())
                                        httpConnection = HttpConnection(URL(MainActivity.SIGNUP_URL))
                                        httpConnection.signUp(user.uid.toString(), user.displayName.toString(), user.email.toString())

                                        myApp.isLogined = true;
                                        myApp.userName = user.displayName
                                        myApp.loginType = "g"
                                        myApp.userID = user.uid
                                        myApp.userEmail = user.email

                                        val intent = Intent(this@SignUpActivity, MainActivity::class.java)
                                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                                        startActivity(intent)
                                        finish()
                                    }
                                }


                    } else {
                        Toast.makeText(this@SignUpActivity, "등록 에러", Toast.LENGTH_SHORT).show()
                        return@OnCompleteListener
                    }
                })
    }

    fun sendEmailVerification() {
        // [START send_email_verification]
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        user!!.sendEmailVerification()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("sendEmailVerification", "Email sent.")
                    }
                }
        // [END send_email_verification]
    }
}
