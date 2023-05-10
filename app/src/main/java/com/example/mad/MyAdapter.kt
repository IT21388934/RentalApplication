package com.example.mad


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso



class MyAdapter(private val accList: ArrayList<Accs>) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {



        override fun onCreateViewHolder(parent : ViewGroup , viewType : Int ) : MyAdapter.MyViewHolder{

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.mainpage_place_1 ,
        parent , false)

        return MyViewHolder(itemView)

        }


    override fun onBindViewHolder(holder : MyViewHolder , position : Int){

        val acc : Accs = accList[position]
        val id  = acc.accId

        holder.apply {


            Picasso.get().load(acc.imageUri).into(image)
            locationName.text = acc.locsName
            price.text = acc.accosPrice
            dates.text = acc.accosDate




                image.setOnLongClickListener{

                    MaterialAlertDialogBuilder(holder.itemView.context)
                        .setTitle("Delete Item permanently")
                        .setMessage("Are sure you want to delete this item ?")
                        .setPositiveButton("Yes"){_,_ ->

                            val firebaseRef = FirebaseDatabase.getInstance().getReference("Accommodations")
                            val storageRef = FirebaseStorage.getInstance().getReference("Images")
                            storageRef.child(acc.accId.toString()).delete()

                            firebaseRef.child(acc.accId.toString()).removeValue()
                                .addOnSuccessListener {

                                    Toast.makeText(holder.itemView.context , "Item removed Successfully" , Toast.LENGTH_SHORT).show()

                                }
                                .addOnFailureListener{ error ->

                                    Toast.makeText(holder.itemView.context , "error ${error.message}" , Toast.LENGTH_SHORT).show()

                                }


                        }
                        .setNegativeButton("No"){_,_ ->

                            Toast.makeText(holder.itemView.context , "Cancelled" , Toast.LENGTH_SHORT).show()

                        }
                        .show()

                    return@setOnLongClickListener true


                }


        }

//        holder.locationName.text = acc.locsName
//        holder.price.text = acc.accosPrice
//        holder.dates.text = acc.accosDate
//        holder.Picasso.get().load(acc.imageUri).into(img1)


    }


    override fun getItemCount(): Int {
        return accList.size
    }

    public class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        val container : ConstraintLayout = itemView.findViewById(R.id.place1)
        val image:ImageView = itemView.findViewById(R.id.img1)
        val locationName : TextView = itemView.findViewById(R.id.tvLoc)
        val dates : TextView = itemView.findViewById(R.id.tvDates)
        val price : TextView = itemView.findViewById(R.id.tvPrice)


    }


}