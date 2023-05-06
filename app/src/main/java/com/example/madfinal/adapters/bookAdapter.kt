package com.example.madfinal.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.madfinal.R
import com.example.madfinal.models.BookModel


class bookAdapter (private val bookList: ArrayList<BookModel>):
    RecyclerView.Adapter<bookAdapter.ViewHolder>() {

    private lateinit var mListener: OnItemClickListener

    interface OnItemClickListener{
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(clickListener: OnItemClickListener){
        mListener = clickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder{
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.order_list_activity, parent, false)
        return ViewHolder(itemView, mListener)
    }
    override fun onBindViewHolder(holder:ViewHolder, position: Int){
        val currentBookhotel = bookList[position]
        holder.HotelName.text = currentBookhotel.hotel_name

        val currentBook = bookList[position]
        holder.checkIN.text = currentBook.Check_in //xml text checkIN - model checkin

        val cuurentBookCheckot = bookList[position]
        holder.checkOUT.text = cuurentBookCheckot.Check_out
    }

    override fun getItemCount(): Int{
        return bookList.size
    }

    class ViewHolder(itemView: View, clickListener: OnItemClickListener) :RecyclerView.ViewHolder(itemView){

        val HotelName: TextView = itemView.findViewById(R.id.HotelName)
        val checkIN: TextView = itemView.findViewById(R.id.checkIN)
        val checkOUT:TextView = itemView.findViewById(R.id.checkOUT)

        init {
            itemView.setOnClickListener{
                clickListener.onItemClick(adapterPosition)
            }
        }
    }
}