package com.example.rentalapplication

object Validatior {

    fun validateinput(email: String, password:String, conformPass:String, name: String, phone:String): Boolean {

        return !(password.length < 6 || email.isEmpty()|| password!=conformPass ||name.isEmpty() || phone.isEmpty() )

    }
}