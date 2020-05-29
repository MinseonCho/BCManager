package com.example.bcmanager

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.android.synthetic.main.activity_image_o_c_r.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext


class LoginActivity : AppCompatActivity(), View.OnClickListener, CoroutineScope {


    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    var GOOGLE_LOGIN_CODE = 9001

    private lateinit var mJob: Job
    override val coroutineContext: CoroutineContext
        get() = mJob + Dispatchers.Main

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        // User is signed in
        val user = FirebaseAuth.getInstance().currentUser
        if (user?.displayName != null) {
            // User is signed in
        } else {
            // No user is signed in
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mJob = Job()

        login_btn.setOnClickListener(this)
        login_btn_google.setOnClickListener(this)
        login_btn_signup.setOnClickListener(this)


        auth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    fun googleLogin(){
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, GOOGLE_LOGIN_CODE)
    }

//    private fun createAccount(email: String, password: String) {
//        Log.d(TAG, "createAccount:$email")
//        if (!validateForm()) {
//            Log.d(TAG, "!validateForm()")
//            return
//        }
//
////        showProgressDialog()
//        auth.createUserWithEmailAndPassword(email, password)
//                .addOnCompleteListener(this) { task ->
//                    if (task.isSuccessful) {
//                        // Sign in success, update UI with the signed-in user's information
//
//                        val user = FirebaseAuth.getInstance().currentUser
//
//                        Log.d(TAG, "createUserWithEmail:success = " + user.toString())
//
//                        (task.result!!.user)?.let { moveMainPage(it) }
//
//
//                        val profileUpdates = UserProfileChangeRequest.Builder()
//                                .setDisplayName("minseon").build()
//
//                        user!!.updateProfile(profileUpdates)
//                                .addOnCompleteListener { task ->
//                                    if (task.isSuccessful) {
//                                        Log.d(LoginActivity.TAG, "User profile updated.")
//                                    }
//                                }
////                        sendEmailVerification()
//                    }
////                    else if (!(task.exception?.message.isNullOrEmpty())) {
////                        //Show the error message
////                        Log.w(TAG, "createUserWithEmail:failure", task.exception)
////                        Toast.makeText(baseContext, "Authentication failed.",
////                                Toast.LENGTH_SHORT).show()
////
////                    }
//                    else {
//                        // If sign in fails, display a message to the user.
//                        signinEmail()
////                        updateUI(null)
//                    }
//
//                }
//    }

    private fun validateForm(): Boolean {
        var valid = true

        val email = login_email.text.toString()
        if (TextUtils.isEmpty(email)) {
            login_email.error = "Required."
            valid = false
        } else {
            login_email.error = null
        }

        val password = login_pw.text.toString()
        if (TextUtils.isEmpty(password)) {
            login_pw.error = "Required."
            valid = false
        } else {
            login_pw.error = null
        }

        return valid
    }


    fun signinEmail() {

        Log.d(TAG, "signIn:$email")
        if (!validateForm()) {
            return
        }

        auth.signInWithEmailAndPassword(login_email.text.toString(),
                login_pw.text.toString()).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                //Login
                (task.result!!.user)?.let { moveMainPage(it) }
                val user = auth.currentUser
            } else {
                //Show the error message
                Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
            }


        }

    }
    private fun signOut() {
        auth.signOut()
    }

//    private fun sendEmailVerification() {
//        // Disable button
//        login_btn_verification.isEnabled = false
//
//        // Send verification email
//        // [START send_email_verification]
//        val user = auth.currentUser
//        user?.sendEmailVerification()
//                ?.addOnCompleteListener(this) { task ->
//                    // [START_EXCLUDE]
//                    // Re-enable button
//                    login_btn_verification.isEnabled = true
//
//                    if (task.isSuccessful) {
//                        Toast.makeText(baseContext,
//                                "Verification email sent to ${user.email} ",
//                                Toast.LENGTH_SHORT).show()
//                    } else {
//                        Log.e(TAG, "sendEmailVerification", task.exception)
//                        Toast.makeText(baseContext,
//                                "Failed to send verification email.",
//                                Toast.LENGTH_SHORT).show()
//                    }
//                    // [END_EXCLUDE]
//                }
//        // [END send_email_verification]
//    }

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


    fun moveMainPage(user: FirebaseUser) {
        MainActivity.isLogined = true;
        val intent = Intent(this, MainActivity::class.java)
        if (user != null) {
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
    }

    companion object {
        private const val TAG = "LoginActivity"
    }

    override fun onClick(v: View?) {
        val i = v?.id

        when (i) {
            R.id.login_btn -> signinEmail()
            R.id.login_btn_google -> googleLogin()
            R.id.login_btn_signup -> {
                val intent = Intent(this@LoginActivity, SignUpActivity::class.java)
                startActivity(intent)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == GOOGLE_LOGIN_CODE){
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if(result!!.isSuccess){
                val account = result.signInAccount
                firebaseAuthWithGoogle(account)
            }
        }
    }


    fun firebaseAuthWithGoogle(account : GoogleSignInAccount?){
        val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                //Login
                (task.result!!.user)?.let { moveMainPage(it) }
            } else {
                //Show the error message
                Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        mJob.cancel()
    }
}
