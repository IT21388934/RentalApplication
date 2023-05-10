package com.example.mad

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.StorageReference

class AccommodationManager : AppCompatActivity() {

    private lateinit var btn1 : Button
    private lateinit var btn2 : Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_accommodation_manager)

        btn1 = findViewById(R.id.btnCreate)
        btn2 = findViewById(R.id.btnView)


        btn1.setOnClickListener(View.OnClickListener { startActivity(Intent(this , CreateAcco::class.java)) })
        btn2.setOnClickListener(View.OnClickListener { startActivity(Intent(this , MainActivity::class.java)) })

    }

}





