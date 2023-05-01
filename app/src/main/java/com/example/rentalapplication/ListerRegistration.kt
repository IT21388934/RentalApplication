package com.example.rentalapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.TestLooperManager
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import org.w3c.dom.Text
import java.lang.ref.PhantomReference

class ListerRegistration : AppCompatActivity() {

    private lateinit var auth:FirebaseAuth
    var databaseReference: DatabaseReference? = null
    var database:FirebaseDatabase? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lister_registration2)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        databaseReference = database?.reference!!.child("lister")

        listerRegis()
    }

    private fun listerRegis(){
        var listerRegisButton = findViewById<Button>(R.id.listerRegisBtn)
        var edtListerName = findViewById<EditText>(R.id.edtListerName)
        var edtListerEmail = findViewById<EditText>(R.id.edtListerEmail)
        var editListerPassword = findViewById<EditText>(R.id.editListerPassword)
        var editListerRePassword = findViewById<EditText>(R.id.editListerRePassword)
        var editListerPhone = findViewById<EditText>(R.id.editListerPhone)
        var editListerNic =findViewById<EditText>(R.id.editListerNic)
        var editListerAddress =findViewById<EditText>(R.id.editListerAddress)



        listerRegisButton.setOnClickListener {

                if(TextUtils.isEmpty(edtListerName.text.toString())){
                    edtListerName.error = "Please Enter Name"
                    return@setOnClickListener
                }else if(TextUtils.isEmpty(edtListerEmail.text.toString())){
                    edtListerEmail.error="Please Enter Email"
                    return@setOnClickListener
                }
                else if(TextUtils.isEmpty(editListerPassword.text.toString())){
                    edtListerEmail.error="Please Enter Password"
                    return@setOnClickListener
                }
            var email = edtListerEmail.text.toString()
            var password = editListerPassword.text.toString()

            auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener {
                    if(it.isSuccessful){
                        val currentLister = auth.currentUser
                        val currentListerDb = databaseReference?.child((currentLister?.uid!!))
                        currentListerDb?.child("listerName")?.setValue(edtListerName.text.toString())
                        currentListerDb?.child("phone")?.setValue(editListerPhone.text.toString())
                        currentListerDb?.child("nic")?.setValue(editListerNic.text.toString())
                        currentListerDb?.child("address")?.setValue(editListerAddress.text.toString())
                        currentListerDb?.child("email")?.setValue(edtListerEmail.text.toString())

                        Toast.makeText(this@ListerRegistration, "Registration Success",Toast.LENGTH_LONG).show()
                        finish()

                    }else{
                        Toast.makeText(this@ListerRegistration, "Registration failed, please try again",Toast.LENGTH_LONG).show()

                    }
                }

//            var email = edtListerEmail.text.toString().trim()
//            var password = editListerPassword.text.toString().trim()
//
//            auth.createUserWithEmailAndPassword(email, password)
//                .addOnCompleteListener(this) { task ->
//                    if (task.isSuccessful) {
//                        // Sign in success, update UI with the signed-in user's information
//
//                        val currentLister = auth.currentUser
//                        val currentListerDb = databaseReference?.child((currentLister?.uid!!))
//                        currentListerDb?.child("listerName")?.setValue(edtListerName.text.toString())
//                        currentListerDb?.child("phone")?.setValue(editListerPhone.text.toString())
//                        currentListerDb?.child("nic")?.setValue(editListerNic.text.toString())
//                        currentListerDb?.child("address")?.setValue(editListerAddress.text.toString())
//                        currentListerDb?.child("email")?.setValue(edtListerEmail.text.toString())
//                        Toast.makeText(this@ListerRegistration, "Registration Success",Toast.LENGTH_LONG).show()
//                        finish()
//                    } else {
//                        // If sign in fails, display a message to the user.
//
//                        Toast.makeText(
//                            baseContext,
//                            "Authentication failed.",
//                            Toast.LENGTH_SHORT,
//                        ).show()
//
//                    }
//                }
        }

    }





}