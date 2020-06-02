package com.example.bcmanager

import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_user_profile.*

class UserProfileActivity : AppCompatActivity(), View.OnClickListener {
    var user: FirebaseUser? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var newPW: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        user = FirebaseAuth.getInstance().currentUser
        auth = FirebaseAuth.getInstance()

        profile_name.text = Editable.Factory.getInstance().newEditable(user?.displayName.toString())
        profile_email.text = Editable.Factory.getInstance().newEditable(user?.email.toString())
        if (user?.photoUrl != null) {
            Glide.with(this).load(user?.photoUrl).apply(RequestOptions.centerCropTransform()).into(profile_image)
        } else {
            profile_image.setImageResource(R.drawable.rabbit)
        }


        profile_btn.setOnClickListener(this)
        textview_pw.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        val i = v?.id

        when (i) {
            R.id.profile_btn -> finish()
            R.id.textview_pw -> {
                val etPW = EditText(this@UserProfileActivity)
                val dialog: AlertDialog.Builder = AlertDialog.Builder(this,R.style.MyAlertDialogStyle)

                val container = FrameLayout(this@UserProfileActivity)
                val params: FrameLayout.LayoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                params.leftMargin = resources.getDimensionPixelSize(R.dimen.dialog_margin)
                params.rightMargin = resources.getDimensionPixelSize(R.dimen.dialog_margin)
                etPW.setLayoutParams(params)
//                etPW.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
                etPW.transformationMethod = PasswordTransformationMethod.getInstance()
                container.addView(etPW)

                dialog.setTitle("비밀번호 변경")
                        .setMessage("변경할 비밀번호를 입력하세요.")
                        .setIcon(R.mipmap.ic_launcher)
                        .setCancelable(false)
                        .setView(container)
                        .setPositiveButton("확인", DialogInterface.OnClickListener {
                            dialog, which ->
                            newPW = etPW.text.toString()
                            changePW(newPW)
                        })
                        .setNegativeButton("취소", DialogInterface.OnClickListener { dialog, which ->
                            dialog.dismiss()
                        })

                val alert = dialog.create()
                alert.show()
            }
        }
    }


    fun changePW(password: String){
        auth.currentUser?.updatePassword(password)
                ?.addOnCompleteListener(this, OnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Password changes successfully", Toast.LENGTH_LONG)
                                .show()
                        finish()
                    } else {
                        Toast.makeText(this, "password not changed", Toast.LENGTH_LONG)
                                .show()
                    }
                })
    }
}
