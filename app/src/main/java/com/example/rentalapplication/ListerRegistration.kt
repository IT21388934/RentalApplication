package com.example.rentalapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.TestLooperManager
import android.text.TextUtils
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
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
                }else if(!Patterns.EMAIL_ADDRESS.matcher(edtListerEmail.text.toString()).matches()){
                    edtListerEmail.error="Valid Email is Required!"
                    return@setOnClickListener
                }else if(TextUtils.isEmpty(editListerPhone.text.toString())){
                    editListerPhone.error="Please Enter Phone Number"
                    return@setOnClickListener
                }else if(editListerPassword.text.toString().length < 6){
                    editListerPassword.error="Password Should have at least 6 characters"
                    return@setOnClickListener
                }else if(editListerRePassword.text.toString().length < 6){
                    editListerRePassword.error="Please Enter conform password"
                    return@setOnClickListener
                }else if(editListerPassword.text.toString() != editListerRePassword.text.toString()){
                    editListerRePassword.error="Password doesn't match"
                    return@setOnClickListener
                }


            var email = edtListerEmail.text.toString()
            var password = editListerPassword.text.toString()

            auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this) { task ->
                    if(task.isSuccessful){
                        val currentLister = auth.currentUser
                        val currentListerDb = databaseReference?.child((currentLister?.uid!!))
                        currentListerDb?.child("listerName")?.setValue(edtListerName.text.toString())
                        currentListerDb?.child("phone")?.setValue(editListerPhone.text.toString())
                        currentListerDb?.child("nic")?.setValue(editListerNic.text.toString())
                        currentListerDb?.child("address")?.setValue(editListerAddress.text.toString())
                        currentListerDb?.child("email")?.setValue(edtListerEmail.text.toString())

//                        currentLister?.sendEmailVerification()

                        Toast.makeText(this@ListerRegistration, "Registration Success",Toast.LENGTH_LONG).show()
                        finish()

                    }else{
                        try {
                            throw task.exception!!
                        } catch (e: FirebaseAuthWeakPasswordException) {
                            editListerPassword.error = "Your password is too Weak. Kindly use a mix of alphabets, numbers and special characters"
                            editListerPassword.requestFocus()
                        } catch (e: FirebaseAuthInvalidCredentialsException) {
                            edtListerEmail.error = "Your email is invalid or already in use. Kindly re-enter."
                            edtListerEmail.requestFocus()
                        } catch (e: FirebaseAuthUserCollisionException) {
                            edtListerEmail.setError("User is already registered with this email. Use another email.")
                            edtListerEmail.requestFocus()
                        } catch (e: Exception) {
//                   

                            Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
                        }

                    }

                }

        }

    }





}