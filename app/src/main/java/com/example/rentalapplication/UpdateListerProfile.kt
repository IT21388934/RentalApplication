package com.example.rentalapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


class UpdateListerProfile : AppCompatActivity() {

    lateinit var auth:FirebaseAuth
    var databaseReference : DatabaseReference? = null
    var database : FirebaseDatabase? = null

    lateinit var txtName: TextView
    lateinit var txtEmail: TextView
    lateinit var txtPhone: TextView
    lateinit var txtAddress: TextView
    lateinit var txtNic: TextView
    lateinit var txtDescription:TextView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_lister_profile)

        txtName=findViewById(R.id.edtListerName)
        txtEmail=findViewById(R.id.edtListerEmail)
        txtPhone=findViewById(R.id.editListerPhone)
        txtAddress=findViewById(R.id.editListerAddress)
        txtNic=findViewById(R.id.editListerNic)
        txtDescription=findViewById(R.id.editListerNic)



        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        databaseReference = database?.reference!!.child("lister")

        getprofileData()

        var editProfile = findViewById<Button>(R.id.listerUpdateBtn)
        editProfile.setOnClickListener(){
            updataProfile()
        }

        var backToProfile = findViewById<Button>(R.id.btnCancelUpdate)
        backToProfile.setOnClickListener(){
            startActivity(Intent(this@UpdateListerProfile,ListerProfileActivity::class.java))
            finish()
        }
    }

    fun getprofileData(){


        val lister = auth.currentUser
        val listerReference = databaseReference?.child(lister?.uid!!)

        listerReference?.addValueEventListener(object: ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                txtName.text = snapshot.child("listerName").value.toString()
                txtEmail.text = snapshot.child("email").value.toString()
                txtNic.text = snapshot.child("nic").value.toString()
                txtAddress.text = snapshot.child("address").value.toString()
                txtPhone.text =snapshot.child("phone").value.toString()
                txtDescription.text =snapshot.child("description").value.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }

    fun updataProfile(){
        var newName = findViewById<EditText>(R.id.edtListerName)
        var newNic = findViewById<EditText>(R.id.editListerNic)
        var newPhone = findViewById<EditText>(R.id.editListerPhone)
        var newDic = findViewById<EditText>(R.id.editListerDes)
        var newAddress =findViewById<EditText>(R.id.editListerAddress)

        val currentLister = auth.currentUser
        val currentListerDb = databaseReference?.child((currentLister?.uid!!))



        if(TextUtils.isEmpty(newName.text.toString())){
            newName.error = "Please Enter Name"
            newName.requestFocus()
            return
        }else if(TextUtils.isEmpty(newPhone.text.toString())){
            newPhone.error="Please Enter Phone Number"
            newName.requestFocus()
            return
        }


        // update user data in Firebase Realtime Database
        currentListerDb?.child("listerName")?.setValue(newName.text.toString())
        currentListerDb?.child("phone")?.setValue(newPhone.text.toString())
        currentListerDb?.child("nic")?.setValue(newNic.text.toString())
        currentListerDb?.child("address")?.setValue(newAddress.text.toString())
        currentListerDb?.child("description")?.setValue(newDic.text.toString())

        Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()

        startActivity(Intent(this@UpdateListerProfile,ListerProfileActivity::class.java))
        finish()

    }





}
