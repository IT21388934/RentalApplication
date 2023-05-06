package com.example.madfinal.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.madfinal.R
import com.example.madfinal.models.BookModel
import com.google.firebase.database.FirebaseDatabase

class BookDetailsActivity : AppCompatActivity() {

    private lateinit var tvBookID: TextView
    private lateinit var tvHotelName: TextView
    private lateinit var tvHotelPrice:TextView
    private lateinit var total:TextView
    private lateinit var tvCheckIn: TextView
    private lateinit var tvCheckOut: TextView
    private lateinit var tvGuests: TextView
    private lateinit var ubdateBTN: Button
    private lateinit var deletaBTN: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_details)

        initView()
        setValuesToViews()

        ubdateBTN.setOnClickListener {
            openUpdateDialog(
                intent.getStringExtra("bookId").toString()
            )
        }

        deletaBTN.setOnClickListener {
            deleteRecord(
                intent.getStringExtra("bookId").toString()
            )
        }
    }

    //delete booked record
    private fun deleteRecord(
        tvBookID: String
    ) {
        val dbRef = FirebaseDatabase.getInstance().getReference("My Book").child(tvBookID)
        val mTask = dbRef.removeValue()

        mTask.addOnSuccessListener {
            Toast.makeText(this, "Booked data deleted", Toast.LENGTH_LONG).show()

            val intent = Intent(this, Activity_fetching::class.java)
            finish()
            startActivity(intent)
        }.addOnFailureListener { error ->
            Toast.makeText(this, "Deleting Err ${error.message}", Toast.LENGTH_LONG).show()
        }
    }


    private fun initView() {
        tvBookID = findViewById(R.id.tvBookId)
        tvHotelName = findViewById(R.id.tvHotelName)
        tvHotelPrice = findViewById(R.id.tvHotelPrice)
        total = findViewById(R.id.tvTotalPrice)
        tvCheckIn = findViewById(R.id.tvCheckIn)
        tvCheckOut = findViewById(R.id.tvCheckOut)
        tvGuests = findViewById(R.id.tvbookGuest)
        ubdateBTN = findViewById(R.id.btnUpdate)
        deletaBTN = findViewById(R.id.btnDelete)
    }

    //push data values to the booked details chart
    private fun setValuesToViews() {
        tvBookID.text = intent.getStringExtra("bookId")
        tvHotelName.text = intent.getStringExtra("hotelName")
        tvHotelPrice.text = intent.getStringExtra("hotelPrice")
        total.text = intent.getStringExtra("totalPrice")
        tvCheckIn.text = intent.getStringExtra("checkIn")
        tvCheckOut.text = intent.getStringExtra("checkOut")
        tvGuests.text = intent.getStringExtra("guest")
    }

    //Navigate to the update panel and update data
    private fun openUpdateDialog(
        bookId: String
    ){
        val mDialog = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val mDialogView = inflater.inflate(R.layout.update_dialog, null)

        mDialog.setView(mDialogView)

        val etCheckIn = mDialogView.findViewById<EditText>(R.id.etCheckIn)
        val etCheckOut = mDialogView.findViewById<EditText>(R.id.etCheckOut)
        val etGuests = mDialogView.findViewById<EditText>(R.id.etGuests)

        val btnUpdateData = mDialogView.findViewById<Button>(R.id.btnUpdateData)

        etCheckIn.setText(intent.getStringExtra("checkIn").toString())
        etCheckOut.setText(intent.getStringExtra("checkOut").toString())
        etGuests.setText(intent.getStringExtra("guest").toString())

        mDialog.setTitle("Update Booked Record")

        val alertDialog = mDialog.create()
        alertDialog.show()

        btnUpdateData.setOnClickListener {
            updateBookData(
                bookId,
                etCheckIn.text.toString(),
                etCheckOut.text.toString(),
                etGuests.text.toString()
            )
            //display toast msg when the update is successful
            Toast.makeText(applicationContext, "Book Data Updated", Toast.LENGTH_LONG).show()

            //we are setting updated data to our textviews

            tvCheckIn.text = etCheckIn.text.toString()
            tvCheckOut.text = etCheckOut.text.toString()
            tvGuests.text = etGuests.text.toString()

            alertDialog.dismiss()

        }

    }
    private fun updateBookData(
        tvBookID: String,
        tvCheckIn: String,
        tvCheckOut: String,
        tvGuests: String
    ) {
        val dbRef = FirebaseDatabase.getInstance().getReference("My Book").child(tvBookID)
        val bookInfo = BookModel(tvBookID, tvCheckIn, tvCheckOut, tvGuests)
        dbRef.setValue(bookInfo)
    }

}


