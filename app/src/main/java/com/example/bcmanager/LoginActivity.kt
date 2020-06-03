package com.example.bcmanager

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.kakao.auth.ISessionCallback
import com.kakao.auth.Session
import com.kakao.util.exception.KakaoException
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import java.net.URL
import java.util.*
import kotlin.coroutines.CoroutineContext


class LoginActivity : AppCompatActivity(), View.OnClickListener, CoroutineScope {


    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var httpConnection: HttpConnection
    private lateinit var email: String
    private lateinit var myApp: BCMApplication
    var GOOGLE_LOGIN_CODE = 9001

    private lateinit var mJob: Job
    override val coroutineContext: CoroutineContext
        get() = mJob + Dispatchers.Main

    //kakao
    // 세션 콜백 구현
    private val sessionCallback: ISessionCallback = object : ISessionCallback {
        override fun onSessionOpened() {
            Log.i("KAKAO_SESSION", "로그인 성공")
        }

        override fun onSessionOpenFailed(exception: KakaoException?) {
            Log.e("KAKAO_SESSION", "로그인 실패", exception)
        }
    }
    //kakao end

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
        setTitle("로그인")
        Objects.requireNonNull(supportActionBar)!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM // 커스텀 사용
        supportActionBar!!.setCustomView(R.layout.actionbar_title_nobtn) // 커스텀 사용할 파일 위치
        supportActionBar!!.title = "로그인"

        myApp = application as BCMApplication

        mJob = Job()

        login_btn.setOnClickListener(this)
        login_btn_google.setOnClickListener(this)
        login_btn_signup.setOnClickListener(this)
        login_forgotpw.setOnClickListener(this)


        auth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        //kakao
        // 세션 콜백 등록
//        Session.getCurrentSession().addCallback(sessionCallback);
        //kakao end


    }

    fun googleLogin() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, GOOGLE_LOGIN_CODE)
    }


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
            R.id.login_forgotpw -> {
                val etPW = EditText(this@LoginActivity)
                val dialog: AlertDialog.Builder = AlertDialog.Builder(this, R.style.MyAlertDialogStyle)

                val container = FrameLayout(this@LoginActivity)
                val params: FrameLayout.LayoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                params.leftMargin = resources.getDimensionPixelSize(R.dimen.dialog_margin)
                params.rightMargin = resources.getDimensionPixelSize(R.dimen.dialog_margin)
                etPW.setLayoutParams(params)
                container.addView(etPW)

                dialog.setTitle("비밀번호 찾기")
                        .setMessage("등록된 이메일을 입력하세요.")
                        .setCancelable(false)
                        .setView(container)
                        .setPositiveButton("확인", DialogInterface.OnClickListener { dialog, which ->
                            email = etPW.text.toString()
                            sendEmailForChangingPW(email)
                        })
                        .setNegativeButton("취소", DialogInterface.OnClickListener { dialog, which ->
                            dialog.dismiss()
                        })

                val alert = dialog.create()
                alert.show()
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // 카카오톡|스토리 간편로그인 실행 결과를 받아서 SDK로 전달
//        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
//            return;
//        }
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GOOGLE_LOGIN_CODE) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if (result!!.isSuccess) {
                val account = result.signInAccount
                firebaseAuthWithGoogle(account)
            }
        }
    }


    fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {
        val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                //Login
                val user = FirebaseAuth.getInstance().currentUser
                httpConnection = HttpConnection(URL(MainActivity.SIGNUP_URL))
                if (user != null) {
                    httpConnection.signUp(user.uid.toString(), user.displayName.toString(), user.email.toString())
                }
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
        // 세션 콜백 삭제
//        Session.getCurrentSession().removeCallback(sessionCallback); //kakao
    }

    fun sendEmailForChangingPW(email: String) {
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(this, OnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Reset link sent to your email", Toast.LENGTH_LONG)
                                .show()
                    } else {
                        Toast.makeText(this, "Unable to send reset mail", Toast.LENGTH_LONG)
                                .show()
                    }
                })
    }

}
