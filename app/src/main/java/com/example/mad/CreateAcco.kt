package com.example.mad

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class CreateAcco : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var locName : EditText
    private lateinit var price : EditText
    private lateinit var dates : EditText
    private lateinit var btnSave : Button
    private lateinit var btnUpload : Button
    private  var uri : Uri?= null


    private lateinit var dbref : DatabaseReference
    private lateinit var stref : StorageReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.lister_lisiting_reg)

        imageView = findViewById(R.id.selectImage)
        locName = findViewById(R.id.edtLoco1)
        price = findViewById(R.id.pricePerNight1)
        dates = findViewById(R.id.bookDates1)
        btnSave = findViewById(R.id.updateAcco)
        btnUpload = findViewById(R.id.btnImage)



        dbref = FirebaseDatabase.getInstance().getReference("Accommodations")
        stref = FirebaseStorage.getInstance().getReference("Images")

        btnSave.setOnClickListener{

            saveAccommodationData()

        }

        val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()){
            imageView.setImageURI(it)
            if(it != null){
                uri = it
            }
        }

        btnUpload.setOnClickListener{

            pickImage.launch("image/*")


        }

    }



    private fun saveAccommodationData(){

        val locoName = locName.text.toString()
        val locoPrice = price.text.toString()
        val locoDates  = dates.text.toString()

        val accId = dbref.push().key!!


        var accommodation : Accs

        uri?.let{

            stref.child(accId).putFile(it)
                .addOnSuccessListener { task->

                    task.metadata!!.reference!!.downloadUrl
                        .addOnSuccessListener { url ->

                            Toast.makeText(this , "Image inserted Successfully " , Toast.LENGTH_LONG).show()
                            val imgUrl = url.toString()

                            accommodation = Accs(accId , locoName , locoPrice , locoDates , imgUrl)

                            dbref.child(accId).setValue(accommodation)
                                .addOnCompleteListener{

                                    Toast.makeText(this , "Data inserted Successfully " , Toast.LENGTH_LONG).show()

                                    locName.text.clear()
                                    dates.text.clear()
                                    price.text.clear()


                                }.addOnFailureListener{ err ->

                                    Toast.makeText(this , "Error ${err.message}" , Toast.LENGTH_LONG).show()

                                }


                        }

                }

        }





    }

}