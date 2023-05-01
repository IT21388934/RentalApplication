package com.example.rentalapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.Profile
import android.text.TextUtils
import android.util.Patterns
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class ListerLoginActivity : AppCompatActivity() {

   private lateinit var auth:FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lister_loging)

        auth= FirebaseAuth.getInstance()

        val currentLister = auth.currentUser
        if(currentLister != null){
            startActivity(Intent(this@ListerLoginActivity,ListerProfileActivity::class.java))
        }

        listerLogin()
    }


    private fun listerLogin(){

        var listerLoginButton = findViewById<Button>(R.id.ListerLoginButton)
        var edtLoginEmail = findViewById<TextView>(R.id.edtLoginEmail)
        var editTxtPassword = findViewById<TextView>(R.id.editTxtPassword)
        val directToListerSignup = findViewById<TextView>(R.id.directToListerSignup)

        listerLoginButton.setOnClickListener {
            if(TextUtils.isEmpty(edtLoginEmail.text.toString())){
                edtLoginEmail.error = "Please Enter Name"
                return@setOnClickListener
            }else if(TextUtils.isEmpty(editTxtPassword.text.toString())){
                editTxtPassword.error="Please enter password"
                return@setOnClickListener
            } else if(!Patterns.EMAIL_ADDRESS.matcher(editTxtPassword.text.toString()).matches()){
                editTxtPassword.error="Valid Email is Required!"
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(edtLoginEmail.text.toString(), editTxtPassword.text.toString())
                .addOnCompleteListener {
                    if(it.isSuccessful){

                        startActivity(Intent(this@ListerLoginActivity,ListerProfileActivity::class.java))
                        finish()

                    }else{
                        Toast.makeText(this@ListerLoginActivity,"Login failed , please try again", Toast.LENGTH_LONG).show()

                    }
                }
        }

        directToListerSignup.setOnClickListener{
            startActivity(Intent(this@ListerLoginActivity,ListerRegistration::class.java))

        }

    }
}

