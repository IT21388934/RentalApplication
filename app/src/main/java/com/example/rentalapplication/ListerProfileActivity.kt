package com.example.rentalapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import org.w3c.dom.Text

class ListerProfileActivity : AppCompatActivity() {

    lateinit var auth:FirebaseAuth
    var databaseReference : DatabaseReference? = null
    var database : FirebaseDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lister_profile)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        databaseReference = database?.reference!!.child("profile")
    }

    private fun loadListerProfile(){

        val logout = findViewById<ImageButton>(R.id.listerLogOutButton)
        val lister = auth.currentUser
        val listerReference = databaseReference?.child(lister?.uid!!)

        listerReference?.addValueEventListener(object: ValueEventListener{

            val tvListerName= findViewById<TextView>(R.id.tvListerName)
            val tvListerNIC = findViewById<TextView>(R.id.tvListerNIC)
            val tvListerEmail = findViewById<TextView>(R.id.tvListerEmail)
            val tvListerPhone = findViewById<TextView>(R.id.tvListerPhone)
            val tvListerAddress =findViewById<TextView>(R.id.tvListerAddress)


            override fun onDataChange(snapshot: DataSnapshot) {
                tvListerName.text = snapshot.child("listerName").value.toString()
                tvListerEmail.text = snapshot.child("email").value.toString()
                tvListerNIC.text = snapshot.child("nic").value.toString()
                tvListerAddress.text = snapshot.child("address").value.toString()
                tvListerPhone.text =snapshot.child("phone").value.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        logout.setOnClickListener{
            auth.signOut()
            startActivity(Intent(this@ListerProfileActivity,ListerLoginActivity::class.java))
            finish()
        }

    }
}