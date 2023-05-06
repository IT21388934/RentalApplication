package com.example.mad_project

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso

class UploadProfilePictureActivity : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar
    private lateinit var imageViewUploadPicture: ImageView
    private lateinit var authProfile: FirebaseAuth

    private lateinit var storageReference: StorageReference
    private lateinit var firebaseUser: FirebaseUser

    private var uriImage: Uri? = null

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_profile_picture)

        val buttonUploadPictureChoose = findViewById<Button>(R.id.upload_pic_Choose_button)
        var buttonUploadPicture = findViewById<Button>(R.id.upload_pic_button)

        progressBar = findViewById(R.id.progressBar)
        imageViewUploadPicture = findViewById(R.id.imageView_profile_picture)

        authProfile = FirebaseAuth.getInstance()
        firebaseUser = authProfile.currentUser!!

        storageReference = FirebaseStorage.getInstance().getReference("Display Picture")

        val uri: Uri? = firebaseUser.photoUrl

//        Picasso.with().load(uri).into(imageViewUploadPicture)
        Picasso.with(this).load(uri).into(imageViewUploadPicture)

        buttonUploadPictureChoose.setOnClickListener {
            openFileChooser()
        }
    }

    private fun openFileChooser() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data?.data != null) {
            uriImage = data.data
            imageViewUploadPicture.setImageURI(uriImage)
        }
    }

    //Creating Action bar
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        //Inflate menu item
        menuInflater.inflate(R.menu.common_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    //When any menu item is selected
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_refresh -> {
                //Refresh Activity
                startActivity(intent)
                finish()
                overridePendingTransition(0, 0)
            }
            R.id.menu_update_profile -> {
                val intent = Intent(this, UpdateProfileActivity::class.java)
                startActivity(intent)
                finish()
            }
            R.id.menu_delete_profile -> {
                val intent = Intent(this, DeleteProfileActivity::class.java)
                startActivity(intent)
                finish()
            }
            R.id.menu_logout -> {
                authProfile.signOut()
                Toast.makeText(this, "Logged Out", Toast.LENGTH_LONG).show()
                val intent = Intent(this, MainActivity::class.java)

                //Clear stack to prevent user coming back to UserProfileActivity on Pressing back button after Logging out
                intent.flags =
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()        //Close UserProfileActivity
            }
            else -> {
                Toast.makeText(this, "Something Wrong!", Toast.LENGTH_LONG).show()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}