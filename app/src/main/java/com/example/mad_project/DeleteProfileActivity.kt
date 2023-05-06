package com.example.mad_project


import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase

@Suppress("DEPRECATION")
class DeleteProfileActivity : AppCompatActivity() {

    private lateinit var authProfile: FirebaseAuth

    private lateinit var editTextUserPassword: EditText
    private lateinit var textViewAuthenticated: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var buttonDeleteUser: Button
    private lateinit var buttonReAuthenticate: Button

    private companion object {
        private const val TAG = "DeleteProfileActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delete_profile)

        supportActionBar?.title = "Delete Your Profile"

        progressBar = findViewById(R.id.progressBar)
        editTextUserPassword = findViewById(R.id.editText_delete_user_pwd)
        textViewAuthenticated = findViewById(R.id.textView_delete_user_authenticated)
        buttonDeleteUser = findViewById(R.id.button_delete_user)
        buttonReAuthenticate = findViewById(R.id.button_delete_user_authenticate)

        //Disable delete user button until user is authenticated
        buttonDeleteUser.isEnabled = false

        authProfile = FirebaseAuth.getInstance()

        val firebaseUser = authProfile.currentUser

        if (firebaseUser == null || firebaseUser.uid.isEmpty()) {
            Toast.makeText(this, "Something wrong! User details are not available at the moment", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, UserProfileActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            reAuthenticateUser(firebaseUser)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun reAuthenticateUser(firebaseUser: FirebaseUser) {
        buttonReAuthenticate.setOnClickListener {
            val userPwd = editTextUserPassword.text.toString()

            if (userPwd.isEmpty()) {
                Toast.makeText(this, "Password is needed", Toast.LENGTH_SHORT).show()
                editTextUserPassword.error = "Please enter your password to authenticate"
                editTextUserPassword.requestFocus()
            } else {
                progressBar.visibility = View.VISIBLE

                // Re-authenticate user
                val credential = EmailAuthProvider.getCredential(firebaseUser.email!!, userPwd)

                firebaseUser.reauthenticate(credential).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        progressBar.visibility = View.GONE

                        // Disable editText for password.
                        editTextUserPassword.isEnabled = false

                        // Enable delete user button. Disable authenticate button.
                        buttonReAuthenticate.isEnabled = false
                        buttonDeleteUser.isEnabled = true

                        // Set textView to show user is authenticated/verified
                        textViewAuthenticated.text = "You are authenticated/verified. You can delete your profile and related data now."
                        Toast.makeText(this, "Password has been verified. You can delete your profile now. Be careful, this action is irreversible.", Toast.LENGTH_LONG).show()

                        // Update color of delete user button.
                        buttonDeleteUser.backgroundTintList = ContextCompat.getColorStateList(this, R.color.dark_green)

                        buttonDeleteUser.setOnClickListener {
                            showAlertDialog(firebaseUser)
                        }
                    } else {
                        task.exception?.let { e ->
                            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                    progressBar.visibility = View.GONE
                }
            }
        }
    }

    private fun showAlertDialog(firebaseUser: FirebaseUser) {

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete User and Related Data")
        builder.setMessage("Do you really want to delete your profile and related data? This action is irreversible")

        builder.setPositiveButton("Continue") { _, _ ->
            deleteUser(firebaseUser)
        }

        // Return to user Profile Activity if user presses cancel button
        builder.setNegativeButton("Cancel") { _, _ ->
            val intent = Intent(this, UserProfileActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Create the AlertDialog
        val alertDialog = builder.create()

        // Change the button color of continue
        alertDialog.setOnShowListener {
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setTextColor(resources.getColor(R.color.red))
        }

        // Show the alertDialog
        alertDialog.show()
    }

    //Delete User
    private fun deleteUser(firebaseUser: FirebaseUser) {
        firebaseUser.delete().addOnCompleteListener { task ->
            if (task.isSuccessful) {

                deleteUserData(firebaseUser)

                authProfile.signOut()

                Toast.makeText(this, "User has been deleted!", Toast.LENGTH_LONG).show()

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                try {
                    throw task.exception!!
                } catch (e: Exception) {
                    Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                }
            }
            progressBar.visibility = View.GONE
        }
    }

    // Delete all the data of the user
    private fun deleteUserData(firebaseUser: FirebaseUser) {

        // Delete data from Realtime Database
        val databaseReference = FirebaseDatabase.getInstance().getReference("Registered Users")
        databaseReference.child(firebaseUser.uid).removeValue().addOnSuccessListener {
            Log.d(TAG, "OnSuccess: User Data Deleted")
        }.addOnFailureListener { e ->
            e.message?.let { Log.d(TAG, it) }
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
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
            }
            R.id.menu_delete_profile -> {
                val intent = Intent(this, DeleteProfileActivity::class.java)
                startActivity(intent)
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