package com.example.bcmanager

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.net.URL


class SignUpActivity : AppCompatActivity(), View.OnClickListener {

    var firebaseAuth: FirebaseAuth? = null
    private lateinit var httpConnection: HttpConnection

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

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

                        user!!.updateProfile(profileUpdates)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Log.d("SignUpActivity", "User profile updated.")
                                        Log.d("유저인포", user.uid.toString() + " " + user.displayName.toString() + "  " + user.email.toString())
                                        httpConnection = HttpConnection(URL(MainActivity.SIGNUP_URL))
                                        httpConnection.signUp(user.uid.toString(), user.displayName.toString(), user.email.toString())

                                        MainActivity.isLogined = true;
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


}
