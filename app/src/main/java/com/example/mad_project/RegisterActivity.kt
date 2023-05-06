package com.example.mad_project

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.*
import com.google.firebase.database.FirebaseDatabase
import java.util.regex.Pattern

class RegisterActivity : AppCompatActivity() {

    private lateinit var editTextRegisterFullname: EditText
    private lateinit var editTextRegisterEmail: EditText
    private lateinit var editTextRegisterDob: EditText
    private lateinit var editTextRegisterMobile: EditText
    private lateinit var editTextRegisterpassword: EditText
    private lateinit var editTextRegisterConfirmPassword: EditText

    private lateinit var progressBar: ProgressBar
    private lateinit var radioGroupRegisterGender: RadioGroup
    private lateinit var radioButtonRegistraterGenderSelected: RadioButton

    companion object {
        private const val TAG = "RegisterActivity"
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        getSupportActionBar()?.setTitle("Register")

        Toast.makeText(this, "You Can Register Now", Toast.LENGTH_LONG).show()

        progressBar = findViewById(R.id.progressBar)
        editTextRegisterFullname = findViewById(R.id.editText_register_full_name)
        editTextRegisterEmail = findViewById(R.id.editText_register_email)
        editTextRegisterDob = findViewById(R.id.editText_register_dob)
        editTextRegisterMobile = findViewById(R.id.editText_register_mobile)
        editTextRegisterpassword = findViewById(R.id.editText_register_password)
        //editTextRegisterConfirmPassword = findViewById(R.id.editText_register_confirm_password)
       editTextRegisterConfirmPassword = findViewById(R.id.editText_register_password)

        //RadioButton for gender
        radioGroupRegisterGender = findViewById(R.id.radio_group_register_gender)
        radioGroupRegisterGender.clearCheck()


        val buttonRegister = findViewById<Button>(R.id.button_register)
        buttonRegister.setOnClickListener {
            val selectGenderId = radioGroupRegisterGender.checkedRadioButtonId
            radioButtonRegistraterGenderSelected = findViewById(selectGenderId)

            //obtain the entered data
            val textFullName = editTextRegisterFullname.text.toString()
            val textEmail = editTextRegisterEmail.text.toString()
            val textDob = editTextRegisterDob.text.toString()
            val textMobile = editTextRegisterMobile.text.toString()
            val textPassword = editTextRegisterpassword.text.toString()
            val textConfirmPassword = editTextRegisterConfirmPassword.text.toString()
            var textGender: String? = null //can't obtain the value before verifying if any button was selected or not

            //validate mobile number using matcher and pattern (regular expression)
            val mobileRegex = "[0-9][0-9]" //first no. can be (6,8,9) and rest 9 no can be any no
            val mobilePattern = Pattern.compile(mobileRegex)
            val mobileMatcher = mobilePattern.matcher(textMobile)


            if (textFullName.isEmpty()) {
                Toast.makeText(this,"Please Enter Your Full Name", Toast.LENGTH_LONG).show()
                editTextRegisterFullname.setError("Full Name is Required!")
                editTextRegisterFullname.requestFocus()
            } else if (textEmail.isEmpty()) {
                Toast.makeText(this,"Please Enter Your Email", Toast.LENGTH_LONG).show()
                editTextRegisterEmail.setError("Email is Required!")
                editTextRegisterEmail.requestFocus()
            } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
                Toast.makeText(this, "Please Re-enter Your Email", Toast.LENGTH_LONG).show()
                editTextRegisterEmail.setError("Valid Email is Required!")
                editTextRegisterEmail.requestFocus()
            } else if (textDob.isEmpty()) {
                Toast.makeText(this, "Please enter Your Birth day", Toast.LENGTH_LONG).show()
                editTextRegisterDob.setError("Date of Birth is Required!")
                editTextRegisterDob.requestFocus()
            } else if (radioGroupRegisterGender.checkedRadioButtonId == -1) {
                Toast.makeText(this, "Please enter Your Gender", Toast.LENGTH_LONG).show()
                radioButtonRegistraterGenderSelected.setError("Gender is Required!")
                radioButtonRegistraterGenderSelected.requestFocus()
            } else if (textMobile.isEmpty()) {
                Toast.makeText(this, "Please enter Your Contact Number", Toast.LENGTH_LONG).show()
                editTextRegisterMobile.setError("Contact Number is Required!")
                editTextRegisterMobile.requestFocus()
            } else if (textMobile.length != 10) {
                Toast.makeText(this, "Please Re-enter Your Contact Number", Toast.LENGTH_LONG).show()
                editTextRegisterMobile.setError("Contact Number Shoul be 10 digit!")
                editTextRegisterMobile.requestFocus()
            } else if(!mobileMatcher.find()){
                Toast.makeText(this, "Please Re-enter Your Contact Number", Toast.LENGTH_LONG).show()
                editTextRegisterMobile.setError("Contact Number is not valid!")
                editTextRegisterMobile.requestFocus()
            } else if (textPassword.isEmpty()) {
                Toast.makeText(this, "Please Re-enter Your Password", Toast.LENGTH_LONG).show()
                editTextRegisterpassword.setError("Password is Required!")
                editTextRegisterpassword.requestFocus()
            } else if (textPassword.length < 6) {
                Toast.makeText(this, "Password should be at least 6 digits", Toast.LENGTH_LONG).show()
                editTextRegisterpassword.setError("Password too Weak!")
                editTextRegisterpassword.requestFocus()
            } else if (textConfirmPassword.isEmpty()) {
                Toast.makeText(this, "Please Confirm Your Password", Toast.LENGTH_LONG).show()
                editTextRegisterConfirmPassword.setError("Password Confirmation is Required!")
                editTextRegisterConfirmPassword.requestFocus()
            } else if (textPassword != textConfirmPassword) {
                Toast.makeText(this, "Please Enter the same Password", Toast.LENGTH_LONG).show()
                editTextRegisterConfirmPassword.setError("Password Confirmation is Required!")
                editTextRegisterConfirmPassword.requestFocus()
                //clear the entered password
                editTextRegisterpassword.text.clear()
                editTextRegisterConfirmPassword.text.clear()
            } else {
                textGender = radioButtonRegistraterGenderSelected.text.toString()
                progressBar.visibility = View.VISIBLE
                registerUser(textFullName, textEmail, textDob, textGender ,textMobile, textPassword, textConfirmPassword)
            }
        }
    }

    //register user using the credentials given
    private fun registerUser(
        textFullName: String,
        textEmail: String,
        textDob: String,
        textGender: String,
        textMobile: String,
        textPassword: String,
        textConfirmPassword: String
    ) {

        val auth = FirebaseAuth.getInstance()

        //Create user Profile
        auth.createUserWithEmailAndPassword(textEmail, textPassword).addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    val firebaseUser = auth.currentUser

                    //Update display name of User
                    val profileChangeRequest = UserProfileChangeRequest.Builder()
                        .setDisplayName(textFullName)
                        .build()

                    firebaseUser?.updateProfile(profileChangeRequest)

                    //Enter user data into the firebase realtime database.
                    //ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(textFullName, textDob, textGender, textMobile)
                    class ReadWriteUserDetails(
                        val doB: String,
                        val gender: String,
                        val mobile: String
                    )

                    val writeUserDetails = ReadWriteUserDetails(textDob, textGender, textMobile)

                    //Extracting user reference from database for "Register Users"
                    val referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users")

                    firebaseUser?.let {
                        referenceProfile.child(it.uid).setValue(writeUserDetails).addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    //send verification email
                                    firebaseUser?.sendEmailVerification()

                                    Toast.makeText(this, "User Registered Successfully, please Verify your email", Toast.LENGTH_LONG).show()

                                //open user profile after successful registration
                                val intent = Intent(this,UserProfileActivity::class.java).apply {
                                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                }
                                    startActivity(intent)
                                    finish() //to close register activity
                                } else {
                                    Toast.makeText(this, "User Registered Fail, please try again", Toast.LENGTH_LONG).show()
                                }
                                progressBar.visibility = View.GONE
                            }
                    }

                } else {
                    try {
                        throw task.exception!!
                    } catch (e: FirebaseAuthWeakPasswordException) {
                        editTextRegisterpassword.setError("Your password is too Weak. Kindly use a mix of alphabets, numbers and special characters")
                        editTextRegisterpassword.requestFocus()
                    } catch (e: FirebaseAuthInvalidCredentialsException) {
                        editTextRegisterpassword.setError("Your email is invalid or already in use. Kindly re-enter.")
                        editTextRegisterpassword.requestFocus()
                    } catch (e: FirebaseAuthUserCollisionException) {
                        editTextRegisterpassword.setError("User is already registered with this email. Use another email.")
                        editTextRegisterpassword.requestFocus()
                    } catch (e: Exception) {
                        Log.e(TAG, e.message!!)
                        Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
                    }
                    progressBar.visibility = View.GONE
                }
            }
    }
}
