package com.example.bcmanager

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_user_profile.*

class UserProfileActivity : AppCompatActivity(), View.OnClickListener {
    var user: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        user = FirebaseAuth.getInstance().currentUser

        profile_name.text = Editable.Factory.getInstance().newEditable(user?.displayName.toString())
        profile_email.text = Editable.Factory.getInstance().newEditable(user?.email.toString())
        if(user?.photoUrl != null) {
            Glide.with(this).load(user?.photoUrl).apply(RequestOptions.centerCropTransform()).into(profile_image)
        }else{
            profile_image.setImageResource(R.drawable.rabbit)
        }
        profile_btn.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        val i = v?.id

        when (i) {
            R.id.profile_btn -> finish()

        }
    }
}
