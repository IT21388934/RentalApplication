package com.example.mad_project

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NavUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class UserProfileActivity : AppCompatActivity() {

    private lateinit var textViewWelcome: TextView
    private lateinit var textViewFullName: TextView
    private lateinit var textViewEmail: TextView
    private lateinit var textViewDob: TextView
    private lateinit var textViewGender: TextView
    private lateinit var textViewMobile: TextView

    private lateinit var imageView: ImageView


    private lateinit var progressBar: ProgressBar

    private lateinit var authProfile: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        supportActionBar?.title = "Home"

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        textViewWelcome = findViewById(R.id.textView_show_welcome)
        textViewFullName = findViewById(R.id.textView_show_full_name)
        textViewEmail = findViewById(R.id.textView_show_email)
        textViewDob = findViewById(R.id.textView_show_dob)
        textViewGender = findViewById(R.id.textView_show_gender)
        textViewMobile = findViewById(R.id.textView_show_mobile)
        progressBar = findViewById(R.id.progress_bar)


        authProfile = FirebaseAuth.getInstance()
        val firebaseUser = authProfile.currentUser

        if (firebaseUser == null) {
            Toast.makeText(
                this,
                "Something went wrong! User's details are not available at the moment",
                Toast.LENGTH_LONG
            ).show()
        } else {
            checkIfEmailVerified(firebaseUser)
            progressBar.visibility = View.VISIBLE
            showUserProfile(firebaseUser)
        }
    }

    private fun checkIfEmailVerified(firebaseUser: FirebaseUser) {
        if (!firebaseUser.isEmailVerified()) {
            showAlertDialog()
        }
    }

    private fun showAlertDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Email Not Verified")
        builder.setMessage("Please verify your email now. You cannot login without email verification next time")

        builder.setPositiveButton("Continue") { dialog, which ->
            val intent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_APP_EMAIL)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
        }

        val alertDialog = builder.create()
        alertDialog.show()
    }

    private fun showUserProfile(firebaseUser: FirebaseUser) {
        val userID = firebaseUser.uid

        // Extracting user reference from database for "Registered Users"
        val referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users")

        referenceProfile.child(userID).addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                val readUserDetails = snapshot.getValue(ReadWriteUserDetails::class.java)

                readUserDetails?.let {
                    val fullName = firebaseUser.displayName
                    val email = firebaseUser.email
                    val doB = readUserDetails.doB
                    val gender = readUserDetails.gender
                    val mobile = readUserDetails.mobile

                    textViewWelcome.text = "Welcome $fullName!"
                    textViewFullName.text = fullName
                    textViewEmail.text = email
                    textViewDob.text = doB
                    textViewGender.text = gender
                    textViewMobile.text = mobile

                } ?: run {
                    Toast.makeText(
                        this@UserProfileActivity,
                        "Failed to read user details from database!",
                        Toast.LENGTH_LONG
                    ).show()
                }

                progressBar.visibility = View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@UserProfileActivity,
                    "Failed to read user details from database!",
                    Toast.LENGTH_LONG
                ).show()
                progressBar.visibility = View.GONE
            }
        })
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
            android.R.id.home -> {
                NavUtils.navigateUpFromSameTask(this)
                return true
            }
            R.id.menu_refresh -> {
                // Refresh activity
                finish()
                startActivity(intent)
                overridePendingTransition(0, 0)
                return true
            }
            R.id.menu_update_profile -> {
                val intent = Intent(this, UpdateProfileActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.menu_delete_profile -> {
                val intent = Intent(this, DeleteProfileActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.menu_logout -> {
                authProfile.signOut()
                Toast.makeText(this, "Logged Out", Toast.LENGTH_LONG).show()
                val intent = Intent(this, MainActivity::class.java)

                // Clear stack to prevent user from coming back to UserProfileActivity on pressing back button after logging out
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish() // Close UserProfileActivity
                return true
            }
            else -> {
                Toast.makeText(this, "Something went wrong!", Toast.LENGTH_LONG).show()
                return super.onOptionsItemSelected(item)
            }
        }
    }
}
