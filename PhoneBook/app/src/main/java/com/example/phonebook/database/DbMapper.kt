package com.example.phonebook.database

import com.example.phonebook.domain.model.ColorModel
import com.example.phonebook.domain.model.NEW_CONTACT_ID
import com.example.phonebook.domain.model.PhoneBookModel

class DbMapper {
    fun mapContacts(
        phoneBookDbModels: List<PhoneBookDbModel>,
        colorDbModels: Map<Long, ColorDbModel>
    ): List<PhoneBookModel> = phoneBookDbModels.map {
        val colorDbModel = colorDbModels[it.colorId]
            ?: throw RuntimeException("Color for colorId: ${it.colorId} was not found. Make sure that all colors are passed to this method")

        mapContact(it, colorDbModel)
    }

    fun mapContact(phoneBookDbModel: PhoneBookDbModel, colorDbModel: ColorDbModel): PhoneBookModel {
        val color = mapColor(colorDbModel)
        return with(phoneBookDbModel) { PhoneBookModel(id, name, phone_number, color) }
    }

    fun mapColors(colorDbModels: List<ColorDbModel>): List<ColorModel> =
        colorDbModels.map { mapColor(it) }

    fun mapColor(colorDbModel: ColorDbModel): ColorModel =
        with(colorDbModel) { ColorModel(id, name, hex) }

    fun mapDbContact(contact: PhoneBookModel): PhoneBookDbModel =
        with(contact) {
            if (id == NEW_CONTACT_ID)
                PhoneBookDbModel(
                    name = name,
                    phone_number = phone_number,
                    colorId = color.id,
                    isInTrash = false
                )
            else
                PhoneBookDbModel(id, name, phone_number, color.id, false)
        }
}