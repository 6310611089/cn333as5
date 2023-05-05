package com.example.phonebook.domain.model

const val NEW_CONTACT_ID = -1L

data class PhoneBookModel(
    val id: Long = NEW_CONTACT_ID,
    val name: String = "",
    val phone_number: String = "",
    val color: ColorModel = ColorModel.DEFAULT
)