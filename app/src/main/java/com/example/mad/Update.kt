package com.example.mad

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.database.FirebaseDatabase

class Update : AppCompatActivity() {

    private lateinit var tvLocName : TextView
    private lateinit var tvLocPrice : TextView
    private lateinit var tvLocDate : TextView
    private lateinit var btnUpdate : Button


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.update)

        tvLocName = findViewById(R.id.locName)
        tvLocPrice = findViewById(R.id.locPrice)
        tvLocDate = findViewById(R.id.locDate)
        btnUpdate = findViewById(R.id.changes)

        btnUpdate.setOnClickListener{

            openUpdateDialog(

                intent.getStringExtra("accId").toString() ,
                intent.getStringExtra("locoName").toString()


            )

        }

        setValuesToViews()



    }

    private fun openUpdateDialog(

        accId : String ,
        locoName : String

    ){

        val mDialog = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val mDialogView = inflater.inflate(R.layout.dialogupdate , null)

        mDialog.setView(mDialogView)

        val etLocName = mDialogView.findViewById<EditText>(R.id.edtLocoName)
        val etLocPrice = mDialogView.findViewById<EditText>(R.id.edtPrice)
        val etLocDate = mDialogView.findViewById<EditText>(R.id.edtDates)
        val updateButton = mDialogView.findViewById<Button>(R.id.updateBtn)

        etLocName.setText(intent.getStringExtra("locoName").toString())
        etLocPrice.setText(intent.getStringExtra("accPrice").toString())
        etLocDate.setText(intent.getStringExtra("accDate").toString())

        mDialog.setTitle("Updating $locoName Record")

        val alertDialog = mDialog.create()
        alertDialog.show()

        updateButton.setOnClickListener{

            updateAccData(

                accId,
                etLocName.text.toString(),
                etLocPrice.text.toString(),
                etLocDate.text.toString()

            )

            Toast.makeText(applicationContext , "Accommodation Data Updateed" , Toast.LENGTH_LONG).show()

            //set updated data to txtView
            tvLocName.text = etLocName.text.toString()
            tvLocPrice.text = etLocPrice.text.toString()
            tvLocDate.text = etLocDate.text.toString()

            alertDialog.dismiss()

        }



    }

    private fun updateAccData(

        id : String,
        locName : String ,
        locPrice : String ,
        locDate : String
    ){

        val dbRef = FirebaseDatabase.getInstance().getReference("Accommodations").child(id)
        val accInfo = Accs(id , locName , locPrice , locDate )
        dbRef.setValue(accInfo)

    }

    private fun setValuesToViews(){

        tvLocName.text = intent.getStringExtra("locoName")
        tvLocPrice.text = intent.getStringExtra("accPrice")
        tvLocDate.text = intent.getStringExtra("accDate")



    }
}