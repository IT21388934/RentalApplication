package com.example.madfinal.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.madfinal.models.BookModel
import com.example.madfinal.R
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class activity_Insertion : AppCompatActivity() {

    private lateinit var hotelPrice: EditText
    private lateinit var total:TextView
    private lateinit var addBtn:Button

    private lateinit var backButton:Button


    private lateinit var hotelName: EditText
    private lateinit var checkIn: EditText
    private lateinit var checkOut: EditText
    private lateinit var guests: EditText
    private lateinit var confirmBook_btn: Button

    private lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_insertion)

        //calculate total amount
        hotelPrice = findViewById(R.id.hotelPrice)
        total = findViewById(R.id.totalView)
        addBtn = findViewById(R.id.add)

        addBtn.setOnClickListener{
            val n1 = guests.text.toString().toDouble()
            val n2 = hotelPrice.text.toString().toDouble()
            val result = n1 * n2
//            total.text = result.toString()
            total.text = "%.2f".format(result)

        }


        hotelName = findViewById(R.id.nameOfHotel)
        checkIn = findViewById(R.id.vCheckIn)
        checkOut = findViewById(R.id.vCheckOut)
        guests = findViewById(R.id.vGuests)
        confirmBook_btn = findViewById(R.id.bookConfirm_btn)

///////////////////////////////////////////////



///////////////////////////////
        dbRef = FirebaseDatabase.getInstance().getReference("My Book")

        confirmBook_btn.setOnClickListener{
            saveBookDetails()
        }
    }


    private fun saveBookDetails(){

        val hotel_name = hotelName.text.toString()
        val hotel_price = hotelPrice.text.toString()
        val total_Price = total.text.toString()
        val check_In = checkIn.text.toString()
        val check_Out = checkOut.text.toString()
        val guests_no = guests.text.toString()

        if (check_In.isEmpty()){
            checkIn.error = "Please enter check in date"
        }
        if (check_Out.isEmpty()){
            checkOut.error = "Please enter check out date"
        }
        if (guests_no.isEmpty()){
            guests.error = "Please enter number of guests"
        }


        val BookID = dbRef.push().key!!

        val myOrder = BookModel(BookID,hotel_name,hotel_price,total_Price,check_In,check_Out,guests_no)

        dbRef.child(BookID).setValue(myOrder)
            .addOnCompleteListener{
                Toast.makeText(this,"Data insert successfully", Toast.LENGTH_LONG).show()
                checkIn.text.clear()
                checkOut.text.clear()
                guests.text.clear()

            }.addOnFailureListener{
                Toast.makeText(this,"Error", Toast.LENGTH_LONG).show()
            }



    }
}