package com.example.mad_project

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.*
import java.util.regex.Pattern

class UpdateProfileActivity : AppCompatActivity() {

    private lateinit var editTextUpdateName: EditText
    private lateinit var editTextUpdateDob: EditText
    private lateinit var editTextUpdateMobile: EditText
    private lateinit var radioGroupUpdateGender: RadioGroup
    //private lateinit var radioButtonUpdateGenderSelected: RadioButton
    private lateinit var authProfile: FirebaseAuth
    private lateinit var progressBar: ProgressBar

    private var textFullName: String = ""
    private var textDob: String = ""
    private var textGender: String = ""
    private var textMobile: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_profile)

        supportActionBar?.title = "Update Profile Details"

        progressBar = findViewById(R.id.progressBar)
        editTextUpdateName = findViewById(R.id.editText_update_profile_name)
        editTextUpdateDob = findViewById(R.id.editText_update_profile_dob)
        editTextUpdateMobile = findViewById(R.id.editText_update_profile_mobile)

        radioGroupUpdateGender = findViewById(R.id.radio_group_update_profile_gender)

        authProfile = FirebaseAuth.getInstance()
        val firebaseUser = authProfile.currentUser

        // Show Profile Data
        showProfile(firebaseUser)

        // Upload Profile Picture
        val buttonUploadProfilePicture = findViewById<Button>(R.id.button_update_profile)
        buttonUploadProfilePicture.setOnClickListener {
            val intent = Intent(this, UploadProfilePictureActivity::class.java)
            startActivity(intent)
            finish()
        }

        //Update Profile
        val buttonUpdateProfile = findViewById<Button>(R.id.button_update_profile)
        buttonUpdateProfile.setOnClickListener {
            updateProfile(firebaseUser)
        }
    }

    private fun updateProfile(firebaseUser: FirebaseUser?) {
        val selectedGenderID = radioGroupUpdateGender.checkedRadioButtonId
        val radioButtonUpdateGenderSelected = findViewById<RadioButton>(selectedGenderID)

        // validate mobile number using matcher and pattern (regular expression)
        val mobileRegex = "[0-9][0-9]{9}"
        val mobilePattern = Pattern.compile(mobileRegex)
        val mobileMatcher = mobilePattern.matcher(editTextUpdateMobile.text.toString())

        val textFullName = editTextUpdateName.text.toString().trim()
        val textDob = editTextUpdateDob.text.toString().trim()
        val textMobile = editTextUpdateMobile.text.toString().trim()
        val textGender = radioButtonUpdateGenderSelected.text.toString()

        when {
            textFullName.isEmpty() -> {
                Toast.makeText(this, "Please enter your full name", Toast.LENGTH_LONG).show()
                editTextUpdateName.error = "Full Name is Required!"
                editTextUpdateName.requestFocus()
            }
            textDob.isEmpty() -> {
                Toast.makeText(this, "Please enter your birth day", Toast.LENGTH_LONG).show()
                editTextUpdateDob.error = "Date of Birth is Required!"
                editTextUpdateDob.requestFocus()
            }
            textGender.isEmpty() -> {
                Toast.makeText(this, "Please enter your gender", Toast.LENGTH_LONG).show()
                radioButtonUpdateGenderSelected.error = "Gender is Required!"
                radioButtonUpdateGenderSelected.requestFocus()
            }
            textMobile.isEmpty() -> {
                Toast.makeText(this, "Please enter your contact number", Toast.LENGTH_LONG).show()
                editTextUpdateMobile.error = "Contact Number is Required!"
                editTextUpdateMobile.requestFocus()
            }
            textMobile.length != 10 -> {
                Toast.makeText(this, "Please enter a 10-digit contact number", Toast.LENGTH_LONG)
                    .show()
                editTextUpdateMobile.error = "Contact Number should be 10 digits!"
                editTextUpdateMobile.requestFocus()
            }
            !mobileMatcher.matches() -> {
                Toast.makeText(this, "Please enter a valid contact number", Toast.LENGTH_LONG)
                    .show()
                editTextUpdateMobile.error = "Contact Number is not valid!"
                editTextUpdateMobile.requestFocus()
            }
            else -> {
                // enter user data into the Firebase Realtime Database
                val writeUserDetails = ReadWriteUserDetails(textDob, textGender, textMobile)

                // extract user reference from database for "Registered Users"
                val referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users")

                val userID = firebaseUser?.uid

                progressBar.visibility = View.VISIBLE

                userID?.let {
                    referenceProfile.child(it).setValue(writeUserDetails)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                // setting new display name
                                val profileUpdates =
                                    UserProfileChangeRequest.Builder().setDisplayName(textFullName)
                                        .build()
                                firebaseUser.updateProfile(profileUpdates)

                                Toast.makeText(this, "Update Successful!", Toast.LENGTH_LONG).show()

                                // stop user from returning to UpdateProfileActivity on pressing back button and closes activity
                                val intent = Intent(this, UserProfileActivity::class.java)
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                                finish()
                            } else {
                                try {
                                    throw task.exception!!
                                } catch (e: Exception) {
                                    Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
                                }
                            }
                            progressBar.visibility = View.GONE
                        }
                }
            }
        }
    }

    private fun showProfile(firebaseUser: FirebaseUser?) {
        val userID = firebaseUser?.uid ?: return

        val reference = FirebaseDatabase.getInstance().getReference("Registered Users")

        progressBar.visibility = View.VISIBLE

        reference.child(userID).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userDetails = snapshot.getValue(ReadWriteUserDetails::class.java)

                if (userDetails != null) {
                    val fullName = firebaseUser.displayName
                    val dob = userDetails.doB
                    val gender = userDetails.gender
                    val mobile = userDetails.mobile

                    editTextUpdateName.setText(fullName)
                    editTextUpdateDob.setText(dob)
                    editTextUpdateMobile.setText(mobile)

                    // Show gender through radio button
                    val radioButtonId = if (gender == "Male") R.id.radio_male else R.id.radio_female
                    val radioButton = findViewById<RadioButton>(radioButtonId)
                    radioButton.isChecked = true
                } else {
                    Toast.makeText(
                        this@UpdateProfileActivity,
                        "Something went wrong!",
                        Toast.LENGTH_LONG
                    ).show()
                }
                progressBar.visibility = View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@UpdateProfileActivity,
                    "Something went wrong!",
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
