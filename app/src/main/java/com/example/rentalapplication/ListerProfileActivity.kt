package com.example.rentalapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import org.w3c.dom.Text

class ListerProfileActivity : AppCompatActivity() {

    //declare variables

    lateinit var auth:FirebaseAuth
    var databaseReference : DatabaseReference? = null
    var database : FirebaseDatabase? = null

    lateinit var txtName: TextView
    lateinit var txtEmail: TextView
    lateinit var txtPhone: TextView
    lateinit var txtAddress: TextView
    lateinit var txtNic:TextView
    lateinit var txtDes:TextView
    lateinit var imgProfile:ShapeableImageView

    lateinit var editProfile:ImageButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lister_profile)

        //initialize variables
        txtName=findViewById(R.id.tvListerName)
        txtEmail=findViewById(R.id.tvListerEmail)
        txtPhone=findViewById(R.id.tvListerPhone)
        txtAddress=findViewById(R.id.tvListerAddress)
        txtNic=findViewById(R.id.tvListerNIC)
        txtDes=findViewById(R.id.tvListerDis)
        imgProfile=findViewById<ShapeableImageView>(R.id.listerImg)


        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        databaseReference = database?.reference!!.child("lister")

        loadListerProfile()

    }


    //retrieve lister data and display
    private fun loadListerProfile(){


        val logout = findViewById<ImageButton>(R.id.listerLogOutButton)
        var editProfile = findViewById<ImageView>(R.id.btnEditProfile)
        val lister = auth.currentUser
        val listerReference = databaseReference?.child(lister?.uid!!)

        listerReference?.addValueEventListener(object: ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {
                txtName.text = snapshot.child("listerName").value.toString()
                txtEmail.text = snapshot.child("email").value.toString()
                txtNic.text = snapshot.child("nic").value.toString()
                txtAddress.text = snapshot.child("address").value.toString()
                txtPhone.text =snapshot.child("phone").value.toString()
                txtDes.text = snapshot.child("description").value.toString()

                val profileImageUrl = snapshot.child("profileImageUrl").value.toString()
                if (profileImageUrl.isNotEmpty()) {
                    Picasso.get().load(profileImageUrl).into(imgProfile)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })


        //logout
        logout.setOnClickListener{
            auth.signOut()
            startActivity(Intent(this@ListerProfileActivity,ListerLoginActivity::class.java))
            finish()
        }

        //direct to the edit profile
        editProfile.setOnClickListener(){
            startActivity(Intent(this@ListerProfileActivity,UpdateListerProfile::class.java))
            finish()
        }

    }
}