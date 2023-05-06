package com.example.mad_project

class ReadWriteUserDetails(textDob: String, textGender: String, textMobile: String) {

    var doB: String = textDob
    var gender: String = textGender
    var mobile: String = textMobile

    // Secondary constructor with default values
    constructor() : this("", "", "") {
    }
}
