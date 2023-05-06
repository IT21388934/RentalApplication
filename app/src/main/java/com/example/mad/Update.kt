package com.example.mad

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class Update : AppCompatActivity(){

    private lateinit var locationName : EditText
    private lateinit var locationPrice : EditText
    private lateinit var locationDataes : EditText
    private lateinit var database : DatabaseReference
    private lateinit var updateButton : Button
    private lateinit var acc : Accs

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState : Bundle?){

        super.onCreate(savedInstanceState)
        setContentView(R.layout.updateacco)

        locationName = findViewById(R.id.edtLoco1)
        locationPrice = findViewById(R.id.pricePerNight1)
        locationDataes = findViewById(R.id.bookDates1)
        updateButton = findViewById(R.id.updateAcco)

        updateButton.setOnClickListener{



            val locName = locationName.text.toString()
            val locPrice = locationPrice.text.toString()
            val locDates = locationDataes.text.toString()

            updateData(locName , locPrice , locDates)

        }

    }



    private fun updateData(locName : String , locPrice : String , locDates : String){

        database = FirebaseDatabase.getInstance().getReference("Accommodations")

        val acc = mapOf<String , String>(

            "locName" to locName,
            "locPrice" to locPrice ,
            "locDates" to locDates

        )

        database.updateChildren(acc).addOnSuccessListener {

            locationName.text.clear()
            locationPrice.text.clear()
            locationDataes.text.clear()
            Toast.makeText(this , "Successfully Updated" , Toast.LENGTH_SHORT).show()

        }.addOnFailureListener{

            Toast.makeText(this , "Failed To Update" , Toast.LENGTH_SHORT).show()

        }

    }




}