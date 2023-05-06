package com.example.madfinal.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.madfinal.R
import com.example.madfinal.adapters.bookAdapter
import com.example.madfinal.models.BookModel
import com.google.firebase.database.*

class Activity_fetching : AppCompatActivity() {

    private lateinit var bookRecyclerView: RecyclerView
    private lateinit var loadingData:TextView
    private lateinit var bookList:ArrayList<BookModel>
    private lateinit var dbRef:DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fetching)

        bookRecyclerView =findViewById(R.id.rvBook)
        bookRecyclerView.layoutManager = LinearLayoutManager(this)
        bookRecyclerView.setHasFixedSize(true)
        loadingData = findViewById(R.id.tvLoadingData)

        bookList = arrayListOf<BookModel>()

        getBookData()
    }

    private fun getBookData(){
        bookRecyclerView.visibility = View.GONE
        loadingData.visibility = View.VISIBLE

        dbRef = FirebaseDatabase.getInstance().getReference("My Book")

        dbRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                bookList.clear()
                if (snapshot.exists()){
                    for (bookSnap in snapshot.children){
                        val bookData = bookSnap.getValue(BookModel::class.java)
                        bookList.add(bookData!!)
                    }
                    val mAdapter = bookAdapter(bookList)
                    bookRecyclerView.adapter = mAdapter

                    mAdapter.setOnItemClickListener(object : bookAdapter.OnItemClickListener{
                        override fun onItemClick(position: Int) {
                            val intent = Intent(this@Activity_fetching, BookDetailsActivity::class.java)

                            //put extras
                            //"bookId" is newly implemented in here and purple 'bookID' is from the BookModel
                            intent.putExtra("bookId", bookList[position].bookID)
                            intent.putExtra("hotelName", bookList[position].hotel_name)
                            intent.putExtra("hotelPrice", bookList[position].hotel_price)
                            intent.putExtra("totalPrice", bookList[position].total_Price)
                            intent.putExtra("checkIn", bookList[position].Check_in)
                            intent.putExtra("checkOut", bookList[position].Check_out)
                            intent.putExtra("guest", bookList[position].Guest)
                            startActivity(intent)
                        }

                    })
                    bookRecyclerView.visibility = View.VISIBLE
                    loadingData.visibility = View.GONE

                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}