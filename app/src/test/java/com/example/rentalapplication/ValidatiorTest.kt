package com.example.rentalapplication


import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4


@RunWith(JUnit4::class)
class ValidatiorTest{

    @Test
    fun whenInputIsValid(){

        val email = "ru@gmail.com"
        val password = "1234567"
        val conformPass= "1234567"
        val phone="+94779011093"
        val name ="Rumesh"
        val result = Validatior.validateinput(email, password,conformPass, phone, name )
        assertThat(result).isEqualTo(true)

    }

    @Test
    fun whenPasswordInValid(){

        val email = "ru@gmail.com"
        val password = "1234"
        val conformPass= "1234"
        val phone="+94779011093"
        val name ="Rumesh"
        val result = Validatior.validateinput(email, password, conformPass,phone,name)
        assertThat(result).isEqualTo(false)

    }

    @Test
    fun whenPasswordsAreNotSame(){
        val email = "ru@gmail.com"
        val password = "1234567"
        val conformPass= "1234576"
        val phone="+94779011093"
        val name ="Rumesh"
        val result = Validatior.validateinput(email, password, conformPass,phone,name)
        assertThat(result).isEqualTo(false)
    }
}