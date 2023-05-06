package com.example.mad_project

import android.annotation.SuppressLint

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class LoginActivity : AppCompatActivity() {

    private lateinit var editTextLoginEmail: EditText
    private lateinit var editTextLoginPassword: EditText
    private lateinit var progressBar: ProgressBar
    private lateinit var authProfile: FirebaseAuth

    companion object {
        private const val TAG = "LoginActivity"
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        supportActionBar?.title = "LOGIN"

        editTextLoginEmail = findViewById(R.id.editText_login_email)
        editTextLoginPassword = findViewById(R.id.editText_login_password)
        progressBar = findViewById(R.id.progressBar)

        authProfile = FirebaseAuth.getInstance()

        //Reset Password
        val textViewLinkResetPwd = findViewById<TextView>(R.id.textView_forgot_password_link)
        textViewLinkResetPwd.setOnClickListener {
            Toast.makeText(this, "You can reset your password now!", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }

        //Register
        val textViewLinkRegister = findViewById<TextView>(R.id.textView_register_link)
        textViewLinkRegister.setOnClickListener {
            Toast.makeText(this, "You can reset your password now!", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        //show hide password using Eye icon
        val imageViewShowHidePassword: ImageView = findViewById(R.id.imageView_show_hide_password)
        imageViewShowHidePassword.setImageResource(R.drawable.ic_hide_password)
        imageViewShowHidePassword.setOnClickListener {
            if (editTextLoginPassword.transformationMethod == HideReturnsTransformationMethod.getInstance()) {
                //if password is visible the hide it
                editTextLoginPassword.transformationMethod = PasswordTransformationMethod.getInstance()

                //change icon
                imageViewShowHidePassword.setImageResource(R.drawable.ic_hide_password)
            } else {
                editTextLoginPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
                imageViewShowHidePassword.setImageResource(R.drawable.ic_show_password)
            }
        }

        //Login User
        val buttonLogin = findViewById<Button>(R.id.button_login)
        buttonLogin.setOnClickListener {
            val textEmail = editTextLoginEmail.text.toString()
            val textPassword = editTextLoginPassword.text.toString()

            if (textEmail.isEmpty()) {
                Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show()
                editTextLoginEmail.error = "Email is required"
                editTextLoginEmail.requestFocus()
            } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
                Toast.makeText(this, "Please re-enter your email", Toast.LENGTH_SHORT).show()
                editTextLoginEmail.error = "Valid email is required"
                editTextLoginEmail.requestFocus()
            } else if (textPassword.isEmpty()) {
                Toast.makeText(this, "Please enter your Password", Toast.LENGTH_SHORT).show()
                editTextLoginPassword.error = "Password is required"
                editTextLoginPassword.requestFocus()
            } else {
                progressBar.visibility = View.VISIBLE
                loginUser(textEmail, textPassword)
            }
        }
    }

    private fun loginUser(textEmail: String, textPassword: String) {
        authProfile.signInWithEmailAndPassword(textEmail,textPassword).addOnCompleteListener { task ->
            if(task.isSuccessful){

                //get instance of the current user
                val firebaseUser = authProfile.currentUser

                //check if email is verified before user can access their profile
                if (firebaseUser!!.isEmailVerified) {
                    Toast.makeText(this, "You are logged in now", Toast.LENGTH_SHORT).show()

//                    //Open User Profile
//                    //start the UserProfileActivity
                    startActivity(Intent(this, UserProfileActivity::class.java))
                    finish()

                } else {
                    firebaseUser.sendEmailVerification()
                    authProfile.signOut()
                    showAlertDialog()
                }

            } else{
                try{
                    throw task.exception!!
                }catch (e: FirebaseAuthInvalidUserException){
                    editTextLoginEmail.setError("User does not exists or is no longer valid. Please register again")
                    editTextLoginEmail.requestFocus()
                }catch (e: FirebaseAuthInvalidCredentialsException){
                    editTextLoginEmail.setError("Invalid Credentials. Kindly check and re-enter")
                    editTextLoginEmail.requestFocus()
                }catch (e: Exception){
                    e.message?.let { Log.e(TAG, it) }
                    Toast.makeText(this, e.message , Toast.LENGTH_SHORT).show()
                }
            }
            progressBar.visibility = View.GONE
        }
    }

    private fun showAlertDialog() {
        //set up alert
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Email Not Verified")
        builder.setMessage("Please verify your email now. You cannot login without email verification")

        //open email apps if user clicks/taps continue button
        builder.setPositiveButton("Continue") { dialog, which ->
            val intent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_APP_EMAIL)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)  //to email app in new window and not within our app
            }
            startActivity(intent)
        }

        //create the alert Dialog
        val alertDialog = builder.create()

        //show the AlertDialog
        alertDialog.show()
    }

    //check if user is already logged in. In such case, straightaway take the user to the users profile
    override fun onStart() {
        super.onStart()

        if (authProfile.currentUser != null) {
            Toast.makeText(this, "Already Logged In", Toast.LENGTH_SHORT).show()

            // Start the user Profile Activity
            startActivity(Intent(this, UserProfileActivity::class.java))
            finish()
        } else {
            Toast.makeText(this, "You can log Now", Toast.LENGTH_SHORT).show()
        }
    }
}