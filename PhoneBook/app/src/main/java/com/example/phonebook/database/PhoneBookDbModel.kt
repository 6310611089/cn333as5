package com.example.phonebook.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PhoneBookDbModel(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "phone_number") val phone_number: String,
    @ColumnInfo(name = "color_id") val colorId: Long,
    @ColumnInfo(name = "in_trash") val isInTrash: Boolean
) {
    companion object {
        val DEFAULT_CONTACTS = listOf(
            PhoneBookDbModel(1, "Selena Gomez", "0021846334", 1, false),
            PhoneBookDbModel(2, "Taylor Swift", "5799056704", 2, false),
            PhoneBookDbModel(3, "Peter Parker", "5253709172", 3, false),
            PhoneBookDbModel(4, "Natasha Romanoff", "0674051218", 4, false),
            PhoneBookDbModel(5, "Carol Danvers", "3525280679", 5, false),
            PhoneBookDbModel(6, "Wanda Maximoff", "9966448911", 6, false),
            PhoneBookDbModel(7, "Red Skull", "0089807453", 7, true),
            PhoneBookDbModel(8, "Thanos", "0723825438", 8, true),

            )
    }
}
