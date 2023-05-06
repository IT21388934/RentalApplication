package com.example.rentalapplication

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.*
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso


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
    lateinit var imgProfile:ShapeableImageView

    val IMAGE_PICK_REQUEST_CODE = 1





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_lister_profile)

        txtName=findViewById(R.id.edtListerName)
        txtEmail=findViewById(R.id.edtListerEmail)
        txtPhone=findViewById(R.id.editListerPhone)
        txtAddress=findViewById(R.id.editListerAddress)
        txtNic=findViewById(R.id.editListerNic)
        txtDescription=findViewById(R.id.editListerDes)
        imgProfile=findViewById(R.id.listerProfImg)



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

        imgProfile.setOnClickListener(){
            chooseProfile()
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

                val profileImageUrl = snapshot.child("profileImageUrl").value.toString()
                if (profileImageUrl.isNotEmpty()) {
                    Picasso.get().load(profileImageUrl).into(imgProfile)
                }

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

    private fun chooseProfile() {

        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_REQUEST_CODE)

        startActivityForResult(Intent.createChooser(intent,"Select Image"),IMAGE_PICK_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_REQUEST_CODE && resultCode == RESULT_OK) {
            val imageUri = data?.data
            // Upload the image to Firebase Storage
            if (imageUri != null) {
                uploadImage(imageUri)
            }
        }
    }

    //
    private fun uploadImage(imageUri: Uri?) {
        if (imageUri != null) {
            // Create a reference to the location where the image will be stored
            val storage = FirebaseStorage.getInstance()
            val imageRef = storage.reference.child("${FirebaseAuth.getInstance().currentUser?.uid}.jpg")
            // Upload the image to Firebase Storage
            val uploadTask = imageRef.putFile(imageUri)
            uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let { throw it }
                }
                // Continue with the task to get the download URL
                imageRef.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Store the download URL in Firebase Realtime Database
                    val imageUrl = task.result.toString()
                    val userId = FirebaseAuth.getInstance().currentUser?.uid
                    // Set the profile image in the ImageView
                    val profilePic= findViewById<ShapeableImageView>(R.id.listerProfImg)
                    Glide.with(this).load(imageUrl).into(profilePic)

                    if (userId != null) {
                        FirebaseDatabase.getInstance().reference.child("lister").child(userId).child("profileImageUrl").setValue(imageUrl)
                    }


                    Toast.makeText(this, "Profile Picture updated successfully", Toast.LENGTH_SHORT).show()
                } else {
                    // Handle errors
                    Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
        }
    }





}
