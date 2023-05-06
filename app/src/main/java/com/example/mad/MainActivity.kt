package com.example.mad

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var accList : ArrayList<Accs>
    private lateinit var databaseReference: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.imageRecycler)
        recyclerView.layoutManager = LinearLayoutManager(this)

        accList = arrayListOf()

        databaseReference = FirebaseDatabase.getInstance().getReference("Accommodations")
        databaseReference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(dataSnapShot in snapshot.children){
                        val acco = dataSnapShot.getValue(Accs::class.java)
                        accList.add(acco!!)

                    }
                    recyclerView.adapter = MyAdapter(accList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity , error.toString() , Toast.LENGTH_SHORT).show()
            }
        })


    }
}